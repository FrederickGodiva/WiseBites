package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.API.APIClient
import com.lab5.wisebites.API.APIService
import com.lab5.wisebites.adapter.RecipeAdapter
import com.lab5.wisebites.databinding.ActivityHomeBinding
import com.lab5.wisebites.model.Recipe
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSort.setOnClickListener() {
            val sortModalBottomSheet = SortModalBottomSheetDialog()
            sortModalBottomSheet.show(supportFragmentManager, sortModalBottomSheet.tag)
        }

        // Logic Recycler View dari populer Recipes
        val apiService = APIClient.instance.create(APIService::class.java)

        apiService.getRandomRecipe().enqueue(object: Callback<Map<String, List<Recipe>>> {
            override fun onResponse(
                call: retrofit2.Call<Map<String, List<Recipe>>>,
                response: Response<Map<String, List<Recipe>>>
            ) {
                if (response.isSuccessful) {
                    val meals = response.body()?.get("meals")
                    if (!meals.isNullOrEmpty()) {
                        binding.rvPopularRecipes.adapter = response.body()?.let { RecipeAdapter(meals) }
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<Map<String, List<Recipe>>>, t: Throwable) {
                // TODO: Need to make the error handling
                Log.e("HomeActivity", "Error:${t.message}")
            }
        })

        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_home

        // TODO: MOVE INTO ANOTHER FILE
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

    override fun onRestart() {
        super.onRestart()
        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_home
    }
}