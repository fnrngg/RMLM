package ge.custom.rmlm.presenatation.mapper

import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.presenatation.model.RecordingUiData
import java.text.SimpleDateFormat
import java.util.Locale

class RecordingMapper {

    fun mapRecordingDataToRecordingUiData(recordingData: RecordingData): RecordingUiData {
        val durationFormatter = SimpleDateFormat(DURATION_PATTERN, Locale.getDefault())
        val dateFormatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return RecordingUiData(
            name = recordingData.name,
            uri = recordingData.uri,
            duration = durationFormatter.format(recordingData.duration),
            date = dateFormatter.format(recordingData.date)
        )
    }

    companion object {
        private const val DATE_PATTERN = "MM/dd/yyyy HH:mm:ss"
        private const val DURATION_PATTERN = "mm:ss"
    }
}