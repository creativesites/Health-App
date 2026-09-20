package com.example

import android.app.Application
import com.example.core.database.AppDatabase
import com.example.core.repository.mock.AppRepositoryLocator

class HealthcareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        AppRepositoryLocator.initializeDatabase(database)
    }
}
