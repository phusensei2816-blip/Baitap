package com.uth.uthauth.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** Đổi ngôn ngữ theo từng ứng dụng (Per-app language). AppCompat tự lưu lựa chọn. */
object LanguageManager {
    const val VIETNAMESE = "vi"
    const val ENGLISH = "en"

    fun set(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}
