package ge.custom.rmlm.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
    private val contentResolver: ContentResolver
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
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val formatedDate = SimpleDateFormat(
            DATE_PATTERN,
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val uniqueFilename =
            SAVED_FILE_NAME.format(formatedDate)

        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, uniqueFilename)
            put(MediaStore.Audio.Media.MIME_TYPE, mimeType)
            put(MediaStore.Audio.Media.IS_PENDING, IS_PENDING_TRUE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                put(
                    MediaStore.Audio.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_RECORDINGS + SAVED_FILE_DIRECTORY
                )
            } else {
                put(
                    MediaStore.Audio.Media.DATA,
                    Environment.getExternalStorageDirectory().absolutePath + SAVED_FILE_PATH.format(
                        uniqueFilename
                    )
                )
            }
        }

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
        private const val AUDIO_MIME_TYPE = "audio/mpeg"
        private const val DATE_PATTERN = "dd-MM-yy HH:mm"
        private const val RANDOM_ACCESS_MODE_READ = "r"
        private const val SAVED_FILE_PATH = "/RMLM/%s"
        private const val SAVED_FILE_NAME = "MLM %s"
        private const val SAVED_FILE_DIRECTORY = "/RMLM"
        private const val IS_PENDING_TRUE = 1
        private const val IS_PENDING_FALSE = 0
    }
}