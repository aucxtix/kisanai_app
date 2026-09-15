package com.example.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Serializable
data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String? = null
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val responseMimeType: String? = null
)

@Serializable
data class GeminiGenerateResponse(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null
)

/**
 * Robust, production-grade client for Google Gemini 2.5 Flash API.
 * Adheres strictly to AI Studio Android guidelines with safe timeouts and structured fallback.
 */
object GeminiApiClient {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    suspend fun generateContent(
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null,
        temperature: Float = 0.2f
    ): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null
        try {
            val requestPayload = GeminiGenerateRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                systemInstruction = systemInstruction?.let { GeminiContent(parts = listOf(GeminiPart(text = it))) },
                generationConfig = GeminiGenerationConfig(temperature = temperature)
            )

            val body = json.encodeToString(GeminiGenerateRequest.serializer(), requestPayload)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val responseBody = response.body?.string() ?: return@withContext null
                val result = json.decodeFromString(GeminiGenerateResponse.serializer(), responseBody)
                result.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
