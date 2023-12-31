package pl.edu.pja.plantare.model.service.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.AlarmReceiver
import pl.edu.pja.plantare.model.service.AlarmSchedulerService
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

class AlarmSchedulerServiceImpl(
    private val context: Context
) : AlarmSchedulerService {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun schedule(plant: Plant) {
        // Create an intent that will be triggered when the alarm fires
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            // You can include extra information like plant ID or name in the intent if needed
            putExtra("PLANT_NAME", plant.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plant.name.hashCode(), // Use a unique ID for each plant to avoid conflicts
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate the time when the alarm should go off (next watering date)
        val convertedLastWateringDate = convertStringToDate(plant.lastWateringDate)
        println("convertedLastWateringDate: $convertedLastWateringDate")
        println("wateringFrequencyDays: ${plant.wateringFrequencyDays}")
        val nextWateringDate = calculateNextWateringDate(convertedLastWateringDate, plant.wateringFrequencyDays.toInt())
        val alarmTime = Calendar.getInstance().apply {
            timeInMillis = LocalDate.now().toEpochSecond(LocalTime.now(), ZoneOffset.UTC) //nextWateringDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        }

        // Schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime.timeInMillis,
            pendingIntent
        )
        println("Alarm set for (while idle): ${alarmTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)}")
    }

    private fun calculateNextWateringDate(lastWateringDate: LocalDate, wateringFrequencyDays: Int): LocalDate {
        return lastWateringDate.plus(wateringFrequencyDays.toLong(), ChronoUnit.DAYS)
    }

    private fun convertStringToDate(dateString: String): LocalDate {
        // Define the date format
        val dateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)

        // Parse the string into a LocalDate object
        return LocalDate.parse(dateString, dateFormat)
    }

    override fun cancel(plant: Plant) {
        // Create the same intent that was used to schedule the alarm
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
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
