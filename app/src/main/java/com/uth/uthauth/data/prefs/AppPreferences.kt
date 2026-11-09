package com.uth.uthauth.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

/** Lưu cài đặt nhỏ bằng DataStore (thay cho SharedPreferences). */
class AppPreferences(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    /** Đã đi qua luồng xin quyền lần đầu chưa. */
    val permissionOnboardingDone: Flow<Boolean> =
        dataStore.data.map { it[KEY_PERMISSION_DONE] ?: false }

    suspend fun setPermissionOnboardingDone() {
        dataStore.edit { it[KEY_PERMISSION_DONE] = true }
    }

    private companion object {
        val KEY_PERMISSION_DONE = booleanPreferencesKey("permission_onboarding_done")
    }
}
