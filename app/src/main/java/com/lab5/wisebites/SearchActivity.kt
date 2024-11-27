package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.lab5.wisebites.adapter.RecipeAdapter
import com.lab5.wisebites.databinding.ActivitySearchBinding
import com.lab5.wisebites.utils.BottomNavigationHandler
import com.lab5.wisebites.utils.SearchHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, OnBoarding1Activity::class.java))
            finish()
        }

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SearchLogic to set up API service
        SearchHandler.initApiService()

        // Set up the RecyclerView Adapter
        recipeAdapter = RecipeAdapter(this, mutableListOf())
        binding.rvRecipesSearchResult.adapter = recipeAdapter

        binding.cvSearchView.setOnClickListener {
            binding.searchView.requestFocus()
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.setIconified(true)
                binding.searchView.clearFocus()

                // Submit the query when the user presses the enter key
                query?.let {
                    searchRecipes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Submit the query as the user types
                newText?.let {
                    searchRecipes(it)
                }
                return true
            }
        })


        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_search
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    private fun searchRecipes(query: String) {
        // Use lifecycleScope for proper coroutine handling inside the Activity
        lifecycleScope.launch(Dispatchers.Main) {
            SearchHandler.searchRecipeByName(query, this@SearchActivity, recipeAdapter)
        }
    }

    override fun onRestart() {
        super.onRestart()
        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_search
    }
}