package ge.custom.rmlm.presenatation.recorder

import ge.custom.rmlm.domain.usecase.SaveRecordingUseCase
import ge.custom.rmlm.domain.usecase.SaveRecordingUseCaseParam
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.RandomAccessFile

class RecordingCacheImpl(
    private val localDirPath: String,
    private val temporaryFileProvider: TemporaryFileProvider,
    private val saveRecordingUseCase: SaveRecordingUseCase
) : RecordingCache {
    private val bufferMutex = Mutex()
    private var recordingTempFile: File? = null
    private var recordingTempRandomAccessFile: RandomAccessFile? = null
    private var isBufferFull: Boolean = false

    // Replace with recordingTempFileStream.filePointer?
    private var currentOffset = 0

    override fun initCache() {
        recordingTempFile = temporaryFileProvider.getFile(
            localDirPath,
            RECORDING_TEMP_NAME.format(System.currentTimeMillis())
        )
        recordingTempRandomAccessFile = temporaryFileProvider.getRandomAccessFile(recordingTempFile!!, READ_WRITE_ACCESS)
        currentOffset = 0
        isBufferFull = false
    }

    override suspend fun saveInCache(
        tempBytes: ByteArray,
        readBytesCount: Int,
        recordingSize: Int
    ) {
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

    override suspend fun saveRecording(restart: Boolean, minBufferSize: Int) {
        val (file, offset) = bufferMutex.withLock {
            Pair(recordingTempFile, if (isBufferFull) currentOffset else 0).also {
                if (restart) {
                    initCache()
                }
            }
        }

        file?.let { file ->
            saveRecordingUseCase(SaveRecordingUseCaseParam(file, offset, minBufferSize))
        }
    }

    override suspend fun stopCaching(deleteCache: Boolean) {
        bufferMutex.withLock {
            if (deleteCache) {
                recordingTempFile?.delete()
            }
            currentOffset = 0
            isBufferFull = false
            recordingTempFile = null
            recordingTempRandomAccessFile?.close()
            recordingTempRandomAccessFile = null
        }
    }

    companion object {
        private const val RECORDING_TEMP_NAME = "recording_%d"
        private const val READ_WRITE_ACCESS = "rw"
    }
}

interface RecordingCache {

    fun initCache()

    suspend fun saveInCache(tempBytes: ByteArray, readBytesCount: Int, recordingSize: Int)

    suspend fun saveRecording(restart: Boolean, minBufferSize: Int)

    suspend fun stopCaching(deleteCache: Boolean)
}