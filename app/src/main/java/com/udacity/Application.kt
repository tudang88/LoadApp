package com.udacity

import timber.log.Timber

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        // initialize timber for logging
        Timber.plant(Timber.DebugTree())
    }
}