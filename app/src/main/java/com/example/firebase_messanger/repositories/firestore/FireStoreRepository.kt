package com.example.firebase_messanger.repositories.firestore

import androidx.lifecycle.MutableLiveData
import com.example.firebase_messanger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import javax.inject.Inject

class FireStoreRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) {
    private val currentUser = firebaseAuth.currentUser

    fun addIntoUsersList() {
        if (currentUser != null) {
            val user = User(currentUser.displayName ?: "", currentUser.email!!, currentUser.uid)
            fireStore.collection("users").document(currentUser.uid)
                .set(user)
                .addOnSuccessListener {
                    Timber.d("User Added Successfully...")
                }
                .addOnFailureListener { e ->
                    Timber.tag("fireStore").e(e.message.toString())
                }
        }
    }

    fun getUserList(): MutableLiveData<MutableList<User>> {

        val userList: MutableLiveData<MutableList<User>> = MutableLiveData()
        val list: MutableList<User> = mutableListOf()
        if (currentUser != null) {
            fireStore.collection("users").get().addOnSuccessListener { querySnapshot ->
                for (user in querySnapshot) {
                    val uid = user.id
                    val name = user.getString("name")
                    val email = user.getString("email")
                    list.add(
                        User(
                            name = name.toString(),
                            email = email.toString(),
                            uId = uid
                        )
                    )
                }

                list.remove(
                    User(
                        currentUser.displayName!!,
                        currentUser.email!!,
                        currentUser.uid
                    )
                )
                userList.value = list
            }
        }
        return userList
    }
}