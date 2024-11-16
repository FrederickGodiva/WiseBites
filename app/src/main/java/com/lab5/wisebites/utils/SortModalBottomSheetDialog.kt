package com.lab5.wisebites.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lab5.wisebites.databinding.SortBottomsheetBinding

class SortModalBottomSheetDialog : BottomSheetDialogFragment() {
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
        binding.btnClose.setOnClickListener{
            dismiss()
        }

        val radioButtons = listOf(
            binding.rbRecipeAtoz, binding.rbRecipeZtoa,
            binding.rbCategoryAtoz, binding.rbCategoryZtoa,
            binding.rbAreaAtoz, binding.rbAreaZtoa)

        // Handle sorting selection
        radioButtons.forEach { radioButton ->
            radioButton.setOnCheckedChangeListener{ _, isChecked ->
                if(isChecked) {
                    // Reset all RadioButton except for the one that is checked
                    radioButtons.filter { it != radioButton }.forEach { it.isChecked = false }

                    val selectedSortOption = radioButton.text.toString()
                    sortSelectionHandler(selectedSortOption)
                    dismiss()
                }
            }
        }
    }
}

// Function for all sorting selection handler
private fun sortSelectionHandler(selection: String) {
    // TODO:Sort Logic
}