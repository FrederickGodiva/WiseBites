package com.lab5.wisebites.utils

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lab5.wisebites.BookmarkActivity
import com.lab5.wisebites.HomeActivity
import com.lab5.wisebites.Profile
import com.lab5.wisebites.R
import com.lab5.wisebites.SearchActivity

object BottomNavigationHandler {
    fun handleNavigation(context: Context, bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.i_home -> {
                    context.startActivity(Intent(context, HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
                R.id.i_search -> {
                    context.startActivity(Intent(context, SearchActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
                R.id.i_bookmark -> {
                    context.startActivity(Intent(context, BookmarkActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
                R.id.i_profile -> {
                    context.startActivity(Intent(context, Profile::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
                else -> false
            }
        }
    }
}