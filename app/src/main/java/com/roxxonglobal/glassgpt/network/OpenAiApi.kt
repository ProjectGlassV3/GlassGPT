package com.roxxonglobal.glassgpt.network

import com.roxxonglobal.glassgpt.models.ChatCompletionRequest
import com.roxxonglobal.glassgpt.models.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface OpenAiApi {

    @POST("openai/deployments/{deployment_id}/chat/completions?api-version=2024-02-15-preview")
    @Headers("Content-Type: application/json")
    suspend fun getChatCompletion(
        @Path("deployment_id") deploymentId: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}