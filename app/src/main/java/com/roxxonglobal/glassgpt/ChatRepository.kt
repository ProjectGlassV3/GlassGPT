package com.roxxonglobal.glassgpt

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.service.OpenAiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This repository is responsible for communication with the OpenAI API.
 */
class ChatRepository {
  private val service = OpenAiService(BuildConfig.API_KEY)

  suspend fun getResponse(
    voiceInput: String,
  ): String {
    // Go request a response on background thread
    val result = withContext(Dispatchers.IO) {
      val chatCompletionRequest = ChatCompletionRequest.builder()
        .model("gpt-4-1106-preview")
        .messages(
          listOf(
            ChatMessage(
              "user",
              voiceInput,
            )
          )
        )
        .build()

      val response = service.createChatCompletion(chatCompletionRequest)

      require(response.choices.isNotEmpty()) { "Response was empty from GPT" }
      response.choices[0].message
    }

    // Return first message content
    return result.content
  }
}