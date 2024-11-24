package uk.ac.tees.mad.S3269326

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import uk.ac.tees.mad.S3269326.databinding.ActivityNewMessageBinding
import java.io.ByteArrayOutputStream
import java.util.UUID


class NewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMessageBinding
    private var newMessageImageView: ImageView? = null
    private var messageEditText: EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"
    private val PICK_IMAGE_PERMISSION_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        newMessageImageView = binding.imageViewNewMessage
        messageEditText = binding.editTextMessage
    }

    private fun handleImageSelection(data: Intent?) {
        try {
            val selectedImage = data?.data
            if (selectedImage != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                newMessageImageView?.setImageBitmap(bitmap)
            } else {
                Log.e("NewMessageActivity", "Selected image is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("NewMessageActivity", "Error handling image selection: ${e.message}")
        }
    }

    fun chooseImageClicked(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getPhoto()
        } else {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PICK_IMAGE_PERMISSION_REQUEST
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                newMessageImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PICK_IMAGE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun nextClicked(view: View) {

        newMessageImageView?.setDrawingCacheEnabled(true)
        newMessageImageView?.buildDrawingCache()
        val bitmap = newMessageImageView?.getDrawingCache()
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)

        uploadTask.addOnFailureListener(OnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"UploadFailed",Toast.LENGTH_SHORT).show()
        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot>{ taskSnapshot ->

            val result = taskSnapshot.storage.downloadUrl
            result.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                //createNewPost(imageUrl);
                val intent = Intent(this, ChooseUserActivity::class.java)
                intent.putExtra("imageURL",imageUrl)
                intent.putExtra("imageName",imageName)
                intent.putExtra("message",messageEditText?.text.toString())
                startActivity(intent)
            }

/*            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
            Log.i("URL", downloadUrl.toString())

            val intent = Intent(this, ChooseUserActivity::class.java)
            intent.putExtra("imageURL",downloadUrl.toString())
            intent.putExtra("imageName",imageName)
            intent.putExtra("message",messageEditText?.text.toString())
            startActivity(intent)*/
        })

    }

}
