package pl.edu.pja.plantare.screens.plants

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.pja.plantare.R.drawable as AppIcon
import pl.edu.pja.plantare.common.composable.DropdownContextMenu
import pl.edu.pja.plantare.common.ext.contextMenu
import pl.edu.pja.plantare.common.ext.hasDueDate
import pl.edu.pja.plantare.common.ext.hasDueTime
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.theme.DarkOrange
import java.lang.StringBuilder

@Composable
@ExperimentalMaterialApi
fun PlantItem(
  plant: Plant,
  options: List<String>,
  onCheckChange: () -> Unit,
  onActionClick: (String) -> Unit
) {
  Card(
    backgroundColor = MaterialTheme.colors.background,
    modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Checkbox(
        checked = plant.completed,
        onCheckedChange = { onCheckChange() },
        modifier = Modifier.padding(8.dp, 0.dp)
      )

      Column(modifier = Modifier.weight(1f)) {
        Text(text = plant.title, style = MaterialTheme.typography.subtitle2)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          Text(text = getDueDateAndTime(plant), fontSize = 12.sp)
        }
      }

      if (plant.flag) {
        Icon(
          painter = painterResource(AppIcon.ic_flag),
          tint = DarkOrange,
          contentDescription = "Flag"
        )
      }

      DropdownContextMenu(options, Modifier.contextMenu(), onActionClick)
    }
  }
}

private fun getDueDateAndTime(plant: Plant): String {
  val stringBuilder = StringBuilder("")

  if (plant.hasDueDate()) {
    stringBuilder.append(plant.dueDate)
    stringBuilder.append(" ")
  }

  if (plant.hasDueTime()) {
    stringBuilder.append("at ")
    stringBuilder.append(plant.dueTime)
  }

  return stringBuilder.toString()
}
