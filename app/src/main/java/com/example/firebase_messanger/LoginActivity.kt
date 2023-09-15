package com.example.firebase_messanger

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.example.firebase_messanger.databinding.ActivityLoginBinding
import com.example.firebase_messanger.other.Resource
import com.example.firebase_messanger.other.Utils
import com.example.firebase_messanger.viewmodels.auth.AuthViewModel
import com.example.firebase_messanger.viewmodels.auth.AuthViewModelFactory
import com.example.firebase_messanger.viewmodels.firestore.FireStoreViewModel
import com.example.firebase_messanger.viewmodels.firestore.FireStoreViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var initialVisibleHeight = 0


    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory
    private val viewModel by viewModels<AuthViewModel> { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.contentLoadingProgressBar.visibility = View.GONE

        binding.loginPassword.viewTreeObserver.addOnGlobalLayoutListener(OnGlobalLayoutListener {
            val r = Rect()
            binding.loginPasswordEdit.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = binding.loginPasswordEdit.rootView.height
            val visibleHeight: Int = r.bottom - r.top
            if (initialVisibleHeight == 0) {
                initialVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }
            val heightDiff = screenHeight - visibleHeight
            if (heightDiff > screenHeight / 4) {
                // Keyboard is visible
                val translationY = -heightDiff / 2f
                binding.loginCard.animate().translationY(translationY).setDuration(200).start()
            } else {
                // Keyboard is not visible
                binding.loginCard.animate().translationY(0F).setDuration(200).start()
            }
        })

        val text = "Didn't Registered Yet? <font color='#0000FF'>SignUp</font> here...!!"

        binding.loginSignupLine.apply {
            this.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }

        binding.loginEmailEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!validateEmail(s.toString())) {
                    binding.loginEmail.boxStrokeColor = Color.RED
                } else {
                    binding.loginEmail.boxStrokeColor = Color.BLUE

                }
            }

        })
        binding.loginPasswordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!validatePassword(s.toString())) {
                    binding.loginPassword.boxStrokeColor = Color.RED
                } else {
                    binding.loginPassword.boxStrokeColor = Color.BLUE
                }
            }

        })
        binding.loginSignupLine.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmailEdit.text.toString()
            val password = binding.loginPasswordEdit.text.toString()
            if (validateEmail(email) && validatePassword(password))
                viewModel.signInUserWithFirebase(
                    email = email,
                    password = password
                ).observe(this) { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.contentLoadingProgressBar.visibility = View.GONE

                            Utils.currentUserUID = viewModel.getCurrentUser().uid
                            if (viewModel.getCurrentUser().phoneNumber.isNullOrBlank()) {
                                startActivity(Intent(this, PhoneRegistrationActivity::class.java))
                                finish()
                            } else {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }

                        }

                        is Resource.Error -> {
                            binding.contentLoadingProgressBar.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                resource.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        is Resource.Loading -> {
                            binding.contentLoadingProgressBar.visibility = View.VISIBLE
                        }
                    }

                }
        }
    }
}

private fun validateEmail(email: String): Boolean {
    val emailPattern = Pattern.compile(
        "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$"
    )
    return emailPattern.matcher(email).matches()
}

private fun validatePassword(password: String): Boolean {
    return password.length >= 6
}