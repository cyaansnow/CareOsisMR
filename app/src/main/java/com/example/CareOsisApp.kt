package com.example

import android.app.Application
import com.example.data.local.db.CareOsisDatabase
import com.example.data.repository.CareOsisRepository

class CareOsisApp : Application() {
    val database: CareOsisDatabase by lazy { CareOsisDatabase.getDatabase(this) }
    val repository: CareOsisRepository by lazy { CareOsisRepository(database) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: CareOsisApp
            private set
    }
}
