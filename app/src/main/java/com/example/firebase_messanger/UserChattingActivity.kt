package com.example.firebase_messanger

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_messanger.adapter.UserChattingAdapter
import com.example.firebase_messanger.databinding.ActivityUserChattingBinding
import com.example.firebase_messanger.databinding.AttachFileMenuBinding
import com.example.firebase_messanger.model.User
import com.example.firebase_messanger.model.UserChatting
import com.example.firebase_messanger.other.Resource
import com.example.firebase_messanger.viewmodels.auth.AuthViewModel
import com.example.firebase_messanger.viewmodels.auth.AuthViewModelFactory
import com.example.firebase_messanger.viewmodels.chats.ChatsViewModel
import com.example.firebase_messanger.viewmodels.chats.ChatsViewModelFactory
import com.example.firebase_messanger.viewmodels.storage.StorageViewModel
import com.example.firebase_messanger.viewmodels.storage.StorageViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class UserChattingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserChattingBinding
    private lateinit var menuBinding: AttachFileMenuBinding
    private lateinit var adapter: UserChattingAdapter
    private lateinit var sender: User
    private lateinit var receiver: User

    @Inject
    lateinit var viewModelFactory: ChatsViewModelFactory
    private val viewModel by viewModels<ChatsViewModel> { viewModelFactory }

    @Inject
    lateinit var authViewModelFactory: AuthViewModelFactory
    private val authViewModel by viewModels<AuthViewModel> { authViewModelFactory }

    @Inject
    lateinit var storageViewModelFactory: StorageViewModelFactory
    private val storageViewModel by viewModels<StorageViewModel> { storageViewModelFactory }

    private lateinit var lastChats: MutableList<UserChatting>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserChattingBinding.inflate(layoutInflater)
        window.statusBarColor = getColor(R.color.toolbar)
        setContentView(binding.root)
        setUpAdapter()
        adapter.getStorageReference(storageViewModel.getStorageReference())
        val menuInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        menuBinding = AttachFileMenuBinding.inflate(menuInflater)


        receiver = intent.getSerializable("user", User::class.java)
        sender = User(
            name = authViewModel.getCurrentUser().displayName ?: "",
            email = authViewModel.getCurrentUser().email!!,
            uId = authViewModel.getCurrentUser().uid
        )

        binding.tvUserName.text = receiver.name.toString()

        viewModel.loadLast20Chats(sender.uId!!, receiver.uId!!).observe(this) { data ->
            lastChats = data
            adapter.asyncDiffer.submitList(lastChats)
            binding.recyclerUserChatting.postDelayed({
                val lastItemPosition = adapter.asyncDiffer.currentList.size - 1
                binding.recyclerUserChatting.onScrolled(0, 10)
                binding.recyclerUserChatting.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                    if (bottom < oldBottom) {
                        binding.recyclerUserChatting.scrollBy(0, oldBottom - bottom)
                        binding.recyclerUserChatting.scrollToPosition(lastItemPosition)

                    }
                }
                binding.recyclerUserChatting.scrollToPosition(lastItemPosition)

            }, 200)

        }


        binding.edtSendUserChat.doOnTextChanged { text, _, _, _ ->

            binding.sendUserChatBtn.visibility =
                if (text.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.cameraUserChat.visibility =
                if (text.isNullOrBlank()) View.VISIBLE else View.GONE
            binding.recordUserChat.visibility =
                if (text.isNullOrBlank()) View.VISIBLE else View.GONE
        }

        binding.sendUserChatBtn.setOnClickListener {
            if (!binding.edtSendUserChat.text.isNullOrBlank()) {
                val userId1 = sender.uId!!
                val userid2 = receiver.uId
                val userChatting = UserChatting(
                    sender = sender,
                    receiver = receiver,
                    time = getCurrentTime(),
                    isRead = false,
                    type = "String",
                    message = binding.edtSendUserChat.text.toString(),
                    imageId = null,
                    audioId = null,
                    videoId = null,
                    otherFilesId = null
                )
                saveChatInRealtimeDatabase(
                    userChatting = userChatting,
                    sender = userId1,
                    receiver = userid2!!
                )

                binding.recyclerUserChatting.postDelayed({
                    val lastItemPosition = adapter.itemCount - 1
                    if (lastItemPosition >= 0) {
                        binding.recyclerUserChatting.scrollToPosition(lastItemPosition)
                    }

                }, 200)
                binding.edtSendUserChat.setText("")
            }
        }

        binding.userChattingBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.sendDocsUserChat.setOnClickListener {
            val popupWindow = PopupWindow(
                menuBinding.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.animationStyle = R.style.popup_window_animation
            popupWindow.showAtLocation(binding.root, Gravity.START + Gravity.BOTTOM, 0, 0)
        }
        binding.cameraUserChat.setOnClickListener {
            val cameraIntent = Intent(this, CameraActivity::class.java)
            cameraActivityLauncher.launch(cameraIntent)
        }

    }

    private fun setUpAdapter() {
        adapter = UserChattingAdapter()
        binding.recyclerUserChatting.adapter = adapter
        binding.recyclerUserChatting.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        binding.recyclerUserChatting.layoutManager = layoutManager


    }

    private fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializableExtra(key, m_class)!!
        else
            this.getSerializableExtra(key) as T
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)

        return String.format("%02d:%02d", hours, minutes)
    }

    override fun onResume() {
        super.onResume()
        adapter.asyncDiffer.submitList(lastChats)
        adapter.notifyDataSetChanged()
        binding.recyclerUserChatting.postDelayed({
            val lastItemPosition = adapter.asyncDiffer.currentList.size - 1
            binding.recyclerUserChatting.onScrolled(0, 10)
            binding.recyclerUserChatting.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    binding.recyclerUserChatting.scrollBy(0, oldBottom - bottom)
                    binding.recyclerUserChatting.scrollToPosition(lastItemPosition)

                }
            }
            binding.recyclerUserChatting.scrollToPosition(lastItemPosition)

        }, 200)
    }

    private var cameraActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? ->
        if (result?.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                val imageUri = data.data.toString()

                saveImageInFirebase(
                    imageUri = Uri.parse(imageUri),
                    sender = sender,
                    receiver = receiver
                )


            }
        }
    }

    private fun saveImageInFirebase(imageUri: Uri, sender: User, receiver: User) {
        val pathName = "Image"
        storageViewModel.saveImageInStorage(imageUri, pathName).observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    saveChatInRealtimeDatabase(
                        userChatting = UserChatting(
                            sender = sender,
                            receiver = receiver,
                            time = getCurrentTime(),
                            type = "Image",
                            message = "",
                            imageId = resource.data!!.toString(),

                            ),
                        sender = sender.uId!!,
                        receiver = receiver.uId!!
                    )
                }

                is Resource.Error -> {
                    Toast.makeText(this, resource.message.toString(), Toast.LENGTH_LONG).show()
                }

                is Resource.Loading -> {

                }

                else -> {}
            }
        }
    }


    private fun saveChatInRealtimeDatabase(
        userChatting: UserChatting,
        sender: String,
        receiver: String
    ) {
        viewModel.saveChatInRealTimeDatabase(userChatting, sender, receiver)
            .observe(this) { resources ->
                when (resources) {
                    is Resource.Success -> {
                        adapter.asyncDiffer.submitList(lastChats)
                        binding.recyclerUserChatting.postDelayed({
                            val lastItemPosition = adapter.asyncDiffer.currentList.size - 1
                            binding.recyclerUserChatting.onScrolled(0, 10)
                            binding.recyclerUserChatting.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                                if (bottom < oldBottom) {
                                    binding.recyclerUserChatting.scrollBy(0, oldBottom - bottom)
                                    binding.recyclerUserChatting.scrollToPosition(lastItemPosition)

                                }
                            }
                            binding.recyclerUserChatting.scrollToPosition(lastItemPosition)

                        }, 200)
                    }

                    is Resource.Error -> {

                    }

                    is Resource.Loading -> {

                    }

                    else -> {

                    }
                }
            }
    }

}


