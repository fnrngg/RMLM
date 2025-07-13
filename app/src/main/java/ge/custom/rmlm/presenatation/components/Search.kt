package ge.custom.rmlm.presenatation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme

@Composable
fun Search(
    modifier: Modifier = Modifier,
    hint: String,
    text: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.spacingXS))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = Dimens.spacingXS, horizontal = Dimens.spacingS),
        value = text.ifEmpty { hint },
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = if (text.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface
        ),
        singleLine = true,
        onValueChange = {
            onValueChange
        }
    ) {
        Row {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                null,
                modifier = Modifier.padding(end = Dimens.spacingS),
                tint = MaterialTheme.colorScheme.onSurface
            )
            it()
        }
    }
}

@PreviewLightDark
@Composable
fun SearchPreview() {
    RMLMTheme {
        var text by remember {
            mutableStateOf("")
        }
        Search(hint = "Search", text = text) { newText ->
            text = newText
        }
    }
}