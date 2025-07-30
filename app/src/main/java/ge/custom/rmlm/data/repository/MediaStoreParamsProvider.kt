package ge.custom.rmlm.data.repository

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_MUSIC
import android.os.Environment.DIRECTORY_RECORDINGS
import android.provider.MediaStore

class MediaStoreParamsProvider {
    fun getFileDirectory() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> DIRECTORY_RECORDINGS
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> DIRECTORY_MUSIC
        else -> Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).absolutePath
    } + APP_MEDIA_DIRECTORY

    fun getSelectionPathFilter() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Audio.Media.RELATIVE_PATH
    else MediaStore.Audio.Media.DATA

    fun getCollection(): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    fun getContentValues(uniqueFilename: String, mimeType: String) = ContentValues().apply {
        put(MediaStore.Audio.Media.DISPLAY_NAME, uniqueFilename)
        put(MediaStore.Audio.Media.MIME_TYPE, mimeType)
        put(MediaStore.Audio.Media.IS_PENDING, IS_PENDING_TRUE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(
                MediaStore.Audio.Media.RELATIVE_PATH,
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    DIRECTORY_RECORDINGS
                else DIRECTORY_MUSIC) + SAVED_FILE_DIRECTORY
            )
        } else {
            put(
                MediaStore.Audio.Media.DATA,
                Environment.getExternalStoragePublicDirectory(
                    DIRECTORY_MUSIC
                ).absolutePath + SAVED_FILE_PATH.format(
                    uniqueFilename
                )
            )
        }
    }

    fun getProjection() = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATE_ADDED,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.RELATIVE_PATH
        } else MediaStore.Audio.Media.DATA
    )

    companion object {
        private const val APP_MEDIA_DIRECTORY = "/RMLM/"
        const val AUDIO_MIME_TYPE = "audio/mpeg"
        const val DATE_PATTERN = "dd-MM-yy HH:mm"
        const val RANDOM_ACCESS_MODE_READ = "r"
        const val SAVED_FILE_PATH = "/RMLM/%s"
        const val SAVED_FILE_NAME = "MLM %s"
        const val SAVED_FILE_DIRECTORY = "/RMLM"
        const val IS_PENDING_TRUE = 1
        const val IS_PENDING_FALSE = 0
    }
}