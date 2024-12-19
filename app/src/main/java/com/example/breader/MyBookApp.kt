package com.example.breader

import android.app.Application
import com.example.breader.Utils.loadAdUnits
import com.example.breader.Utils.loadInterstitialAdIfNull
import com.google.firebase.database.FirebaseDatabase

class MyBookApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}