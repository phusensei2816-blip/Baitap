package com.uth.uthauth.data.remote

import com.uth.uthauth.domain.model.ErrorKind
import com.uth.uthauth.domain.model.LoadState
import java.io.IOException
import retrofit2.HttpException

/** Phân loại lỗi gọi API để UI hiện thông báo phù hợp. */
fun Throwable.toLoadError(): LoadState.Error = when (this) {
    is HttpException -> LoadState.Error(
        kind = if (code() == 401 || code() == 403) ErrorKind.UNAUTHORIZED else ErrorKind.SERVER,
        code = code()
    )
    is IOException -> LoadState.Error(ErrorKind.NETWORK)
    else -> LoadState.Error(ErrorKind.UNKNOWN)
}
