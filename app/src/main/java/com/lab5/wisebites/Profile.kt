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

        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_profile

        // Items Selection Handler
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }

        binding.btnLogOut.setOnClickListener{
            val intent = Intent(this, OnBoarding3::class.java)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_profile
    }
}