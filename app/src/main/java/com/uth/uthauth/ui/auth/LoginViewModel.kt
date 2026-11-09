package com.uth.uthauth.ui.auth

import android.content.Context
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.uthauth.R
import com.uth.uthauth.data.auth.AuthRepository
import com.uth.uthauth.data.auth.GoogleCredentialProvider
import com.uth.uthauth.data.auth.toAuthMessage
import com.uth.uthauth.data.prefs.AppPreferences
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.AuthUser
import com.uth.uthauth.ui.common.UiText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val googleProvider: GoogleCredentialProvider,
    private val telemetry: AppTelemetry,
    private val preferences: AppPreferences,
) : ViewModel() {

    private val loadingAction = MutableStateFlow<LoginAction?>(null)

    val uiState: StateFlow<LoginUiState> = combine(
        loadingAction,
        preferences.permissionOnboardingDone
    ) { action, permissionDone ->
        LoginUiState(loadingAction = action, showPermissionFlow = !permissionDone)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LoginUiState()
    )

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /** Đã đi hết luồng xin quyền (kể cả bấm "Để sau") -> không hiện lại. */
    fun onPermissionFlowFinished() {
        viewModelScope.launch { preferences.setPermissionOnboardingDone() }
    }

    /** [activityContext] chỉ dùng trong lúc gọi, không được lưu lại. */
    fun onGoogleClick(activityContext: Context) {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            loadingAction.value = LoginAction.GOOGLE
            val result = try {
                val idToken = googleProvider.getIdToken(activityContext)
                authRepository.signInWithGoogle(idToken)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
            handleResult(result)
        }
    }

    fun onGuestClick() {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            loadingAction.value = LoginAction.GUEST
            handleResult(authRepository.signInAnonymously())
        }
    }

    private suspend fun handleResult(result: Result<AuthUser>) {
        loadingAction.value = null
        result
            .onSuccess { user ->
                telemetry.logLogin(if (user.isAnonymous) "anonymous" else "google")
                telemetry.setUser(user.uid)
                val name = user.nameOrNull
                _events.send(
                    LoginEvent.Success(
                        if (name != null) UiText.Res(R.string.login_success_named, name)
                        else UiText.Res(R.string.login_success_guest)
                    )
                )
            }
            .onFailure { e ->
                if (e !is GetCredentialCancellationException) telemetry.recordError(e)
                _events.send(LoginEvent.Error(e.toAuthMessage()))
            }
    }
}
