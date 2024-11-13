package com.lab5.wisebites.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.lab5.wisebites.R
import com.lab5.wisebites.model.Category

class CategoriesAdapter (private val categories: List<Category>):
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val button: MaterialButton): RecyclerView.ViewHolder(button){
        fun bind(category: Category) {
            button.text = category.name
            button.setIconResource(category.iconResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val button = LayoutInflater.
            from(parent.context).
            inflate(R.layout.item_categories, parent, false)
                as MaterialButton
        return CategoryViewHolder(button)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}