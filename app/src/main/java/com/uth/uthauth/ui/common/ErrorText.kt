package com.uth.uthauth.ui.common

import com.uth.uthauth.R
import com.uth.uthauth.domain.model.ErrorKind
import com.uth.uthauth.domain.model.LoadState

/** Thông báo lỗi (đa ngôn ngữ) cho cả lỗi tải dữ liệu lẫn lỗi thao tác PATCH/DELETE. */
fun LoadState.Error.toUiText(): UiText = when (kind) {
    ErrorKind.NETWORK -> UiText.Res(R.string.load_error_network)
    ErrorKind.SERVER -> UiText.Res(R.string.load_error_server, code ?: 0)
    ErrorKind.UNAUTHORIZED -> UiText.Res(R.string.load_error_unauthorized)
    ErrorKind.UNKNOWN -> UiText.Res(R.string.load_error_unknown)
}
