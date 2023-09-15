package com.example.firebase_messanger

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebase_messanger.databinding.ActivityMainBinding
import com.example.firebase_messanger.other.Utils.currentUserUID
import com.example.firebase_messanger.viewmodels.auth.AuthViewModel
import com.example.firebase_messanger.viewmodels.auth.AuthViewModelFactory
import com.example.firebase_messanger.viewmodels.firestore.FireStoreViewModel
import com.example.firebase_messanger.viewmodels.firestore.FireStoreViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory
    private val viewModel by viewModels<AuthViewModel> { viewModelFactory }


    @Inject
    lateinit var fireStoreViewModelFactory: FireStoreViewModelFactory
    private val fireStoreViewModel by viewModels<FireStoreViewModel> { fireStoreViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStoreViewModel.addIntoUsersList()
        if (!viewModel.checkLoginSession()) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        } else {
            if (viewModel.checkPhoneSession()) {
                startActivity(Intent(this@MainActivity, PhoneRegistrationActivity::class.java))
                currentUserUID = viewModel.getCurrentUser().uid
                finish()
            } else {
                currentUserUID = viewModel.getCurrentUser().uid
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.navView.setupWithNavController(navController)

    }
}