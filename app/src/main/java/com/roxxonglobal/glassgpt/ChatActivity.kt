package com.roxxonglobal.glassgpt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.MotionEvent
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.roxxonglobal.glassgpt.databinding.ActivityChatBinding
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture.SWIPE_DOWN
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture.SWIPE_UP
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture.TAP
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.OnGestureListener
import logcat.LogPriority
import logcat.logcat
import java.util.*

class ChatActivity : AppCompatActivity(), OnGestureListener, TextToSpeech.OnInitListener {
  private var glassGestureDetector: GlassGestureDetector? = null
  private val viewModel = ChatViewModel()
  private lateinit var tts: TextToSpeech
  private lateinit var activityChatBinding: ActivityChatBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    try {
      glassGestureDetector = GlassGestureDetector(this, this)
    } catch (e: Exception) {
      logcat(LogPriority.ERROR) { "Failed to create gesture detector: $e" }
    }

    tts = TextToSpeech(this, this)

    // Inflate view and set content to it
    // TODO: migrate to compose
    activityChatBinding = ActivityChatBinding.inflate(layoutInflater)
    setContentView(activityChatBinding.root)

    viewModel.chatHistory.observe(this) { chatHistoryText ->
      activityChatBinding.results.text = chatHistoryText
    }

    viewModel.latestResponse.observe(this) { response ->
      speakOut(response)
    }
  }

  override fun onDestroy() {
    // Shutdown TTS when activity is destroyed
    if (tts.isSpeaking) {
      tts.stop()
    }
    tts.shutdown()
    super.onDestroy()
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
        scrollChat(-100)
        true
      }
      SWIPE_UP -> {
        scrollChat(100)
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

  private fun speakOut(text: String) {
    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts.setLanguage(Locale.US)
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        logcat(LogPriority.ERROR) { "TTS: Language not supported" }
      }
    } else {
      logcat(LogPriority.ERROR) { "TTS: Initialization failed" }
    }
  }

  private fun scrollChat(scrollAmount: Int) {
    activityChatBinding.scrollView.scrollBy(0, scrollAmount)
  }
}