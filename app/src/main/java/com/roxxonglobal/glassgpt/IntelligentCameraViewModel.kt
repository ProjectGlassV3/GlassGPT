package com.roxxonglobal.glassgpt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntelligentCameraViewModel : ViewModel() {
    private val chatRepository = ChatRepository()
    private val _imageDescription = MutableLiveData<String>()
    val imageDescription: LiveData<String> = _imageDescription

    fun requestImageDescription(imageBytes: ByteArray) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                chatRepository.getImageDescription(imageBytes)
            }

            _imageDescription.postValue(response)
        }
    }
}