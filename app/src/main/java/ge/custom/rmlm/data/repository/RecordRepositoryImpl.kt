package ge.custom.rmlm.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import ge.custom.rmlm.data.repository.MediaStoreParamsProvider.Companion.AUDIO_MIME_TYPE
import ge.custom.rmlm.data.repository.MediaStoreParamsProvider.Companion.DATE_PATTERN
import ge.custom.rmlm.data.repository.MediaStoreParamsProvider.Companion.IS_PENDING_FALSE
import ge.custom.rmlm.data.repository.MediaStoreParamsProvider.Companion.RANDOM_ACCESS_MODE_READ
import ge.custom.rmlm.data.repository.MediaStoreParamsProvider.Companion.SAVED_FILE_NAME
import ge.custom.rmlm.domain.repository.RecordRepository
import ge.custom.rmlm.presenatation.recorder.RecorderImpl.Companion.SAMPLE_RATE
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.min

class RecordRepositoryImpl(
    private val wavHeaderGenerator: AudioHeaderGenerator,
    private val contentResolver: ContentResolver,
    private val mediaStoreParamsProvider: MediaStoreParamsProvider
) : RecordRepository {

    override suspend fun saveRecordingAsWAV(
        file: File,
        offset: Int,
        bufferSize: Int
    ) {
        val fileSize = file.length()
        val header = wavHeaderGenerator.getEncodedHeader(
            fileSize,
            SAMPLE_RATE,
            CHANNEL_COUNT
        )

        val mimeType = AUDIO_MIME_TYPE
        val collection = mediaStoreParamsProvider.getCollection()

        val formatedDate = SimpleDateFormat(
            DATE_PATTERN,
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val uniqueFilename =
            SAVED_FILE_NAME.format(formatedDate)

        val values = mediaStoreParamsProvider.getContentValues(uniqueFilename, mimeType)

        var newUri: Uri? = null
        try {
            newUri = contentResolver.insert(collection, values)
            newUri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(header)

                    val tempRandomAccessFile = RandomAccessFile(file, RANDOM_ACCESS_MODE_READ)
                    tempRandomAccessFile.seek(offset.toLong())
                    var maxOffset = fileSize
                    var localIsFull = offset != 0

                    while (true) {
                        if (tempRandomAccessFile.filePointer == maxOffset) {
                            if (localIsFull) {
                                localIsFull = false
                                maxOffset = offset.toLong()
                                tempRandomAccessFile.seek(0)
                            } else break
                        }
                        val audioBytes =
                            ByteArray(
                                min(
                                    bufferSize,
                                    (maxOffset - tempRandomAccessFile.filePointer).toInt()
                                )
                            )
                        val readBytesCount = tempRandomAccessFile.read(audioBytes)
                        if (readBytesCount < 0) {
                            break
                        }
                        outputStream.write(audioBytes)


                    }
                    tempRandomAccessFile.close()
                    outputStream.flush()
                }
                values.clear()
                values.put(MediaStore.Audio.Media.IS_PENDING, IS_PENDING_FALSE)
                contentResolver.update(uri, values, null, null)
                file.delete()
            }
        } catch (e: IOException) {
            newUri?.let { contentResolver.delete(it, null, null) }
            throw e
        }
    }

    companion object {
        const val CHANNEL_COUNT = 1
    }
}