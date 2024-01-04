package pl.edu.pja.plantare.model

import com.google.firebase.firestore.DocumentId

data class Plant(
  @DocumentId val id: String = "",
  val name: String = "",
  val lastWateringDate: String = "",
  val wateringFrequencyDays: String = "7",
  val description: String = "",
  var imageUri: String = "",
  val userId: String = ""
)
