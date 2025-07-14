package ge.custom.rmlm.presenatation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.components.Search
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.RMLMTheme

@Composable
fun RecordingsScreen(
    modifier: Modifier,
    searchValue: String,
    recordings: List<String>,
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

        LazyColumn {
            items(recordings.size) { index ->

            }
        }
    }
}

@PreviewLightDark
@Composable
fun RecordingsScreenPreview() {
    RMLMTheme {
        var searchValue by remember {
            mutableStateOf("")
        }
        RecordingsScreen(
            Modifier,
            searchValue,
            emptyList()
        ) {
            searchValue = it
        }
    }
}