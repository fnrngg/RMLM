package ge.custom.rmlm.presenatation.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.components.Choice
import ge.custom.rmlm.presenatation.components.Picker
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.LocalCustomColorsPalette
import ge.custom.rmlm.presenatation.theme.RMLMTheme
import ge.custom.rmlm.presenatation.viewmodels.RecordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordScreen(
    modifier: Modifier = Modifier,
    viewModel: RecordViewModel = koinViewModel()
) {
    val recordUiState by viewModel.recordUiState.collectAsStateWithLifecycle()
    RecordScreen(modifier, recordUiState.isRecording)
}

@Composable
fun RecordScreen(modifier: Modifier = Modifier, isRecording: Boolean) {
    Column(
        modifier
            .background(MaterialTheme.colorScheme.background)
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
                        .padding(top = 140.dp)
                )
            }

            val context = LocalContext.current
            var title by rememberSaveable {
                mutableStateOf(context.getString(R.string.picker_title))
            }
            DurationPicker(isEnabled = isRecording, title = title) { choice ->
                title = choice.title
            }
        }
    }
}

@Composable
fun WelcomeText(modifier: Modifier = Modifier) {
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
fun DescriptionText(modifier: Modifier = Modifier, isRecording: Boolean) {
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

@Composable
fun StartRecordingButton(modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.spacingXS),
        colors = buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        onClick = {}
    ) {
        Text(stringResource(R.string.record_start_recording))
    }
}

@Composable
fun StopAndSaveButtons(modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            SaveButton(textResId = R.string.record_save_and_stop_recording)
            SaveButton(textResId = R.string.record_save_and_restart_recording)
        }
        StopButton()
    }
}

@Composable
fun SaveButton(modifier: Modifier = Modifier, @StringRes textResId: Int) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.spacingM),
        colors = buttonColors(
            containerColor = LocalCustomColorsPalette.current.success.color,
            contentColor = LocalCustomColorsPalette.current.success.onColor
        ),
        onClick = {},
        contentPadding = PaddingValues(
            horizontal = Dimens.spacingS,
            vertical = Dimens.spacingM
        )
    ) {
        Text(stringResource(textResId))
    }
}

@Composable
fun StopButton(modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.padding(top = Dimens.spacing3XL),
        shape = RoundedCornerShape(Dimens.spacingXS),
        colors = buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        onClick = {},
        contentPadding = PaddingValues(
            horizontal = Dimens.spacingS,
            vertical = Dimens.spacingXS
        )
    ) {
        Text(stringResource(R.string.record_stop_recording))
    }
}

@Composable
fun DurationPicker(
    isEnabled: Boolean,
    title: String,
    onChoiceClick: (DurationChoice) -> Unit
) {
    Picker(
        Modifier.padding(
            horizontal = Dimens.spacing3XL, vertical = Dimens.spacing3XL
        ),
        isEnabled,
        title,
        listOf(
            DurationChoice(
                stringResource(R.string.picker_choice_duration_5),
                5
            ),
            DurationChoice(
                stringResource(R.string.picker_choice_duration_10),
                10
            ),
            DurationChoice(
                stringResource(R.string.picker_choice_duration_20),
                20
            ),
            DurationChoice(
                stringResource(R.string.picker_choice_duration_30),
                30
            ),
            DurationChoice(
                stringResource(R.string.picker_choice_duration_60),
                60
            ),
        ),
        onChoiceClick = { choice ->
            onChoiceClick(choice)
        }
    )
}

data class DurationChoice(
    override val title: String,
    val durationInMinutes: Int
) : Choice(title)

@PreviewLightDark
@Composable
fun RecordScreenPreview() {
    KoinScreenPreview {
        RMLMTheme {
            RecordScreen()
        }
    }
}