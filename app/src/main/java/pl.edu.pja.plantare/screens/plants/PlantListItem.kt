package pl.edu.pja.plantare.screens.plants

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.pja.plantare.R
import pl.edu.pja.plantare.common.ext.getLastWateringDate
import pl.edu.pja.plantare.common.ext.getNextWateringDateString
import pl.edu.pja.plantare.model.Plant

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlantListItem(
  plant: Plant,
  onWaterClick: (Plant) -> Unit,
  onPlantClick: (Plant) -> Unit
) {
  // Dimensions
  val cardSideMargin = 12.dp
  val marginNormal = 16.dp

  val lastWateringDate = plant.getLastWateringDate()
  val nextWateringDate = plant.getNextWateringDateString()

  Card(
    onClick = { onPlantClick(plant) },
    modifier = Modifier
      .padding(start = cardSideMargin, end = cardSideMargin, bottom = 26.dp),
    backgroundColor = MaterialTheme.colors.secondary,
    contentColor = MaterialTheme.colors.onSecondary,
  ) {
    Column(Modifier.fillMaxWidth()) {
      PlantareImage(
        model = plant.imageUri,
        contentDescription = plant.description,
        Modifier.fillMaxWidth().height(95.dp),
        contentScale = ContentScale.Crop,
      )

      // Plant name
      Text(
        text = plant.name,
        Modifier.padding(vertical = marginNormal, horizontal = marginNormal)
          .align(Alignment.CenterHorizontally)
          .height(42.dp),
        style = MaterialTheme.typography.subtitle1,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        maxLines = 2
      )

      // Last watered
      Text(
        text = "Last Watered",
        Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.body2
      )
      Text(
        text = lastWateringDate,
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

      val waterToday = nextWateringDate == "Today"

      Button(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(
            bottom = marginNormal,
          ),
        enabled = waterToday,
        onClick = { onWaterClick(plant) }
      ) {
        if (waterToday) {
          Text(text = "Water me!", style = MaterialTheme.typography.button)
          Icon(
            painter = painterResource(R.drawable.baseline_water_drop_24),
            contentDescription = "Water droplet",
          )
        } else {
          Text(text = "Watered", style = MaterialTheme.typography.button)
          Icon(
            painter = painterResource(R.drawable.baseline_local_florist_24),
            contentDescription = "A flower",
          )
        }
      }
    }
  }
}

