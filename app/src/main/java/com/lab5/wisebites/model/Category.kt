package com.lab5.wisebites.model

import com.lab5.wisebites.R

data class Category(
    val name: String,
    val iconResId: Int
) {
    companion object {
        val categoriesList = listOf(
            Category("Beef", R.drawable.ic_beef),
            Category("Chicken", R.drawable.ic_chicken),
            Category("Dessert", R.drawable.ic_dessert),
            Category("Lamb", R.drawable.ic_lamb),
            Category("Miscellaneous", R.drawable.ic_bookmark), //TODO: Ganti icon
            Category("Pasta", R.drawable.ic_pasta),
            Category("Pork", R.drawable.ic_pork),
            Category("Seafood", R.drawable.ic_seafood),
            Category("Side", R.drawable.ic_side),
            Category("Starter", R.drawable.ic_starter),
            Category("Vegan", R.drawable.ic_bookmark), //TODO: Ganti icon
            Category("Vegetarian", R.drawable.ic_vegetarian),
            Category("Breakfast", R.drawable.ic_breakfast),
            Category("Goat", R.drawable.ic_goat)
        )
    }
}
