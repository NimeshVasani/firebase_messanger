package com.example.firebase_messanger

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_messanger.databinding.ActivityRegistrationBinding
import com.example.firebase_messanger.other.Resource
import com.example.firebase_messanger.viewmodels.auth.AuthViewModel
import com.example.firebase_messanger.viewmodels.auth.AuthViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private var initialVisibleHeight = 0

    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory
    private val authViewModel by viewModels<AuthViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registrationPassword1!!.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            binding.registrationPassword1!!.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = binding.registrationPassword1!!.rootView.height
            val visibleHeight: Int = r.bottom - r.top
            if (initialVisibleHeight == 0) {
                initialVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }
            val heightDiff = screenHeight - visibleHeight
            if (heightDiff > screenHeight / 4) {
                // Keyboard is visible
                val translationY = -heightDiff / 2f
                binding.registrationCard!!.animate().translationY(translationY).setDuration(200)
                    .start()
            } else {
                // Keyboard is not visible
                binding.registrationCard!!.animate().translationY(0F).setDuration(200).start()
            }
        })

        binding.registrationBtn!!.setOnClickListener {
            authViewModel.registerUserWithFirebase(
                binding.registrationNameEdit!!.text.toString(),
                binding.registrationEmailEdit!!.text.toString(),
                binding.registrationPasswordEdit2!!.text.toString()
            ).observe(this){resource->
                when(resource){
                    is Resource.Success ->{
                        startActivity(Intent(this,LoginActivity::class.java))
                        finish()
                    }
                    is Resource.Loading ->{

                    }
                    is Resource.Error ->{
                        Toast.makeText(this,"Something Went Wrong",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}