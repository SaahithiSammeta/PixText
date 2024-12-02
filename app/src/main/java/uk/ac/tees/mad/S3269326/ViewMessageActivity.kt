package uk.ac.tees.mad.S3269326

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.ac.tees.mad.S3269326.databinding.ActivityViewMessageBinding

class ViewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMessageBinding
    private lateinit var adapter: ChatAdapter
    private val messages: MutableList<MessageData> = mutableListOf()
    private val mAuth = FirebaseAuth.getInstance()
    private val currentUser = mAuth.currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sender = intent.getStringExtra("sender")
        if (sender == null || currentUser == null) {
            Toast.makeText(this, "Invalid sender or user!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up the Toolbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = sender // Set the sender's name as the title
            setDisplayHomeAsUpEnabled(true) // Optional: to add a back button
        }
        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up RecyclerView
        adapter = ChatAdapter(messages, currentUser)
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@ViewMessageActivity).apply { stackFromEnd = true }
            adapter = this@ViewMessageActivity.adapter
        }

        // Fetch chat messages
        fetchMessages(sender)

        // Set up Send button
        binding.buttonSendReply.setOnClickListener {
            val replyText = binding.editTextReply.text.toString().trim()
            if (replyText.isNotEmpty()) {
                sendMessage(sender, replyText)
                binding.editTextReply.text.clear()
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchMessages(sender: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("messages")

        dbRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                messages.clear()
                for (child in snapshot.children) {
                    val message = child.getValue(MessageData::class.java)
                    if (message != null) {
                        // Check if the message belongs to this chat
                        if ((message.sender == sender && message.receiver == currentUser) ||
                            (message.sender == currentUser && message.receiver == sender)
                        ) {
                            messages.add(message)
                        }
                    }
                }
                messages.sortBy { it.timestamp }
                adapter.notifyDataSetChanged()
                binding.recyclerViewChat.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(this@ViewMessageActivity, "Failed to fetch messages", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage(receiver: String, messageContent: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("messages")

        val message = MessageData(
            sender = currentUser ?: return,
            receiver = receiver,
            message = messageContent,
            timestamp = System.currentTimeMillis()
        )

        dbRef.push()
            .setValue(message)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                fetchMessages(receiver) // Refresh messages to include the new one
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    // Handle back button press
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
