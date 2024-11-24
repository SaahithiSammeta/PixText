package uk.ac.tees.mad.S3269326

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import uk.ac.tees.mad.S3269326.databinding.ActivityNewMessageBinding
import uk.ac.tees.mad.S3269326.databinding.ActivityViewMessageBinding
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ViewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMessageBinding
    private var messageTextView: TextView? = null
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageTextView = binding.textViewMessage
        val snapImageView: ImageView = binding.imageViewSnap
        messageTextView?.text = intent.getStringExtra("message")

        val imageUrl = intent.getStringExtra("imageURL")

        // Load image with AsyncTask
        LoadImageTask(snapImageView).execute(imageUrl)
    }

    private class LoadImageTask(private val imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                return BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                imageView.setImageBitmap(result)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid?:"")
            .child("snaps").child(intent.getStringExtra("snapKey")?:"").removeValue()
        FirebaseStorage.getInstance().getReference().child("images")
            .child(intent.getStringExtra("imageName")?:"").delete()
    }

}
