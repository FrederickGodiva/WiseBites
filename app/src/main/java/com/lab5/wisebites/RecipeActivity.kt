package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.lab5.wisebites.databinding.ActivityRecipeBinding

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBookmark.setOnClickListener{
            binding.btnBookmark.isSelected = !binding.btnBookmark.isSelected
            if (binding.btnBookmark.isSelected) {
                // Code for when the bookmark is checked
            } else {
                // Code for when the bookmark is unchecked
            }
        }


        binding.tlTabs.addTab(binding.tlTabs.newTab().setText(getString(R.string.recipe_tab_details)))
        binding.tlTabs.addTab(binding.tlTabs.newTab().setText(getString(R.string.recipe_tab_ingredients)))
        binding.tlTabs.addTab(binding.tlTabs.newTab().setText(getString(R.string.recipe_tab_direction)))

        // Tab Layout Handler
        binding.tlTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Hide all content views
                binding.detailsContent.visibility = View.GONE
                binding.ingredientsContent.visibility = View.GONE
                binding.directionContent.visibility = View.GONE

                // Show the selected tab's content
                when (tab?.position) {
                    0 -> binding.detailsContent.visibility = View.VISIBLE
                    1 -> binding.ingredientsContent.visibility = View.VISIBLE
                    2 -> binding.directionContent.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = View.NO_ID

        // Items Selection Handler
        binding.bnMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.i_home -> {
                    intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }
                R.id.i_search -> {
                    intent = Intent(this, SearchActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }
                R.id.i_bookmark -> {
                    intent = Intent(this, BookmarkActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }
                R.id.i_profile -> {
                    // startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}