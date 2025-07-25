package ge.custom.rmlm.presenatation.viewmodels

import androidx.lifecycle.ViewModel
import ge.custom.rmlm.presenatation.recorder.RecorderDuration
import ge.custom.rmlm.presenatation.service.RecordState
import ge.custom.rmlm.presenatation.service.RecorderServiceState
import kotlinx.coroutines.flow.StateFlow

class RecordViewModel(
    private val recorderServiceState: RecorderServiceState
) : ViewModel() {

    val recordState: StateFlow<RecordState> = recorderServiceState.recordState

    fun setDuration(duration: RecorderDuration) {
        recorderServiceState.setRecorderDuration(duration)
    }
}