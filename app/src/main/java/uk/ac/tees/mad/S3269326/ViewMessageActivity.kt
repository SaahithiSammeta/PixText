package uk.ac.tees.mad.S3269326

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.ac.tees.mad.S3269326.databinding.ActivityViewMessageBinding

class ViewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMessageBinding
    private lateinit var senderTextView: TextView
    private lateinit var messageTextView: TextView
    private lateinit var replyEditText: EditText
    private lateinit var sendButton: Button

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // UI components
        senderTextView = binding.textViewSender
        messageTextView = binding.textViewMessages
        replyEditText = binding.editTextReply
        sendButton = binding.buttonSendReply

        // Retrieve the sender name from the Intent
        val sender = intent.getStringExtra("sender") ?: "Unknown Sender"
        senderTextView.text = "Messages from $sender"

        // Fetch messages from Firebase based on the sender
        fetchMessages(sender)

        // Handle Send button click
        sendButton.setOnClickListener {
            val replyText = replyEditText.text.toString().trim()
            if (replyText.isNotEmpty()) {
                sendMessage(sender, replyText)
                replyEditText.text.clear() // Clear the input field
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Fetches messages from Firebase for the given sender.
     */
    private fun fetchMessages(sender: String) {
        // Create a reference to the messages in Firebase
        val messagesRef = FirebaseDatabase.getInstance().getReference("messages")

        // Query to get all messages sent by the sender
        messagesRef.orderByChild("sender").equalTo(sender)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Retrieve messages and display them
                    val messages = mutableListOf<MessageData>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(MessageData::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                    displayMessages(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ViewMessageActivity, "Error loading messages", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Displays messages in the TextView.
     */
    private fun displayMessages(messages: List<MessageData>) {
        if (messages.isNotEmpty()) {
            val timeline = buildMessageTimeline(messages)
            messageTextView.text = timeline
        } else {
            messageTextView.text = "No messages found."
        }
    }

    /**
     * Constructs a formatted message timeline.
     */
    private fun buildMessageTimeline(messages: List<MessageData>): String {
        val timelineBuilder = StringBuilder()
        for (message in messages.sortedBy { it.timestamp }) {
            timelineBuilder.append("${message.timestampFormatted}\n")
            timelineBuilder.append("${message.message}\n\n")
        }
        return timelineBuilder.toString().trim()
    }

    /**
     * Sends a reply message to the sender using Firebase.
     */
    private fun sendMessage(receiver: String, messageContent: String) {
        val currentUser = mAuth.currentUser ?: return
        val sender = currentUser.email ?: return

        // Prepare message data
        val messageData = mapOf(
            "sender" to sender,
            "receiver" to receiver,
            "message" to messageContent,
            "timestamp" to System.currentTimeMillis()
        )

        // Push message to Firebase
        FirebaseDatabase.getInstance().getReference("messages").push()
            .setValue(messageData)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                // Optionally fetch updated messages
                fetchMessages(receiver)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }
}
