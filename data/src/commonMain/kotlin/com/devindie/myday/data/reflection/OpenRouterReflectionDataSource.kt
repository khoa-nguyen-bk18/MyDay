package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.data.coroutines.runIoResult
import com.devindie.myday.domain.model.reflection.ReflectionError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val OPENROUTER_CHAT_COMPLETIONS_URL = "https://openrouter.ai/api/v1/chat/completions"

class OpenRouterReflectionDataSource(private val httpClient: HttpClient, private val dispatchers: DispatcherProvider) {
    suspend fun generate(source: String, model: String, apiKey: String): Result<String> = complete(
        model = model,
        apiKey = apiKey,
        userMessage = ReflectionPrompts.userMessageForGeneration(source),
    )

    suspend fun shorten(currentMarkdown: String, model: String, apiKey: String): Result<String> = complete(
        model = model,
        apiKey = apiKey,
        userMessage = ReflectionPrompts.userMessageForShorten(currentMarkdown),
    )

    @Suppress("SwallowedException")
    private suspend fun complete(model: String, apiKey: String, userMessage: String): Result<String> =
        withContext(dispatchers.io) {
            runIoResult {
                try {
                    val response =
                        httpClient.post(OPENROUTER_CHAT_COMPLETIONS_URL) {
                            contentType(ContentType.Application.Json)
                            header(HttpHeaders.Authorization, "Bearer $apiKey")
                            setBody(
                                ChatCompletionRequest(
                                    model = model,
                                    messages =
                                    listOf(
                                        ChatMessage(role = "system", content = ReflectionPrompts.systemPrompt),
                                        ChatMessage(role = "user", content = userMessage),
                                    ),
                                ),
                            )
                        }
                    parseCompletion(response)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: ClientRequestException) {
                    throw mapHttpException(e.response)
                } catch (e: ServerResponseException) {
                    throw mapHttpException(e.response)
                } catch (e: JsonConvertException) {
                    throw ReflectionError.MalformedOutput
                } catch (e: ReflectionError) {
                    throw e
                } catch (_: Exception) {
                    throw ReflectionError.Network
                }
            }
        }

    private suspend fun parseCompletion(response: HttpResponse): String {
        if (!response.status.isSuccess()) {
            throw mapHttpStatus(response.status.value)
        }
        val body = response.body<ChatCompletionResponse>()
        val content =
            body.choices
                .firstOrNull()
                ?.message
                ?.content
                ?.trim()
        if (content.isNullOrEmpty()) {
            throw ReflectionError.MalformedOutput
        }
        return content
    }

    private fun mapHttpException(response: HttpResponse): ReflectionError {
        val status = response.status.value
        return mapHttpStatus(status)
    }

    private fun mapHttpStatus(status: Int): ReflectionError = when (status) {
        HttpStatusCode.Unauthorized.value ->
            ReflectionError.Provider(code = status, message = "OpenRouter authentication failed")
        else -> ReflectionError.Provider(code = status, message = "OpenRouter request failed")
    }
}

@Serializable
private data class ChatCompletionRequest(val model: String, val messages: List<ChatMessage>)

@Serializable
private data class ChatMessage(val role: String, val content: String)

@Serializable
private data class ChatCompletionResponse(val choices: List<ChatChoice> = emptyList())

@Serializable
private data class ChatChoice(val message: ChatChoiceMessage? = null)

@Serializable
private data class ChatChoiceMessage(val content: String? = null, @SerialName("role") val role: String? = null)
