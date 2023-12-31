package pl.edu.pja.plantare.screens.plants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.pja.plantare.common.composable.DropdownContextMenu
import pl.edu.pja.plantare.common.ext.contextMenu
import pl.edu.pja.plantare.common.ext.getNextWateringDateString
import pl.edu.pja.plantare.common.ext.hasDueDate
import pl.edu.pja.plantare.common.ext.hasDueTime
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.R.drawable as AppIcon

/*@Composable
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
        Text(text = plant.name, style = MaterialTheme.typography.subtitle2)
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
}*/

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlantListItem(
  plant: Plant,
  options: List<String>,
  onActionClick: (String) -> Unit
  //onPlantClick: (PlantAndGardenPlantings) -> Unit
) {
  // Dimensions
  val cardSideMargin = 12.dp
  val marginNormal = 16.dp
  val nextWateringDate = plant.getNextWateringDateString()

  Card(
   // onClick = { onPlantClick(plant) },
    modifier = Modifier.padding(
      start = cardSideMargin,
      end = cardSideMargin,
      bottom = 26.dp
    ),
    backgroundColor = MaterialTheme.colors.secondary,
    contentColor = MaterialTheme.colors.onSecondary,
    //colors = CardDefaults.cardColors(containerColor = MaterialTheme.colors.secondary)
  ) {
    Column(Modifier.fillMaxWidth()) {
      PlantareImage(
        model = plant.imageUri,
        contentDescription = plant.description,
        Modifier
          .fillMaxWidth()
          .height(130.dp),
        contentScale = ContentScale.Crop,
      )

      // Plant name
      Text(
        text = plant.name,
        Modifier
          .padding(vertical = marginNormal)
          .align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.subtitle1,
        textAlign = TextAlign.Center
      )

      // Last watered
      Text(
        text = "Last Watered",
        Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.body2
      )
      Text(
        text = plant.lastWateringDate,
        Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.overline
      )

      // Next watering
      Text(
        text = "Next Watering",
        Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.body2
      )
      Text(
        text = nextWateringDate,
        Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.overline,
      )

      if (nextWateringDate == "Today") {
        Button(onClick = { /*TODO*/ }) {
          
        }
        Icon(painter = painterResource(AppIcon.baseline_water_drop_24), contentDescription = "Water droplet")
      }

      DropdownContextMenu(
        options,
        Modifier
          .contextMenu()
          .align(AbsoluteAlignment.Right)
          .padding(bottom = marginNormal), // remember about padding on the lowest element to maintain margin from bottom!
        onActionClick
      )

      /*// Last Watered
      Text(
        text = stringResource(id = R.string.watered_date_header),
        Modifier
          .align(Alignment.CenterHorizontally)
          .padding(top = marginNormal),
        style = MaterialTheme.typography.titleSmall
      )
      Text(
        text = vm.waterDateString,
        Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.labelSmall
      )
      Text(
        text = pluralStringResource(
          id = R.plurals.watering_next,
          count = vm.wateringInterval,
          vm.wateringInterval
        ),
        Modifier
          .align(Alignment.CenterHorizontally)
          .padding(bottom = marginNormal),
        style = MaterialTheme.typography.labelSmall
      )*/
    }
  }
}

private fun getDueDateAndTime(plant: Plant): String {
  val stringBuilder = StringBuilder("")

  if (plant.hasDueDate()) {
    stringBuilder.append(plant.lastWateringDate)
    stringBuilder.append(" ")
  }

  if (plant.hasDueTime()) {
    stringBuilder.append("at ")
    stringBuilder.append(plant.dueTime)
  }

  return stringBuilder.toString()
}
