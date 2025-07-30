package ge.custom.rmlm.presenatation.viewmodels

import ge.custom.rmlm.presenatation.recorder.RecorderDuration
import ge.custom.rmlm.presenatation.service.RecorderServiceStateImpl
import org.junit.Test
import kotlin.test.assertEquals

class RecordViewModelTest {

    @Test
    fun `initial state should have isRecording false and durationChoice null`() {
        val viewModel = RecordViewModel(RecorderServiceStateImpl())

        assertEquals(false, viewModel.recordState.value.isRecording)
        assertEquals(null, viewModel.recordState.value.recorderDuration)
    }
    @Test

    fun `setDuration should update durationChoice`() {
        val viewModel = RecordViewModel(RecorderServiceStateImpl())

        val newDurationChoice = RecorderDuration.SHORTEST
        viewModel.setDuration(newDurationChoice)
        assertEquals(newDurationChoice, viewModel.recordState.value.recorderDuration)
    }
}