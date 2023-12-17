package pl.edu.pja.plantare.model.service.impl

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import pl.edu.pja.plantare.BuildConfig
import pl.edu.pja.plantare.R.xml as AppConfig
import pl.edu.pja.plantare.model.service.ConfigurationService
import pl.edu.pja.plantare.model.service.trace

class ConfigurationServiceImpl @Inject constructor() : ConfigurationService {
  private val remoteConfig
    get() = Firebase.remoteConfig

  init {
    if (BuildConfig.DEBUG) {
      val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
      remoteConfig.setConfigSettingsAsync(configSettings)
    }

    remoteConfig.setDefaultsAsync(AppConfig.remote_config_defaults)
  }

  override suspend fun fetchConfiguration(): Boolean =
    trace(FETCH_CONFIG_TRACE) { remoteConfig.fetchAndActivate().await() }

  override val isShowPlantEditButtonConfig: Boolean
    get() = remoteConfig[SHOW_PLANT_EDIT_BUTTON_KEY].asBoolean()

  companion object {
    private const val SHOW_PLANT_EDIT_BUTTON_KEY = "show_plant_edit_button"
    private const val FETCH_CONFIG_TRACE = "fetchConfig"
  }
}
