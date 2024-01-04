package pl.edu.pja.plantare.screens.edit_plant

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import pl.edu.pja.plantare.BuildConfig
import pl.edu.pja.plantare.R.drawable as AppIcon
import pl.edu.pja.plantare.R.string as AppText
import pl.edu.pja.plantare.common.composable.ActionToolbar
import pl.edu.pja.plantare.common.composable.BasicField
import pl.edu.pja.plantare.common.composable.DialogConfirmButton
import pl.edu.pja.plantare.common.composable.RegularCardEditor
import pl.edu.pja.plantare.common.ext.card
import pl.edu.pja.plantare.common.ext.fieldModifier
import pl.edu.pja.plantare.common.ext.spacer
import pl.edu.pja.plantare.common.ext.toolbarActions
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.theme.PlantareTheme

@Composable
@ExperimentalMaterialApi
fun EditPlantScreen(popUpScreen: () -> Unit, viewModel: EditPlantViewModel = hiltViewModel()) {
  val screenMode by viewModel.screenMode
  val plant by viewModel.plant
  val loading by viewModel.loading
  val isDeleteDialogVisible by viewModel.isDeleteDialogVisible
  val context = LocalContext.current
  val activity = LocalContext.current as AppCompatActivity

  EditPlantScreenContent(
    screenMode = screenMode,
    onDeleteClick = viewModel::onDeleteClick,
    isDeleteDialogVisible = isDeleteDialogVisible,
    onDeleteCancel = viewModel::onDeleteCancel,
    onDeleteConfirm = { viewModel.onDeleteConfirm(popUpScreen, context) },
    onEditClick = viewModel::onEditClick,
    plant = plant,
    onDoneClick = { viewModel.onDoneClick(popUpScreen, context) },
    onNameChange = viewModel::onNameChange,
    onDescriptionChange = viewModel::onDescriptionChange,
    onWateringFrequencyChange = viewModel::onWateringFrequencyChange,
    onImageUriChange = viewModel::onImageUriChange,
    onDateChange = viewModel::onDateChange,
    activity = activity
  )

  if (loading) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.3f)),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(color = MaterialTheme.colors.onBackground)
    }
  }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@ExperimentalMaterialApi
fun EditPlantScreenContent(
  modifier: Modifier = Modifier,
  screenMode: EditPlantScreenMode,
  onDeleteClick: () -> Unit,
  isDeleteDialogVisible: Boolean,
  onDeleteCancel: () -> Unit,
  onDeleteConfirm: () -> Unit,
  onEditClick: () -> Unit,
  plant: Plant,
  onDoneClick: () -> Unit,
  onNameChange: (String) -> Unit,
  onDescriptionChange: (String) -> Unit,
  onWateringFrequencyChange: (String) -> Unit,
  onImageUriChange: (String) -> Unit,
  onDateChange: (Long) -> Unit,
  activity: AppCompatActivity?
) {
  Scaffold(
    floatingActionButton = {
      if (screenMode == EditPlantScreenMode.DETAILS) {
        FloatingActionButton(
          onClick = onEditClick,
          backgroundColor = MaterialTheme.colors.primary,
          contentColor = MaterialTheme.colors.onPrimary,
          modifier = modifier.padding(16.dp)
        ) {
          Icon(Icons.Filled.Edit, "Edit")
        }
      }
    }
  ) {
    Column(
      modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val title =
        when (screenMode) {
          EditPlantScreenMode.ADD -> AppText.add_plant
          EditPlantScreenMode.EDIT -> AppText.edit_plant
          EditPlantScreenMode.DETAILS -> AppText.plant_details
        }
      ActionToolbar(
        title = title,
        modifier = Modifier.toolbarActions(),
        endActionIcon = AppIcon.ic_check,
        endAction = { onDoneClick() }
      )

      Spacer(modifier = Modifier.spacer())

      SubcomposeAsyncImage(
        model =
          ImageRequest.Builder(LocalContext.current).data(plant.imageUri).crossfade(true).build(),
        modifier = Modifier.padding(16.dp, 8.dp).clip(MaterialTheme.shapes.medium),
        contentDescription = null,
        loading = { LoadingAnimation() }
      )

      if (screenMode != EditPlantScreenMode.DETAILS) {
        PhotoTaker(onImageUriChange)
      }

      Spacer(modifier = Modifier.spacer())

      val fieldModifier = Modifier.fieldModifier()
      BasicField(
        text = AppText.name,
        value = plant.name,
        onNewValue = onNameChange,
        modifier = fieldModifier,
        keyboardOptions =
          KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences
          ),
        readOnly = screenMode == EditPlantScreenMode.DETAILS
      )
      BasicField(
        text = AppText.description,
        value = plant.description,
        onNewValue = onDescriptionChange,
        modifier = fieldModifier,
        keyboardOptions =
          KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences
          ),
        readOnly = screenMode == EditPlantScreenMode.DETAILS
      )
      BasicField(
        text = AppText.watering_frequency,
        value = plant.wateringFrequencyDays,
        onNewValue = onWateringFrequencyChange,
        modifier = fieldModifier,
        keyboardOptions =
          KeyboardOptions(
            keyboardType = KeyboardType.Number,
          ),
        readOnly = screenMode == EditPlantScreenMode.DETAILS
      )

      if (screenMode != EditPlantScreenMode.DETAILS) {
        CardEditors(plant, onDateChange, activity)
      } else {
        BasicField(
          text = AppText.last_watering_date,
          value = plant.lastWateringDate,
          modifier = fieldModifier,
          readOnly = true
        )
      }

      Spacer(modifier = Modifier.spacer())

      if (screenMode == EditPlantScreenMode.DETAILS) {
        OutlinedButton(
          onClick = onDeleteClick,
          colors =
            ButtonDefaults.buttonColors(
              backgroundColor = Color.Transparent,
              contentColor = Color.Red
            ),
          border = BorderStroke(1.dp, Color.Red)
        ) {
          Text(text = "Delete")
        }
      }

      if (isDeleteDialogVisible) {
        DeleteConfirmationDialog(onDeleteConfirm = onDeleteConfirm, onDeleteCancel = onDeleteCancel)
      }

      Spacer(modifier = Modifier.spacer())
    }
  }
}

@Composable
fun DeleteConfirmationDialog(onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDeleteCancel,
    title = { Text("Delete Plant") },
    text = { Text("Are you sure you want to delete this plant? This action is permanent!") },
    buttons = {
      Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        DialogConfirmButton(text = AppText.delete_plant_confirm, action = onDeleteConfirm)
        DialogConfirmButton(text = AppText.delete_plant_cancel, action = onDeleteCancel)
      }
    }
  )
}

@Composable
fun LoadingAnimation() {
  val animation = rememberInfiniteTransition(label = "")
  val progress by
    animation.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(1000),
          repeatMode = RepeatMode.Restart,
        ),
      label = ""
    )

  Box(
    modifier =
      Modifier.size(60.dp)
        .scale(progress)
        .alpha(1f - progress)
        .border(5.dp, color = MaterialTheme.colors.primary, shape = CircleShape)
  )
}

fun Context.createImageFile(): File {
  val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
  val imageFileName = "JPEG_" + timeStamp + "_"
  return File.createTempFile(imageFileName, ".jpg", externalCacheDir)
}

@Composable
fun PhotoTaker(onUrlChange: (String) -> Unit) {
  val context = LocalContext.current
  val file = context.createImageFile()
  val uri =
    file.let {
      FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider",
        it
      )
    }

  var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

  val cameraLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
      if (uri != null) {
        capturedImageUri = uri
      }
    }

  val permissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
      if (it) {
        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        cameraLauncher.launch(uri)
      } else {
        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
      }
    }

  Column(
    Modifier.fillMaxSize().padding(10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Button(
      onClick = {
        val permissionCheckResult =
          ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
          cameraLauncher.launch(uri)
        } else {
          permissionLauncher.launch(Manifest.permission.CAMERA)
        }
      }
    ) {
      Text(text = "Take a picture")
    }
  }

  if (capturedImageUri.path?.isNotEmpty() == true) {
    onUrlChange(capturedImageUri.toString())
  }
}

@ExperimentalMaterialApi
@Composable
private fun CardEditors(plant: Plant, onDateChange: (Long) -> Unit, activity: AppCompatActivity?) {
  RegularCardEditor(
    AppText.last_watering_date,
    AppIcon.ic_calendar,
    plant.lastWateringDate,
    Modifier.card()
  ) {
    showDatePicker(activity, onDateChange)
  }
}

private fun showDatePicker(activity: AppCompatActivity?, onDateChange: (Long) -> Unit) {
  val picker = MaterialDatePicker.Builder.datePicker().build()

  activity?.let {
    picker.show(it.supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener { timeInMillis -> onDateChange(timeInMillis) }
  }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun EditPlantScreenPreview() {
  val plant = Plant(name = "Plant name", description = "Plant description")

  PlantareTheme {
    EditPlantScreenContent(
      screenMode = EditPlantScreenMode.EDIT,
      onDeleteClick = {},
      isDeleteDialogVisible = false,
      onDeleteCancel = {},
      onDeleteConfirm = {},
      onEditClick = {},
      plant = plant,
      onDoneClick = {},
      onNameChange = {},
      onDescriptionChange = {},
      onWateringFrequencyChange = {},
      onImageUriChange = {},
      onDateChange = {},
      activity = null
    )
  }
}
