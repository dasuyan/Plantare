package pl.edu.pja.plantare.screens.edit_plant

import android.Manifest
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import pl.edu.pja.plantare.BuildConfig
import pl.edu.pja.plantare.common.composable.ActionToolbar
import pl.edu.pja.plantare.common.composable.BasicField
import pl.edu.pja.plantare.common.composable.CardSelector
import pl.edu.pja.plantare.common.composable.RegularCardEditor
import pl.edu.pja.plantare.common.ext.card
import pl.edu.pja.plantare.common.ext.fieldModifier
import pl.edu.pja.plantare.common.ext.spacer
import pl.edu.pja.plantare.common.ext.toolbarActions
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.Priority
import pl.edu.pja.plantare.theme.PlantareTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import pl.edu.pja.plantare.R.drawable as AppIcon
import pl.edu.pja.plantare.R.string as AppText

@Composable
@ExperimentalMaterialApi
fun EditPlantScreen(popUpScreen: () -> Unit, viewModel: EditPlantViewModel = hiltViewModel()) {
  val plant by viewModel.plant
  val loading by viewModel.loading
  val context = LocalContext.current
  val activity = LocalContext.current as AppCompatActivity

  EditPlantScreenContent(
    plant = plant,
    onDoneClick = { viewModel.onDoneClick(popUpScreen, context) },
    onNameChange = viewModel::onNameChange,
    onDescriptionChange = viewModel::onDescriptionChange,
    onWateringFrequencyChange = viewModel::onWateringFrequencyChange,
    onImageUriChange = viewModel::onImageUriChange,
    onDateChange = viewModel::onDateChange,
    onTimeChange = viewModel::onTimeChange,
    onPriorityChange = viewModel::onPriorityChange,
    onFlagToggle = viewModel::onFlagToggle,
    activity = activity
  )

  if (loading) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Gray.copy(alpha = 0.3f)),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(color = MaterialTheme.colors.onBackground)
    }
  }
}

@Composable
@ExperimentalMaterialApi
fun EditPlantScreenContent(
  modifier: Modifier = Modifier,
  plant: Plant,
  onDoneClick: () -> Unit,
  onNameChange: (String) -> Unit,
  onDescriptionChange: (String) -> Unit,
  onWateringFrequencyChange: (String) -> Unit,
  onImageUriChange: (String) -> Unit,
  onDateChange: (Long) -> Unit,
  onTimeChange: (Int, Int) -> Unit,
  onPriorityChange: (String) -> Unit,
  onFlagToggle: (String) -> Unit,
  activity: AppCompatActivity?
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    ActionToolbar(
      title = AppText.edit_plant,
      modifier = Modifier.toolbarActions(),
      endActionIcon = AppIcon.ic_check,
      endAction = { onDoneClick() }
    )

    Spacer(modifier = Modifier.spacer())

    SubcomposeAsyncImage(
      model = ImageRequest.Builder(LocalContext.current)
        .data(plant.imageUri)
        .crossfade(true)
        .build(),
      modifier = Modifier
        .padding(16.dp, 8.dp)
        .clip(MaterialTheme.shapes.medium),
      contentDescription = null,
      loading = { LoadingAnimation() }
    )
    PhotoTaker(onImageUriChange)

    Spacer(modifier = Modifier.spacer())

    val fieldModifier = Modifier.fieldModifier()
    BasicField(AppText.name, plant.name, onNameChange, fieldModifier)
    BasicField(AppText.description, plant.description, onDescriptionChange, fieldModifier)
    BasicField(AppText.watering_frequency, plant.wateringFrequencyDays, onWateringFrequencyChange, fieldModifier)
    //BasicField(AppText.url, plant.imageUri, onImageUriChange, fieldModifier)

    Spacer(modifier = Modifier.spacer())

    CardEditors(plant, onDateChange, onTimeChange, activity)
    CardSelectors(plant, onPriorityChange, onFlagToggle)

    Spacer(modifier = Modifier.spacer())
  }
}

@Composable
fun LoadingAnimation() {
  val animation = rememberInfiniteTransition(label = "")
  val progress by animation.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000),
      repeatMode = RepeatMode.Restart,
    ), label = ""
  )

  Box(
    modifier = Modifier
      .size(60.dp)
      .scale(progress)
      .alpha(1f - progress)
      .border(
        5.dp,
        color = MaterialTheme.colors.primary,
        shape = CircleShape
      )
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
    Modifier
      .fillMaxSize()
      .padding(10.dp),
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
private fun CardEditors(
  plant: Plant,
  onDateChange: (Long) -> Unit,
  onTimeChange: (Int, Int) -> Unit,
  activity: AppCompatActivity?
) {
  RegularCardEditor(AppText.last_watering_date, AppIcon.ic_calendar, plant.lastWateringDate, Modifier.card()) {
    showDatePicker(activity, onDateChange)
  }

  RegularCardEditor(AppText.time, AppIcon.ic_clock, plant.dueTime, Modifier.card()) {
    showTimePicker(activity, onTimeChange)
  }
}

@Composable
@ExperimentalMaterialApi
private fun CardSelectors(
  plant: Plant,
  onPriorityChange: (String) -> Unit,
  onFlagToggle: (String) -> Unit
) {
  val prioritySelection = Priority.getByName(plant.priority).name
  CardSelector(AppText.priority, Priority.getOptions(), prioritySelection, Modifier.card()) {
    newValue ->
    onPriorityChange(newValue)
  }

  val flagSelection = EditFlagOption.getByCheckedState(plant.flag).name
  CardSelector(AppText.flag, EditFlagOption.getOptions(), flagSelection, Modifier.card()) { newValue
    ->
    onFlagToggle(newValue)
  }
}

private fun showDatePicker(activity: AppCompatActivity?, onDateChange: (Long) -> Unit) {
  val picker = MaterialDatePicker.Builder.datePicker().build()

  activity?.let {
    picker.show(it.supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener { timeInMillis -> onDateChange(timeInMillis) }
  }
}

private fun showTimePicker(activity: AppCompatActivity?, onTimeChange: (Int, Int) -> Unit) {
  val picker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

  activity?.let {
    picker.show(it.supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener { onTimeChange(picker.hour, picker.minute) }
  }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun EditPlantScreenPreview() {
  val plant = Plant(name = "Plant name", description = "Plant description", flag = true)

  PlantareTheme {
    EditPlantScreenContent(
      plant = plant,
      onDoneClick = {},
      onNameChange = {},
      onDescriptionChange = {},
      onWateringFrequencyChange = {},
      onImageUriChange = {},
      onDateChange = {},
      onTimeChange = { _, _ -> },
      onPriorityChange = {},
      onFlagToggle = {},
      activity = null
    )
  }
}
