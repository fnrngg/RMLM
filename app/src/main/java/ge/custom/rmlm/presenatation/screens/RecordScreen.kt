package ge.custom.rmlm.presenatation.screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.PermissionChecker
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.custom.rmlm.R
import ge.custom.rmlm.common.openPermissionSettings
import ge.custom.rmlm.presenatation.components.Choice
import ge.custom.rmlm.presenatation.components.Picker
import ge.custom.rmlm.presenatation.components.QuestionDialog
import ge.custom.rmlm.presenatation.recorder.RecorderDuration
import ge.custom.rmlm.presenatation.service.RecorderService
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.LocalCustomColorsPalette
import ge.custom.rmlm.presenatation.theme.RMLMTheme
import ge.custom.rmlm.presenatation.viewmodels.RecordViewModel
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordScreen(
    modifier: Modifier = Modifier,
    viewModel: RecordViewModel = koinViewModel()
) {
    val recordState by viewModel.recordState.collectAsStateWithLifecycle()
    RecordScreen(
        modifier = modifier,
        isRecording = recordState.isRecording,
        recordDuration = recordState.recorderDuration,
        onChoiceClick = { choice ->
            viewModel.setDuration(choice)
        }
    )
}

@Composable
private fun RecordScreen(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    recordDuration: RecorderDuration?,
    onChoiceClick: (RecorderDuration) -> Unit
) {
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showDialog) {
        PermissionDialog {
            showDialog = false
        }
    }
    Column(
        modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(top = Dimens.spacing4XL)
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeText(modifier.align(Alignment.CenterHorizontally))
        DescriptionText(
            modifier = modifier.align(Alignment.CenterHorizontally),
            isRecording = isRecording
        )
        Box {
            if (isRecording) {
                StopAndSaveButtons(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(top = 140.dp)
                )
            } else {
                StartRecordingButton(
                    Modifier
                        .align(Alignment.Center)
                        .padding(top = 140.dp),
                    recordDuration
                ) {
                    showDialog = true
                }
            }

            DurationPicker(
                isEnabled = !isRecording,
                currentDuration = recordDuration?.duration
            ) { choice ->
                onChoiceClick(choice)
            }
        }
    }
}

@Composable
private fun WelcomeText(modifier: Modifier = Modifier) {
    Text(
        stringResource(R.string.record_welcome_to),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.Center
    )
    Text(
        stringResource(R.string.record_welcome_app_name),
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun DescriptionText(modifier: Modifier = Modifier, isRecording: Boolean) {
    Text(
        text = buildAnnotatedString {
            val description = stringResource(
                if (isRecording)
                    R.string.record_started_description
                else R.string.record_description
            )
            append(
                description
            )
            if (isRecording) {
                val positiveText = stringResource(
                    R.string.record_started_description_positive
                )
                val positiveTextStart = description.indexOf(positiveText)
                addStyle(
                    SpanStyle(
                        LocalCustomColorsPalette.current.success.color
                    ),
                    start = positiveTextStart,
                    end = positiveTextStart + positiveText.length
                )
                val negativeText = stringResource(R.string.record_started_description_negative)
                val negativeTextStart = description.lastIndexOf(negativeText)
                addStyle(
                    SpanStyle(
                        MaterialTheme.colorScheme.error
                    ),
                    start = negativeTextStart,
                    end = negativeTextStart + negativeText.length
                )
            }
        },
        modifier = modifier
            .padding(
                top = Dimens.spacing3XL,
                start = Dimens.spacingM,
                end = Dimens.spacingM
            ),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
}

private fun startService(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(
            RecorderService.getStartIntent(
                context
            )
        )
    } else {
        context.startService(
            RecorderService.getStartIntent(
                context
            )
        )
    }
}

@Composable
private fun StartRecordingButton(
    modifier: Modifier = Modifier,
    recordDuration: RecorderDuration?,
    showPermissionDialog: () -> Unit
) {
    val context = LocalContext.current
    val recordVoicePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            recordDuration?.let { choice ->
                startService(context)
            }
        } else {
            showPermissionDialog()
        }
    }
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.spacingXS),
        enabled = recordDuration != null,
        colors = buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        onClick = {
            val recorderPermission = PermissionChecker.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            )
            if (recorderPermission != PermissionChecker.PERMISSION_GRANTED) {
                recordVoicePermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO
                )
            } else {
                startService(context)
            }
        }
    ) {
        Text(stringResource(R.string.record_start_recording))
    }
}

@Composable
private fun PermissionDialog(modifier: Modifier = Modifier, hideDialog: () -> Unit) {
    val context = LocalContext.current
    QuestionDialog(
        modifier,
        onDismissRequest = hideDialog,
        text = stringResource(R.string.record_audio_permission_description),
        positiveText = stringResource(R.string.record_audio_permission_grant),
        negativeText = stringResource(R.string.record_audio_permission_close),
        onPositiveClick = {
            openPermissionSettings(context)
            hideDialog()
        },
        onNegativeClick = hideDialog
    )
}

@Composable
private fun StopAndSaveButtons(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            SaveTypeButton(textResId = R.string.record_save_and_stop_recording) {
                context.startService(RecorderService.getSaveIntent(context))
            }
            SaveTypeButton(textResId = R.string.record_save_and_restart_recording) {
                context.startService(RecorderService.getSaveAndRestartIntent(context))
            }
        }
        StopButton()
    }
}

@Composable
private fun SaveTypeButton(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.spacingM),
        colors = buttonColors(
            containerColor = LocalCustomColorsPalette.current.success.color,
            contentColor = LocalCustomColorsPalette.current.success.onColor
        ),
        onClick = onClick,
        contentPadding = PaddingValues(
            horizontal = Dimens.spacingS,
            vertical = Dimens.spacingM
        )
    ) {
        Text(stringResource(textResId))
    }
}

@Composable
private fun StopButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(
        modifier = modifier.padding(top = Dimens.spacing3XL),
        shape = RoundedCornerShape(Dimens.spacingXS),
        colors = buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        onClick = {
            context.startService(
                RecorderService.getStopIntent(context)
            )
        },
        contentPadding = PaddingValues(
            horizontal = Dimens.spacingS,
            vertical = Dimens.spacingXS
        )
    ) {
        Text(stringResource(R.string.record_stop_recording))
    }
}


@Composable
private fun DurationPicker(
    isEnabled: Boolean,
    currentDuration: Int?,
    onChoiceClick: (RecorderDuration) -> Unit
) {
    Picker(
        Modifier.padding(
            horizontal = Dimens.spacing3XL, vertical = Dimens.spacing3XL
        ),
        isEnabled,
        currentDuration?.let {
            stringResource(R.string.picker_choice_duration, it)
        } ?: stringResource(R.string.picker_title),
        RecorderDuration.entries.map { duration ->
            DurationChoice(
                stringResource(
                    R.string.picker_choice_duration,
                    duration.duration
                ),
                duration
            )
        },
        onChoiceClick = { choice ->
            onChoiceClick(choice.duration)
        }
    )
}

@Parcelize
data class DurationChoice(
    override val title: String,
    val duration: RecorderDuration
) : Choice(title), Parcelable

@PreviewLightDark
@Composable
private fun RecordScreenPreview() {
    KoinScreenPreview {
        RMLMTheme {
            RecordScreen()
        }
    }
}