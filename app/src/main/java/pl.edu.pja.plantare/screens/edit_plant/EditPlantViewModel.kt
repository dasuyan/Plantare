package pl.edu.pja.plantare.screens.edit_plant

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import pl.edu.pja.plantare.EDIT_PLANT_SCREEN_MODE
import pl.edu.pja.plantare.PLANT_ID
import pl.edu.pja.plantare.common.ext.idFromParameter
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.AlarmSchedulerService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.model.service.StorageService
import pl.edu.pja.plantare.model.service.impl.AlarmSchedulerServiceImpl
import pl.edu.pja.plantare.screens.PlantareViewModel
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel
@Inject
constructor(
  savedStateHandle: SavedStateHandle,
  logService: LogService,
  private val storageService: StorageService,
) : PlantareViewModel(logService) {

  val plant = mutableStateOf(Plant())
  val loading = mutableStateOf(false)
  val screenMode = mutableStateOf(EditPlantScreenMode.ADD)
  val isDeleteDialogVisible = mutableStateOf(false)
  val apiKey = "dJ9FLrAqBYEXeKU7Xv4Q03Hf33oX37tBlI4oCl1611hqhKFJZe"
  private var withPicture = false

  init {
    val plantId = savedStateHandle.get<String>(PLANT_ID)
    if (plantId != null && plantId != "{null}") {
      launchCatching { plant.value = storageService.getPlant(plantId.idFromParameter()) ?: Plant() }
    }

    val screenMode = savedStateHandle.get<String>(EDIT_PLANT_SCREEN_MODE)
    if (screenMode != null) {
      this.screenMode.value = EditPlantScreenMode.valueOf(screenMode.idFromParameter())
    }
    println("Screen mode: $screenMode")
  }

  fun onEditClick() {
    screenMode.value = EditPlantScreenMode.EDIT
  }

  fun onDeleteClick() {
    isDeleteDialogVisible.value = true
  }

  fun onDeleteCancel() {
    isDeleteDialogVisible.value = false
  }

  fun onDeleteConfirm(popUpScreen: () -> Unit, context: Context) {
    launchCatching { storageService.delete(plant.value.id) }
    val alarmScheduler: AlarmSchedulerService = AlarmSchedulerServiceImpl(context)
    alarmScheduler.cancel(plant.value)

    popUpScreen()
  }

  fun onNameChange(newValue: String) {
    plant.value = plant.value.copy(name = newValue)
  }

  fun onSpeciesChange(newValue: String) {
    plant.value = plant.value.copy(species = newValue)
  }

  fun onDescriptionChange(newValue: String) {
    plant.value = plant.value.copy(description = newValue)
  }

  fun onDateChange(newValue: Long) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
    calendar.timeInMillis = newValue
    val newDueDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(calendar.time)
    plant.value = plant.value.copy(lastWateringDate = newDueDate)
  }

  fun onWateringFrequencyChange(newValue: String) {
    plant.value = plant.value.copy(wateringFrequencyDays = newValue)
  }

  fun onImageUriChange(newValue: String) {
    withPicture = true
    plant.value = plant.value.copy(imageUri = newValue)
  }

  fun onIdentifyClick() {
    if (plant.value.imageUri.isBlank()) {
      return
    }
    viewModelScope.launch {
      loading.value = true
      val species = makePostRequestWithImage(plant.value.imageUri, apiKey)
      plant.value = plant.value.copy(species = species)
      loading.value = false
    }
  }

  fun onDoneClick(popUpScreen: () -> Unit, context: Context) {

    launchCatching {
      loading.value = true
      val editedPlant = plant.value
      val alarmScheduler: AlarmSchedulerService = AlarmSchedulerServiceImpl(context)

      if (editedPlant.id.isBlank()) {
        storageService.save(editedPlant, withPicture)
      } else {
        storageService.update(editedPlant, withPicture)
      }

      if (
        screenMode.value != EditPlantScreenMode.DETAILS &&
          editedPlant.lastWateringDate.isNotBlank() &&
          editedPlant.wateringFrequencyDays.isNotBlank()
      ) {
        alarmScheduler.schedule(editedPlant)
      }

      popUpScreen()
    }
  }

  private suspend fun makePostRequestWithImage(imageUri: String, apiKey: String): String {
    return withContext(Dispatchers.IO) {
      try {
        val imageBase64 = downloadAndEncodeImage(imageUri)

        val client = OkHttpClient().newBuilder().build()
        val mediaType = "application/json".toMediaTypeOrNull()

        val formatToJson = """{"images": ["data:image/jpg;base64,$imageBase64"]}"""
        val params = JSONObject(formatToJson)

        val request =
          Request.Builder()
            .url("https://plant.id/api/v3/identification")
            .post(params.toString().toRequestBody(mediaType))
            .header("Api-Key", apiKey)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        println("Response: $response")

        val responseJSONObject = JSONObject(responseBody)
        val plantName =
          responseJSONObject
            .getJSONObject("result")
            .getJSONObject("classification")
            .getJSONArray("suggestions")
            .getJSONObject(0)
            .getString("name")
        plantName
      } catch (e: IOException) {
        e.printStackTrace()
        ""
      }
    }
  }

  private suspend fun downloadAndEncodeImage(imageUrl: String): String? {
    return withContext(Dispatchers.IO) {
      var connection: HttpURLConnection? = null
      var inputStream: InputStream? = null
      try {
        val url = URL(imageUrl)
        connection = url.openConnection() as HttpURLConnection
        connection.connect()

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
          inputStream = connection.inputStream
          val bitmap = BitmapFactory.decodeStream(inputStream)
          val byteArrayOutputStream = ByteArrayOutputStream()
          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
          val byteArray = byteArrayOutputStream.toByteArray()
          Base64.encodeToString(byteArray, Base64.DEFAULT)
        } else {
          null
        }
      } catch (e: IOException) {
        e.printStackTrace()
        null
      } finally {
        inputStream?.close()
        connection?.disconnect()
      }
    }
  }

  companion object {
    private const val UTC = "UTC"
    private const val DATE_FORMAT = "EEE, dd MMM yyyy"
  }
}
