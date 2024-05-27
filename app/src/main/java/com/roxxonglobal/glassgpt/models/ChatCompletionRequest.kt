package com.roxxonglobal.glassgpt.models

data class ChatCompletionRequest(
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)