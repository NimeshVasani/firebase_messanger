package com.example.firebase_messanger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebase_messanger.databinding.ActivityProfileSetUpBinding
import com.example.firebase_messanger.viewmodels.auth.AuthViewModel
import com.example.firebase_messanger.viewmodels.auth.AuthViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileSetUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetUpBinding
    private var finalUserName = "User Name"

    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory
    private val authViewModel by viewModels<AuthViewModel> { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        finalUserName = authViewModel.getCurrentUser().displayName.toString()

        val finalImageUri = "android.resource://" + packageName + "/" + R.drawable.background

        binding.profileSetUpName.apply {
            setText(finalUserName)
            setOnFocusChangeListener { _, hasFocus ->
                binding.editCancelBtn.visibility = if (hasFocus) View.VISIBLE else View.GONE
                binding.editDoneBtn.visibility = if (hasFocus) View.VISIBLE else View.GONE
                binding.profileSetUpBack.visibility = if (hasFocus) View.GONE else View.VISIBLE
            }
        }

        binding.editDoneBtn.setOnClickListener {
            //hide key board
            finalUserName = binding.profileSetUpName.text.toString()
        }

        binding.editCancelBtn.setOnClickListener {
            binding.profileSetUpName.setText(finalUserName)
            //hide key board
        }
        authViewModel.getCurrentUser().photoUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.profileSetUpImgView)
        }

        binding.profileSetUpImgView.setOnClickListener {
            val cameraIntent = Intent(this, CameraActivity::class.java)
            cameraActivityLauncher.launch(cameraIntent)
        }

        binding.saveProfileBtn.setOnClickListener {
            authViewModel.updateProfile(
                displayName = finalUserName,
                photoUri = finalImageUri
            )
        }
    }

    private var cameraActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? ->
        if (result?.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                val imageUri = data.data.toString()
                Glide.with(this)
                    .load(Uri.parse(imageUri))
                    .into(binding.profileSetUpImgView)
                authViewModel.updateProfile(
                    displayName = binding.profileSetUpName.text.toString(),
                    photoUri = imageUri
                )

            }
        }
    }

}