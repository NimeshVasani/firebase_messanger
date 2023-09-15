package com.example.firebase_messanger.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_messanger.R
import com.example.firebase_messanger.UserChattingActivity
import com.example.firebase_messanger.adapter.ChatMainAdapter
import com.example.firebase_messanger.databinding.FragmentChatBinding
import com.example.firebase_messanger.model.User
import com.example.firebase_messanger.viewmodels.firestore.FireStoreViewModel
import com.example.firebase_messanger.viewmodels.firestore.FireStoreViewModelFactory
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null

    private lateinit var chatMainAdapter: ChatMainAdapter

    private val binding get() = _binding!!


    @Inject
    lateinit var viewModelFactory: FireStoreViewModelFactory
    private val viewModel by viewModels<FireStoreViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpAdapter()

        viewModel.getUserList().observe(requireActivity()) {userList->
            chatMainAdapter.asyncDiffer.submitList(userList)

        }
        binding.scrollChatMain.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.scrollChatMain.scrollY

            if (scrollY >= 150) {
                binding.titleTextview.text = getString(R.string.title_chat)
                binding.toolbar.setBackgroundColor("#f9f9f9".toColorInt())
            } else {
                binding.titleTextview.text = ""
                binding.toolbar.setBackgroundColor(Color.WHITE)

            }
        }

        binding.chatSearch.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val inputMethodManager =
                    requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)
            }
        }

        chatMainAdapter.setOnItemClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    UserChattingActivity::class.java
                ).putExtra("user", it)
            )
        }

        return root

    }

    private fun setUpAdapter() {
        chatMainAdapter = ChatMainAdapter()
        binding.chatRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.chatRecycler.adapter = chatMainAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

