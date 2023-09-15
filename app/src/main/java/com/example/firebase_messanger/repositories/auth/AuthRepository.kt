package com.example.firebase_messanger.repositories.auth

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.firebase_messanger.model.User
import com.example.firebase_messanger.other.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var verificationId: String


    fun registerUserWithFirebase(
        displayName: String,
        email: String,
        password: String
    ): MutableLiveData<Resource<User>> {
        val authenticationData = MutableLiveData<Resource<User>>()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isNewUser: Boolean = task.result.additionalUserInfo?.isNewUser ?: false
                    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                    firebaseUser?.let {
                        firebaseUser.updateProfile(buildProfileUpdateRequest(displayName, null))
                            .addOnSuccessListener {
                                val uId = firebaseUser.uid
                                val name = firebaseUser.displayName
                                val userEmail = firebaseUser.email
                                val user = User(name!!, userEmail!!, uId)
                                user.isNew = isNewUser
                                authenticationData.value = Resource.Success(user)
                            }
                    }
                } else {
                    Timber.d("firebase User is null ${task.exception?.message}")
                }
            }
        return authenticationData
    }

    fun signInWithFirebase(email: String, password: String): MutableLiveData<Resource<User>> {
        val authenticationData = MutableLiveData<Resource<User>>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                authenticationData.value = Resource.Loading()
                if (task.isSuccessful) {
                    val isNewUser: Boolean = task.result.additionalUserInfo?.isNewUser ?: false
                    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                    firebaseUser?.let {
                        val uId = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val userEmail = firebaseUser.email
                        val user = User(name ?: "", userEmail!!, uId)
                        user.isNew = isNewUser
                        authenticationData.value = Resource.Success(user)
                    }
                } else {
                    Timber.d("firebase User is null ${task.exception?.message}")
                    authenticationData.value = Resource.Error(task.exception?.message.toString())
                }
            }
        return authenticationData
    }

    fun updateProfile(displayName: String, photoUri: String?): MutableLiveData<Resource<Boolean>> {
        val isUpdated: MutableLiveData<Resource<Boolean>> = MutableLiveData()
        firebaseAuth.currentUser!!.updateProfile(
            buildProfileUpdateRequest(
                displayName = displayName,
                photoUri = photoUri
            )
        ).addOnCompleteListener { task ->
            if (task.isComplete) {
                if (task.isSuccessful) {
                    isUpdated.value = Resource.Success(true)
                } else {
                    isUpdated.value = Resource.Error(task.exception?.message.toString())
                }
            } else {
                isUpdated.value = Resource.Loading()
            }
        }
        return isUpdated
    }

    fun checkLoginSession(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun checkPhoneSession(): Boolean {
        return firebaseAuth.currentUser!!.phoneNumber.isNullOrBlank()
    }

    fun getCurrentUser(): FirebaseUser {
        return firebaseAuth.currentUser!!
    }


    fun initCallbacks(
        onVerificationCompleted: (PhoneAuthCredential) -> Unit,
        onVerificationFailed: (FirebaseException) -> Unit,
        onCodeSent: (String, PhoneAuthProvider.ForceResendingToken) -> Unit
    ) {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onVerificationCompleted(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onVerificationFailed(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@AuthRepository.verificationId = verificationId
                onCodeSent(verificationId, token)
            }
        }
    }

    fun verifyPhone(
        phoneNum: String,
        activity: Activity
    ) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNum)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithCode(code: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, code)
    }

    private fun buildProfileUpdateRequest(
        displayName: String,
        photoUri: String?
    ): UserProfileChangeRequest {
        return UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .setPhotoUri(Uri.parse(photoUri))
            .build()
    }

}