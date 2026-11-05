package com.plat.osmium.utils

import android.annotation.SuppressLint
import kotlin.math.roundToInt

@SuppressLint("DefaultLocale")
fun fileSizeFormatter(size: Long): String {
  var sizeFloat: Float = size.toFloat()
  var i = 0

  while (sizeFloat > 500f) {
    sizeFloat /= 1024f
    i += 1
  }
  val sizeStr: String
  if (sizeFloat >= 100f) sizeStr = ((sizeFloat / 10f).roundToInt() * 10).toString()
  else if (sizeFloat >= 10f) sizeStr = sizeFloat.roundToInt().toString()
  else if (sizeFloat >= 1f) sizeStr =
    String.format("%.1f", (sizeFloat * 10f).roundToInt().toFloat() / 10f)
  else sizeStr = String.format("%.2f", (sizeFloat * 100f).roundToInt().toFloat() / 100f)

  return String.format(
    "%s %s", sizeStr, when (i) {
      0 -> "B"
      1 -> "KB"
      2 -> "MB"
      3 -> "GB"
      else -> ""
    }
  )
}
