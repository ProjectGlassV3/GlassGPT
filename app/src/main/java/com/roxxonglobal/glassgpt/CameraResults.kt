package com.roxxonglobal.glassgpt

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MotionEvent
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.roxxonglobal.glassgpt.databinding.ActivityChatBinding
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.OnGestureListener
import logcat.LogPriority
import logcat.logcat
import java.util.*

class CameraResults : AppCompatActivity(), OnGestureListener, TextToSpeech.OnInitListener {
    private var glassGestureDetector: GlassGestureDetector? = null
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

        activityChatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(activityChatBinding.root)

        val description = intent.getStringExtra("description") ?: "No description available"
        activityChatBinding.results.text = description
        speakOut(description)

        activityChatBinding.instruction.text = "Tap to Capture"
    }

    override fun onDestroy() {
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
            Gesture.TAP -> {
                startActivity(Intent(this, IntelligentCamera::class.java))
                true
            }
            Gesture.SWIPE_DOWN -> {
                scrollChat(-100)
                true
            }
            Gesture.SWIPE_UP -> {
                scrollChat(100)
                true
            }
            else -> false
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