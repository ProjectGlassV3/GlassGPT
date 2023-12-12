package com.roxxonglobal.glassgpt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {
  // TODO: use hilt instead
  private val chatRepository = ChatRepository()
  private val chatLog = ArrayDeque<String>()

  private val _chatHistory = MutableLiveData<String>()

  // This converts the chat history away from mutable live data so UI cant modify it, only VM
  val chatHistory: LiveData<String> = _chatHistory

  fun requestResponse(voiceInput: String) {
    viewModelScope.launch {
      // Add the user input first
      updateChatLog(voiceInput)

      // This will run in background to get the server response
      val response = withContext(Dispatchers.IO) {
        chatRepository.getResponse(voiceInput)
      }

      updateChatLog(response)
    }
  }

  private fun updateChatLog(entry: String) {
    chatLog.addFirst(entry)
    if (chatLog.size >= 4) {
      chatLog.removeLast()
    }

    // Update the UI
    _chatHistory.postValue(getChatLogForUi())
  }

  private fun getChatLogForUi() = chatLog.joinToString("\n")
}