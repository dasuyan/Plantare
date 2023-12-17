package pl.edu.pja.plantare.model.service

interface ConfigurationService {
  suspend fun fetchConfiguration(): Boolean
  val isShowPlantEditButtonConfig: Boolean
}
