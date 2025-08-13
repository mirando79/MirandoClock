package com.example.mirandoclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HourlyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val serviceIntent = Intent(context, HourlyNotificationService::class.java)
            context.startService(serviceIntent)
        }
    }
}
