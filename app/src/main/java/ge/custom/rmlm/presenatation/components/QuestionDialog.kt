package ge.custom.rmlm.presenatation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.Dialog
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme

@Composable
fun QuestionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    text: String,
    positiveText: String,
    negativeText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit
) {
    Dialog(
        onDismissRequest
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(Dimens.spacingM))
                .background(MaterialTheme.colorScheme.surface)
                .padding(Dimens.spacing2XL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.spacingL)
            ) {
                Text(
                    text = negativeText,
                    modifier = Modifier.clickable { onNegativeClick() },
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = positiveText,
                    modifier = Modifier.clickable {
                        onPositiveClick
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun QuestionDialogPreview() {
    RMLMTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            QuestionDialog(
                onDismissRequest = {},
                text = stringResource(R.string.record_audio_permission_description),
                positiveText = stringResource(R.string.record_audio_permission_grant),
                negativeText = stringResource(R.string.record_audio_permission_close),
                onPositiveClick = {},
                onNegativeClick = {}
            )
        }
    }
}