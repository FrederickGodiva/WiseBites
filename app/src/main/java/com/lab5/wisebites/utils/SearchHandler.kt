package com.lab5.wisebites.utils

import android.content.Context
import android.widget.Toast
import com.lab5.wisebites.API.APIClient
import com.lab5.wisebites.API.APIService
import com.lab5.wisebites.adapter.RecipeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SearchHandler {
    private lateinit var apiService: APIService

    fun initApiService() {
        apiService = APIClient.instance.create(APIService::class.java)
    }

    fun searchRecipeByName(query: String, context: Context, adapter: RecipeAdapter) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getRecipeByName(query)
                val recipes = response["meals"]?: emptyList()

                // Switch to Main thread to update the UI
                withContext(Dispatchers.Main) {
                    adapter.updateRecipes(recipes)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}