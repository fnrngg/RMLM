package ge.custom.rmlm.domain.repository

import java.io.File

interface RecordRepository {
    suspend fun saveRecordingAsWAV(
        file: File,
        offset: Int,
        bufferSize: Int
    )
}