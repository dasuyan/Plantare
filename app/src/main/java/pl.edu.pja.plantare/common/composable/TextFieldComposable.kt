package pl.edu.pja.plantare.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import pl.edu.pja.plantare.R.drawable as AppIcon
import pl.edu.pja.plantare.R.string as AppText

@Composable
fun BasicField(
  @StringRes text: Int,
  value: String,
  modifier: Modifier = Modifier,
  onNewValue: (String) -> Unit = {},
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  readOnly: Boolean
) {
  OutlinedTextField(
    label = { Text(stringResource(text)) },
    singleLine = false,
    modifier = modifier,
    value = value,
    onValueChange = { onNewValue(it) },
    placeholder = { Text(stringResource(text)) },
    keyboardOptions = keyboardOptions,
    readOnly = readOnly
  )
}

@Composable
fun EmailField(value: String, onNewValue: (String) -> Unit, modifier: Modifier = Modifier) {
  OutlinedTextField(
    label = { Text(stringResource(AppText.email)) },
    singleLine = true,
    modifier = modifier,
    value = value,
    onValueChange = { onNewValue(it) },
    placeholder = { Text(stringResource(AppText.email)) },
    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
  )
}

@Composable
fun PasswordField(value: String, onNewValue: (String) -> Unit, modifier: Modifier = Modifier) {
  PasswordField(
    value = value,
    placeholder = AppText.password,
    onNewValue = onNewValue,
    modifier = modifier
  )
}

@Composable
fun RepeatPasswordField(
  value: String,
  onNewValue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  PasswordField(
    value = value,
    placeholder = AppText.repeat_password,
    onNewValue = onNewValue,
    modifier = modifier
  )
}

@Composable
private fun PasswordField(
  value: String,
  @StringRes placeholder: Int,
  onNewValue: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var isVisible by remember { mutableStateOf(false) }

  val icon =
    if (isVisible) painterResource(AppIcon.ic_visibility_on)
    else painterResource(AppIcon.ic_visibility_off)

  val visualTransformation =
    if (isVisible) VisualTransformation.None else PasswordVisualTransformation()

  OutlinedTextField(
    label = { Text(text = stringResource(placeholder)) },
    modifier = modifier,
    value = value,
    onValueChange = { onNewValue(it) },
    placeholder = { Text(text = stringResource(placeholder)) },
    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
    trailingIcon = {
      IconButton(onClick = { isVisible = !isVisible }) {
        Icon(painter = icon, contentDescription = "Visibility")
      }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    visualTransformation = visualTransformation
  )
}
