package com.lab5.wisebites

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.databinding.ActivityBookmarkBinding
import com.lab5.wisebites.utils.BottomNavigationHandler
import com.lab5.wisebites.utils.SortModalBottomSheetDialog
import com.lab5.wisebites.utils.SortOptionListener

class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding
    private var lastSelectedSortOption: String = "Recipe A to Z"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSort.setOnClickListener {
            val sortModalBottomSheet = SortModalBottomSheetDialog(lastSelectedSortOption, object: SortOptionListener {
                override fun onSortOptionSelected(option: String) {
                    lastSelectedSortOption = option
                    // sortRecipes(option)
                }
            })
            sortModalBottomSheet.show(supportFragmentManager, sortModalBottomSheet.tag)
        }

        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_bookmark

        // Items Selection Handler
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    override fun onRestart() {
        super.onRestart()
        // Set the default selected item in Navigation Menu
        binding.bnMenu.selectedItemId = R.id.i_bookmark
    }
}