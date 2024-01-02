package pl.edu.pja.plantare.model

import com.google.firebase.firestore.DocumentId

data class Plant(
  @DocumentId val id: String = "",
  val name: String = "",
  val priority: String = "",
  val lastWateringDate: String = "",
  val dueTime: String = "",
  val wateringFrequencyDays: String = "",
  val description: String = "",
  var imageUri: String = "",
  val flag: Boolean = false,
  val completed: Boolean = false,
  val userId: String = ""
)
