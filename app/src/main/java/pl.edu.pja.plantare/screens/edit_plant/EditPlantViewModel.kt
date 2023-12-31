package pl.edu.pja.plantare.screens.edit_plant

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.edu.pja.plantare.PLANT_ID
import pl.edu.pja.plantare.common.ext.idFromParameter
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.AlarmSchedulerService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.model.service.StorageService
import pl.edu.pja.plantare.model.service.impl.AlarmSchedulerServiceImpl
import pl.edu.pja.plantare.screens.PlantareViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel
@Inject
constructor(
  savedStateHandle: SavedStateHandle,
  logService: LogService,
  private val storageService: StorageService,
) : PlantareViewModel(logService) {

  val plant = mutableStateOf(Plant())
  private var withPicture = false
  val loading = mutableStateOf(false)

  init {
    val plantId = savedStateHandle.get<String>(PLANT_ID)
    if (plantId != null) {
      launchCatching { plant.value = storageService.getPlant(plantId.idFromParameter()) ?: Plant() }
    }
  }

  fun onNameChange(newValue: String) {
    plant.value = plant.value.copy(name = newValue)
  }

  fun onDescriptionChange(newValue: String) {
    plant.value = plant.value.copy(description = newValue)
  }

  fun onDateChange(newValue: Long) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
    calendar.timeInMillis = newValue
    val newDueDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(calendar.time)
    plant.value = plant.value.copy(lastWateringDate = newDueDate)
  }

  fun onTimeChange(hour: Int, minute: Int) {
    val newDueTime = "${hour.toClockPattern()}:${minute.toClockPattern()}"
    plant.value = plant.value.copy(dueTime = newDueTime)
  }

  fun onFlagToggle(newValue: String) {
    val newFlagOption = EditFlagOption.getBooleanValue(newValue)
    plant.value = plant.value.copy(flag = newFlagOption)
  }

  fun onPriorityChange(newValue: String) {
    plant.value = plant.value.copy(priority = newValue)
  }

  fun onWateringFrequencyChange(newValue: String) {
    plant.value = plant.value.copy(wateringFrequencyDays = newValue)
  }

  fun onImageUriChange(newValue: String) {
    withPicture = true
    plant.value = plant.value.copy(imageUri = newValue)
  }

  fun onDoneClick(popUpScreen: () -> Unit, context: Context) {
    val alarmScheduler: AlarmSchedulerService = AlarmSchedulerServiceImpl(context)

    launchCatching {
      loading.value = true
      val editedPlant = plant.value

      if (editedPlant.id.isBlank()) {
        storageService.save(editedPlant, withPicture)
      } else {
        storageService.update(editedPlant, withPicture)
      }

      if (editedPlant.lastWateringDate.isNotBlank() && editedPlant.wateringFrequencyDays.isNotBlank()) {
        alarmScheduler.schedule(editedPlant)
      }

      popUpScreen()
    }
  }

  private fun Int.toClockPattern(): String {
    return if (this < 10) "0$this" else "$this"
  }

  companion object {
    private const val UTC = "UTC"
    private const val DATE_FORMAT = "EEE, dd MMM yyyy"
  }
}
