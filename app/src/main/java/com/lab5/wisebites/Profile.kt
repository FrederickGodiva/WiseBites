package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.lab5.wisebites.databinding.ActivityProfileBinding
import com.lab5.wisebites.utils.BottomNavigationHandler

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            binding.tvUsername.text = user.displayName
            binding.tvEmail.text = user.email
            user.photoUrl?.let { photoUrl ->
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(binding.ivProfilePicture)
            } ?: run {
                Glide.with(this)
                    .load(R.drawable.ic_nigga)
                    .circleCrop()
                    .into(binding.ivProfilePicture)
            }
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
        }

        binding.btnLogOut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_profile

        // Items Selection Handler
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    override fun onRestart() {
        super.onRestart()
        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_profile
    }
}