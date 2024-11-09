package uk.ac.tees.mad.S3269326

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.ac.tees.mad.S3269326.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            // Perform login operation (no backend here, so just show a message)
            binding.tvStatus.text = "Login successful!"
        }

        binding.tvSignup.setOnClickListener {
            // Go back to SignUpActivity
            finish() // This will go back to the SignUpActivity if it was open
        }
    }
}
