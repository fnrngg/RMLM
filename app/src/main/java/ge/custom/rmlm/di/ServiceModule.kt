package ge.custom.rmlm.di

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder
import ge.custom.rmlm.data.repository.headergenerator.AudioHeaderGenerator
import ge.custom.rmlm.data.repository.RecordRepositoryImpl
import ge.custom.rmlm.data.repository.headergenerator.AACHeaderGenerator
import ge.custom.rmlm.data.repository.headergenerator.WAVHeaderGenerator
import ge.custom.rmlm.domain.repository.RecordRepository
import ge.custom.rmlm.domain.usecase.SaveInMemoryRecordingUseCase
import ge.custom.rmlm.domain.usecase.SaveRecordingUseCase
import ge.custom.rmlm.presenatation.recorder.Recorder
import ge.custom.rmlm.presenatation.recorder.RecorderImpl
import ge.custom.rmlm.presenatation.recorder.RecorderImpl.Companion.SAMPLE_RATE
import ge.custom.rmlm.presenatation.recorder.cache.RecordingCache
import ge.custom.rmlm.presenatation.recorder.TemporaryFileProvider
import ge.custom.rmlm.presenatation.recorder.cache.RecordingIOCache
import ge.custom.rmlm.presenatation.recorder.cache.RecordingInMemoryCache
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

        factory<RecordingCache>(named(RECORDING_IO_CACHE)) {
            RecordingIOCache(
                androidContext().filesDir.absolutePath,
                get(),
                get()
            )
        }

        factory<RecordingCache>(named(RECORDING_IN_MEMORY_CACHE)) {
            RecordingInMemoryCache(
                get(),
                get(named(AAC_HEADER_GENERATOR))
            )
        }

        factory<Recorder> {
            RecorderImpl(
//                get(named(RECORDING_IO_CACHE)),
                get(named(RECORDING_IN_MEMORY_CACHE)),
                get(named(IODispatcher)),
                get()
            )
        }

        scoped<AudioHeaderGenerator>(named(WAV_HEADER_GENERATOR)) { WAVHeaderGenerator() }
        scoped<AudioHeaderGenerator>(named(AAC_HEADER_GENERATOR)) { AACHeaderGenerator() }

        scoped<RecordRepository> {
            RecordRepositoryImpl(
                get(named(WAV_HEADER_GENERATOR)),
                get(named(AAC_HEADER_GENERATOR)),
                get(),
                get()
            )
        }

        scoped<SaveRecordingUseCase> {
            SaveRecordingUseCase(
                get(named(RecordingIOErrorsSuspendRunner)),
                get()
            )
        }

        scoped<SaveInMemoryRecordingUseCase> {
            SaveInMemoryRecordingUseCase(
                get(named(RecordingIOErrorsSuspendRunner)),
                get()
            )
        }
    }
}

private const val RECORDING_IO_CACHE = "RECORDING_IO_CACHE"
private const val RECORDING_IN_MEMORY_CACHE = "RECORDING_IN_MEMORY_CACHE"
private const val WAV_HEADER_GENERATOR = "WAV_HEADER_GENERATOR"
private const val AAC_HEADER_GENERATOR = "AAC_HEADER_GENERATOR"