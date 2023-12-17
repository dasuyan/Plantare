package pl.edu.pja.plantare.model.service.impl

import android.net.Uri
import androidx.core.net.toUri
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.model.service.AccountService
import pl.edu.pja.plantare.model.service.StorageService
import pl.edu.pja.plantare.model.service.trace
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await

class StorageServiceImpl
@Inject
constructor(
  private val firestore: FirebaseFirestore,
  private val auth: AccountService,
  private val storageRef: StorageReference
) :
    StorageService {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val plants: Flow<List<Plant>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        firestore.collection(PLANT_COLLECTION).whereEqualTo(USER_ID_FIELD, user.id).dataObjects()
      }

  override suspend fun getPlant(plantId: String): Plant? =
    firestore.collection(PLANT_COLLECTION).document(plantId).get().await().toObject()

  override suspend fun save(plant: Plant, withPicture: Boolean): String =
    trace(SAVE_PLANT_TRACE) {
      if (withPicture) {
        uploadFile(plant)
      }

      val plantWithUserId = plant.copy(userId = auth.currentUserId)
      firestore.collection(PLANT_COLLECTION).add(plantWithUserId).await().id
    }

  override suspend fun update(plant: Plant, withPicture: Boolean): Unit =
    trace(UPDATE_PLANT_TRACE) {
      if (withPicture) {
        uploadFile(plant)
      }

      firestore.collection(PLANT_COLLECTION).document(plant.id).set(plant).await()
    }

  override suspend fun uploadFile(plant: Plant): Uri? = coroutineScope {
    trace(UPLOAD_FILE_TRACE) {
      val file = plant.url.toUri()
      val ref = storageRef.child("plants/${file.lastPathSegment}")

      try {
        ref.putFile(file).await()

        val downloadUri = ref.downloadUrl.await()
        downloadUri?.let {
          plant.url = it.toString()
        }

        downloadUri
      } catch (e: Exception) {
        println("Error uploading or retrieving file URL: ${e.message}")
        null
      }
    }
  }

  override suspend fun delete(plantId: String) {
    firestore.collection(PLANT_COLLECTION).document(plantId).delete().await()
  }

  companion object {
    private const val USER_ID_FIELD = "userId"
    private const val PLANT_COLLECTION = "plants"
    private const val SAVE_PLANT_TRACE = "savePlant"
    private const val UPDATE_PLANT_TRACE = "updatePlant"
    private const val UPLOAD_FILE_TRACE = "uploadFile"
  }
}
