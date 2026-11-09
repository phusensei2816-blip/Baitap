package com.uth.uthauth.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uth.uthauth.UthAuthApp

class UthMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        val container = (application as UthAuthApp).container
        container.authRepository.currentUser?.uid?.let { container.pushRegistrar.saveToken(it, token) }
    }

    /** Chỉ chạy khi app đang mở (foreground). Khi app ở nền, hệ thống tự hiển thị thông báo. */
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"].orEmpty()
        val body = message.notification?.body ?: message.data["body"] ?: return
        NotificationHelper.show(this, title, body)
    }
}
