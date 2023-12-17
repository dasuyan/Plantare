package pl.edu.pja.plantare.screens.sign_up

import androidx.compose.runtime.mutableStateOf
import pl.edu.pja.plantare.R.string as AppText
import pl.edu.pja.plantare.SETTINGS_SCREEN
import pl.edu.pja.plantare.SIGN_UP_SCREEN
import pl.edu.pja.plantare.common.ext.isValidEmail
import pl.edu.pja.plantare.common.ext.isValidPassword
import pl.edu.pja.plantare.common.ext.passwordMatches
import pl.edu.pja.plantare.common.snackbar.SnackbarManager
import pl.edu.pja.plantare.model.service.AccountService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.screens.PlantareViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
  private val accountService: AccountService,
  logService: LogService
) : PlantareViewModel(logService) {
  var uiState = mutableStateOf(SignUpUiState())
    private set

  private val email
    get() = uiState.value.email
  private val password
    get() = uiState.value.password

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
  }

  fun onRepeatPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(repeatPassword = newValue)
  }

  fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    if (!password.isValidPassword()) {
      SnackbarManager.showMessage(AppText.password_error)
      return
    }

    if (!password.passwordMatches(uiState.value.repeatPassword)) {
      SnackbarManager.showMessage(AppText.password_match_error)
      return
    }

    launchCatching {
      accountService.linkAccount(email, password)
      openAndPopUp(SETTINGS_SCREEN, SIGN_UP_SCREEN)
    }
  }
}
