package ge.custom.rmlm.presenatation.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import ge.custom.rmlm.R
import ge.custom.rmlm.di.appModule
import ge.custom.rmlm.di.serviceModule
import ge.custom.rmlm.presenatation.service.RecorderService.Companion.NOTIFICATION_CHANNEL_ID
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule, serviceModule)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_name_recording_management),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}