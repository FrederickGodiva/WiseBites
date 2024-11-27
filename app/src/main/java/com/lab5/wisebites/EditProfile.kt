package com.lab5.wisebites

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.lab5.wisebites.databinding.ActivityEditProfileBinding
import com.lab5.wisebites.utils.BottomNavigationHandler

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveChanges.setOnClickListener{
            finish()
        }

        binding.btnCancelChanges.setOnClickListener {
            finish()
        }

        binding.editPassword.setOnClickListener{
            val isVisible = binding.editPasswordField.visibility == View.VISIBLE
            binding.editPasswordField.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        binding.editPhotoProfile.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }


        //        BELOM SIAP - JANGAN DISENTUH
//        binding.confirmPassword.editText?.addTextChangedListener(object : TextWatcher) {
//            override fun afterTextChanged(s: Editable?){
//                validatePasswords()
//            }
//        }

//        binding.newPassword.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                binding.confirmPassword.setText(s.toString())
//            }
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })

        binding.bnMenu.selectedItemId = R.id.i_profile
        BottomNavigationHandler.handleNavigation(this, binding.bnMenu)
    }

    override fun onRestart() {
        super.onRestart()
        binding.bnMenu.selectedItemId = R.id.i_profile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null){
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                binding.lisaPhotoProfile.setImageBitmap(bitmap)
                }
        }
    }
}