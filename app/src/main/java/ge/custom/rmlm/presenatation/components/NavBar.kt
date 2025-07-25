package ge.custom.rmlm.presenatation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.screens.Route
import ge.custom.rmlm.presenatation.theme.Dimens
import ge.custom.rmlm.presenatation.theme.Dimens.spacingM
import ge.custom.rmlm.presenatation.theme.Dimens.spacingXS

@Composable
fun NavBar(onClick: (Route) -> Unit, items: List<NavItem>) {
    Column {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.borderSize)
        )
        Row(Modifier.fillMaxWidth()) {
            items.forEach { item ->
                NavBarItem(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick,
                    item.title,
                    item.isSelected,
                    item.cornerLocation,
                    item.route
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    modifier: Modifier,
    onClick: (Route) -> Unit,
    title: String,
    isSelected: Boolean,
    cornerLocation: CornerLocation,
    route: Route
) {
    Box(
        modifier
            .clip(
                if (isSelected) when (cornerLocation) {
                    CornerLocation.TOP_END -> RoundedCornerShape(topEnd = spacingXS)
                    CornerLocation.TOP_START -> RoundedCornerShape(topStart = spacingXS)
                } else RectangleShape
            )
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surface
            )
            .padding(vertical = spacingM)
            .then(if (!isSelected) Modifier.clickable { onClick(route) } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
        )
    }
}

enum class CornerLocation {
    TOP_END,
    TOP_START
}

data class NavItem(
    val title: String,
    val isSelected: Boolean,
    val cornerLocation: CornerLocation,
    val route: Route
)


@Preview
@Composable
private fun NavBarPreview() {
    NavBar(
        {},
        listOf(
            NavItem(
                stringResource(R.string.navigation_title_record),
                true,
                CornerLocation.TOP_END,
                Route.Record
            ),
            NavItem(
                stringResource(R.string.navigation_title_recordings),
                false,
                CornerLocation.TOP_START,
                Route.Recordings
            )
        )
    )
}