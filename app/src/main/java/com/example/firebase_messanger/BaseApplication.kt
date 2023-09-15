package com.example.firebase_messanger

import android.app.Application
import androidx.camera.core.CameraX
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()


    }
}