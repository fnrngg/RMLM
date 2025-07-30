package ge.custom.rmlm.presenatation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.custom.rmlm.common.Result
import ge.custom.rmlm.common.latestEventFlow
import ge.custom.rmlm.domain.usecase.DeleteRecordingUseCase
import ge.custom.rmlm.domain.usecase.LoadRecordingsUseCase
import ge.custom.rmlm.presenatation.mapper.RecordingMapper
import ge.custom.rmlm.presenatation.model.RecordingUiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class RecordingsViewModel(
    loadRecordingsUseCase: LoadRecordingsUseCase,
    private val deleteRecordingUseCase: DeleteRecordingUseCase,
    private val recordingMapper: RecordingMapper
) : ViewModel() {

    private val _recordingsUiState = MutableStateFlow(
        RecordingsScreenUiState()
    )
    val recordingsUiState: StateFlow<RecordingsScreenUiState> =
        _recordingsUiState.asStateFlow()

    private val recordingsRequestFlow = latestEventFlow<String>()
    private val deleteRecordingRequestFlow = latestEventFlow<Uri>()

    init {
        viewModelScope.launch {
            recordingsRequestFlow
                .onStart {
                    emit("")
                }
                .debounce(SEARCH_DELAY)
                .collectLatest { searchValue ->
                    _recordingsUiState.value = _recordingsUiState.value.copy(
                        recordings = loadRecordingsUseCase(searchValue).map {
                            it.map(recordingMapper::mapRecordingDataToRecordingUiData)
                        }
                    )
                }
        }
        viewModelScope.launch {
            deleteRecordingRequestFlow.collectLatest { uri ->
                if (deleteRecordingUseCase(uri) is Result.Success) {
                    _recordingsUiState.value = _recordingsUiState.value.copy(
                        showDeleteDialog = false,
                        chosenRecordingUri = null,
                    )
                    search(recordingsUiState.value.search)
                } // else show error?
            }
        }
    }

    fun search(searchValue: String) {
        recordingsRequestFlow.tryEmit(searchValue)
        _recordingsUiState.value = _recordingsUiState.value.copy(
            search = searchValue,
            recordings = Result.Loading
        )
    }

    fun refresh() {
        search(recordingsUiState.value.search)
    }

    fun deleteRecording(uri: Uri) {
        _recordingsUiState.value = _recordingsUiState.value.copy(
            showDeleteDialog = true,
            chosenRecordingUri = uri
        )
    }

    fun deleteAgreed() {
        recordingsUiState.value.chosenRecordingUri?.let { uri ->
            deleteRecordingRequestFlow.tryEmit(uri)
        }
    }

    fun deleteCancelled() {
        _recordingsUiState.value = _recordingsUiState.value.copy(
            showDeleteDialog = false,
            chosenRecordingUri = null
        )
    }

    companion object {
        private const val SEARCH_DELAY = 500L
    }
}

data class RecordingsScreenUiState(
    val search: String = "",
    val showDeleteDialog: Boolean = false,
    val chosenRecordingUri: Uri? = null,
    val recordings: Result<List<RecordingUiData>> = Result.Loading
)