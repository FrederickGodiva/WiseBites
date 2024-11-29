package com.lab5.wisebites

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.lab5.wisebites.databinding.ActivityEditProfileBinding
import com.lab5.wisebites.utils.BottomNavigationHandler

class EditProfile<FirebaseUser> : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            binding.tfUsername.text = Editable.Factory.getInstance().newEditable(user.displayName)
            binding.email.text = Editable.Factory.getInstance().newEditable(user.email)

            binding.editPassword.setOnClickListener{
                val isVisible = binding.editPasswordField.visibility == View.VISIBLE
                binding.editPasswordField.visibility = if (isVisible) View.GONE else View.VISIBLE
            }

            binding.editPhotoProfile.setOnClickListener{
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_IMAGE)
            }

            binding.btnSaveChanges.setOnClickListener {
                handleSaveChanges(user)
            }

            binding.btnCancelChanges.setOnClickListener {
                finish()
            }

            binding.bnMenu.selectedItemId = R.id.i_profile
            BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
        }
    }

    private fun handleSaveChanges(user: com.google.firebase.auth.FirebaseUser?) {
        if (user == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val oldPassword = binding.oldPassword.editText?.text.toString()
        val newPassword = binding.newpassword.editText?.text.toString()
        val confirmPassword = binding.confirmpassword.editText?.text.toString()
        val newUsername = binding.editname.editText?.text.toString() // New username from EditText

        val isPasswordChanged = newPassword.isNotEmpty() && newPassword == confirmPassword

        if (isPasswordChanged) {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateUserProfile(user, newUsername, newPassword)
                    } else {
                        Toast.makeText(this, "Old password incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            updateUserProfile(user, newUsername, null)
        }
    }

    private fun updateUserProfile(user: com.google.firebase.auth.FirebaseUser?, newUsername: String?, newPassword: String?) {
        if (user == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        var updateProfileSuccessful = false
        var updatePasswordSuccessful = false

        if (newUsername?.isNotEmpty() == true) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { profileTask ->
                    if (profileTask.isSuccessful) {
                        updateProfileSuccessful = true
                    } else {
                        Toast.makeText(this, "Username update failed", Toast.LENGTH_SHORT).show()
                    }

                    if (updateProfileSuccessful || updatePasswordSuccessful) {
                        showSuccessToast(updateProfileSuccessful, updatePasswordSuccessful)
                    }
                }
        } else {
            if (updatePasswordSuccessful) {
                showSuccessToast(updateProfileSuccessful, updatePasswordSuccessful)
            }
        }

        if (newPassword != null && newPassword.isNotEmpty()) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        updatePasswordSuccessful = true
                    } else {
                        Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                    }
                    if (updateProfileSuccessful || updatePasswordSuccessful) {
                        showSuccessToast(updateProfileSuccessful, updatePasswordSuccessful)
                    }
                }
        } else {
            if (updateProfileSuccessful) {
                showSuccessToast(updateProfileSuccessful, updatePasswordSuccessful)
            }
        }
    }


    // Toast Update
    private fun showSuccessToast(updateProfileSuccessful: Boolean, updatePasswordSuccessful: Boolean) {
        if (updateProfileSuccessful && updatePasswordSuccessful) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        } else if (updateProfileSuccessful) {
            Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show()
        } else if (updatePasswordSuccessful) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
        }

        finish()
    }



    override fun onRestart() {
        super.onRestart()
        binding.bnMenu.selectedItemId = R.id.i_profile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null){
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                binding.lisaPhotoProfile.setImageBitmap(bitmap)
                }
        }
    }
}