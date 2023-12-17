package pl.edu.pja.plantare.screens.splash

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import pl.edu.pja.plantare.PLANTS_SCREEN
import pl.edu.pja.plantare.SPLASH_SCREEN
import pl.edu.pja.plantare.model.service.AccountService
import pl.edu.pja.plantare.model.service.ConfigurationService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.screens.PlantareViewModel

@HiltViewModel
class SplashViewModel
@Inject
constructor(
  configurationService: ConfigurationService,
  private val accountService: AccountService,
  logService: LogService
) : PlantareViewModel(logService) {
  val showError = mutableStateOf(false)

  init {
    launchCatching { configurationService.fetchConfiguration() }
  }

  fun onAppStart(openAndPopUp: (String, String) -> Unit) {

    showError.value = false
    if (accountService.hasUser) openAndPopUp(PLANTS_SCREEN, SPLASH_SCREEN)
    else createAnonymousAccount(openAndPopUp)
  }

  private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
    launchCatching(snackbar = false) {
      try {
        accountService.createAnonymousAccount()
      } catch (ex: FirebaseAuthException) {
        showError.value = true
        throw ex
      }
      openAndPopUp(PLANTS_SCREEN, SPLASH_SCREEN)
    }
  }
}
