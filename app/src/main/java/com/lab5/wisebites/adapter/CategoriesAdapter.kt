package com.lab5.wisebites.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.lab5.wisebites.R
import com.lab5.wisebites.model.Category

class CategoriesAdapter (
    private val categories: List<Category>,
    private val onCategoryClick: (String?) -> Unit
): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private var selectedPosition: Int? = null

    inner class CategoryViewHolder(val button: MaterialButton): RecyclerView.ViewHolder(button){
        fun bind(category: Category, isSelected: Boolean) {
            button.text = category.name
            button.setIconResource(category.iconResId)

            // set button background tint color
            val color = if (isSelected) {
                ContextCompat.getColor(button.context, R.color.primary)
            } else {
                ContextCompat.getColor(button.context, R.color.tertiary)
            }
            button.backgroundTintList = ColorStateList.valueOf(color)

            // on click listener
            button.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition ?: -1)
                    notifyItemChanged(adapterPosition)
                    onCategoryClick(category.name)
                } else {
                    // If the same button is pressed again, reset state
                    selectedPosition = null
                    notifyItemChanged(adapterPosition)
                    onCategoryClick(null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val button = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_categories, parent, false)
                as MaterialButton
        return CategoryViewHolder(button)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val isSelected = selectedPosition == position
        holder.bind(categories[position], isSelected)
    }

    override fun getItemCount(): Int = categories.size
}