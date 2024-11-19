package com.lab5.wisebites.utils

import com.lab5.wisebites.model.Recipe

object SortHandler {
    fun sortRecipes(recipes: MutableList<Recipe>, option: String): List<Recipe> {
        return when (option) {
            "Recipe A to Z" -> recipes.sortedBy { it.strMeal }
            "Recipe Z to A" -> recipes.sortedByDescending { it.strMeal }
            "Category A to Z" -> recipes.sortedBy { it.strCategory }
            "Category Z to A" -> recipes.sortedByDescending { it.strCategory }
            "Area A to Z" -> recipes.sortedBy { it.strArea }
            "Area Z to A" -> recipes.sortedByDescending { it.strArea }
            else -> recipes
        }
    }
}