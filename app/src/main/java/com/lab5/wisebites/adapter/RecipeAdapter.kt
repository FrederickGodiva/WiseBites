package com.lab5.wisebites.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lab5.wisebites.BookmarkActivity
import com.lab5.wisebites.R
import com.lab5.wisebites.RecipeActivity
import com.lab5.wisebites.model.Recipe
import com.lab5.wisebites.repository.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeAdapter(
    private val context: Context,
    private var recipes: List<Recipe>,
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> (){
    private val firestoreRepository = FirestoreRepository()
    private var bookmarkedRecipeIds: List<Int> = emptyList()

    init {
        fetchBookmarkedRecipeIds()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchBookmarkedRecipeIds() {
        CoroutineScope(Dispatchers.Main).launch {
            val result = firestoreRepository.getBookmarkIds()
            if (result.isSuccess) {
                val bookmarkedRecipeIdsLong = result.getOrNull() ?: emptyList()
                bookmarkedRecipeIds = bookmarkedRecipeIdsLong.map { it.toInt() }
                notifyDataSetChanged()
            } else {
                val exception = result.exceptionOrNull()
                Log.e("RecipeAdapter", "Error fetching bookmark ids: ${exception?.message}")
            }
        }
    }

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val recipeName: TextView = view.findViewById(R.id.tv_recipe_name)
        private val recipeCategory: TextView = view.findViewById(R.id.tv_recipe_tags)
        private val recipeArea: TextView = view.findViewById(R.id.tv_recipe_style)
        private val recipeImage:ImageView = view.findViewById(R.id.iv_recipe)
        private val bookmarkButton:ImageButton = view.findViewById(R.id.iv_recipe_bookmark)

        fun bind(recipe: Recipe) {
            recipeName.text = recipe.strMeal
            recipeCategory.text = recipe.strCategory
            recipeArea.text = recipe.strArea
            Glide.with(itemView.context).load((recipe.strMealThumb)).into(recipeImage)

            bookmarkButton.isSelected = bookmarkedRecipeIds.contains(recipe.idMeal)

            itemView.setOnClickListener {
                val intent = Intent(context, RecipeActivity::class.java).apply {
                    putExtra("RECIPE_DATA", recipe)
                }
                context.startActivity(intent)
            }

            bookmarkButton.setOnClickListener {
                val isBookmarked = !bookmarkButton.isSelected
                bookmarkButton.isSelected = isBookmarked

                bookmarkedRecipeIds = if (isBookmarked) {
                    bookmarkedRecipeIds + recipe.idMeal
                } else {
                    bookmarkedRecipeIds.filterNot { it == recipe.idMeal }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val result = if (isBookmarked) {
                        firestoreRepository.addBookmark(recipe.idMeal)
                    } else {
                        firestoreRepository.deleteBookmarkById(recipe.idMeal)
                    }

                    if (result.isSuccess) {
                        val message = if (isBookmarked) {
                            "Recipe added to bookmarks"
                        } else {
                            "Recipe removed from bookmarks"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        (context as? BookmarkActivity)?.refreshBookmarkList()
                    } else {
                        val exception = result.exceptionOrNull()
                        val errorMessage = if (isBookmarked) {
                            "Failed to bookmark recipe: ${exception?.message}"
                        } else {
                            "Failed to remove bookmark: ${exception?.message}"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

                        bookmarkButton.isSelected = !isBookmarked
                        bookmarkedRecipeIds = bookmarkedRecipeIds.filterNot { it == recipe.idMeal }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipes, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes.toList()
        notifyDataSetChanged()
    }
}