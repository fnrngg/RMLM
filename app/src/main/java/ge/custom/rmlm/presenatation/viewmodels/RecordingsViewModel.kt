package ge.custom.rmlm.presenatation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.custom.rmlm.common.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecordingsViewModel : ViewModel() {

    private val _recordingsUiState = MutableStateFlow(
        RecordingsScreenUiState()
    )
    val recordingsUiState: StateFlow<RecordingsScreenUiState> =
        _recordingsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(10L)
            _recordingsUiState.value =
                _recordingsUiState.value.copy(recordings = Result.Success(emptyList()))
        }
    }

    fun search(searchValue: String) {

    }
}

data class RecordingsScreenUiState(
    val search: String = "",
    val recordings: Result<List<String>> = Result.Loading
)