package com.roxxonglobal.glassgpt.network

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class ImageCaption(val text: String, val confidence: Double)
data class ImageDescription(val captions: List<ImageCaption>)
data class ImageAnalysisResponse(val description: ImageDescription)

interface ComputerVisionApi {
    @POST("vision/v3.2/analyze?visualFeatures=Description&language=en&model-version=latest")
    @Headers("Content-Type: application/octet-stream")
    fun analyzeImage(
        @Query("visualFeatures") visualFeatures: String,
        @Body image: RequestBody
    ): Call<ImageAnalysisResponse>
}