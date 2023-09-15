package com.example.firebase_messanger.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class User(
    var name: String?=null,
    var email: String?=null,
    var uId: String?=null
) : Serializable {

    @Exclude
    var isAuthenticated: Boolean = false

    @Exclude
    var isNew: Boolean = false

    @Exclude
    var isCreated: Boolean = false
}
