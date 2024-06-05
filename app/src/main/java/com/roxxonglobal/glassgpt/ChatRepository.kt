package com.roxxonglobal.glassgpt

import com.roxxonglobal.glassgpt.network.ComputerVisionApi
import com.roxxonglobal.glassgpt.models.ChatCompletionRequest
import com.roxxonglobal.glassgpt.models.Message
import com.roxxonglobal.glassgpt.network.OpenAiApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChatRepository {

  private val apiKey = BuildConfig.AZURE_OPENAI_API_KEY
  private val endpoint = BuildConfig.AZURE_OPENAI_ENDPOINT
  private val visionEndpoint = BuildConfig.AZURE_COMPUTER_VISION_ENDPOINT
  private val visionApiKey = BuildConfig.AZURE_COMPUTER_VISION_API_KEY

  private val chatApi: OpenAiApi
  private val visionApi: ComputerVisionApi

  init {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
      .addInterceptor { chain ->
        val request = chain.request().newBuilder()
          .addHeader("api-key", apiKey)
          .build()
        chain.proceed(request)
      }
      .addInterceptor(logging)
      .connectTimeout(30, TimeUnit.SECONDS)  // Increase connect timeout
      .readTimeout(30, TimeUnit.SECONDS)     // Increase read timeout
      .writeTimeout(30, TimeUnit.SECONDS)    // Increase write timeout
      .build()

    val retrofit = Retrofit.Builder()
      .baseUrl(endpoint)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    chatApi = retrofit.create(OpenAiApi::class.java)

    val visionClient = OkHttpClient.Builder()
      .addInterceptor { chain ->
        val request = chain.request().newBuilder()
          .addHeader("Ocp-Apim-Subscription-Key", visionApiKey)
          .build()
        chain.proceed(request)
      }
      .addInterceptor(logging)
      .connectTimeout(30, TimeUnit.SECONDS)  // Increase connect timeout
      .readTimeout(30, TimeUnit.SECONDS)     // Increase read timeout
      .writeTimeout(30, TimeUnit.SECONDS)    // Increase write timeout
      .build()

    val visionRetrofit = Retrofit.Builder()
      .baseUrl(visionEndpoint)
      .client(visionClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    visionApi = visionRetrofit.create(ComputerVisionApi::class.java)
  }

  suspend fun getResponse(voiceInput: String): String {
    val request = ChatCompletionRequest(
      messages = listOf(
        Message(role = "user", content = voiceInput)
      )
    )

    val response = withContext(Dispatchers.IO) {
      chatApi.getChatCompletion("4o", request)
    }

    require(response.choices.isNotEmpty()) { "Response was empty from GPT" }
    return response.choices[0].message.content
  }

  suspend fun getImageDescription(imageBytes: ByteArray): String {
    val requestBody = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), imageBytes)
    val response = withContext(Dispatchers.IO) {
      val call = visionApi.analyzeImage("Description", requestBody)
      val response = call.execute()
      response.body() ?: throw Exception("Failed to analyze image")
    }

    require(response.description.captions.isNotEmpty()) { "No description available for the image" }
    return response.description.captions[0].text
  }
}