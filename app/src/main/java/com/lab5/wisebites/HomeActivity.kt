package com.lab5.wisebites

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.API.APIClient
import com.lab5.wisebites.API.APIService
import com.lab5.wisebites.adapter.RecipeAdapter
import com.lab5.wisebites.databinding.ActivityHomeBinding
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.lab5.wisebites.adapter.CategoriesAdapter
import com.lab5.wisebites.model.Category
import com.lab5.wisebites.utils.BottomNavigationHandler
import com.lab5.wisebites.utils.SortModalBottomSheetDialog

class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    private lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = APIClient.instance.create(APIService::class.java)

        binding.btnSort.setOnClickListener() {
            val sortModalBottomSheet = SortModalBottomSheetDialog()
            sortModalBottomSheet.show(supportFragmentManager, sortModalBottomSheet.tag)
        }

        // Categories Recycler View Logic
        categoriesRecyclerViewHandler()
        showEmptyState()

        // Recycler View Logic of popular Recipes
        fetchMultipleRecipes()

        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_home
        // Items Selection Handler
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    override fun onRestart() {
        super.onRestart()
        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_home
    }


    private fun categoriesRecyclerViewHandler() {
        val categoriesAdapter = CategoriesAdapter(Category.categoriesList) { category ->
            if (category != null) {
                fetchRecipeByCategory(category)
            } else {
                showEmptyState()
            }
        }
        binding.rvCategories.adapter = categoriesAdapter
    }

    private fun showEmptyState() {
        binding.rvFilteredRecipes.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = layoutInflater.inflate(R.layout.item_filter_empty, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun getItemCount(): Int = 1

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        }
    }

    private fun fetchRecipeByCategory(category: String) {
        lifecycleScope.launch {
            try{
                val response = apiService.getRecipesByCategory(category)
                val recipesList = response["meals"] ?: emptyList()
                if (recipesList.isNotEmpty()) {
                    binding.rvFilteredRecipes.adapter = RecipeAdapter(recipesList)
                } else{
                    showEmptyState()
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error fetching recipes: ${e.message}")
            }
        }
    }

    private fun fetchMultipleRecipes() {
        // Use Coroutine in `lifecycleScope`
        lifecycleScope.launch {
            try {
                // Call API 10 times with paralelism
                val recipesList = withContext(Dispatchers.IO) {
                    (1..10).map {
                        async { apiService.getRandomRecipe()["meals"]?.firstOrNull() }
                    }.awaitAll()
                }.filterNotNull()

                // Show result to RecyclerView
                if (recipesList.isNotEmpty()) {
                    binding.rvPopularRecipes.adapter = RecipeAdapter(recipesList)
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error: ${e.message}")
            }
        }
    }
}
