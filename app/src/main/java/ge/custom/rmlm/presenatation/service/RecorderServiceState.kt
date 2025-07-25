package ge.custom.rmlm.presenatation.service

import ge.custom.rmlm.presenatation.recorder.RecorderDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecorderServiceStateImpl : RecorderServiceState {
    private var _recordState = MutableStateFlow(RecordState())
    override val recordState: StateFlow<RecordState> = _recordState.asStateFlow()
    override fun setActive(isRecording: Boolean) {
        _recordState.value = _recordState.value.copy(isRecording = isRecording)
    }

    override fun setRecorderDuration(recorderDuration: RecorderDuration?) {
        _recordState.value = _recordState.value.copy(recorderDuration = recorderDuration)
    }
}

interface RecorderServiceState {
    val recordState: StateFlow<RecordState>
    fun setActive(isRecording: Boolean)
    fun setRecorderDuration(recorderDuration: RecorderDuration?)
}

data class RecordState(
    val isRecording: Boolean = false,
    val recorderDuration: RecorderDuration? = null
)