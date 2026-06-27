package com.devindie.myday.data.auth

import com.devindie.myday.data.network.ApiPaths
import com.devindie.myday.data.network.ApiResult
import com.devindie.myday.data.network.NetworkConfig
import com.devindie.myday.data.network.dto.RefreshTokenRequestDto
import com.devindie.myday.data.network.dto.TokenResponseDto
import com.devindie.myday.data.network.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Refreshes access tokens using a plain [HttpClient] without the Auth plugin (avoids refresh recursion).
 */
class TokenRefreshDataSource(private val refreshClient: HttpClient, private val networkConfig: NetworkConfig) {
    suspend fun refresh(refreshToken: String): ApiResult<TokenResponseDto> = safeApiCall {
        refreshClient.post(networkConfig.baseUrl.trimEnd('/') + ApiPaths.AUTH_REFRESH) {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequestDto(refreshToken = refreshToken))
        }.body()
    }
}
