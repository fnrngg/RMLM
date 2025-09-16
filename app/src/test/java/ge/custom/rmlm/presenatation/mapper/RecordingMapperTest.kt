package ge.custom.rmlm.presenatation.mapper

import ge.custom.rmlm.domain.model.RecordingData
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class RecordingMapperTest {

    @Test
    fun `mapRecordingDataToRecordingUiData should map RecordingData to RecordingUiData`() =
        runTest {
            val recordingMapper = RecordingMapper(
                mockk {
                    every { getCurrentLocale() } returns Locale.ENGLISH
                    every { getCurrentTimeZone() } returns TimeZone.getTimeZone("UTC")
                }
            )
            val recordingData = RecordingData(
                "test",
                mockk(),
                1000,
                1753822725000
            )
            val recordingUiData = recordingMapper.mapRecordingDataToRecordingUiData(recordingData)
            assert(recordingUiData.name == recordingData.name)
            assert(recordingUiData.uri == recordingData.uri)

            println(recordingUiData.date)
            assert(recordingUiData.duration == "00:01")
            assert(recordingUiData.date == "07/29/2025 20:58:45")
        }
}