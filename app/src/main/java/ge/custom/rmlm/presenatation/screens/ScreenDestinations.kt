package ge.custom.rmlm.presenatation.screens

import androidx.annotation.StringRes
import ge.custom.rmlm.R
import ge.custom.rmlm.presenatation.components.CornerLocation
import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    object Record : Route()

    @Serializable
    object Recordings : Route()
}

data class ScreenDestination(
    @param:StringRes
    val titleRes: Int,
    val cornerLocation: CornerLocation,
    val route: Route
)

val navDestinations = listOf(
    ScreenDestination(
        R.string.navigation_title_record,
        CornerLocation.TOP_END,
        Route.Record
    ),
    ScreenDestination(
        R.string.navigation_title_recordings,
        CornerLocation.TOP_START,
        Route.Recordings
    )
)