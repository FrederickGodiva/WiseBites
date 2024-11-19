package com.lab5.wisebites.API

import com.lab5.wisebites.model.Recipe
import retrofit2.http.GET

interface APIService {
    @GET("random.php")
    suspend fun getRandomRecipe(): Map<String, List<Recipe>>

    @GET("filter.php")
    suspend fun getRecipesByCategory(@retrofit2.http.Query("c") category: String): Map<String, List<Recipe>>

    @GET("lookup.php")
    suspend fun getRecipeById(@retrofit2.http.Query("i") id: Int): Map<String, List<Recipe>>

    @GET("search.php")
    suspend fun getRecipeByName(@retrofit2.http.Query("s") name: String): Map<String, List<Recipe>>
}