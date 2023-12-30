package pl.edu.pja.plantare.model

import com.google.firebase.firestore.DocumentId

data class Plant(
  @DocumentId val id: String = "",
  val title: String = "",
  val priority: String = "",
  val dueDate: String = "",
  val dueTime: String = "" ,
  val wateringFrequencyDays: String = "",
  val lastWateringDate: String = "",
  val description: String = "",
  var imageUri: String = "",
  val flag: Boolean = false,
  val completed: Boolean = false,
  val userId: String = ""
)
