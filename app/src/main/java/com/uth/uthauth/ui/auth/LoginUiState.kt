package com.uth.uthauth.ui.auth

import com.uth.uthauth.ui.common.UiText

enum class LoginAction { GOOGLE, GUEST }

data class LoginUiState(
    val loadingAction: LoginAction? = null,
    val showPermissionFlow: Boolean = false,
) {
    val isLoading: Boolean get() = loadingAction != null
}

/** Sự kiện một lần (không lưu trong state) để UI hiện thông báo / điều hướng. */
sealed interface LoginEvent {
    data class Success(val message: UiText) : LoginEvent
    data class Error(val message: UiText) : LoginEvent
}
