package uk.ac.tees.mad.S3269326

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import uk.ac.tees.mad.S3269326.databinding.ActivityPixtextBinding

class PixTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPixtextBinding
    val mAuth = FirebaseAuth.getInstance();
    var listView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var messages: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPixtextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "PixText App"

        /*
        listView = binding.messageListView;
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        listView?.adapter = adapter

        Firebase.database.getReference().child("users").child(mAuth.currentUser?.uid?:"")
            .child("snaps").addChildEventListener(object:
                ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {
                    emails.add(p0.child("from").value as String)
                    messages.add(p0)
                    adapter.notifyDataSetChanged()
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onChildRemoved(p0: DataSnapshot) {
                    var index = 0
                    for (snap: DataSnapshot in messages) {
                        if (snap.key == p0.key) {
                            messages.removeAt(index)
                            emails.removeAt(index)
                        }
                        index++
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
            */

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menulist,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.createMessage) {
            Toast.makeText(this, "New Message Option", Toast.LENGTH_SHORT).show()
        } else if (item?.itemId == R.id.logout){
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
    }
}