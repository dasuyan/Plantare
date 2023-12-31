package pl.edu.pja.plantare.screens.plants

import android.annotation.SuppressLint
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.pja.plantare.common.composable.ActionToolbar
import pl.edu.pja.plantare.common.ext.smallSpacer
import pl.edu.pja.plantare.common.ext.toolbarActions
import pl.edu.pja.plantare.model.Plant
import pl.edu.pja.plantare.theme.PlantareTheme
import pl.edu.pja.plantare.R.drawable as AppIcon
import pl.edu.pja.plantare.R.string as AppText

@Composable
@ExperimentalMaterialApi
fun PlantsScreen(openScreen: (String) -> Unit, viewModel: PlantsViewModel = hiltViewModel()) {
  val plants = viewModel.plants.collectAsStateWithLifecycle(emptyList())
  val options by viewModel.options

  PlantsScreenContent(
    plants = plants.value,
    options = options,
    onAddClick = viewModel::onAddClick,
    onSettingsClick = viewModel::onSettingsClick,
    onPlantCheckChange = viewModel::onPlantCheckChange,
    onPlantActionClick = viewModel::onPlantActionClick,
    openScreen = openScreen
  )

  LaunchedEffect(viewModel) { viewModel.loadPlantOptions() }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@ExperimentalMaterialApi
fun PlantsScreenContent(
  modifier: Modifier = Modifier,
  plants: List<Plant>,
  options: List<String>,
  onAddClick: ((String) -> Unit) -> Unit,
  onSettingsClick: ((String) -> Unit) -> Unit,
  onPlantCheckChange: (Plant) -> Unit,
  onPlantActionClick: ((String) -> Unit, Plant, String) -> Unit,
  openScreen: (String) -> Unit
) {
  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { onAddClick(openScreen) },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        modifier = modifier.padding(16.dp)
      ) {
        Icon(Icons.Filled.Add, "Add")
      }
    }
  ) {
    Column(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()) {
      ActionToolbar(
        title = AppText.plants,
        modifier = Modifier.toolbarActions(),
        endActionIcon = AppIcon.ic_settings,
        endAction = { onSettingsClick(openScreen) }
      )

      Spacer(modifier = Modifier.smallSpacer())

      PlantList(
        plants = plants,
        options = options,
        onPlantActionClick = onPlantActionClick,
        openScreen = openScreen,
        modifier = modifier)
      /*LazyColumn {
        items(plants, key = { it.id }) { plantItem ->
          PlantItem(
            plant = plantItem,
            options = options,
            onCheckChange = { onPlantCheckChange(plantItem) },
            onActionClick = { action -> onPlantActionClick(openScreen, plantItem, action) }
          )
        }
      }*/
    }
  }
}

@Composable
private fun PlantList(
  plants: List<Plant>,
  options: List<String>,
  onPlantActionClick: ((String) -> Unit, Plant, String) -> Unit,
  openScreen: (String) -> Unit,
  //onPlantClick: (PlantAndGardenPlantings) -> Unit,
  modifier: Modifier = Modifier,
) {
  // Call reportFullyDrawn when the garden list has been rendered
  val gridState = rememberLazyGridState()
  ReportDrawnWhen { gridState.layoutInfo.totalItemsCount > 0 }
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier,
    state = gridState,
    contentPadding = PaddingValues(
      horizontal = 12.dp,
      vertical = 16.dp
    )
  ) {
    plants.listIterator().forEach { plant ->
      item {
        PlantListItem(
          plant = plant,
          options = options,
          onActionClick = { action -> onPlantActionClick(openScreen, plant, action) }
        )
      }
    }
  }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun PlantsScreenPreview() {
  val plant = Plant(name = "Plant name", flag = true, completed = true)

  val options = PlantActionOption.getOptions(hasEditOption = true)

  PlantareTheme {
    PlantsScreenContent(
      plants = listOf(plant),
      options = options,
      onAddClick = {},
      onSettingsClick = {},
      onPlantCheckChange = {},
      onPlantActionClick = { _, _, _ -> },
      openScreen = {}
    )
  }
}
