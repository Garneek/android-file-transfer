package com.plat.osmium.utils

import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {
  fun provideUIContext(): CoroutineContext
  fun provideIOContext(): CoroutineContext
}