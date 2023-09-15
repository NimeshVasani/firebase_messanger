package com.example.firebase_messanger.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.firebase_messanger.ProfileSetUpActivity
import com.example.firebase_messanger.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingBinding.inflate(layoutInflater)

        binding.profile.setOnClickListener {
            startActivity(Intent(requireContext(),ProfileSetUpActivity::class.java))
        }

        return binding.root
    }

}