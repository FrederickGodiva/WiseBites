package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.lab5.wisebites.api.APIClient
import com.lab5.wisebites.api.APIService
import com.lab5.wisebites.adapter.RecipeAdapter
import com.lab5.wisebites.databinding.ActivityBookmarkBinding
import com.lab5.wisebites.model.Recipe
import com.lab5.wisebites.repository.FirestoreRepository
import com.lab5.wisebites.utils.BottomNavigationHandler
import com.lab5.wisebites.utils.SearchHandler
import com.lab5.wisebites.utils.SortHandler
import com.lab5.wisebites.utils.SortModalBottomSheetDialog
import com.lab5.wisebites.utils.SortOptionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding
    private lateinit var apiService: APIService
    private lateinit var recipeAdapter: RecipeAdapter
    private val repository = FirestoreRepository()
    private var lastSelectedSortOption: String = ""
    private lateinit var recipeList: MutableList<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, OnBoarding1Activity::class.java))
            finish()
        } else {
            binding.tvGreetings.text = "Hello, ${user.displayName}"
            user.photoUrl?.let { photoUrl ->
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(binding.ivAvatar)
            } ?: run {
                Glide.with(this)
                    .load(R.drawable.ic_nigga)
                    .circleCrop()
                    .into(binding.ivAvatar)
            }
        }

        apiService = APIClient.instance.create(APIService::class.java)

        lifecycleScope.launch {
            fetchRecipesbyBookmark()
        }

        SearchHandler.initApiService()

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

        binding.btnSort.setOnClickListener {
            val sortModalBottomSheet = SortModalBottomSheetDialog(lastSelectedSortOption, object: SortOptionListener {
                override fun onSortOptionSelected(option: String) {
                    lastSelectedSortOption = option
                    sortRecipes(option)
                }
            })
            sortModalBottomSheet.show(supportFragmentManager, sortModalBottomSheet.tag)
        }

        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_bookmark

        // Items Selection Handler
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    override fun onRestart() {
        super.onRestart()
        binding.bnMenu.selectedItemId = R.id.i_bookmark
    }

    private suspend fun fetchRecipesbyBookmark() {
        try {
            binding.cpiBookmarkRecipes.visibility = View.VISIBLE
            val result = repository.getBookmarkIds()
            result.onSuccess { bookmarkIds ->
                val bookmarkIdsAsInt = bookmarkIds.map { it.toInt() }
                if (bookmarkIdsAsInt.isNotEmpty()) {
                    val recipes = coroutineScope {
                        bookmarkIdsAsInt.map { bookmarkId ->
                            async(Dispatchers.IO) {
                                apiService.getRecipeById(bookmarkId)
                            }
                        }.awaitAll()
                    }
                    val allRecipes = recipes.flatMap { it["meals"] ?: emptyList() }

                    withContext(Dispatchers.Main) {
                        recipeList = allRecipes.toMutableList()
                        recipeAdapter = RecipeAdapter(this@BookmarkActivity, recipeList)
                        binding.rvPopularRecipes.adapter = recipeAdapter
                    }
                }
            }.onFailure { error ->
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@BookmarkActivity,
                        "Failed to fetch bookmarks: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@BookmarkActivity, "Error fetching bookmarks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } finally {
            binding.cpiBookmarkRecipes.visibility = View.GONE
        }
    }

    fun refreshBookmarkList() {
        binding.rvPopularRecipes.adapter = null
        lifecycleScope.launch {
            fetchRecipesbyBookmark()
        }
    }

    private fun searchRecipes(query: String) {
        val filteredList = if(query.isEmpty()){
            recipeList.toList()
        } else {
            recipeList.filter { recipe ->
                recipe.strMeal.contains(query, ignoreCase = true)
            }
        }

        if (::recipeAdapter.isInitialized) {
            recipeAdapter.updateRecipes(filteredList)
        }
    }

    private fun sortRecipes(option: String) {
        if (option.isNotEmpty()) {
            if (::recipeList.isInitialized) {
                val sortedList = SortHandler.sortRecipes(recipeList, option)
                recipeAdapter.updateRecipes(sortedList)
            }
        } else {
            Log.e("BookmarkActivity", "Received empty sort option")
        }
    }
}