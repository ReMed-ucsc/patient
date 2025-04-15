package com.example.remed

import android.app.Application
import androidx.room.Room
import com.example.remed.database.ReminderDatabase

class MainApplication : Application() {
    companion object{
        lateinit var reminderDatabase: ReminderDatabase
    }

    override fun onCreate() {
        super.onCreate()
        reminderDatabase = Room.databaseBuilder(
            applicationContext,
            ReminderDatabase::class.java,
            ReminderDatabase.DATABASE_NAME
        ).build()
    }
}