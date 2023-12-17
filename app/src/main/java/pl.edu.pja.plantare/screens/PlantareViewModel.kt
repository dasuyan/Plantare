package pl.edu.pja.plantare.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.edu.pja.plantare.common.snackbar.SnackbarManager
import pl.edu.pja.plantare.common.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import pl.edu.pja.plantare.model.service.LogService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class PlantareViewModel(private val logService: LogService) : ViewModel() {
  fun launchCatching(snackbar: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch(
      CoroutineExceptionHandler { _, throwable ->
        if (snackbar) {
          SnackbarManager.showMessage(throwable.toSnackbarMessage())
        }
        logService.logNonFatalCrash(throwable)
      },
      block = block
    )
}
