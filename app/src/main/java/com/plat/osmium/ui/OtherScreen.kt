package com.plat.osmium.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.plat.osmium.R
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.plat.osmium.ui.components.OsmiumTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(
  modifier: Modifier = Modifier,
  canNavBack: Boolean = true,
  navBack: () -> Unit = {}
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  Scaffold(
    modifier
      .fillMaxSize()
      .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
      OsmiumTopBar(
        navBack = navBack,
        title = stringResource(R.string.servers_header_text),
        subtitle = "Test string",
        canNavBack = canNavBack,
        scrollBehavior = scrollBehavior
      )
    }) { padding ->
    LazyColumn(Modifier.padding(padding)) {
      item {
        Text(text = LoremIpsum(words = 4096).values.fold("") { a, b -> a + b })
      }
    }
  }

}
