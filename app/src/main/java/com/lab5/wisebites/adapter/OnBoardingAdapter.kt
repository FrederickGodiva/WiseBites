package com.lab5.wisebites.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lab5.wisebites.R

class OnBoardingAdapter (
    private val layouts: List<Int>,
    private val skipClickListener: SkipClickListener
): RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {
    interface SkipClickListener {
        fun onSkipClicked()
    }

    inner class OnBoardingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return OnBoardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        // Attach the skip button listener only on the first page
        if (position == 0) {
            holder.itemView.findViewById<TextView>(R.id.skipButton)?.apply {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    Log.d("OnBoardingAdapter", "Skip button clicked")
                    skipClickListener.onSkipClicked()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = layouts[position]

    override fun getItemCount(): Int = layouts.size
}