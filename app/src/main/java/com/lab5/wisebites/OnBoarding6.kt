package com.lab5.wisebites

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lab5.wisebites.databinding.ActivityOnBoarding6Binding

class OnBoarding6 : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoarding6Binding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoarding6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Status...")
        progressDialog.setMessage("Please wait")

        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isEmpty()) {
                binding.etEmail.error = "Please enter your email"
            } else {
                progressDialog.show()
                resetPassword(email)
            }
        }
    }

    private fun resetPassword(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Password reset link sent to your email. Please check your inbox.",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("OnBoarding6", "Reset email sent to $email")
                val intent = Intent(this, OnBoarding4::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Log.e("OnBoarding6", "Reset failed: ${it.message}")
            }
    }
}
