package ge.custom.rmlm.presenatation.recorder

import android.media.AudioFormat
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.os.Parcelable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

class RecorderImpl(
    private val recordingCache: RecordingCache,
    private val dispatcher: CoroutineDispatcher,
    private val audioRecord: AudioRecord
) : Recorder {
    private val minBufferSize = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        ENCODING_PCM_16BIT
    ) * 2

    override suspend fun startRecording(duration: RecorderDuration) {

        recordingCache.initCache()

        val recordingSize = getRecordingBufferSize(duration)

        audioRecord.startRecording()

        withContext(dispatcher) {
            val tempBytes = ByteArray(
                minBufferSize
            )
            while (true) {
                val readBytesCount = audioRecord.read(
                    tempBytes,
                    0,
                    minBufferSize
                )
                if (readBytesCount < 0) {
                    break
                } else if (readBytesCount == 0) {
                    continue
                }
                recordingCache.saveInCache(tempBytes, readBytesCount, recordingSize)
            }
        }
    }

    private fun getRecordingBufferSize(duration: RecorderDuration): Int =
        SAMPLE_RATE * BIT_DEPTH / 8 * SECONDS_IN_MINUTE * duration.duration

    override suspend fun stopRecording() {
        audioRecord.stop()
        audioRecord.release()
        recordingCache.stopCaching(true)
    }

    override suspend fun saveRecording(
        restart: Boolean
    ) {

        recordingCache.saveRecording(restart, minBufferSize)

        if (!restart) {
            recordingCache.stopCaching(false)
        }
    }

    companion object {
        const val SAMPLE_RATE = 44100
        private const val BIT_DEPTH = 16
        private const val SECONDS_IN_MINUTE = 60
    }
}

@Parcelize
enum class RecorderDuration(
    val duration: Int
) : Parcelable {
    SHORTEST(5),
    SHORT(10),
    MEDIUM(20),
    LONG(30),
    LONGEST(60)
}

interface Recorder {
    suspend fun startRecording(duration: RecorderDuration)
    suspend fun stopRecording()
    suspend fun saveRecording(restart: Boolean)
}