package ge.custom.rmlm.presenatation.mapper

import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.presenatation.model.RecordingUiData
import java.text.SimpleDateFormat

class RecordingMapper(private val localeProvider: LocaleProvider) {

    fun mapRecordingDataToRecordingUiData(recordingData: RecordingData): RecordingUiData {
        val currLocale = localeProvider.getCurrentLocale()
        val durationFormatter =
            SimpleDateFormat(DURATION_PATTERN, currLocale)
        durationFormatter.timeZone = localeProvider.getCurrentTimeZone()
        val dateFormatter = SimpleDateFormat(DATE_PATTERN, currLocale)
        dateFormatter.timeZone = localeProvider.getCurrentTimeZone()
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