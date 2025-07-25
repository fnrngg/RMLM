package ge.custom.rmlm.presenatation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.custom.rmlm.common.Result
import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.domain.usecase.LoadRecordingsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecordingsViewModel(
    private val loadRecordingsUseCase: LoadRecordingsUseCase
) : ViewModel() {

    private val _recordingsUiState = MutableStateFlow(
        RecordingsScreenUiState()
    )
    val recordingsUiState: StateFlow<RecordingsScreenUiState> =
        _recordingsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _recordingsUiState.value =
                _recordingsUiState.value.copy(
                    recordings = loadRecordingsUseCase("")
                )
        }
    }

    fun search(searchValue: String) {

    }
}

data class RecordingsScreenUiState(
    val search: String = "",
    val recordings: Result<List<RecordingData>> = Result.Loading
)