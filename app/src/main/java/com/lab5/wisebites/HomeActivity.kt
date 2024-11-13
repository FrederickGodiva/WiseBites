package com.lab5.wisebites

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.API.APIClient
import com.lab5.wisebites.API.APIService
import com.lab5.wisebites.adapter.RecipeAdapter
import com.lab5.wisebites.databinding.ActivityHomeBinding
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import com.lab5.wisebites.utils.BottomNavigationHandler
import com.lab5.wisebites.utils.SortModalBottomSheetDialog

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

    private fun fetchMultipleRecipes() {
        val apiService = APIClient.instance.create(APIService::class.java)

        // Menggunakan Coroutine di `lifecycleScope`
        lifecycleScope.launch {
            try {
                // Memanggil API 10 kali secara paralel
                val recipesList = withContext(Dispatchers.IO) {
                    (1..10).map {
                        async { apiService.getRandomRecipe()["meals"]?.firstOrNull() }
                    }.awaitAll()
                }.filterNotNull()

                // Menampilkan hasil ke RecyclerView
                if (recipesList.isNotEmpty()) {
                    binding.rvPopularRecipes.adapter = RecipeAdapter(recipesList)
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error: ${e.message}")
            }
        }
    }
}
