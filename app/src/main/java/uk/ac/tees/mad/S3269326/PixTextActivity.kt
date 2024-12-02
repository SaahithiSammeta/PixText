package uk.ac.tees.mad.S3269326

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uk.ac.tees.mad.S3269326.databinding.ActivityPixtextBinding

class PixTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPixtextBinding
    private val mAuth = FirebaseAuth.getInstance()
    private var listView: ListView? = null
    private val uniqueSenders: MutableSet<String> = mutableSetOf() // Ensure unique senders
    private val senderMessages: MutableMap<String, MutableList<DataSnapshot>> = mutableMapOf() // Map sender to their messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPixtextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "PixText App"

        listView = binding.messageListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList(uniqueSenders))
        listView?.adapter = adapter

        // Listen for messages where the current user is the receiver
        Firebase.database.getReference("messages")
            .orderByChild("receiver")
            .equalTo(mAuth.currentUser?.email)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val sender = snapshot.child("sender").getValue(String::class.java)
                    if (sender != null) {
                        if (!uniqueSenders.contains(sender)) {
                            uniqueSenders.add(sender)
                            senderMessages[sender] = mutableListOf()
                        }
                        senderMessages[sender]?.add(snapshot) // Add the message to the sender's list
                        adapter.clear()
                        adapter.addAll(uniqueSenders)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val sender = snapshot.child("sender").getValue(String::class.java)
                    if (sender != null && senderMessages.containsKey(sender)) {
                        senderMessages[sender]?.remove(snapshot)
                        if (senderMessages[sender]?.isEmpty() == true) {
                            uniqueSenders.remove(sender)
                            senderMessages.remove(sender)
                        }
                        adapter.clear()
                        adapter.addAll(uniqueSenders)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })

        // Navigate to ViewMessageActivity on list item click
        listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val sender = adapter.getItem(position)
            val intent = Intent(this, ViewMessageActivity::class.java)
            intent.putExtra("sender", sender) // Pass the sender name
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menulist, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.createMessage -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                mAuth.signOut()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
        finishAffinity()
    }
}
