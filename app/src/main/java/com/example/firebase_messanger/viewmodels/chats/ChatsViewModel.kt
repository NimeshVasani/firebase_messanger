package com.example.firebase_messanger.viewmodels.chats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_messanger.model.UserChatting
import com.example.firebase_messanger.other.Resource
import com.example.firebase_messanger.repositories.chats.ChatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(private val repository: ChatsRepository) : ViewModel() {
    fun saveChatInRealTimeDatabase(
        userChatting: UserChatting,
        userId1: String,
        userId2: String
    ): MutableLiveData<Resource<String>> {
        return repository.saveChatInRealTimeDatabase(userChatting, userId1, userId2)
    }

    fun loadLast20Chats(
        userId1: String,
        userId2: String
    ): MutableLiveData<MutableList<UserChatting>> {
        return repository.getChatFromRealtimeDatabase(userId1, userId2)

    }
}