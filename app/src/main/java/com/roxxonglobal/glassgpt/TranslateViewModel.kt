package com.roxxonglobal.glassgpt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TranslateViewModel : ViewModel() {
    private val chatRepository = ChatRepository()
    private val _translation = MutableLiveData<String>()
    val translation: LiveData<String> = _translation

    fun requestTranslation(voiceInput: String) {
        viewModelScope.launch {
            val prompt = "Determine the language of the following text and translate to English, just giving the translation and nothing else: $voiceInput"

            val response = withContext(Dispatchers.IO) {
                chatRepository.getResponse(prompt)
            }

            _translation.postValue(response)
        }
    }
}