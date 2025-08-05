package ge.custom.rmlm.di

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder
import ge.custom.rmlm.data.repository.AudioHeaderGenerator
import ge.custom.rmlm.data.repository.RecordRepositoryImpl
import ge.custom.rmlm.data.repository.WAVHeaderGenerator
import ge.custom.rmlm.domain.repository.RecordRepository
import ge.custom.rmlm.domain.usecase.SaveRecordingUseCase
import ge.custom.rmlm.presenatation.recorder.Recorder
import ge.custom.rmlm.presenatation.recorder.RecorderImpl
import ge.custom.rmlm.presenatation.recorder.RecorderImpl.Companion.SAMPLE_RATE
import ge.custom.rmlm.presenatation.recorder.RecordingCache
import ge.custom.rmlm.presenatation.recorder.RecordingCacheImpl
import ge.custom.rmlm.presenatation.recorder.TemporaryFileProvider
import ge.custom.rmlm.presenatation.service.RecorderService
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

@SuppressLint("MissingPermission")
val serviceModule = module {
    includes(sharedModule)
    scope<RecorderService> {
        scoped { androidContext().contentResolver }

        factory {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    ENCODING_PCM_16BIT
                )
            )
        }

        scoped { TemporaryFileProvider() }

        factory<RecordingCache> {
            RecordingCacheImpl(
                androidContext().filesDir.absolutePath,
                get(),
                get()
            )
        }

        factory<Recorder> {
            RecorderImpl(
                get(),
                get(named(IODispatcher)),
                get()
            )
        }

        scoped<AudioHeaderGenerator>(named(WAV_HEADER_GENERATOR)) { WAVHeaderGenerator() }

        scoped<RecordRepository> {
            RecordRepositoryImpl(
                get(named(WAV_HEADER_GENERATOR)),
                get(),
                get()
            )
        }

        scoped<SaveRecordingUseCase> {
            SaveRecordingUseCase(
                get(named(RecordingErrorsSuspendRunner)),
                get()
            )
        }
    }
}

private const val WAV_HEADER_GENERATOR = "WAV_HEADER_GENERATOR"