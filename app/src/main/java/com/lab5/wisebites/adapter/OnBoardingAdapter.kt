package com.lab5.wisebites.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.lab5.wisebites.OnBoarding4
import com.lab5.wisebites.OnBoarding5
import com.lab5.wisebites.R

class OnBoardingAdapter (
    private val layouts: List<Int>,
    private val skipClickListener: SkipClickListener,
    private val googleSignInLauncher: ActivityResultLauncher<Intent>,
    context: Context
) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {
    interface SkipClickListener {
        fun onClicked()
    }

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    )

    inner class OnBoardingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return OnBoardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        // Attach the skip button listener only on the first page
        when (position) {
            0 -> {
                holder.itemView.findViewById<TextView>(R.id.skipButton)?.apply {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        skipClickListener.onClicked()
                    }
                }
            }
            1 -> {
                holder.itemView.findViewById<MaterialButton>(R.id.getStartedButton)?.setOnClickListener{
                    skipClickListener.onClicked()
                }
            }
            2 -> {
                holder.itemView.findViewById<TextView>(R.id.tv_sign_in)?.apply {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener{
                        val intent = Intent(context, OnBoarding4::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }

                holder.itemView.findViewById<TextView>(R.id.btn_google)?.apply {
                    setOnClickListener {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    }
                }

                holder.itemView.findViewById<MaterialButton>(R.id.btn_signup)?.apply {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener{
                        val intent = Intent(context, OnBoarding5::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = layouts[position]

    override fun getItemCount(): Int = layouts.size
}