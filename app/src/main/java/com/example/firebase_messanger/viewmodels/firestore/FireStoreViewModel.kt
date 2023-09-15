package com.example.firebase_messanger.viewmodels.firestore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_messanger.model.User
import com.example.firebase_messanger.repositories.firestore.FireStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FireStoreViewModel @Inject constructor(private val repository: FireStoreRepository) :
    ViewModel() {
    fun addIntoUsersList() = repository.addIntoUsersList()

    fun getUserList(): MutableLiveData<MutableList<User>> {
        return repository.getUserList()
    }
}