package ge.custom.rmlm.domain.repository

import android.net.Uri
import ge.custom.rmlm.domain.model.RecordingData

interface RecordingsRepository {
    fun getRecordings(searchValue: String): List<RecordingData>
    fun deleteRecording(uri: Uri)
}