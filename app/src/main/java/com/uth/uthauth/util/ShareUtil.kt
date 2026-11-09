package com.uth.uthauth.util

import android.content.Context
import android.content.Intent
import com.uth.uthauth.R

/** Thay cho Firebase Invites/Dynamic Links (đã ngừng): dùng share sheet của Android. */
fun shareInvite(context: Context) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.invite_text))
    }
    context.startActivity(Intent.createChooser(send, context.getString(R.string.invite_chooser)))
}
