package ge.custom.rmlm.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

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