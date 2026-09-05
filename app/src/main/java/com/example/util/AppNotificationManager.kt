package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object AppNotificationManager {
  const val CHANNEL_ID = "factory_alerts_channel"
  const val CHANNEL_NAME = "هشدارهای مدیریتی کارخانه و انبار"
  const val CHANNEL_DESC = "نمایش اعلان‌ها و هشدارهای فوری کسری پارچه، تولید و سفارشات"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESC
        enableLights(true)
        enableVibration(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showNotification(
    context: Context,
    title: String,
    message: String,
    notificationId: Int = (System.currentTimeMillis() % 10000).toInt()
  ) {
    createNotificationChannel(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Using app launcher icon or default drawable
    val iconRes = android.R.drawable.ic_dialog_alert

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(iconRes)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)

    try {
      val notificationManager = NotificationManagerCompat.from(context)
      notificationManager.notify(notificationId, builder.build())
    } catch (e: SecurityException) {
      // Permission might not be granted yet on Android 13+
      e.printStackTrace()
    }
  }
}
