package ge.custom.rmlm.presenatation.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ge.custom.rmlm.R
import ge.custom.rmlm.di.IODispatcher
import ge.custom.rmlm.presenatation.recorder.Recorder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.android.scope.serviceScope
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

class RecorderService() : Service(), AndroidScopeComponent {
    private val job = SupervisorJob()

    override val scope: Scope by serviceScope()

    private var recorder: Recorder? = null
    private val recorderServiceState: RecorderServiceState by inject()
    private val dispatcher: CoroutineDispatcher by inject(named(IODispatcher))

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun startInCoroutine(task: suspend () -> Unit) {
        CoroutineScope(dispatcher + job).launch {
            task()
        }
    }

    private fun start() {
        val recorderDuration = recorderServiceState.recordState.value.recorderDuration!!

        val saveAndRestartIntent = PendingIntent.getService(
            this,
            1,
            Intent(
                this,
                RecorderService::class.java
            ).apply {
                action = ACTION_SAVE_RECORDING_AND_RESTART
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopRecordingIntent = PendingIntent.getService(
            this,
            1,
            Intent(
                this,
                RecorderService::class.java
            ).apply {
                action = ACTION_STOP_RECORDING
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        val saveRecordingIntent = PendingIntent.getService(
            this,
            1,
            Intent(
                this,
                RecorderService::class.java
            ).apply {
                action = ACTION_SAVE_RECORDING
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_arrow_down)
            .setContentTitle(
                getString(
                    R.string.recorder_service_notification_title,
                    recorderDuration.duration
                ))
            .setContentText(getString(R.string.recorder_service_notification_description))
            .addAction(
                R.drawable.ic_arrow_down,
                getString(R.string.notification_save_and_restart), saveAndRestartIntent
            )
            .addAction(
                R.drawable.ic_arrow_down,
                getString(R.string.notification_stop_recording), stopRecordingIntent
            )
            .addAction(
                R.drawable.ic_arrow_down,
                getString(R.string.notification_save_recording), saveRecordingIntent
            )
            .build()


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(
                    SERVICE_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                )
            } else {
                startForeground(SERVICE_ID, notification)
            }
            recorderServiceState.setActive(true)
        } catch (_: Exception) {
            stopSelf()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
//                && e is ForegroundServiceStartNotAllowedException
//            ) {
//
//            }
        }

        recorder = get()
        startInCoroutine {
            try {
                recorder?.startRecording(
                    recorderDuration
                )
            } catch (_: Exception) {
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORDING -> {
                start()
            }

            ACTION_STOP_RECORDING -> {
                startInCoroutine {
                    recorder?.stopRecording()
                    recorder = null
                    stopSelf()
                }
            }

            ACTION_SAVE_RECORDING -> {
                startInCoroutine {
                    recorder?.saveRecording(false)
                    recorder = null
                    sendBroadcast(Intent(ACTION_NEW_RECORDING_SAVED))
                    stopSelf()
                }
            }

            ACTION_SAVE_RECORDING_AND_RESTART -> {
                startInCoroutine {
                    recorder?.saveRecording(true)
                    sendBroadcast(Intent(ACTION_NEW_RECORDING_SAVED))
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        recorderServiceState.setActive(false)
        recorderServiceState.setRecorderDuration(null)
        scope.close()
    }

    companion object {
        private const val ACTION_START_RECORDING = "ACTION_START_RECORDING"
        private const val ACTION_STOP_RECORDING = "ACTION_STOP_RECORDING"
        private const val ACTION_SAVE_RECORDING = "ACTION_SAVE_RECORDING"
        private const val ACTION_SAVE_RECORDING_AND_RESTART = "ACTION_SAVE_RECORDING_AND_RESTART"

        const val ACTION_NEW_RECORDING_SAVED = "ACTION_NEW_RECORDING_SAVED"

        private const val SERVICE_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

        fun getStartIntent(context: Context): Intent {
            return Intent(context, RecorderService::class.java).apply {
                action = ACTION_START_RECORDING
            }
        }

        fun getStopIntent(context: Context): Intent {
            return Intent(context, RecorderService::class.java).apply {
                action = ACTION_STOP_RECORDING
            }
        }

        fun getSaveIntent(context: Context): Intent {
            return Intent(context, RecorderService::class.java).apply {
                action = ACTION_SAVE_RECORDING
            }
        }

        fun getSaveAndRestartIntent(context: Context): Intent {
            return Intent(context, RecorderService::class.java).apply {
                action = ACTION_SAVE_RECORDING_AND_RESTART
            }
        }
    }
}