package com.example.firebase_messanger.viewmodels.auth

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_messanger.model.User
import com.example.firebase_messanger.other.Resource
import com.example.firebase_messanger.repositories.auth.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun signInUserWithFirebase(email: String, password: String): MutableLiveData<Resource<User>> {
        return authRepository.signInWithFirebase(email = email, password = password)
    }

    fun registerUserWithFirebase(
        displayName: String,
        email: String,
        password: String
    ): MutableLiveData<Resource<User>> {
        return authRepository.registerUserWithFirebase(displayName, email, password)
    }

    fun updateProfile(displayName: String, photoUri: String): MutableLiveData<Resource<Boolean>> {
        return authRepository.updateProfile(displayName = displayName, photoUri = photoUri)
    }

    fun checkLoginSession(): Boolean {
        return authRepository.checkLoginSession()
    }

    fun checkPhoneSession(): Boolean {
        return authRepository.checkPhoneSession()
    }

    fun getCurrentUser(): FirebaseUser {
        return authRepository.getCurrentUser()
    }


    fun verifyPhone(
        phoneNum: String,
        activity: Activity
    ) {
        authRepository.verifyPhone(phoneNum = phoneNum, activity = activity)
    }

    fun initCallbacks(
        onVerificationCompleted: (PhoneAuthCredential) -> Unit,
        onVerificationFailed: (FirebaseException) -> Unit,
        onCodeSent: (String, PhoneAuthProvider.ForceResendingToken) -> Unit
    ) {
        authRepository.initCallbacks(onVerificationCompleted, onVerificationFailed, onCodeSent)
    }

    fun verifyPhoneNumberWithCode(code: String): PhoneAuthCredential {
        return authRepository.verifyPhoneNumberWithCode(code)
    }


}