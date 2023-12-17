package pl.edu.pja.plantare.screens.plants

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import pl.edu.pja.plantare.EDIT_PLANT_SCREEN
import pl.edu.pja.plantare.PLANT_ID
import pl.edu.pja.plantare.SETTINGS_SCREEN
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.ConfigurationService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.model.service.StorageService
import pl.edu.pja.plantare.screens.PlantareViewModel

@HiltViewModel
class PlantsViewModel
@Inject
constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val configurationService: ConfigurationService
) : PlantareViewModel(logService) {
  val options = mutableStateOf<List<String>>(listOf())

  val plants = storageService.plants

  fun loadPlantOptions() {
    val hasEditOption = configurationService.isShowPlantEditButtonConfig
    options.value = PlantActionOption.getOptions(hasEditOption)
  }

  fun onPlantCheckChange(plant: Plant) {
    launchCatching { storageService.update(plant.copy(completed = !plant.completed)) }
  }

  fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_PLANT_SCREEN)

  fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

  fun onPlantActionClick(openScreen: (String) -> Unit, plant: Plant, action: String) {
    when (PlantActionOption.getByTitle(action)) {
      PlantActionOption.EditPlant -> openScreen("$EDIT_PLANT_SCREEN?$PLANT_ID={${plant.id}}")
      PlantActionOption.ToggleFlag -> onFlagPlantClick(plant)
      PlantActionOption.DeletePlant -> onDeletePlantClick(plant)
    }
  }

  private fun onFlagPlantClick(plant: Plant) {
    launchCatching { storageService.update(plant.copy(flag = !plant.flag)) }
  }

  private fun onDeletePlantClick(plant: Plant) {
    launchCatching { storageService.delete(plant.id) }
  }
}
