package pl.edu.pja.plantare.screens.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import pl.edu.pja.plantare.LOGIN_SCREEN
import pl.edu.pja.plantare.SIGN_UP_SCREEN
import pl.edu.pja.plantare.SPLASH_SCREEN
import pl.edu.pja.plantare.model.service.AccountService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.screens.PlantareViewModel

@HiltViewModel
class SettingsViewModel
@Inject
constructor(logService: LogService, private val accountService: AccountService) :
  PlantareViewModel(logService) {
  val uiState = accountService.currentUser.map { SettingsUiState(it.isAnonymous) }

  fun onLoginClick(openScreen: (String) -> Unit) = openScreen(LOGIN_SCREEN)

  fun onSignUpClick(openScreen: (String) -> Unit) = openScreen(SIGN_UP_SCREEN)

  fun onSignOutClick(restartApp: (String) -> Unit) {
    launchCatching {
      accountService.signOut()
      restartApp(SPLASH_SCREEN)
    }
  }

  fun onDeleteMyAccountClick(restartApp: (String) -> Unit) {
    launchCatching {
      accountService.deleteAccount()
      restartApp(SPLASH_SCREEN)
    }
  }
}
