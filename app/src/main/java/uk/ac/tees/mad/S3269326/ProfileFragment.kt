package uk.ac.tees.mad.S3269326

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.ac.tees.mad.S3269326.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Handle back press to navigate to PixTextActivity
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToPixTextActivity()
                }
            }
        )

        // Set the username and profile picture
        val currentUser = mAuth.currentUser
        currentUser?.let {
            binding.textUsername.text = it.email // Set username
            loadProfilePic(it.uid) // Load profile picture if available
        }

        // Set up the button to upload a new profile picture
        binding.buttonUploadProfilePic.setOnClickListener {
            openImagePicker()
        }

        return binding.root
    }

    private fun navigateToPixTextActivity() {
        val intent = Intent(requireContext(), PixTextActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Ensure the current activity is finished to avoid stacking
    }

    private fun loadProfilePic(userId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        dbRef.child("profilePicUrl").get().addOnSuccessListener { snapshot ->
            val profilePicUrl = snapshot.value as? String
            if (profilePicUrl != null) {
                Glide.with(requireContext())
                    .load(profilePicUrl)
                    .circleCrop()
                    .into(binding.profileImageView)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICKER) {
            data?.data?.let { imageUri ->
                saveProfileImageToLocalStorage(imageUri)
            }
        }
    }

    private fun saveProfileImageToLocalStorage(imageUri: Uri) {
        val userId = mAuth.currentUser?.uid ?: return
        val fileName = "$userId.jpg"

        try {
            // Save the selected image to internal storage
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val file = File(requireContext().filesDir, fileName)

            file.outputStream().use { output ->
                inputStream?.copyTo(output)
            }

            Log.d("ProfileFragment", "File saved at: ${file.absolutePath}")

            // Update the profile in Firebase with the local file path
            updateProfilePicUrl(file.absolutePath)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("ProfileFragment", "Error saving file", e)
        }
    }

    private fun updateProfilePicUrl(filePath: String) {
        val userId = mAuth.currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        dbRef.child("profilePicUrl").setValue(filePath)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()
                loadProfilePic(userId) // Refresh the profile picture
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to update profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}
