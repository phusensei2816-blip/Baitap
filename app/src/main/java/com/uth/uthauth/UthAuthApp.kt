package com.uth.uthauth

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.uth.uthauth.di.AppContainer
import com.uth.uthauth.push.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UthAuthApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)
        // AdMob nên khởi tạo ở luồng nền
        CoroutineScope(Dispatchers.IO).launch { MobileAds.initialize(this@UthAuthApp) }
    }
}
