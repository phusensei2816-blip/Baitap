package com.uth.uthauth.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.uthauth.data.auth.AuthRepository
import com.uth.uthauth.data.presence.UserPresenceRepository
import com.uth.uthauth.data.task.TaskRepository
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.AuthUser
import com.uth.uthauth.domain.model.DayStat
import com.uth.uthauth.domain.model.TaskSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: AuthUser? = null,
    val summary: TaskSummary = TaskSummary(0, 0),
    val weekly: List<DayStat> = emptyList(),
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    taskRepository: TaskRepository,
    private val telemetry: AppTelemetry,
    private val presence: UserPresenceRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.observeUser(),
        taskRepository.tasks,
        taskRepository.weeklyStats
    ) { user, tasks, weekly ->
        ProfileUiState(
            user = user,
            summary = TaskSummary(total = tasks.size, done = tasks.count { it.isDone }),
            weekly = weekly
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState(user = authRepository.currentUser)
    )

    /** Gửi một lỗi không gây crash lên Crashlytics để kiểm tra tích hợp. */
    fun sendTestError() {
        telemetry.recordError(RuntimeException("Lỗi thử nghiệm Crashlytics từ màn hình Hồ sơ"))
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            presence.stop()          // đặt offline khi còn đăng nhập
            telemetry.setUser(null)
            authRepository.signOut()
            onDone()
        }
    }
}
