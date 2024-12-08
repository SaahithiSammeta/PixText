package uk.ac.tees.mad.S3269326

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.ac.tees.mad.S3269326.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val email = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signUp(email, password)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            // Go to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Get the current user's UID
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        // Create a reference to the "users" node in Firebase Realtime Database
                        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

                        // Create a map of user data
                        val userData = mapOf(
                            "email" to email,
                            "uid" to userId
                        )

                        // Save user data to Firebase Realtime Database
                        userRef.setValue(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User data saved!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }
                    }

                    Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                    // Optionally, navigate to another screen (e.g., MainActivity or LoginActivity)
                } else {
                    Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        finish()
    }
}
