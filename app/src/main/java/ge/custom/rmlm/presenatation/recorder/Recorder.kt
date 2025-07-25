package ge.custom.rmlm.presenatation.recorder

import android.Manifest
import android.media.AudioFormat
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.os.Parcelable
import androidx.annotation.RequiresPermission
import ge.custom.rmlm.domain.usecase.SaveRecordingUseCase
import ge.custom.rmlm.domain.usecase.SaveRecordingUseCaseParam
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.io.File
import java.io.RandomAccessFile

class RecorderImpl(
    private val localDirPath: String,
    private val dispatcher: CoroutineDispatcher,
    private val saveRecordingUseCase: SaveRecordingUseCase,
    private val audioRecord: AudioRecord
) : Recorder {
    private var recordingTempFile: File? = null
    private var recordingTempRandomAccessFile: RandomAccessFile? = null
    private var isBufferFull: Boolean = false

    // Replace with recordingTempFileStream.filePointer?
    private var currentOffset = 0
    private val bufferMutex = Mutex()
    private val minBufferSize = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        ENCODING_PCM_16BIT
    ) * 2

    private suspend fun initData() {

        bufferMutex.withLock {
            recordingTempFile = File(
                localDirPath,
                RECORDING_TEMP_NAME.format(System.currentTimeMillis())
            )
            currentOffset = 0
            recordingTempRandomAccessFile = RandomAccessFile(recordingTempFile, READ_WRITE_ACCESS)
            isBufferFull = false
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun startRecording(duration: RecorderDuration) {

        initData()

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
                }
                bufferMutex.withLock {
                    if (currentOffset + readBytesCount > recordingSize) {
                        val cutPosition = recordingSize - currentOffset
                        recordingTempRandomAccessFile?.write(
                            tempBytes.copyOfRange(
                                0,
                                cutPosition
                            )
                        )
                        recordingTempRandomAccessFile?.seek(0)
                        recordingTempRandomAccessFile?.write(
                            tempBytes.copyOfRange(
                                cutPosition,
                                readBytesCount
                            )
                        )
                        currentOffset = readBytesCount - cutPosition
                    } else {
                        recordingTempRandomAccessFile?.write(
                            tempBytes.copyOfRange(
                                0,
                                readBytesCount
                            )
                        )
                        currentOffset += readBytesCount
                    }
                }
            }
        }
    }

    private fun getRecordingBufferSize(duration: RecorderDuration): Int =
        SAMPLE_RATE * BIT_DEPTH / 8 * SECONDS_IN_MINUTE * duration.duration

    override suspend fun stopRecording(fromSave: Boolean) {
        audioRecord.stop()
        audioRecord.release()
        bufferMutex.withLock {
            if (!fromSave) {
                recordingTempFile?.delete()
            }
            currentOffset = 0
            isBufferFull = false
            recordingTempFile = null
            recordingTempRandomAccessFile?.close()
            recordingTempRandomAccessFile = null
        }
    }

    override suspend fun saveRecording(
        restart: Boolean
    ) {

        val (file, offset) = bufferMutex.withLock {
            Pair(recordingTempFile, if (isBufferFull) currentOffset else 0).also {
                if (restart) {
                    initData()
                }
            }
        }

        file?.let { audioBytes ->
            saveRecordingUseCase(SaveRecordingUseCaseParam(file, offset, minBufferSize))
        }
        if (!restart) {
            stopRecording(true)
        }
    }

    companion object {
        const val SAMPLE_RATE = 44100
        private const val BIT_DEPTH = 16
        private const val SECONDS_IN_MINUTE = 60
        private const val RECORDING_TEMP_NAME = "recording_%d"
        private const val READ_WRITE_ACCESS = "rw"
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
    suspend fun stopRecording(fromSave: Boolean = false)
    suspend fun saveRecording(restart: Boolean)
}