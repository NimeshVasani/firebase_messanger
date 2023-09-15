package com.example.firebase_messanger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.firebase_messanger.databinding.ItemUserChatReceiveBinding
import com.example.firebase_messanger.databinding.ItemUserChatSendBinding
import com.example.firebase_messanger.model.UserChatting
import com.example.firebase_messanger.other.Utils.currentUserUID
import com.google.firebase.storage.FirebaseStorage


class UserChattingAdapter :
    RecyclerView.Adapter<UserChattingAdapter.UserChattingViewHolder>() {

    private lateinit var binding1: ItemUserChatSendBinding
    private lateinit var binding2: ItemUserChatReceiveBinding
    private lateinit var firebaseStorage: FirebaseStorage

    private val differCallback = object : DiffUtil.ItemCallback<UserChatting>() {

        override fun areItemsTheSame(oldItem: UserChatting, newItem: UserChatting): Boolean {
            return oldItem.message == newItem.message
        }

        override fun areContentsTheSame(oldItem: UserChatting, newItem: UserChatting): Boolean {
            return oldItem == newItem
        }
    }

    var asyncDiffer: AsyncListDiffer<UserChatting> = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChattingViewHolder {
        binding1 =
            ItemUserChatSendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding2 =
            ItemUserChatReceiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (viewType == 1) {
            UserChattingViewHolder(binding1)

        } else {
            UserChattingViewHolder(binding2)
        }
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (asyncDiffer.currentList[position].type) {
            "String" -> {
                if (asyncDiffer.currentList[position].sender!!.uId != currentUserUID)
                    2
                else
                    1
            }

            else ->
                1
        }
    }

    override fun onBindViewHolder(holder: UserChattingViewHolder, position: Int) {

        val userChatting = asyncDiffer.currentList[position]
        if (getItemViewType(position) == 1) {
            if (userChatting.imageId.isNullOrEmpty()) {
                holder.apply {
                    sendMessage.visibility = View.VISIBLE
                    sendMsgImg.visibility = View.GONE
                    sendMessage.text = userChatting.message
                    sendMsgTime.text = userChatting.time
                }
            } else {
                holder.apply {
                    sendMessage.visibility = View.GONE
                    sendMsgImg.visibility = View.VISIBLE
                    sendMsgTime.text = userChatting.time
                    Glide.with(binding1.root.context)
                        .load(firebaseStorage.getReferenceFromUrl(userChatting.imageId))
                        .into(sendMsgImg)

                }
            }

        } else {
            if (userChatting.imageId.isNullOrEmpty()) {
                holder.apply {
                    receiveMessage.text = userChatting.message
                    receiveMsgTime.text = userChatting.time
                }
            } else {
                holder.apply {
                    receiveMessage.visibility = View.GONE
                    receiveMessage.visibility = View.VISIBLE
                    receiveMsgTime.text = userChatting.time

                    Glide.with(binding2.root.context)
                        .load(firebaseStorage.getReferenceFromUrl(userChatting.imageId))
                        .into(receiveMsgImg)

                }
            }
        }

    }

    inner class UserChattingViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val sendMessage = binding1.sendMessage
        val receiveMessage = binding2.receiveMessage
        val sendMsgTime = binding1.sendMsgTime
        val receiveMsgTime = binding2.receiveMsgTime
        val sendMsgImg = binding1.sendMsgImg
        val receiveMsgImg = binding2.receiveMsgImg
    }

    fun getStorageReference(firebaseStorage: FirebaseStorage) {
        this.firebaseStorage = firebaseStorage
    }
}