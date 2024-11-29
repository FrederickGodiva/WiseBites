package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.lab5.wisebites.databinding.ActivityEditProfileBinding
import com.lab5.wisebites.utils.BottomNavigationHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

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
        val newUsername = binding.editname.editText?.text.toString()

        if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
            Toast.makeText(this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
            return
        } else if (newPassword.isNotEmpty() && newPassword == oldPassword) {
            Toast.makeText(this, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show()
            return
        }

        val isPasswordChanged = newPassword.isNotEmpty() && newPassword == confirmPassword

        if (isPasswordChanged) {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lifecycleScope.launch {
                            updateUserProfile(user, newUsername, newPassword)
                        }
                    } else {
                        Toast.makeText(this, "Old password incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            lifecycleScope.launch {
                updateUserProfile(user, newUsername, null)
            }
        }
    }

    private suspend fun updateUserProfile(user: com.google.firebase.auth.FirebaseUser?, newUsername: String?, newPassword: String?) {
        if (user == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        var updateProfileSuccessful = false
        var updatePasswordSuccessful = false

        var usernameUpdateCompleted = false
        var passwordUpdateCompleted = false

        if (newUsername?.isNotEmpty() == true && newUsername != user.displayName) {
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
                    usernameUpdateCompleted = true
                    checkCompletion(updateProfileSuccessful, updatePasswordSuccessful, usernameUpdateCompleted, passwordUpdateCompleted)
                }.await()
        } else {
            usernameUpdateCompleted = true
        }

        if (newPassword != null && newPassword.isNotEmpty()) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        updatePasswordSuccessful = true
                    } else {
                        Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                    }
                    passwordUpdateCompleted = true
                    checkCompletion(updateProfileSuccessful, updatePasswordSuccessful, usernameUpdateCompleted, passwordUpdateCompleted)
                }.await()
        } else {
            passwordUpdateCompleted = true
        }

        checkCompletion(updateProfileSuccessful, updatePasswordSuccessful, usernameUpdateCompleted, passwordUpdateCompleted)
    }


    // Toast Update
    private fun checkCompletion(
        updateProfileSuccessful: Boolean,
        updatePasswordSuccessful: Boolean,
        usernameUpdateCompleted: Boolean,
        passwordUpdateCompleted: Boolean
    ) {
        if (usernameUpdateCompleted && passwordUpdateCompleted) {
            if (updateProfileSuccessful && updatePasswordSuccessful) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else if (updateProfileSuccessful) {
                Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show()
            } else if (updatePasswordSuccessful) {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Username and password are the same as before", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }



    override fun onRestart() {
        super.onRestart()
        binding.bnMenu.selectedItemId = R.id.i_profile
    }
}