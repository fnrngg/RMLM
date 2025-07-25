package ge.custom.rmlm.presenatation.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ge.custom.rmlm.R
import ge.custom.rmlm.common.openPermissionSettings
import ge.custom.rmlm.presenatation.components.NavBar
import ge.custom.rmlm.presenatation.components.NavItem
import ge.custom.rmlm.presenatation.components.QuestionDialog
import ge.custom.rmlm.presenatation.screens.KoinScreenPreview
import ge.custom.rmlm.presenatation.screens.RecordScreen
import ge.custom.rmlm.presenatation.screens.RecordingsScreen
import ge.custom.rmlm.presenatation.screens.Route
import ge.custom.rmlm.presenatation.screens.navDestinations
import ge.custom.rmlm.presenatation.service.RecorderService.Companion.ACTION_NEW_RECORDING_SAVED
import ge.custom.rmlm.presenatation.theme.RMLMTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModel()
    private val newRecordingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.newRecordingSaved()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
        ContextCompat.registerReceiver(
            this,
            newRecordingReceiver,
            IntentFilter(ACTION_NEW_RECORDING_SAVED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(newRecordingReceiver)
    }
}

@Composable
private fun MainScreen() {
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
            var showDialog by rememberSaveable {
                mutableStateOf(false)
            }
            if (showDialog) {
                NotificationPermissionDialog {
                    showDialog = false
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    if (!granted) {
                        showDialog = true
                    }
                }
                LaunchedEffect(Unit) {
                    notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
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
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationPermissionDialog(
    modifier: Modifier = Modifier,
    hideDialog: () -> Unit
) {
    val context = LocalContext.current
    QuestionDialog(
        modifier = modifier,
        onDismissRequest = hideDialog,
        text = stringResource(
            R.string.record_notification_permission_description
        ),
        positiveText = stringResource(
            R.string.record_notification_permission_grant
        ),
        negativeText = stringResource(
            R.string.record_notification_permission_close
        ),
        onPositiveClick = { openPermissionSettings(context) },
        onNegativeClick = hideDialog
    )
}


@PreviewLightDark
@Composable
private fun MainScreenPreview() {
    KoinScreenPreview {
        MainScreen()
    }
}


