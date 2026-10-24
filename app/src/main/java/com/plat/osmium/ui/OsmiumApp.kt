package com.plat.osmium.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text

import com.plat.osmium.ui.theme.OsmiumTheme

// @Preview
@Composable
fun OsmiumApp() {
    OsmiumTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Text(
                "Something"
            )
        }
    }

}

