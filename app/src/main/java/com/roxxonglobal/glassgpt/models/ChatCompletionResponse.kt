package com.roxxonglobal.glassgpt.models

data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)