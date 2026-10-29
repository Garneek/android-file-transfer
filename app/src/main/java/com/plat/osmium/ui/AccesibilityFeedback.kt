package com.plat.osmium.ui

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun clickAudio(function: () -> Unit): () -> Unit {
  val audioManager = LocalContext.current.getSystemService(Context.AUDIO_SERVICE) as AudioManager
  return {
    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
    function()
  }
}

@Composable
fun hapticClick(function: () -> Unit): () -> Unit {
  val haptics = LocalHapticFeedback.current
  return clickAudio {
    haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
    function()
  }
}

@Composable
fun hapticLongPress(function: () -> Unit): () -> Unit {
  val haptics = LocalHapticFeedback.current
  return clickAudio {
    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    function()
  }
}

@Composable
fun hapticConfirm(function: () -> Unit): () -> Unit {
  val haptics = LocalHapticFeedback.current
  return clickAudio {
    haptics.performHapticFeedback(HapticFeedbackType.Confirm)
    function()
  }
}

@Composable
fun hapticReject(function: () -> Unit): () -> Unit {
  val haptics = LocalHapticFeedback.current
  return clickAudio {
    haptics.performHapticFeedback(HapticFeedbackType.Reject)
    function()
  }
}

@Composable
fun withHapticFeedback(type: HapticFeedbackType, function: () -> Unit): () -> Unit {
  val haptics = LocalHapticFeedback.current
  return clickAudio {
    haptics.performHapticFeedback(type)
    function()
  }
}