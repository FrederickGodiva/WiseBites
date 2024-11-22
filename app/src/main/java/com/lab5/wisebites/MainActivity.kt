package com.lab5.wisebites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.lab5.wisebites.adapter.OnBoardingAdapter
import com.lab5.wisebites.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnBoardingAdapter.SkipClickListener {

    private lateinit var binding: ActivityMainBinding

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layouts = listOf(
            R.layout.activity_on_boarding1,
            R.layout.activity_on_boarding2,
            R.layout.activity_on_boarding3
        )

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = OnBoardingAdapter(layouts, this, googleSignInLauncher, this)

        // Initialize the dots and background color
        updateDots(0)

        // Set up page change callback to update dot icons
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
            }
        })
    }

    override fun onClicked() {
        // Set ViewPager to the last page
        binding.viewPager.currentItem = 2
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                Log.d("GoogleSignIn", "Authentication successful.")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Log.e("GoogleSignIn", "Authentication failed: ${it.message}")
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDots(position: Int) {
        val dot1 = findViewById<ImageButton>(R.id.dot_1)
        val dot2 = findViewById<ImageButton>(R.id.dot_2)
        val dot3 = findViewById<ImageButton>(R.id.dot_3)

        dot1.setBackgroundResource(if (position == 0) R.drawable.dot_white else R.drawable.dot)
        dot2.setBackgroundResource(if (position == 1) R.drawable.dot_red else R.drawable.dot)
        dot3.setBackgroundResource(if (position == 2) R.drawable.dot_red else R.drawable.dot)
    }
}