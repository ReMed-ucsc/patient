package com.example.remed.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.remed.MainApplication
import com.example.remed.database.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderDao = MainApplication.reminderDatabase.reminderDao()

    fun getAllReminders(): LiveData<List<Reminder>> = reminderDao.getAllReminders()

    fun insertReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.insertReminder(reminder)
        }
    }
}