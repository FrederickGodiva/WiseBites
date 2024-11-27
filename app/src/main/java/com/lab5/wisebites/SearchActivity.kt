package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.lab5.wisebites.adapter.RecipeAdapter
import com.lab5.wisebites.api.APIClient
import com.lab5.wisebites.api.APIService
import com.lab5.wisebites.databinding.ActivitySearchBinding
import com.lab5.wisebites.model.Recipe
import com.lab5.wisebites.utils.BottomNavigationHandler
import com.lab5.wisebites.utils.SearchHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var apiService: APIService
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeList: MutableList<Recipe>

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

        apiService = APIClient.instance.create(APIService::class.java)
        fetchMultipleRecipes()

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
                binding.searchView.isIconified = true
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

    private fun fetchMultipleRecipes() {
        // Use Coroutine in `lifecycleScope`
        lifecycleScope.launch {
            try {
                binding.cpiSearchRecipes.visibility = View.VISIBLE
                // Call API 10 times with parallelism
                recipeList = withContext(Dispatchers.IO) {
                    (1..8).map {
                        async { apiService.getRandomRecipe()["meals"]?.firstOrNull() }
                    }.awaitAll()
                }.filterNotNull().toMutableList()

                if (recipeList.isNotEmpty()) {
//                    binding.rvRecipesSearchResult.adapter = RecipeAdapter(
//                        this@SearchActivity,
//                        recipeList.toMutableList()
//                    )
                    recipeAdapter.updateRecipes(recipeList.toMutableList())
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error: ${e.message}")
            } finally {
                binding.cpiSearchRecipes.visibility = View.GONE
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_search
    }
}