package uk.ac.tees.mad.S3269326

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class MessageData(
    val sender: String = "",
    val receiver: String = "",
    val message: String = "",
    val timestamp: Long = 0L
) : Parcelable {
    val timestampFormatted: String
        get() = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
}