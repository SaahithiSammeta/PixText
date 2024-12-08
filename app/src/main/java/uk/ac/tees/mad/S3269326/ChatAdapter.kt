package uk.ac.tees.mad.S3269326

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: List<MessageData>,
    private val currentUser: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == currentUser) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == VIEW_TYPE_SENT) {
            R.layout.item_message_sent
        } else {
            R.layout.item_message_received
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount() = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.text_message_content)
        private val timeTextView: TextView = itemView.findViewById(R.id.text_message_time)

        fun bind(message: MessageData) {
            messageTextView.text = message.message // Display message content
            timeTextView.text = formatTimestamp(message.timestamp) // Display timestamp
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy h:mm a", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }
    }
}
