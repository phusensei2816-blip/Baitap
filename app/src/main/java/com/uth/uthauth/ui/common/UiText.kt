package com.uth.uthauth.ui.common

import android.content.Context
import androidx.annotation.StringRes

/** Văn bản do ViewModel tạo ra nhưng được dịch ở tầng UI (theo ngôn ngữ hiện tại). */
sealed interface UiText {
    fun asString(context: Context): String

    data class Plain(val value: String) : UiText {
        override fun asString(context: Context) = value
    }

    class Res(@StringRes private val id: Int, private vararg val args: Any) : UiText {
        override fun asString(context: Context): String = context.getString(id, *args)
    }
}
