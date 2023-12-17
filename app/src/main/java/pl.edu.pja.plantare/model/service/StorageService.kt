package pl.edu.pja.plantare.model.service

import android.net.Uri
import pl.edu.pja.plantare.model.Plant
import kotlinx.coroutines.flow.Flow

interface StorageService {
  val plants: Flow<List<Plant>>
  suspend fun getPlant(plantId: String): Plant?
  suspend fun save(plant: Plant, withPicture: Boolean = false): String
  suspend fun update(plant: Plant, withPicture: Boolean = false)
  suspend fun delete(plantId: String)
  suspend fun uploadFile(plant: Plant): Uri?
}
