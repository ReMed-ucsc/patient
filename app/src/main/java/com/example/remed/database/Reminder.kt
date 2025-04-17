package com.example.remed.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val drugName: String,
    val dosage: String,
    val time: String,
    val additionalInfo: String,
    val isEnable: Boolean = true,
    val isRepeat: Boolean = false
)