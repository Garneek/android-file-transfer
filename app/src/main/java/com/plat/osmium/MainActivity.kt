package com.plat.osmium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.plat.osmium.utils.Common
import com.plat.osmium.ui.OsmiumApp


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    Common.activity = this

    enableEdgeToEdge()
    super.onCreate(savedInstanceState)


    setContent {
      OsmiumApp()
    }
  }
}