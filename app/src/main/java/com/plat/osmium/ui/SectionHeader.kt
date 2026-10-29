package com.plat.osmium.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.plat.osmium.R

@Composable
fun SectionHeader(
  headerText: String,
  onIconPress: () -> Unit,
  modifier: Modifier = Modifier,
  iconResourceId: Int = R.drawable.ic_launcher_foreground,
  iconDescriptionId: Int = R.string.default_icon_button_description,
) {
  Row(
    modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = headerText,
      style = MaterialTheme.typography.titleLarge,
      maxLines = 1,
    )

    IconButton(
      onClick = onIconPress
    ) {
      Icon(
        painter = painterResource(id = iconResourceId),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = stringResource(iconDescriptionId),
      )
    }
  }
}