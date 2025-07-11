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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.screens.DurationChoice
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme

@Composable
fun <T : Choice> Picker(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    isOpen: Boolean,
    title: String,
    items: List<T>,
    onPickerClick: () -> Unit,
    onChoiceClick: (T) -> Unit
) {
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
                        Modifier.clickable(onClick = { onPickerClick })
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
                modifier = Modifier.size(Dimens.iconSize)
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
            isEnabled = true, isOpen = true, title = "Duration", items = listOf(
                DurationChoice("5 Minutes", 5),
                DurationChoice("10 Minutes", 10),
                DurationChoice("20 Minutes", 20),
                DurationChoice("30 Minutes", 30),
                DurationChoice("60 Minutes", 60),
            ), onPickerClick = { }, onChoiceClick = { choice -> }
        )
    }
}