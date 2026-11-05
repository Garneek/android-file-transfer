package com.plat.osmium.utils

import android.annotation.SuppressLint
import android.content.Context
import com.plat.osmium.MainActivity

@SuppressLint("StaticFieldLeak")
object Common {
  @Volatile
  lateinit var activity: MainActivity

  @Volatile
  lateinit var appContext: Context

  fun setContext(context: Context) {
    appContext = context
  }
}