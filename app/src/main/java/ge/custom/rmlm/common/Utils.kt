package ge.custom.rmlm.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun openPermissionSettings(context: Context) {
    val uriScheme = "package"
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(
                uriScheme,
                context.packageName,
                null
            )
        )
    )
}

fun openAudioPlayer(context: Context, uri: Uri) {
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            uri
        )
    )
}

fun openShareChooser(context: Context, uri: Uri, mimeType: String) {
    context.startActivity(
        Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
        }
    )
}

fun <T> latestEventFlow() =
    MutableSharedFlow<T>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)