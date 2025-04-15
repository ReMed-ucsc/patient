package com.example.remed.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1)
abstract class ReminderDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "reminder_database"
    }
    abstract fun reminderDao(): ReminderDao
}