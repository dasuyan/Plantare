package pl.edu.pja.plantare.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette =
  darkColors(primary = BrightGreen, primaryVariant = DarkGreen, secondary = MediumGreen)

private val LightColorPalette =
  lightColors(primary = BrightGreen, primaryVariant = DarkGreen, secondary = MediumGreen)

@Composable
fun PlantareTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
  val colors = if (darkTheme) DarkColorPalette else LightColorPalette

  MaterialTheme(colors = colors, typography = Typography, shapes = Shapes, content = content)
}
