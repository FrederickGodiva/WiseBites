package com.lab5.wisebites

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.lab5.wisebites.databinding.ActivityRecipeBinding
import com.lab5.wisebites.model.Recipe
import com.lab5.wisebites.repository.FirestoreRepository
import com.lab5.wisebites.utils.BottomNavigationHandler
import kotlinx.coroutines.launch

class RecipeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecipeBinding
    private val repository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipe = intent.getParcelableExtra<Recipe>("RECIPE_DATA")
        if (recipe != null) {
            Glide.with(this).load(recipe.strMealThumb).into(binding.ivRecipe)
            binding.tvRecipeName.text = recipe.strMeal
            binding.tvRecipeTag.text = recipe.strCategory
            binding.tvStyle.text = recipe.strArea
            binding.tvDirectionContent.text = formatInstructions(recipe.strInstructions)

            val ingredients = getIngredientsList(recipe)
            binding.tvIngredientsContent.text = ingredients.joinToString( "\n" )
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        lifecycleScope.launch {
            val result = repository.getBookmarkIds()
            result.onSuccess { bookmarkIds ->
                val bookmarkIdsAsInt = bookmarkIds.map { it.toInt() }
                if (recipe != null) {
                    binding.btnBookmark.isSelected = bookmarkIdsAsInt.contains(recipe.idMeal)
                }
            }
            result.onFailure { exception ->
                Log.e("RecipeActivity", "Error fetching bookmark ids: ${exception.message}")
            }
        }

        binding.btnBookmark.setOnClickListener{
            binding.btnBookmark.isSelected = !binding.btnBookmark.isSelected
            lifecycleScope.launch {
                if (binding.btnBookmark.isSelected) {
                    if (recipe != null) {
                        val result = repository.addBookmark(recipe.idMeal)
                        if (result.isSuccess) {
                            Toast.makeText(
                                this@RecipeActivity,
                                "Recipe added to bookmarks",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val exception = result.exceptionOrNull()
                            Toast.makeText(
                                this@RecipeActivity,
                                "Failed to bookmark recipe: ${exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    if (recipe != null) {
                        val result = repository.deleteBookmarkById(recipe.idMeal)
                        if (result.isSuccess) {
                            Toast.makeText(
                                this@RecipeActivity,
                                "Recipe removed from bookmarks",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@RecipeActivity,
                                "Failed to remove bookmark",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }


//        binding.tlTabs.addTab(binding.tlTabs.newTab().setText(getString(R.string.recipe_tab_details)))
        binding.tlTabs.addTab(binding.tlTabs.newTab().setText(getString(R.string.recipe_tab_ingredients)))
        binding.tlTabs.addTab(binding.tlTabs.newTab().setText(getString(R.string.recipe_tab_direction)))

        // Tab Layout Handler
        binding.tlTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Hide all content views
//                binding.detailsContent.visibility = View.GONE
                binding.ingredientsContent.visibility = View.GONE
                binding.directionContent.visibility = View.GONE

                // Show the selected tab's content
                when (tab?.position) {
//                    0 -> binding.detailsContent.visibility = View.VISIBLE
                    0 -> binding.ingredientsContent.visibility = View.VISIBLE
                    1 -> binding.directionContent.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = View.NO_ID

        // Items Selection Handler
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    private fun formatInstructions(instructions: String): String {
        val instructionsList = instructions
            .split(".")
            .map { it.trim() }
            .filter{ it.isNotEmpty() }

        return instructionsList.mapIndexed { index, instruction ->
            "${index + 1}. $instruction"
        }.joinToString("\n")
    }

    private fun getIngredientsList(recipe: Recipe): List<String> {
        val list = mutableListOf<String>()

        val ingredientsList = listOf(
            recipe.strIngredient1, recipe.strIngredient2, recipe.strIngredient3,
            recipe.strIngredient4, recipe.strIngredient5, recipe.strIngredient6,
            recipe.strIngredient7, recipe.strIngredient8, recipe.strIngredient9,
            recipe.strIngredient10, recipe.strIngredient11, recipe.strIngredient12,
            recipe.strIngredient13, recipe.strIngredient14, recipe.strIngredient15,
            recipe.strIngredient16, recipe.strIngredient17, recipe.strIngredient18,
            recipe.strIngredient19, recipe.strIngredient20
        )

        val measuresList = listOf(
            recipe.strMeasure1, recipe.strMeasure2, recipe.strMeasure3,
            recipe.strMeasure4, recipe.strMeasure5, recipe.strMeasure6,
            recipe.strMeasure7, recipe.strMeasure8, recipe.strMeasure9,
            recipe.strMeasure10, recipe.strMeasure11, recipe.strMeasure12,
            recipe.strMeasure13, recipe.strMeasure14, recipe.strMeasure15,
            recipe.strMeasure16, recipe.strMeasure17, recipe.strMeasure18,
            recipe.strMeasure19, recipe.strMeasure20
        )

        for(i in ingredientsList.indices) {
            val ingredient = ingredientsList[i]
            val measure = measuresList[i]
            if (!ingredient.isNullOrBlank() && !measure.isNullOrBlank()) {
                list.add("• $ingredient $measure")
            }
        }
        return list
    }
}