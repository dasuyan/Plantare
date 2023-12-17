package pl.edu.pja.plantare.screens.plants

enum class PlantActionOption(val title: String) {
  EditPlant("Edit plants"),
  ToggleFlag("Toggle flag"),
  DeletePlant("Delete plants");

  companion object {
    fun getByTitle(title: String): PlantActionOption {
      entries.forEach { action -> if (title == action.title) return action }

      return EditPlant
    }

    fun getOptions(hasEditOption: Boolean): List<String> {
      val options = mutableListOf<String>()
      entries.forEach { plantAction ->
        if (hasEditOption || plantAction != EditPlant) {
          options.add(plantAction.title)
        }
      }
      return options
    }
  }
}
