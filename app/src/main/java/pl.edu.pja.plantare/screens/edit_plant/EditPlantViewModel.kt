package pl.edu.pja.plantare.screens.edit_plant

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import pl.edu.pja.plantare.common.ext.idFromParameter
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.model.service.StorageService
import pl.edu.pja.plantare.screens.PlantareViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.edu.pja.plantare.PLANT_ID
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  logService: LogService,
  private val storageService: StorageService
) : PlantareViewModel(logService) {

  val plant = mutableStateOf(Plant())
  private var withPicture = false
  val loading = mutableStateOf(false)
  init {
    val plantId = savedStateHandle.get<String>(PLANT_ID)
    if (plantId != null) {
      launchCatching {
        plant.value = storageService.getPlant(plantId.idFromParameter()) ?: Plant()
      }
    }
  }

  fun onTitleChange(newValue: String) {
    plant.value = plant.value.copy(title = newValue)
  }

  fun onDescriptionChange(newValue: String) {
    plant.value = plant.value.copy(description = newValue)
  }

  /*fun onUrlChange(newValue: String) {
    plants.value = plants.value.copy(url = newValue)
  }*/

  fun onDateChange(newValue: Long) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
    calendar.timeInMillis = newValue
    val newDueDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(calendar.time)
    plant.value = plant.value.copy(dueDate = newDueDate)
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

  fun onUrlChange(newValue: String) {
    withPicture = true
    plant.value = plant.value.copy(url = newValue)
  }

  fun onDoneClick(popUpScreen: () -> Unit) {

    launchCatching {
      loading.value = true
      val editedPlant = plant.value

      if (editedPlant.id.isBlank()) {
        storageService.save(editedPlant, withPicture)
      } else {
        storageService.update(editedPlant, withPicture)
      }

      popUpScreen()
    }
  }

  private fun Int.toClockPattern(): String {
    return if (this < 10) "0$this" else "$this"
  }

  companion object {
    private const val UTC = "UTC"
    private const val DATE_FORMAT = "EEE, d MMM yyyy"
  }
}
