package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.databinding.ActivityOnBoarding5Binding

class OnBoarding5 : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoarding5Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnBoarding5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, OnBoarding4::class.java)
            startActivity(intent)
        }
    }
}