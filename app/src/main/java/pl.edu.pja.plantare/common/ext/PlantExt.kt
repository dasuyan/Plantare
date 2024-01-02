package pl.edu.pja.plantare.common.ext

import pl.edu.pja.plantare.model.Plant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun Plant?.hasDueDate(): Boolean {
  return this?.lastWateringDate.orEmpty().isNotBlank()
}

fun Plant?.hasDueTime(): Boolean {
  return this?.dueTime.orEmpty().isNotBlank()
}

fun Plant?.getNextWateringDate(): LocalDate {
  val convertedLastWateringDate = convertStringToDate(this?.lastWateringDate.orEmpty())
  return convertedLastWateringDate.plus(this?.wateringFrequencyDays?.toLong() ?: 0, ChronoUnit.DAYS)
}

fun Plant?.getNextWateringDateString(): String {
  if (this?.lastWateringDate.orEmpty().isBlank()) {
    return "N/A"
  }
  val convertedLastWateringDate = convertStringToDate(this?.lastWateringDate.orEmpty())
  val nextWateringDate =
    convertedLastWateringDate.plus(this?.wateringFrequencyDays?.toLong() ?: 0, ChronoUnit.DAYS)

  if (LocalDate.now() >= nextWateringDate) {
    return "Today"
  }

  val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
  return nextWateringDate.format(formatter)
}

fun Plant?.getLastWateringDate(): String {
  if (this?.lastWateringDate.orEmpty().isBlank()) {
    return "N/A"
  }

  return this?.lastWateringDate ?: "N/A"
}

private fun convertStringToDate(dateString: String): LocalDate {
  val dateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
  return LocalDate.parse(dateString, dateFormat)
}
