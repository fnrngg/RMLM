package ge.custom.rmlm.presenatation.screens

import android.content.Context
import androidx.compose.runtime.Composable
import ge.custom.rmlm.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
fun KoinScreenPreview(
    context: Context?,
    screen: @Composable () -> Unit
) {
    KoinApplication(
        application = {
            context?.let { androidContext(it) }
            modules(appModule)
        }
    ) {
        screen()
    }
}