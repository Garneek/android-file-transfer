package com.plat.osmium.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import com.plat.osmium.MainActivity
import com.plat.osmium.OsmiumApplication
import java.io.File

fun defaultDir(): File = File(Environment.getExternalStorageDirectory().absolutePath)

@SuppressLint("StaticFieldLeak")
object Common {
  @Volatile
  lateinit var activity: MainActivity

  @Volatile
  lateinit var application: OsmiumApplication
}