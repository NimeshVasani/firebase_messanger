package com.example.firebase_messanger.viewmodels.storage

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_messanger.other.Resource
import com.example.firebase_messanger.repositories.storage.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(private val repository: StorageRepository) :
    ViewModel() {

    fun saveImageInStorage(imageUri: Uri,pathName:String): MutableLiveData<Resource<String>> {
        return repository.saveImageInStorage(imageUri,pathName)
    }

    fun getStorageReference():FirebaseStorage {
        return repository.getStorageReference()
    }

}