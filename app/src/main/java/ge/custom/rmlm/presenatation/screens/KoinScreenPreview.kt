package ge.custom.rmlm.presenatation.screens

import androidx.compose.runtime.Composable
import ge.custom.rmlm.di.appModule
import org.koin.compose.KoinApplication

@Composable
fun KoinScreenPreview(
    screen: @Composable () -> Unit
) {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        screen()
    }
}