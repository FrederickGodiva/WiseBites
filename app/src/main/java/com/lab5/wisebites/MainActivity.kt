package com.lab5.wisebites

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.lab5.wisebites.adapter.OnBoardingAdapter
import com.lab5.wisebites.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnBoardingAdapter.SkipClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layouts = listOf(
            R.layout.activity_on_boarding1,
            R.layout.activity_on_boarding2,
            R.layout.activity_on_boarding3
        )

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = OnBoardingAdapter(layouts, this)

        // Initialize the dots and background color
        updateDots(0)

        // Set up page change callback to update dot icons
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
            }
        })
    }

    override fun onSkipClicked() {
        Log.d("MainActivity", "Skip button pressed - navigating to last page")
        // Set ViewPager to the last page
        binding.viewPager.currentItem = 2
    }

    private fun updateDots(position: Int) {
        val dot1 = findViewById<ImageButton>(R.id.dot_1)
        val dot2 = findViewById<ImageButton>(R.id.dot_2)
        val dot3 = findViewById<ImageButton>(R.id.dot_3)

        dot1.setBackgroundResource(if (position == 0) R.drawable.dot_white else R.drawable.dot)
        dot2.setBackgroundResource(if (position == 1) R.drawable.dot_red else R.drawable.dot)
        dot3.setBackgroundResource(if (position == 2) R.drawable.dot_red else R.drawable.dot)
    }
}