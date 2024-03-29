package pl.edu.pja.plantare.model.service

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.edu.pja.plantare.PlantareActivity
import pl.edu.pja.plantare.R

class AlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    // Handle the alarm, e.g., show the notification
    val plantName = intent.getStringExtra("PLANT_NAME")

    // Show the notification
    showNotification(context, plantName)
  }

  @OptIn(ExperimentalMaterialApi::class)
  private fun showNotification(context: Context, plantName: String?) {
    val notificationManager = NotificationManagerCompat.from(context)

    // Create an explicit intent for your main activity
    val contentIntent =
      Intent(context, PlantareActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }

    val pendingIntent =
      PendingIntent.getActivity(
        context,
        0,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

    val notification =
      NotificationCompat.Builder(context, R.string.watering_notification_channel_id.toString())
        .setContentTitle("Water Your Plant")
        .setContentText("$plantName is due for watering!")
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent) // Set the intent for when the user taps the notification
        .setAutoCancel(true) // Automatically remove the notification when tapped
        .build()
    if (
      ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
    ) {
      return
    }
    notificationManager.notify(plantName.hashCode(), notification)
  }
}
