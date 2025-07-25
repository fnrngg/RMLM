package ge.custom.rmlm.presenatation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.custom.rmlm.R
import ge.custom.rmlm.common.Result
import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.presenatation.components.Search
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme
import ge.custom.rmlm.presenatation.viewmodels.RecordingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordingsScreen(
    modifier: Modifier = Modifier,
    viewModel: RecordingsViewModel = koinViewModel()
) {
    val recordingsUiState by viewModel.recordingsUiState.collectAsStateWithLifecycle()

    RecordingsScreen(
        modifier = modifier,
        searchValue = recordingsUiState.search,
        recordings = recordingsUiState.recordings
    ) {
        viewModel.search(it)
    }
}

@Composable
private fun RecordingsScreen(
    modifier: Modifier,
    searchValue: String,
    recordings: Result<List<RecordingData>>,
    onSearchValueChange: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Search(
            modifier = Modifier.padding(Dimens.spacingM),
            hint = stringResource(R.string.recordings_search_hint),
            text = searchValue
        ) { newSearchValue ->
            onSearchValueChange(newSearchValue)
        }

        when (recordings) {
            is Result.Error -> ErrorScreen(Modifier.fillMaxSize(), recordings.errorMessage)
            Result.Loading -> LoadingScreen(Modifier.fillMaxSize())
            is Result.Success -> {

                LazyColumn {
                    recordings.data.forEach { item ->
                        item {
                            Text(
                                text = item.name,
                                modifier = Modifier
                                    .padding(Dimens.spacingM)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun ErrorScreen(modifier: Modifier = Modifier, errorMessage: String?) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(Dimens.iconSize91),
            tint = MaterialTheme.colorScheme.error,
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null
        )
        Text(
            text = errorMessage ?: stringResource(R.string.recordings_error_message),
            color = MaterialTheme.colorScheme.error
        )
    }
}

@PreviewLightDark
@Composable
private fun RecordingsScreenPreview() {
    KoinScreenPreview {
        RMLMTheme {
            RecordingsScreen(
                Modifier
            )
        }
    }
}