package com.lab5.wisebites

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lab5.wisebites.databinding.ActivityOnBoarding4Binding

class OnBoarding4 : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoarding4Binding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnBoarding4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Status...")
        progressDialog.setMessage("Please wait")

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            when {
                binding.etEmail.text.toString().isEmpty() -> binding.etEmail.error =
                    "Please enter email"
                binding.etPassword.text.toString().isEmpty() -> binding.etPassword.error =
                    "Please enter password"
                else -> {
                    val email = binding.etEmail.text.toString()
                    val password = binding.etPassword.text.toString()

                    progressDialog.show()

                    login(email, password)
                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, OnBoarding6::class.java)
            startActivity(intent)
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, OnBoarding5::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun login(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progressDialog.dismiss()
            }
            .addOnSuccessListener {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}
