package ge.custom.rmlm.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.domain.repository.RecordingsRepository

class RecordingsRepositoryImpl(private val contentResolver: ContentResolver) :
    RecordingsRepository {

    override fun getRecordings(): List<RecordingData> {
        val recordings = mutableListOf<RecordingData>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.RELATIVE_PATH
        )

        val selection = "${MediaStore.Audio.Media.RELATIVE_PATH}=?"
        val selectionArgs = arrayOf(RECORDINGS_EXTERNAL_DIRECTORY)
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"


        contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(collection, id.toString())
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)

                recordings.add(RecordingData(name, contentUri, duration))
            }
        }


        return recordings
    }

    companion object {
        private const val RECORDINGS_EXTERNAL_DIRECTORY = "Recordings/RMLM/"
    }
}