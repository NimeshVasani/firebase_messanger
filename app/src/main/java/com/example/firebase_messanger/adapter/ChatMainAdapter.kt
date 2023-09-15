package com.example.firebase_messanger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.firebase_messanger.databinding.ItemChatMainBinding
import com.example.firebase_messanger.model.User

class ChatMainAdapter : RecyclerView.Adapter<ChatMainAdapter.ChatMainHolder>(

) {
    private lateinit var binding: ItemChatMainBinding
    private var differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
    var asyncDiffer: AsyncListDiffer<User> = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMainHolder {
        binding = ItemChatMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatMainHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ChatMainHolder, position: Int) {
        val user = asyncDiffer.currentList[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(user) }
        }

        holder.apply {
            userName.text = user.name ?: "UnKnown"
        }
    }

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }


    inner class ChatMainHolder(itemView: View) : ViewHolder(itemView) {
        val userName = binding.tvChatMainTitle
        val userLastChat = binding.tvChatMainDesc
        val userProfilePic = binding.imgChatMain
    }

}