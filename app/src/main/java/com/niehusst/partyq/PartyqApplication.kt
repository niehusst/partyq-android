package com.niehusst.partyq

import android.app.Application
import timber.log.Timber

class PartyqApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}