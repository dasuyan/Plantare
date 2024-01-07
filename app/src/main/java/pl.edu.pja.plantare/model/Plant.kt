package pl.edu.pja.plantare.model

import com.google.firebase.firestore.DocumentId

data class Plant(
  @DocumentId val id: String = "",
  val name: String = "",
  val species: String = "",
  val description: String = "",
  val lastWateringDate: String = "",
  val wateringFrequencyDays: String = "7",
  var imageUri: String = "",
  val userId: String = ""
)
