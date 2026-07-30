package com.phonelookup.app

import android.app.Application
import com.phonelookup.app.data.local.SessionManager
import com.phonelookup.app.data.api.RetrofitClient

/**
 * Application class — initializes singletons eagerly for instant startup.
 */
class PhoneLookupApp : Application() {

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Initialize session manager and Retrofit client eagerly
        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)
    }

    companion object {
        lateinit var instance: PhoneLookupApp
            private set
    }
}
