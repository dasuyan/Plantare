package pl.edu.pja.plantare.common.ext

import pl.edu.pja.plantare.model.Plant

fun Plant?.hasDueDate(): Boolean {
  return this?.dueDate.orEmpty().isNotBlank()
}

fun Plant?.hasDueTime(): Boolean {
  return this?.dueTime.orEmpty().isNotBlank()
}
