package com.example.remed.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "Received alarm")

        val drugName = intent.getStringExtra("DRUG_NAME") ?: "Medication"
        val dosage = intent.getStringExtra("DOSAGE") ?: ""
        val id = intent.getIntExtra("REMINDER_ID", 0)

        val notificationTitle = "Time to take your medication"
        val notificationContent = "$drugName - $dosage"

        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(notificationTitle, notificationContent)
    }
}