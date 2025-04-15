package com.example.remed.models

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.remed.MainApplication
import com.example.remed.database.Reminder
import com.example.remed.database.ReminderDao
import com.example.remed.notifications.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ReminderDao
//    private val reminderDao = MainApplication.reminderDatabase.reminderDao()
    private val alarmScheduler: AlarmScheduler

    init {
        dao = MainApplication.reminderDatabase.reminderDao()
        alarmScheduler = AlarmScheduler(application)
    }


    fun getAllReminders(): LiveData<List<Reminder>> {
        return dao.getAllReminders()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            // Insert into database
            val id = dao.insertReminder(reminder)

            // If needed, retrieve the reminder with the generated ID
            val insertedReminder = dao.getReminderById(id)

            // Schedule the alarm on the main thread
            launch(Dispatchers.Main) {
                insertedReminder?.let {
                    alarmScheduler.scheduleAlarm(it)
                }
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            // Cancel the alarm first
            launch(Dispatchers.Main) {
                alarmScheduler.cancelAlarm(reminder)
            }
            // Then delete from database
            dao.deleteReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            // Cancel old alarm
            launch(Dispatchers.Main) {
                alarmScheduler.cancelAlarm(reminder)
            }

            // Update in database
            dao.updateReminder(reminder)

            // Schedule new alarm
            launch(Dispatchers.Main) {
                alarmScheduler.scheduleAlarm(reminder)
            }
        }
    }
}