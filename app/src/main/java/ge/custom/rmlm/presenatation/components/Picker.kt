package ge.custom.rmlm.presenatation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.screens.DurationChoice
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme

@Composable
fun <T : Choice> Picker(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    title: String,
    items: List<T>,
    onChoiceClick: (T) -> Unit
) {
    var isOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val pickerShape = RoundedCornerShape(
        Dimens.spacingXS
    )
    Column(
        modifier = modifier
            .border(
                width = Dimens.borderSize,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = pickerShape
            )
            .background(
                color = if (isEnabled) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = pickerShape
            )
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = Dimens.spacingS, horizontal = Dimens.spacingM)
                .then(
                    if (isEnabled)
                        Modifier.clickable(onClick = { isOpen = !isOpen })
                    else Modifier
                )
        ) {
            Text(
                text = title, modifier = Modifier
                    .weight(1f)
                    .padding(end = Dimens.spacingXS),
                color = if (isEnabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                painter = painterResource(
                    if (isOpen) R.drawable.ic_arrow_up
                    else R.drawable.ic_arrow_down
                ),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSize16)
            )
        }

        if (isOpen) {
            items.forEach { choice ->
                HorizontalDivider()
                Text(
                    text = choice.title,
                    modifier = Modifier
                        .padding(vertical = Dimens.spacingS, horizontal = Dimens.spacingM)
                        .clickable(onClick = { onChoiceClick(choice) }),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

abstract class Choice(
    open val title: String
)


@PreviewLightDark
@Composable
fun PickerPreview() {
    RMLMTheme {
        Picker(
            Modifier,
            isEnabled = true,
            title = stringResource(R.string.picker_title),
            items = listOf(
                DurationChoice(stringResource(R.string.picker_choice_duration_5), 5),
                DurationChoice(stringResource(R.string.picker_choice_duration_10), 10),
                DurationChoice(stringResource(R.string.picker_choice_duration_20), 20),
                DurationChoice(stringResource(R.string.picker_choice_duration_30), 30),
                DurationChoice(stringResource(R.string.picker_choice_duration_60), 60),
            ), onChoiceClick = { choice -> }
        )
    }
}