package ge.custom.rmlm.presenatation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ge.custom.rmlm.presenatation.components.NavBar
import ge.custom.rmlm.presenatation.components.NavItem
import ge.custom.rmlm.presenatation.screens.RecordScreen
import ge.custom.rmlm.presenatation.screens.RecordingsScreen
import ge.custom.rmlm.presenatation.screens.Route
import ge.custom.rmlm.presenatation.screens.navDestinations
import ge.custom.rmlm.presenatation.theme.RMLMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@PreviewLightDark
@Composable
fun MainScreen() {
    RMLMTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                NavBar(
                    { destination ->
                        navController.navigate(
                            destination
                        )
                    },
                    navDestinations.map { navItem ->
                        NavItem(
                            stringResource(navItem.titleRes),
                            currentDestination?.hierarchy?.any {
                                it.hasRoute(navItem.route::class)
                            } == true,
                            navItem.cornerLocation,
                            navItem.route
                        )
                    }
                )
            }
        )
        { innerPadding ->
            NavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                startDestination = Route.Record
            ) {

                composable<Route.Record> { entry ->
                    RecordScreen()
                }
                composable<Route.Recordings> {
                    RecordingsScreen(
                        modifier = Modifier,
                        searchValue = "",
                        recordings = emptyList(),
                        onSearchValueChange = {}
                    )
                }
            }
        }
    }
}




