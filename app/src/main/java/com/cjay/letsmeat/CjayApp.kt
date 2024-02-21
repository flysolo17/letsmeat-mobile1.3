package com.cjay.letsmeat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CjayApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}