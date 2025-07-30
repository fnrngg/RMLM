package ge.custom.rmlm.domain.model

import android.net.Uri

data class RecordingData(
    val name: String,
    val uri: Uri,
    val duration: Long,
    val date: Long
)