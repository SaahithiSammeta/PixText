package uk.ac.tees.mad.S3269326

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import uk.ac.tees.mad.S3269326.databinding.ActivityChooseUserBinding
import uk.ac.tees.mad.S3269326.databinding.ActivityNewMessageBinding

class ChooseUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseUserBinding
    var chooseUserListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var keys: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chooseUserListView = binding.chooseUserListView
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        chooseUserListView?.adapter = adapter

        Firebase.database.getReference().child("users").addChildEventListener(object:
            ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {
                val email = p0.child("email").value as String
                emails.add(email)
                keys.add(p0.key.toString())
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapMap: Map<String, String?> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,
                                                "imageName" to intent.getStringExtra("imageName"),
                                                "imageURL" to intent.getStringExtra("imageURL"),
                                                "message" to intent.getStringExtra("message"))

            Firebase.database.getReference().child("users").child(keys.get(i)).child("snaps")
                .push().setValue(snapMap)
            val intent = Intent(this, PixTextActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


    }
}