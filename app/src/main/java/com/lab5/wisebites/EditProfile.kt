package com.lab5.wisebites

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.databinding.ActivityEditProfileBinding
import com.lab5.wisebites.utils.BottomNavigationHandler

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelChanges.setOnClickListener {
            finish()
        }

        binding.bnMenu.selectedItemId = R.id.i_profile
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    override fun onRestart() {
        super.onRestart()
        binding.bnMenu.selectedItemId = R.id.i_profile
    }
}