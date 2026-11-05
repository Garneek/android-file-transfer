package com.plat.osmium.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.plat.osmium.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsmiumTopBar(
  modifier: Modifier = Modifier,
  navBack: () -> Unit = {},
  canNavBack: Boolean = true,
  title: String = "No title",
  subtitle: String? = null,
  actions: @Composable (RowScope.() -> Unit) = {},
  scrollBehavior: TopAppBarScrollBehavior? = null
) {
  MediumTopAppBar(
    scrollBehavior = scrollBehavior,
    title = {
      Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
          )
        }
      }
    },

    navigationIcon = {
      if (canNavBack)
        IconButton(onClick = hapticConfirm(navBack)) {
          Icon(
            painter = painterResource(R.drawable.icon_back),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.nav_back),
          )
        }
    },
    actions = actions,
    modifier = modifier
  )

}