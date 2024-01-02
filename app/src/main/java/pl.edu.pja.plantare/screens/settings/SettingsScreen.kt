package pl.edu.pja.plantare.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import pl.edu.pja.plantare.R.drawable as AppIcon
import pl.edu.pja.plantare.R.string as AppText
import pl.edu.pja.plantare.common.composable.BasicToolbar
import pl.edu.pja.plantare.common.composable.DangerousCardEditor
import pl.edu.pja.plantare.common.composable.DialogCancelButton
import pl.edu.pja.plantare.common.composable.DialogConfirmButton
import pl.edu.pja.plantare.common.composable.RegularCardEditor
import pl.edu.pja.plantare.common.ext.card
import pl.edu.pja.plantare.common.ext.spacer
import pl.edu.pja.plantare.theme.PlantareTheme

@ExperimentalMaterialApi
@Composable
fun SettingsScreen(
  restartApp: (String) -> Unit,
  openScreen: (String) -> Unit,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState(initial = SettingsUiState(false))

  SettingsScreenContent(
    uiState = uiState,
    onLoginClick = { viewModel.onLoginClick(openScreen) },
    onSignUpClick = { viewModel.onSignUpClick(openScreen) },
    onSignOutClick = { viewModel.onSignOutClick(restartApp) },
    onDeleteMyAccountClick = { viewModel.onDeleteMyAccountClick(restartApp) }
  )
}

@ExperimentalMaterialApi
@Composable
fun SettingsScreenContent(
  modifier: Modifier = Modifier,
  uiState: SettingsUiState,
  onLoginClick: () -> Unit,
  onSignUpClick: () -> Unit,
  onSignOutClick: () -> Unit,
  onDeleteMyAccountClick: () -> Unit
) {
  Column(
    modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    BasicToolbar(AppText.settings)

    Spacer(modifier = Modifier.spacer())

    if (uiState.isAnonymousAccount) {
      RegularCardEditor(AppText.sign_in, AppIcon.ic_sign_in, "", Modifier.card()) { onLoginClick() }

      RegularCardEditor(AppText.create_account, AppIcon.ic_create_account, "", Modifier.card()) {
        onSignUpClick()
      }
    } else {
      SignOutCard { onSignOutClick() }
      DeleteMyAccountCard { onDeleteMyAccountClick() }
    }
  }
}

@ExperimentalMaterialApi
@Composable
private fun SignOutCard(signOut: () -> Unit) {
  var showWarningDialog by remember { mutableStateOf(false) }

  RegularCardEditor(AppText.sign_out, AppIcon.ic_exit, "", Modifier.card()) {
    showWarningDialog = true
  }

  if (showWarningDialog) {
    AlertDialog(
      title = { Text(stringResource(AppText.sign_out_title)) },
      text = { Text(stringResource(AppText.sign_out_description)) },
      dismissButton = { DialogCancelButton(AppText.cancel) { showWarningDialog = false } },
      confirmButton = {
        DialogConfirmButton(AppText.sign_out) {
          signOut()
          showWarningDialog = false
        }
      },
      onDismissRequest = { showWarningDialog = false }
    )
  }
}

@ExperimentalMaterialApi
@Composable
private fun DeleteMyAccountCard(deleteMyAccount: () -> Unit) {
  var showWarningDialog by remember { mutableStateOf(false) }

  DangerousCardEditor(
    AppText.delete_my_account,
    AppIcon.ic_delete_my_account,
    "",
    Modifier.card()
  ) {
    showWarningDialog = true
  }

  if (showWarningDialog) {
    AlertDialog(
      title = { Text(stringResource(AppText.delete_account_title)) },
      text = { Text(stringResource(AppText.delete_account_description)) },
      dismissButton = { DialogCancelButton(AppText.cancel) { showWarningDialog = false } },
      confirmButton = {
        DialogConfirmButton(AppText.delete_my_account) {
          deleteMyAccount()
          showWarningDialog = false
        }
      },
      onDismissRequest = { showWarningDialog = false }
    )
  }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun SettingsScreenPreview() {
  val uiState = SettingsUiState(isAnonymousAccount = false)

  PlantareTheme {
    SettingsScreenContent(
      uiState = uiState,
      onLoginClick = {},
      onSignUpClick = {},
      onSignOutClick = {},
      onDeleteMyAccountClick = {}
    )
  }
}
