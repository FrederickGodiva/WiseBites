package com.lab5.wisebites.API

import com.lab5.wisebites.model.Recipe
import retrofit2.http.GET

interface APIService {
    @GET("random.php")
    suspend fun getRandomRecipe(): Map<String, List<Recipe>>

    @GET("filter.php")
    suspend fun getRecipesByCategory(@retrofit2.http.Query("c") category: String): Map<String, List<Recipe>>
}