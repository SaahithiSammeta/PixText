package uk.ac.tees.mad.S3269326

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.ac.tees.mad.S3269326.databinding.ActivityNewMessageBinding

class NewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMessageBinding
    private var messageEditText: EditText? = null
    private lateinit var userSpinner: Spinner
    private var usersList: MutableList<String> = mutableListOf() // To store user emails
    private var selectedUser: String? = null // To store the selected user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageEditText = binding.editTextMessage
        userSpinner = binding.userSpinner

        // Retrieve the list of users from Firebase and populate the spinner
        loadUsers()

        userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUser = usersList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle case where no item is selected (if needed)
            }
        }
    }

    private fun loadUsers() {
        // Get reference to the "users" node in Firebase Realtime Database
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        // Fetch users from the database
        usersRef.get().addOnSuccessListener { snapshot ->
            usersList.clear() // Clear the list before adding new data
            for (userSnapshot in snapshot.children) {
                val userEmail = userSnapshot.child("email").getValue(String::class.java)
                // Only add users whose email is different from the current user's email
                if (userEmail != null && userEmail != currentUserEmail) {
                    usersList.add(userEmail)
                }
            }

            // Set up the spinner with the list of user emails
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                usersList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            userSpinner.adapter = adapter
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
        }
    }

    fun nextClicked(view: View) {
        val message = binding.editTextMessage.text.toString()

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedUser == null) {
            Toast.makeText(this, "Please select a recipient", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val senderId = currentUser?.uid
        val timestamp = System.currentTimeMillis()

        if (senderId != null) {
            val messageId = FirebaseDatabase.getInstance().getReference("messages").push().key

            // Create a map to store the message data
            val messageData = mapOf(
                "message" to message,
                "sender" to currentUser.email,
                "receiver" to selectedUser,
                "timestamp" to timestamp
            )

            // Save the message once in the messages node
            if (messageId != null) {
                FirebaseDatabase.getInstance().getReference("messages")
                    .child(messageId)
                    .setValue(messageData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity after sending the message
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
