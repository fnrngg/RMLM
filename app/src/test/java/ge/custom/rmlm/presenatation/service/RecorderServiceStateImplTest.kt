package ge.custom.rmlm.presenatation.service

import ge.custom.rmlm.presenatation.recorder.RecorderDuration
import org.junit.Test

class RecorderServiceStateImplTest {

    @Test
    fun `initial state should have isRecording false and durationChoice null`() {
        val recorderServiceState = RecorderServiceStateImpl()
        assert(!recorderServiceState.recordState.value.isRecording)
        assert(recorderServiceState.recordState.value.recorderDuration == null)
    }

    @Test
    fun `setActive should update isRecording`() {
        val recorderServiceState = RecorderServiceStateImpl()
        recorderServiceState.setActive(true)
        assert(recorderServiceState.recordState.value.isRecording)
        recorderServiceState.setActive(false)
        assert(!recorderServiceState.recordState.value.isRecording)
    }

    @Test
    fun `setRecorderDuration should update durationChoice`() {
        val recorderServiceState = RecorderServiceStateImpl()
        recorderServiceState.setRecorderDuration(RecorderDuration.SHORTEST)
        assert(recorderServiceState.recordState.value.recorderDuration == RecorderDuration.SHORTEST)
    }
}