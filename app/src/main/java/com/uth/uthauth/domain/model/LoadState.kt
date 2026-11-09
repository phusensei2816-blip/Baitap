package com.uth.uthauth.domain.model

enum class ErrorKind { NETWORK, SERVER, UNAUTHORIZED, UNKNOWN }

/** Trạng thái tải dữ liệu từ API cho màn hình Trang chủ. */
sealed interface LoadState {
    data object Loading : LoadState
    data object Success : LoadState
    data class Error(val kind: ErrorKind, val code: Int? = null) : LoadState
}
