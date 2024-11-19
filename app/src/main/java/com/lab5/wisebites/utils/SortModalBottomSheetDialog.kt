package com.lab5.wisebites.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lab5.wisebites.databinding.SortBottomsheetBinding

class SortModalBottomSheetDialog (
    private val currentSelection: String,
    private val listener: SortOptionListener
) : BottomSheetDialogFragment() {
    private lateinit var binding: SortBottomsheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SortBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener for the close button
        binding.btnClose.setOnClickListener{ dismiss() }

        val radioButtons = listOf(
            binding.rbRecipeAtoz, binding.rbRecipeZtoa,
            binding.rbCategoryAtoz, binding.rbCategoryZtoa,
            binding.rbAreaAtoz, binding.rbAreaZtoa)

        setRadioButtonChecked(currentSelection)

        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                val selectedSortOption = radioButton.tag?.toString() ?: ""
                listener.onSortOptionSelected(selectedSortOption)
                dismiss()
            }
        }
    }

    private fun setRadioButtonChecked(selection: String) {
        when (selection) {
            "Recipe A to Z" -> binding.rbRecipeAtoz.isChecked = true
            "Recipe Z to A" -> binding.rbRecipeZtoa.isChecked = true
            "Category A to Z" -> binding.rbCategoryAtoz.isChecked = true
            "Category Z to A" -> binding.rbCategoryZtoa.isChecked = true
            "Area A to Z" -> binding.rbAreaAtoz.isChecked = true
            "Area Z to A" -> binding.rbAreaZtoa.isChecked = true
        }
    }
}

interface SortOptionListener {
    fun onSortOptionSelected(option: String)
}