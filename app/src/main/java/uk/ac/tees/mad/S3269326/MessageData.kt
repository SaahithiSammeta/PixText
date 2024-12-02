package uk.ac.tees.mad.S3269326

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class MessageData(
    val sender: String = "",
    val receiver: String = "",
    val message: String = "",
    val timestamp: Long = 0L
) : Parcelable {
    val formattedTimestamp: String
        get() = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(Date(timestamp))
}
