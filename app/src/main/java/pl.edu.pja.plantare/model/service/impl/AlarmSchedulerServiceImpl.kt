package pl.edu.pja.plantare.model.service.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale
import pl.edu.pja.plantare.common.ext.getNextWateringDate
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.AlarmReceiver
import pl.edu.pja.plantare.model.service.AlarmSchedulerService

class AlarmSchedulerServiceImpl(private val context: Context) : AlarmSchedulerService {
  private val alarmManager = context.getSystemService(AlarmManager::class.java)

  @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
  override fun schedule(plant: Plant) {
    // Create an intent that will be triggered when the alarm fires
    val intent =
      Intent(context, AlarmReceiver::class.java).apply {
        // You can include extra information like plant ID or name in the intent if needed
        putExtra("PLANT_NAME", plant.name)
      }

    val pendingIntent =
      PendingIntent.getBroadcast(
        context,
        plant.name.hashCode(), // Use a unique ID for each plant to avoid conflicts
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

    val nextWateringDate = plant.getNextWateringDate()
    val alarmTime =
      Calendar.getInstance().apply {
        timeInMillis = nextWateringDate.atTime(12, 0).toEpochSecond(ZoneOffset.UTC) * 1000
      }

    // Schedule the alarm
    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      alarmTime.timeInMillis,
      pendingIntent
    )
    println("Alarm set for (while idle): ${displayTimestamp(alarmTime)}")
  }

  private fun displayTimestamp(calendar: Calendar): String {
    // Convert Calendar to Date
    val date = calendar.time

    // Format the Date using SimpleDateFormat
    val pattern = "EEE, dd MMM yyyy HH:mm:ss z"
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)

    return sdf.format(date)
  }

  override fun cancel(plant: Plant) {
    // Create the same intent that was used to schedule the alarm
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent =
      PendingIntent.getBroadcast(
        context,
        plant.name.hashCode(),
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
      )

    // Cancel the alarm if it exists
    pendingIntent?.let {
      alarmManager.cancel(pendingIntent)
      it.cancel()
    }
  }
}
