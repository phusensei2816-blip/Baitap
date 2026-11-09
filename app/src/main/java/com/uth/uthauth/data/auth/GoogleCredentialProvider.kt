package com.uth.uthauth.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/** Mở hộp thoại chọn tài khoản Google và trả về ID token để đưa cho Firebase. */
class GoogleCredentialProvider(private val webClientId: String) {

    /** [activityContext] phải là Activity context để hiển thị UI chọn tài khoản. */
    suspend fun getIdToken(activityContext: Context): String {
        val option = GetSignInWithGoogleOption.Builder(webClientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val response = CredentialManager.create(activityContext)
            .getCredential(activityContext, request)

        val credential = response.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        throw IllegalStateException("Loại thông tin xác thực không được hỗ trợ")
    }
}
