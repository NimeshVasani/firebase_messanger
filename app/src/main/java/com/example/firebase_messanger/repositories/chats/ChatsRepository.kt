package com.example.firebase_messanger.repositories.chats

import androidx.lifecycle.MutableLiveData
import com.example.firebase_messanger.model.UserChatting
import com.example.firebase_messanger.other.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import javax.inject.Inject

class ChatsRepository @Inject constructor(database: FirebaseDatabase) {
    private val chatRef = database.getReference("chats")

    fun saveChatInRealTimeDatabase(
        userChatting: UserChatting, userId1: String, userId2: String
    ): MutableLiveData<Resource<String>> {

        val chatKey1 = userId1 + userId2
        val chatKey2 = userId2 + userId1
        val chatData: MutableLiveData<Resource<String>> = MutableLiveData()
        chatRef.child(chatKey1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatRef.child(chatKey1).push().apply {
                        setValue(userChatting)
                            .addOnSuccessListener {
                                chatData.value = Resource.Success(key.toString())
                            }.addOnFailureListener {
                                chatData.value = Resource.Error(it.message.toString())
                            }
                    }

                } else {
                    chatRef.child(chatKey2)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                chatRef.child(chatKey2).push().apply {
                                    setValue(userChatting)
                                        .addOnSuccessListener {
                                            chatData.value = Resource.Success(key.toString())
                                        }.addOnFailureListener {
                                            chatData.value = Resource.Error(it.message.toString())
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return chatData
    }

    fun getChatFromRealtimeDatabase(
        userId1: String, userId2: String
    ): MutableLiveData<MutableList<UserChatting>> {

        val chatKey1 = userId1 + userId2
        val chatKey2 = userId2 + userId1

        val userChattingList: MutableLiveData<MutableList<UserChatting>> = MutableLiveData()
        val list = mutableListOf<UserChatting>()

        chatRef.child(chatKey1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    list.clear()

                    for (chatSnapshot in snapshot.children) {
                        list.add(chatSnapshot.getValue<UserChatting>()!!)
                        // Do something with each chat
                    }
                } else {
                    chatRef.child(chatKey2)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                list.clear()

                                for (chatSnapshot in snapshot.children) {
                                    list.add(chatSnapshot.getValue<UserChatting>()!!)

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userChattingList.value = list
        return userChattingList
    }

    fun loadLast20Chats(
        userId1: String,
        userId2: String
    ): MutableLiveData<MutableList<UserChatting>> {

        val chatKey1 = userId1 + userId2
        val chatKey2 = userId2 + userId1

        val userChattingList: MutableLiveData<MutableList<UserChatting>> = MutableLiveData()
        val list = mutableListOf<UserChatting>()


        chatRef.child(chatKey1).limitToLast(20).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    if (task.result != null) {
                        for (snapshot in task.result.children) {
                            snapshot.getValue<UserChatting>()?.let { list.add(it) }
                        }
                    }
                } else {
                    chatRef.child(chatKey2).limitToLast(20).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result != null)
                                for (snapshot in task.result.children) {
                                    snapshot.getValue<UserChatting>()?.let { list.add(it) }
                                }
                        }
                    }
                }
            }
        }
        userChattingList.value = list
        return userChattingList
    }


}