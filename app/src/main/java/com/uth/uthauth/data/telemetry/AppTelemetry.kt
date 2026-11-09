package com.uth.uthauth.data.telemetry

import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.uth.uthauth.domain.model.TaskCategory

/** Gom Firebase Analytics + Crashlytics vào một chỗ để ViewModel dễ dùng và dễ test. */
class AppTelemetry(context: Context) {

    private val analytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()

    fun logLogin(method: String) {
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundleOf(FirebaseAnalytics.Param.METHOD to method))
    }

    fun logAddTask(category: TaskCategory) {
        analytics.logEvent("add_task", bundleOf("category" to category.name))
    }

    fun logCompleteTask(category: TaskCategory) {
        analytics.logEvent("complete_task", bundleOf("category" to category.name))
    }

    fun logDeleteTask() {
        analytics.logEvent("delete_task", null)
    }

    fun logScreen(route: String) {
        analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(FirebaseAnalytics.Param.SCREEN_NAME to route)
        )
    }

    fun setUser(uid: String?) {
        analytics.setUserId(uid)
        crashlytics.setUserId(uid.orEmpty())
    }

    /** Gửi lỗi không gây crash lên Crashlytics. */
    fun recordError(t: Throwable) {
        crashlytics.recordException(t)
    }
}
