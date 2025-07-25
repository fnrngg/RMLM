package ge.custom.rmlm.domain.repository

import ge.custom.rmlm.domain.model.RecordingData

interface RecordingsRepository {
    fun getRecordings(): List<RecordingData>
}