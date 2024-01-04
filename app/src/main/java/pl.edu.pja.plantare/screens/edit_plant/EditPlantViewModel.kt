package pl.edu.pja.plantare.screens.edit_plant

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.edu.pja.plantare.EDIT_PLANT_SCREEN_MODE
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
  val loading = mutableStateOf(false)
  val screenMode = mutableStateOf(EditPlantScreenMode.ADD)
  val isDeleteDialogVisible = mutableStateOf(false)
  private var withPicture = false

  init {
    val plantId = savedStateHandle.get<String>(PLANT_ID)
    if (plantId != null && plantId != "{null}") {
      launchCatching { plant.value = storageService.getPlant(plantId.idFromParameter()) ?: Plant() }
    }

    val screenMode = savedStateHandle.get<String>(EDIT_PLANT_SCREEN_MODE)
    if (screenMode != null) {
      this.screenMode.value = EditPlantScreenMode.valueOf(screenMode.idFromParameter())
    }
    println("Screen mode: $screenMode")
  }

  fun onEditClick() {
    screenMode.value = EditPlantScreenMode.EDIT
  }

  fun onDeleteClick() {
    isDeleteDialogVisible.value = true
  }

  fun onDeleteCancel() {
    isDeleteDialogVisible.value = false
  }

  fun onDeleteConfirm(popUpScreen: () -> Unit, context: Context) {
    launchCatching { storageService.delete(plant.value.id) }
    val alarmScheduler: AlarmSchedulerService = AlarmSchedulerServiceImpl(context)
    alarmScheduler.cancel(plant.value)

    popUpScreen()
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

      if (
        editedPlant.lastWateringDate.isNotBlank() && editedPlant.wateringFrequencyDays.isNotBlank()
      ) {
        alarmScheduler.schedule(editedPlant)
      }

      popUpScreen()
    }
  }

  companion object {
    private const val UTC = "UTC"
    private const val DATE_FORMAT = "EEE, dd MMM yyyy"
  }
}
