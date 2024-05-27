package com.roxxonglobal.glassgpt

import com.roxxonglobal.glassgpt.models.ChatCompletionRequest
import com.roxxonglobal.glassgpt.models.Message
import com.roxxonglobal.glassgpt.network.OpenAiApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ChatRepository {

  private val apiKey = BuildConfig.AZURE_OPENAI_API_KEY
  private val endpoint = BuildConfig.AZURE_OPENAI_ENDPOINT

  private val api: OpenAiApi

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
      .build()

    val retrofit = Retrofit.Builder()
      .baseUrl(endpoint)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    api = retrofit.create(OpenAiApi::class.java)
  }

  suspend fun getResponse(voiceInput: String): String {
    val request = ChatCompletionRequest(
      messages = listOf(
        Message(role = "user", content = voiceInput)
      )
    )

    val response = withContext(Dispatchers.IO) {
      api.getChatCompletion("4o", request)
    }

    require(response.choices.isNotEmpty()) { "Response was empty from GPT" }
    return response.choices[0].message.content
  }
}