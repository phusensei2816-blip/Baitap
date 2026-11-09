package com.uth.uthauth.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.uthauth.R
import com.uth.uthauth.ui.AppViewModelProvider
import com.uth.uthauth.ui.components.LanguageToggle
import com.uth.uthauth.ui.permissions.PermissionFlow
import kotlinx.coroutines.launch

/** Entry point của màn hình: nối ViewModel với UI và xử lý sự kiện một lần. */
@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentOnSuccess by rememberUpdatedState(onLoginSuccess)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.Success -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                    currentOnSuccess()
                }
                is LoginEvent.Error -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch { snackbarHostState.showSnackbar(event.message.asString(context)) }
                }
            }
        }
    }

    LoginScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onGoogleClick = { viewModel.onGoogleClick(context) },
        onGuestClick = viewModel::onGuestClick,
    )

    // Lần đầu mở app: hỏi lần lượt Vị trí -> Thông báo -> Máy ảnh
    if (state.showPermissionFlow) {
        PermissionFlow(onFinished = viewModel::onPermissionFlowFinished)
    }
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onGoogleClick: () -> Unit,
    onGuestClick: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                LanguageToggle()
            }

            Spacer(Modifier.weight(1f))

            LogoBadge()
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.weight(1.3f))

            GoogleSignInButton(
                loading = state.loadingAction == LoginAction.GOOGLE,
                enabled = !state.isLoading,
                onClick = onGoogleClick
            )
            OrDivider(stringResource(R.string.login_or))
            GuestButton(
                loading = state.loadingAction == LoginAction.GUEST,
                enabled = !state.isLoading,
                onClick = onGuestClick
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.login_terms),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
