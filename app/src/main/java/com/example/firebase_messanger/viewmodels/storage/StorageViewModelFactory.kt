package com.example.firebase_messanger.viewmodels.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebase_messanger.repositories.firestore.FireStoreRepository
import com.example.firebase_messanger.repositories.storage.StorageRepository
import com.example.firebase_messanger.viewmodels.firestore.FireStoreViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageViewModelFactory @Inject constructor(private val repository: StorageRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StorageViewModel(repository) as T
    }
}
