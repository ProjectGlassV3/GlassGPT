package com.roxxonglobal.glassgpt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.roxxonglobal.glassgpt.databinding.ActivityIntelligentCameraBinding
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.Gesture
import com.roxxonglobal.glassgpt.utils.GlassGestureDetector.OnGestureListener
import logcat.LogPriority
import logcat.logcat
import java.io.ByteArrayOutputStream
import java.util.*

class IntelligentCamera : AppCompatActivity(), OnGestureListener, TextToSpeech.OnInitListener {
    private var glassGestureDetector: GlassGestureDetector? = null
    private val viewModel = IntelligentCameraViewModel()
    private lateinit var tts: TextToSpeech
    private lateinit var activityIntelligentCameraBinding: ActivityIntelligentCameraBinding

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var imageReader: ImageReader

    private val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = false
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            glassGestureDetector = GlassGestureDetector(this, this)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { "Failed to create gesture detector: $e" }
        }

        tts = TextToSpeech(this, this)

        // Inflate view and set content to it
        activityIntelligentCameraBinding = ActivityIntelligentCameraBinding.inflate(layoutInflater)
        setContentView(activityIntelligentCameraBinding.root)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        activityIntelligentCameraBinding.textureView.surfaceTextureListener = textureListener

        imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            image.close()

            processImage(bytes)
        }, null)

        activityIntelligentCameraBinding.instruction.text = "Tap to Capture"

        viewModel.imageDescription.observe(this) { description ->
            val intent = Intent(this, CameraResults::class.java).apply {
                putExtra("description", description)
            }
            startActivity(intent)
        }
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
                captureImage()
                true
            }
            else -> false
        }
    }

    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
                return
            }
            cameraManager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCameraPreview() {
        try {
            val texture = activityIntelligentCameraBinding.textureView.surfaceTexture!!
            texture.setDefaultBufferSize(640, 480)
            val surface = Surface(texture)
            val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice!!.createCaptureSession(
                listOf(surface, imageReader.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        captureSession = cameraCaptureSession
                        updatePreview()
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        Toast.makeText(this@IntelligentCamera, "Configuration change", Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        if (cameraDevice == null) {
            Log.e("CameraPreview", "updatePreview error, return")
        }
        val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(Surface(activityIntelligentCameraBinding.textureView.surfaceTexture))
        captureSession?.setRepeatingRequest(captureRequestBuilder.build(), null, null)
    }

    private fun captureImage() {
        if (cameraDevice == null) return
        val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(imageReader.surface)

        cameraDevice!!.createCaptureSession(
            listOf(imageReader.surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(
                            captureRequestBuilder.build(),
                            object : CameraCaptureSession.CaptureCallback() {
                                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                                    super.onCaptureCompleted(session, request, result)
                                    Toast.makeText(this@IntelligentCamera, "Image Captured", Toast.LENGTH_SHORT).show()
                                    createCameraPreview()
                                }
                            },
                            null
                        )
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                    Toast.makeText(this@IntelligentCamera, "Capture Failed", Toast.LENGTH_SHORT).show()
                }
            },
            null
        )
    }

    private fun processImage(imageBytes: ByteArray) {
        viewModel.requestImageDescription(imageBytes)
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
}