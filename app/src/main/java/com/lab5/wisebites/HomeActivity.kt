package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.lab5.wisebites.API.APIClient
import com.lab5.wisebites.API.APIService
import com.lab5.wisebites.adapter.RecipeAdapter
import com.lab5.wisebites.databinding.ActivityHomeBinding
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.lab5.wisebites.adapter.CategoriesAdapter
import com.lab5.wisebites.model.Category
import com.lab5.wisebites.model.Recipe
import com.lab5.wisebites.utils.BottomNavigationHandler
import com.lab5.wisebites.utils.SearchHandler
import com.lab5.wisebites.utils.SortHandler
import com.lab5.wisebites.utils.SortModalBottomSheetDialog
import com.lab5.wisebites.utils.SortOptionListener

class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    private lateinit var apiService: APIService
    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var recipeAdapter: RecipeAdapter
    private var lastSelectedSortOption: String = "Recipe A to Z"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, OnBoarding1Activity::class.java))
            finish()
        } else {
            binding.tvGreetings.text = "Hello, ${user.displayName}"
        }

        apiService = APIClient.instance.create(APIService::class.java)
        SearchHandler.initApiService()

        binding.cvSearchView.setOnClickListener {
            binding.searchView.requestFocus()
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                binding.searchView.isIconified = true

                query?.let {
                    searchRecipes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        searchRecipes(it)
                    } else {
                        // Reset to original list if query is empty
                        displayRecipes(recipeList)
                    }
                }
                return true
            }
        })

        binding.btnSort.setOnClickListener() {
            val sortModalBottomSheet = SortModalBottomSheetDialog(lastSelectedSortOption, object: SortOptionListener {
                override fun onSortOptionSelected(option: String) {
                    lastSelectedSortOption = option
                    sortRecipes(option)
                }
            })
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


    private fun searchRecipes(query: String) {
        recipeAdapter = RecipeAdapter(this, recipeList)
        binding.rvPopularRecipes.adapter = recipeAdapter
        SearchHandler.searchRecipeByName(query, this@HomeActivity, recipeAdapter)
    }

    private fun categoriesRecyclerViewHandler() {
        val categoriesAdapter = CategoriesAdapter(Category.categoriesList) { category ->
            if (category != null) {
                binding.rvFilteredRecipes.adapter = null
                fetchRecipeByCategory(category)
            } else {
                showEmptyState()
            }
        }
        binding.rvCategories.adapter = categoriesAdapter
    }

    private fun showEmptyState() {
        binding.cpiFilteredRecipes.visibility = View.GONE
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
                binding.cpiFilteredRecipes.visibility = View.VISIBLE

                val response = apiService.getRecipesByCategory(category)
                val recipesList = response["meals"] ?: emptyList()

                if (recipesList.isNotEmpty()) {
                    val detailedRecipes = withContext(Dispatchers.IO) {
                        recipesList.map { recipe ->
                            async {
                                val detailedRecipe = apiService.getRecipeById(recipe.idMeal)
                                detailedRecipe["meals"]?.firstOrNull()
                            }
                        }.awaitAll()
                    }.filterNotNull()

                    if (detailedRecipes.isNotEmpty()) {
                        binding.rvFilteredRecipes.adapter = RecipeAdapter(
                            this@HomeActivity,
                            detailedRecipes.toMutableList()
                        )
                    } else {
                        showEmptyState()
                    }
                } else{
                    showEmptyState()
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error fetching recipes: ${e.message}")
            } finally {
                // Hide the progress indicator once fetching is complete
                binding.cpiFilteredRecipes.visibility = View.GONE
            }
        }
    }

    private fun fetchMultipleRecipes() {
        // Use Coroutine in `lifecycleScope`
        lifecycleScope.launch {
            try {
                // Call API 10 times with paralelism
                recipeList = withContext(Dispatchers.IO) {
                    (1..10).map {
                        async { apiService.getRandomRecipe()["meals"]?.firstOrNull() }
                    }.awaitAll()
                }.filterNotNull().toMutableList()

                displayRecipes(recipeList)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error: ${e.message}")
            }
        }
    }

    private fun displayRecipes(recipe: List<Recipe>) {
        if(recipe.isNotEmpty()){
            binding.rvPopularRecipes.adapter = RecipeAdapter(
                this@HomeActivity,
                recipe.toMutableList()
            )
        }
    }

    private fun sortRecipes(option: String) {
        if (option.isNotEmpty()) {
            recipeList = SortHandler.sortRecipes(recipeList, option).toMutableList()
            displayRecipes(recipeList)
        } else {
            Log.e("HomeActivity", "Received empty sort option")
        }
    }
}
