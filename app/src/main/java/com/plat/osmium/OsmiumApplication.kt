package com.plat.osmium

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import com.plat.osmium.db.OsmiumDatabase
import com.plat.osmium.db.OsmiumRepository
import com.plat.osmium.utils.Common

// TODO:
// Directories editing menu, pick server
// Directories add menu
// Password input switching keyboard when toggling visibility

class OsmiumApplication : Application(), SingletonImageLoader.Factory {
  val database by lazy { OsmiumDatabase.getDatabase(applicationContext) }
  val repository by lazy { OsmiumRepository(database.directoryDao(), database.serverDao()) }

  override fun newImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context).components {
      add(VideoFrameDecoder.Factory())
    }
      .crossfade(true)
      .build()
  }

  override fun onCreate() {
    SingletonImageLoader.setSafe { context ->
      ImageLoader.Builder(context)
        .crossfade(true)
        .build()
    }
    Common.application = this

    super.onCreate()

  }

}