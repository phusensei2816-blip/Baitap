package com.uth.uthauth.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.uth.uthauth.data.telemetry.AppTelemetry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/** [welcomeMessage] = null nghĩa là chưa cấu hình trên Remote Config -> UI không hiện banner. */
data class AppConfig(
    val welcomeMessage: String? = null,
    val showAds: Boolean = true,
)

/** Remote Config: đổi lời chào / bật tắt quảng cáo từ Firebase Console mà không cần cập nhật app. */
class RemoteConfigRepository(private val telemetry: AppTelemetry) {

    private val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60) // để demo; production nên >= 3600
                .build()
        )
        setDefaultsAsync(
            mapOf(
                KEY_WELCOME to "",
                KEY_SHOW_ADS to true,
            )
        )
    }

    fun current() = AppConfig(
        welcomeMessage = remoteConfig.getString(KEY_WELCOME).takeIf { it.isNotBlank() },
        showAds = remoteConfig.getBoolean(KEY_SHOW_ADS),
    )

    suspend fun refresh() {
        try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            telemetry.recordError(e)
        }
    }

    private companion object {
        const val KEY_WELCOME = "welcome_message"
        const val KEY_SHOW_ADS = "show_ads"
    }
}
