package com.plat.osmium.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.plat.osmium.R
import java.io.File

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ImageLoader(
  file: File,
  modifier: Modifier = Modifier,
) {
  if (file.isDirectory) InternalImage(
    painterResource(R.drawable.img_folder),
    true,
    modifier = modifier
  )
  else if (arrayOf(
      "png", "jpg", "jpeg", "webp", "mp4", "mkv"
    ).contains(file.extension)
  ) {
    val painter = rememberAsyncImagePainter(file)
    val state by painter.state.collectAsState()

    when (state) {
      is AsyncImagePainter.State.Empty,
      is AsyncImagePainter.State.Loading ->
        ContainedLoadingIndicator(modifier = modifier)

      is AsyncImagePainter.State.Success -> {
        InternalImage(
          painter, false,
          modifier = modifier
        )
      }

      is AsyncImagePainter.State.Error ->
        InternalImage(
          painterResource(R.drawable.img_file), false,
          modifier = modifier
        )
    }
  } else InternalImage(
    painterResource(R.drawable.img_file), false,
    modifier = modifier
  )
}

@Composable
private fun InternalImage(painter: Painter, dir: Boolean, modifier: Modifier = Modifier) {
  Image(
    painter = painter,
    contentDescription = stringResource(if (dir) R.string.directory_icon_desc else R.string.preview_desc),
    contentScale = ContentScale.Crop,
    modifier = modifier
  )
}