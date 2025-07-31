package ge.custom.rmlm.presenatation.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.custom.rmlm.R
import ge.custom.rmlm.common.Result
import ge.custom.rmlm.common.openAudioPlayer
import ge.custom.rmlm.common.openShareChooser
import ge.custom.rmlm.data.suspendrunners.PermissionDeniedException
import ge.custom.rmlm.presenatation.components.QuestionDialog
import ge.custom.rmlm.presenatation.components.Search
import ge.custom.rmlm.presenatation.model.RecordingUiData
import ge.custom.rmlm.presenatation.service.RecorderService.Companion.ACTION_NEW_RECORDING_SAVED
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
    val context = LocalContext.current

    RecordingsScreen(
        modifier = modifier,
        searchValue = recordingsUiState.search,
        showDeleteDialog = recordingsUiState.showDeleteDialog,
        recordings = recordingsUiState.recordings,
        onReceive = {
            viewModel.refresh()
        },
        onShareClick = { uri ->
            try {
                openShareChooser(context, uri, "audio/mpeg")
            } catch (_: Exception) {
                // handle no sending app
            }
        },
        onDeleteClick = { uri ->
            viewModel.deleteRecording(uri)
        },
        onDeleteAgreed = {
            viewModel.deleteAgreed()
        },
        onDeleteCancelled = {
            viewModel.deleteCancelled()
        },
        onSearchValueChange = {
            viewModel.search(it)
        }
    )
}

@Composable
private fun RecordingsScreen(
    modifier: Modifier,
    searchValue: String,
    showDeleteDialog: Boolean,
    recordings: Result<List<RecordingUiData>>,
    onReceive: () -> Unit,
    onShareClick: (Uri) -> Unit,
    onDeleteClick: (Uri) -> Unit,
    onDeleteAgreed: () -> Unit,
    onDeleteCancelled: () -> Unit,
    onSearchValueChange: (String) -> Unit
) {
    FileSavedReceiver {
        onReceive()
    }
    if (showDeleteDialog) {
        DeleteDialog(
            modifier = Modifier,
            onDeleteAgreed = {
                onDeleteAgreed()
            },
            onDeleteCancelled = {
                onDeleteCancelled()
            }
        )
    }
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
            is Result.Error -> ErrorScreen(Modifier.fillMaxSize(), recordings.exception)
            Result.Loading -> LoadingScreen(Modifier.fillMaxSize())
            is Result.Success -> {
                if (recordings.data.isNotEmpty()) {
                    HorizontalDivider()
                    LazyColumn {
                        itemsIndexed(recordings.data) { index, recording ->
                            RecordingListItem(
                                modifier = Modifier,
                                name = recording.name,
                                duration = recording.duration,
                                date = recording.date,
                                uri = recording.uri,
                                showDivider = index != recordings.data.lastIndex,
                                onShareClick = { uri ->
                                    onShareClick(uri)
                                },
                                onDeleteClick = { uri ->
                                    onDeleteClick(uri)
                                }
                            )
                        }
                    }

                } else {
                    EmptyScreen(
                        modifier = Modifier.fillMaxSize(),
                        fromSearch = searchValue != ""
                    )
                }
            }
        }
    }
}

@Composable
private fun FileSavedReceiver(onReceive: () -> Unit) {
    val key = rememberUpdatedState(onReceive)
    val context = LocalContext.current

    DisposableEffect(key) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                onReceive()
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(ACTION_NEW_RECORDING_SAVED),
            ContextCompat.RECEIVER_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}

@Composable
private fun DeleteDialog(
    modifier: Modifier = Modifier,
    onDeleteAgreed: () -> Unit,
    onDeleteCancelled: () -> Unit
) {
    QuestionDialog(
        modifier = modifier,
        onDismissRequest = onDeleteCancelled,
        text = stringResource(R.string.recordings_delete_dialog_text),
        positiveText = stringResource(R.string.recordings_delete_dialog_positive_text),
        negativeText = stringResource(R.string.recordings_delete_dialog_negative_text),
        onPositiveClick = onDeleteCancelled,
        onNegativeClick = onDeleteAgreed
    )
}

@Composable
private fun RecordingListItem(
    modifier: Modifier = Modifier,
    name: String,
    duration: String,
    date: String,
    uri: Uri,
    showDivider: Boolean,
    onShareClick: (Uri) -> Unit,
    onDeleteClick: (Uri) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.clickable(
            onClick = {
                try {
                    openAudioPlayer(context, uri)
                } catch (_: Exception) {
                    // handle no audio player
                }
            }
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = Dimens.spacingM)
                .size(Dimens.iconSize24),
            painter = painterResource(R.drawable.ic_file),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = Dimens.spacingXS)
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .padding(bottom = Dimens.spacing3XS)
            )
            Text(
                text = date,
                modifier = Modifier
                    .padding(top = Dimens.spacing3XS)
            )
        }
        Text(
            text = duration,
            modifier = Modifier
                .padding(horizontal = Dimens.spacingXS)
        )
        Icon(
            modifier = Modifier
                .size(Dimens.iconSize24)
                .clickable(
                    onClick = { onShareClick(uri) }
                ),
            painter = painterResource(R.drawable.ic_share),
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .padding(horizontal = Dimens.spacingM)
                .size(Dimens.iconSize24)
                .clickable(
                    onClick = { onDeleteClick(uri) }
                ),
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = null
        )
    }
    if (showDivider) {
        HorizontalDivider()
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
private fun ErrorScreen(modifier: Modifier = Modifier, exception: Exception) {
    val errorMessage = stringResource(
        when (exception) {
            is PermissionDeniedException -> R.string.recordings_error_message_permission_denied
            else -> R.string.recordings_error_message
        }
    )
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
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier, fromSearch: Boolean) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(Dimens.iconSize91),
            painter = painterResource(R.drawable.ic_empty_folder),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = Dimens.spacingM),
            text = stringResource(
                if (fromSearch) R.string.recordings_empty_search_title
                else R.string.recordings_empty_title
            ),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        if (fromSearch) {
            Text(
                text = stringResource(R.string.recordings_empty_description),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun RecordingsScreenPreview() {
    KoinScreenPreview(context = LocalContext.current) {
        RMLMTheme {
            RecordingsScreen(
                Modifier
            )
        }
    }
}