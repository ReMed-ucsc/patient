package com.example.remed.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.remed.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all alarms
            val scheduler = AlarmScheduler(context)
            val dao = MainApplication.reminderDatabase.reminderDao()

            CoroutineScope(Dispatchers.IO).launch {
                val reminders = dao.getAllRemindersSync()
                for (reminder in reminders) {
                    scheduler.scheduleAlarm(reminder)
                }
            }
        }
    }
}