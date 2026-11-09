package com.uth.uthauth.data.auth

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.uth.uthauth.R
import com.uth.uthauth.ui.common.UiText

/** Chuyển exception kỹ thuật thành thông báo lỗi thân thiện (đa ngôn ngữ). */
fun Throwable.toAuthMessage(): UiText = when (this) {
    is GetCredentialCancellationException -> UiText.Res(R.string.err_cancelled)
    is NoCredentialException -> UiText.Res(R.string.err_no_credential)
    is GetCredentialException -> UiText.Res(R.string.err_credential)
    is FirebaseNetworkException -> UiText.Res(R.string.err_network_auth)
    is FirebaseAuthInvalidCredentialsException -> UiText.Res(R.string.err_invalid_credential)
    is FirebaseAuthException -> UiText.Res(R.string.err_firebase_auth, errorCode)
    else -> message?.let { UiText.Plain(it) } ?: UiText.Res(R.string.err_unknown)
}
