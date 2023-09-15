package com.example.firebase_messanger

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_messanger.databinding.ActivityPhoneRegistrationBinding
import com.example.firebase_messanger.viewmodels.auth.AuthViewModel
import com.example.firebase_messanger.viewmodels.auth.AuthViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhoneRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneRegistrationBinding

    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory
    private val viewModel by viewModels<AuthViewModel> { viewModelFactory }

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.initCallbacks(
            { credential -> onVerificationCompleted(credential) },
            { e -> onVerificationFailed(e) },
            { verificationId, token -> onCodeSent(verificationId, token) }
        )

        binding.sendOtpBtn.setOnClickListener {
            viewModel.verifyPhone(
                phoneNum = binding.phoneNumberEdit.text.toString(),
                activity = this
            )
        }

        binding.verifyOtpBtn.setOnClickListener {
            val phoneNumber = binding.phoneVerifyEdit.text.toString()
            if (phoneNumber.isNotEmpty()) {
                val credential: PhoneAuthCredential =
                    viewModel.verifyPhoneNumberWithCode(phoneNumber)

                viewModel.getCurrentUser().updatePhoneNumber(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Phone number update successful
                            Snackbar.make(
                                binding.root,
                                "Phone Number Linked Successfully",
                                Snackbar.LENGTH_LONG
                            ).show()
                            startActivity(
                                Intent(
                                    this@PhoneRegistrationActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            // Phone number update failed
                            Snackbar.make(
                                binding.root,
                                task.result.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun onVerificationCompleted(credential: PhoneAuthCredential) {
        // Handle verification success, e.g., sign in with credential

    }

    private fun onVerificationFailed(e: FirebaseException) {
        // Handle verification failure
        Snackbar.make(
            binding.root,
            e.message.toString(),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        // Handle code sent event, e.g., navigate to the code verification screen

        binding.sendOtpBtn.visibility = View.GONE
        binding.phoneNumber.visibility = View.GONE

        binding.verifyOtpBtn.visibility = View.VISIBLE
        binding.phoneVerify.visibility = View.VISIBLE
    }
}