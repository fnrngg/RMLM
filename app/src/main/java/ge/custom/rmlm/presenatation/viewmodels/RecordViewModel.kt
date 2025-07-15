package ge.custom.rmlm.presenatation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecordViewModel: ViewModel() {
    private val _recordUiState = MutableStateFlow(
        RecordUiState()
    )
    val recordUiState: StateFlow<RecordUiState> =
        _recordUiState.asStateFlow()
}

data class RecordUiState(
    val isRecording: Boolean = true,
)