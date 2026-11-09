package com.uth.uthauth.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uth.uthauth.BuildConfig
import com.uth.uthauth.R
import com.uth.uthauth.data.auth.AuthRepository
import com.uth.uthauth.data.auth.FirebaseAuthRepository
import com.uth.uthauth.data.auth.GoogleCredentialProvider
import com.uth.uthauth.data.config.RemoteConfigRepository
import com.uth.uthauth.data.prefs.AppPreferences
import com.uth.uthauth.data.presence.UserPresenceRepository
import com.uth.uthauth.data.remote.NetworkModule
import com.uth.uthauth.data.task.ApiTaskRepository
import com.uth.uthauth.data.task.TaskRepository
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.push.PushRegistrar

/** Manual DI container: giữ các dependency dùng chung toàn app. */
class AppContainer(context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    val preferences = AppPreferences(context)
    val telemetry = AppTelemetry(context)

    val authRepository: AuthRepository =
        FirebaseAuthRepository(FirebaseAuth.getInstance())

    // Danh sách việc lấy từ REST API /mock/teach/tasks (cấu hình trong local.properties).
    val taskRepository: TaskRepository = ApiTaskRepository(
        api = NetworkModule.createTaskApi(
            baseUrl = BuildConfig.API_BASE_URL,
            token = BuildConfig.API_TOKEN,
            debug = BuildConfig.DEBUG,
        ),
        telemetry = telemetry,
    )

    val remoteConfig = RemoteConfigRepository(telemetry)
    val presence = UserPresenceRepository(telemetry)
    val pushRegistrar = PushRegistrar(firestore, telemetry)

    // default_web_client_id được plugin google-services sinh ra từ google-services.json
    val googleCredentialProvider = GoogleCredentialProvider(
        webClientId = context.getString(R.string.default_web_client_id)
    )
}
