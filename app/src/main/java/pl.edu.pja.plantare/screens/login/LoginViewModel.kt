package pl.edu.pja.plantare.screens.login

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import pl.edu.pja.plantare.LOGIN_SCREEN
import pl.edu.pja.plantare.R.string as AppText
import pl.edu.pja.plantare.SETTINGS_SCREEN
import pl.edu.pja.plantare.common.ext.isValidEmail
import pl.edu.pja.plantare.common.snackbar.SnackbarManager
import pl.edu.pja.plantare.model.service.AccountService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.screens.PlantareViewModel

@HiltViewModel
class LoginViewModel
@Inject
constructor(private val accountService: AccountService, logService: LogService) :
  PlantareViewModel(logService) {
  var uiState = mutableStateOf(LoginUiState())
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

  fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    if (password.isBlank()) {
      SnackbarManager.showMessage(AppText.empty_password_error)
      return
    }

    launchCatching {
      accountService.authenticate(email, password)
      openAndPopUp(SETTINGS_SCREEN, LOGIN_SCREEN)
    }
  }

  fun onForgotPasswordClick() {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    launchCatching {
      accountService.sendRecoveryEmail(email)
      SnackbarManager.showMessage(AppText.recovery_email_sent)
    }
  }
}
