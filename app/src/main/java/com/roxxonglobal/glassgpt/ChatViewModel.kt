package com.roxxonglobal.glassgpt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {
  private val chatRepository = ChatRepository()
  private val chatLog = ArrayDeque<String>()

  private val _chatHistory = MutableLiveData<String>()
  val chatHistory: LiveData<String> = _chatHistory

  private val _latestResponse = MutableLiveData<String>()
  val latestResponse: LiveData<String> = _latestResponse

  fun requestResponse(voiceInput: String) {
    viewModelScope.launch {
      updateChatLog(voiceInput)

      val response = withContext(Dispatchers.IO) {
        chatRepository.getResponse(voiceInput)
      }

      updateChatLog(response)
      _latestResponse.postValue(response)
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