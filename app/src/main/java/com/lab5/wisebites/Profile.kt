package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.databinding.ActivityProfileBinding
import com.lab5.wisebites.utils.BottomNavigationHandler

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
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