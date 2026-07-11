package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.TestDispatcherProvider
import com.devindie.myday.data.coroutines.runDataTest
import com.devindie.myday.domain.model.reflection.ReflectionError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class OpenRouterReflectionDataSourceTest {
    @Test
    fun sendsBearerAndModel() = runDataTest { dispatchers ->
        val engine =
            MockEngine { request ->
                assertEquals("Bearer sk-test", request.headers[HttpHeaders.Authorization])
                respond(
                    content = """{"choices":[{"message":{"content":"## Today at a Glance\n\nHi"}}]}""",
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val dataSource = createDataSource(engine, dispatchers)

        val text = dataSource.generate(source = "journal", model = "openai/gpt-4o-mini", apiKey = "sk-test")

        assertTrue(text.getOrThrow().contains("Today at a Glance"))
    }

    @Test
    fun unauthorized_mapsToProvider() = runDataTest { dispatchers ->
        val engine =
            MockEngine {
                respond(
                    content = """{"error":"invalid key"}""",
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val dataSource = createDataSource(engine, dispatchers)

        val result = dataSource.generate(source = "journal", model = "openai/gpt-4o-mini", apiKey = "bad")

        assertTrue(result.isFailure)
        val error = assertIs<ReflectionError.Provider>(result.exceptionOrNull())
        assertEquals(401, error.code)
    }

    @Test
    fun emptyContent_mapsToMalformedOutput() = runDataTest { dispatchers ->
        val engine =
            MockEngine {
                respond(
                    content = """{"choices":[{"message":{"content":"   "}}]}""",
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val dataSource = createDataSource(engine, dispatchers)

        val result = dataSource.generate(source = "journal", model = "openai/gpt-4o-mini", apiKey = "sk-test")

        assertTrue(result.isFailure)
        assertIs<ReflectionError.MalformedOutput>(result.exceptionOrNull())
    }

    private fun createDataSource(
        engine: MockEngine,
        dispatchers: TestDispatcherProvider,
    ): OpenRouterReflectionDataSource {
        val client =
            HttpClient(engine) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        return OpenRouterReflectionDataSource(
            httpClient = client,
            dispatchers = dispatchers,
        )
    }
}
