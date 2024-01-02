package pl.edu.pja.plantare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import dagger.hilt.android.AndroidEntryPoint

// PlantareActivity starts the first composable, which uses material cards that are still
// experimental.
// TODO: Update material dependency and experimental annotations once the API stabilizes.
@AndroidEntryPoint
@ExperimentalMaterialApi
class PlantareActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    createNotificationChannel()

    setContent { PlantareApp() }
  }

  private fun createNotificationChannel() {
    val channel =
      NotificationChannel(
        R.string.watering_notification_channel_id.toString(),
        "High priority notifications",
        NotificationManager.IMPORTANCE_HIGH
      )

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }
}
