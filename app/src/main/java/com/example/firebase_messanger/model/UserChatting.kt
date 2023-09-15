package com.example.firebase_messanger.model

import com.google.firebase.storage.StorageReference
import java.io.Serializable

data class UserChatting(
    val sender: User? = null,
    val receiver: User? = null,
    val time: String? = null,
    val isRead: Boolean? = false,
    val type: String? = null,
    val message: String? = null,
    val imageId: String? = null,
    val audioId: String? = null,
    val videoId: String? = null,
    val otherFilesId: String? = null
) : Serializable
