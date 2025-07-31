package ge.custom.rmlm.presenatation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.recorder.RecorderDuration
import ge.custom.rmlm.presenatation.screens.DurationChoice
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme
import kotlinx.coroutines.launch

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
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
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
            ).bringIntoViewRequester(bringIntoViewRequester)
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
                modifier = Modifier
                    .size(Dimens.iconSize16)
                    .align(Alignment.CenterVertically)
            )
        }

        if (isOpen) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            }

            items.forEach { choice ->
                Column(
                    modifier = Modifier
                        .clickable(onClick = {
                            isOpen = false
                            onChoiceClick(choice)
                        })
                ) {
                    HorizontalDivider()
                    Text(
                        text = choice.title,
                        modifier = Modifier
                            .padding(vertical = Dimens.spacingS, horizontal = Dimens.spacingM),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

abstract class Choice(
    open val title: String
)


@PreviewLightDark
@Composable
private fun PickerPreview() {
    RMLMTheme {
        Picker(
            Modifier,
            isEnabled = true,
            title = stringResource(R.string.picker_title),
            items = RecorderDuration.entries.map { duration ->
                DurationChoice(
                    stringResource(
                        R.string.picker_choice_duration,
                        duration.duration
                    ),
                    duration
                )
            },
            onChoiceClick = { choice -> }
        )
    }
}