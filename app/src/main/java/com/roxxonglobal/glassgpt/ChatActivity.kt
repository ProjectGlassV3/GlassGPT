package com.roxxonglobal.glassgpt

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.roxxonglobal.glassgpt.databinding.ActivityChatBinding
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture.SWIPE_DOWN
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture.TAP
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.OnGestureListener
import logcat.LogPriority
import logcat.logcat

class ChatActivity : AppCompatActivity(), OnGestureListener {
  private var glassGestureDetector: GlassGestureDetector? = null
  private val viewModel = ChatViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    try {
      glassGestureDetector = GlassGestureDetector(this, this)
    } catch (e: Exception) {
      logcat(LogPriority.ERROR) { "Failed to create gesture detector: $e" }
    }

    // Inflate view and set content to it
    // TODO: migrate to compose
    val activityChatBinding = ActivityChatBinding.inflate(layoutInflater)
    setContentView(activityChatBinding.root)

    viewModel.chatHistory.observe(this) { chatHistoryText ->
      activityChatBinding.results.text = chatHistoryText
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    return glassGestureDetector?.onTouchEvent(ev) ?: false || super.dispatchTouchEvent(ev)
  }

  override fun onGesture(gesture: Gesture?): Boolean {
    return when (gesture) {
      TAP -> {
        requestVoiceRecognition()
        true
      }

      SWIPE_DOWN -> {
        finish()
        true
      }

      else -> false
    }
  }

  private fun requestVoiceRecognition() {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    }

    resultLauncher.launch(intent)
  }

  private val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
    if (result.resultCode == RESULT_OK) {
      val results: List<String>? =
        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      logcat(LogPriority.VERBOSE) { "results: $results" }

      if (!results.isNullOrEmpty() && results[0].isNotEmpty()) {
        viewModel.requestResponse(results[0])
      }
    } else {
      logcat(LogPriority.ERROR) { "Result not OK" }
    }
  }
}