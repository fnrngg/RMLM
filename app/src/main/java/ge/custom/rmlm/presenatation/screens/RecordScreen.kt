package ge.custom.rmlm.presenatation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.components.Choice
import ge.custom.rmlm.presenatation.components.Picker
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme

@Composable
fun RecordScreen() {
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(top = Dimens.spacing4XL)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            stringResource(R.string.record_welcome_to),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            stringResource(R.string.record_welcome_app_name),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            stringResource(R.string.record_description),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Dimens.spacing3XL),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Box {
            StartRecordingButton(Modifier.align(Alignment.Center))
            DurationPicker()
        }
    }
}

@Composable
fun StartRecordingButton(modifier: Modifier = Modifier) {
    Button(
        modifier = modifier
            .padding(top = 140.dp),
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
fun DurationPicker() {
    var isOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isEnabled by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    var title by rememberSaveable {
        mutableStateOf(context.getString(R.string.picker_title))
    }
    Picker(
        Modifier.padding(horizontal = Dimens.spacingM, vertical = Dimens.spacing3XL),
        isEnabled,
        isOpen,
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
        onPickerClick = {
            isOpen = !isOpen
        },
        onChoiceClick = { choice ->
            title = choice.title
            isOpen = !isOpen
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
    RMLMTheme {
        RecordScreen()
    }
}