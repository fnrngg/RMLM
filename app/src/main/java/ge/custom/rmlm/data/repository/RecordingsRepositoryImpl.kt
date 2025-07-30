package ge.custom.rmlm.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.domain.repository.RecordingsRepository

class RecordingsRepositoryImpl(
    private val contentResolver: ContentResolver,
    private val mediaStoreParamsProvider: MediaStoreParamsProvider
) :
    RecordingsRepository {

    override fun getRecordings(searchValue: String): List<RecordingData> {
        val recordings = mutableListOf<RecordingData>()
        val collection = mediaStoreParamsProvider.getCollection()

        val projection = mediaStoreParamsProvider.getProjection()

        val selection = "${mediaStoreParamsProvider.getSelectionPathFilter()} = ? AND " +
                "${MediaStore.Audio.Media.DISPLAY_NAME} LIKE ?"

        val selectionArgs = arrayOf(
            mediaStoreParamsProvider.getFileDirectory(),
            "%$searchValue%"
        )
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
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(collection, id.toString())
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)

                recordings.add(
                    RecordingData(
                        name,
                        contentUri,
                        duration,
                        dateAdded * SECONDS_TO_MILLISECONDS
                    )
                )
            }
        }


        return recordings
    }

    override fun deleteRecording(uri: Uri) {
        contentResolver.delete(uri, null, null)
    }

    companion object {
        private const val SECONDS_TO_MILLISECONDS = 1000
    }
}