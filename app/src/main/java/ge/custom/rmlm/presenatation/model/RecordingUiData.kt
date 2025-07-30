package ge.custom.rmlm.presenatation.model

import android.net.Uri

data class RecordingUiData(
    val name: String,
    val uri: Uri,
    val duration: String,
    val date: String
)
