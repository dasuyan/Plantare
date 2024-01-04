package pl.edu.pja.plantare.screens.plants

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import pl.edu.pja.plantare.EDIT_PLANT_SCREEN
import pl.edu.pja.plantare.EDIT_PLANT_SCREEN_MODE
import pl.edu.pja.plantare.PLANT_ID
import pl.edu.pja.plantare.SETTINGS_SCREEN
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.AlarmSchedulerService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.model.service.StorageService
import pl.edu.pja.plantare.model.service.impl.AlarmSchedulerServiceImpl
import pl.edu.pja.plantare.screens.PlantareViewModel
import pl.edu.pja.plantare.screens.edit_plant.EditPlantScreenMode

@HiltViewModel
class PlantsViewModel
@Inject
constructor(logService: LogService, private val storageService: StorageService) :
  PlantareViewModel(logService) {
  val plants = storageService.plants

  fun onAddClick(openScreen: (String) -> Unit) =
    openScreen(
      "$EDIT_PLANT_SCREEN?$EDIT_PLANT_SCREEN_MODE={${EditPlantScreenMode.ADD.name}}?$PLANT_ID={null}"
    )

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onPlantClick(openScreen: (String) -> Unit, plant: Plant) {
    openScreen(
      "$EDIT_PLANT_SCREEN?$EDIT_PLANT_SCREEN_MODE={${EditPlantScreenMode.DETAILS.name}}?$PLANT_ID={${plant.id}}"
    )
  }

  fun onWaterClick(context: Context, plant: Plant) {
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
    val alarmScheduler: AlarmSchedulerService = AlarmSchedulerServiceImpl(context)

    println("Watering plant: $plant")
    launchCatching {
      storageService.update(plant.copy(lastWateringDate = LocalDate.now().format(formatter)))
      if (plant.lastWateringDate.isNotBlank() && plant.wateringFrequencyDays.isNotBlank()) {
        println("Scheduling alarm for plant: $plant")
        alarmScheduler.schedule(plant)
      }
    }
  }
}
