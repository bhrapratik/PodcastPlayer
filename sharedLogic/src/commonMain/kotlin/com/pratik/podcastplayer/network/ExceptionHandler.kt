package com.pratik.podcastplayer.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

val Throwable.toUserMessage: String
    get() = when (this) {
        // 1. TIMEOUTS & NETWORK CONNECTION LOSSES
        is HttpRequestTimeoutException ->
            "The connection timed out. Please check your signal strength and try again."

        is IOException ->
            "Network error. Please make sure you are connected to the internet."

        // 2. HTTP SERVER RESPONSES (4xx & 5xx)
        is ClientRequestException -> {
            when (this.response.status.value) {
                401 -> "Session expired. Please log in again."
                403 -> "You don't have permission to view this content."
                404 -> "The requested podcast resource could not be found."
                429 -> "Too many requests. Please slow down and wait a moment."
                else -> "Request error (${this.response.status.value}). Please try again."
            }
        }

        is ServerResponseException ->
            "Our servers are experiencing issues right now. We are working to fix it!"

        is ResponseException ->
            "Unexpected server response (${this.response.status.value})."

        // 3. DATA & CORRUPTION PROBLEMS
        is SerializationException ->
            "Failed to process the data from the server. An app update might be required."

        is IllegalArgumentException, is IllegalStateException ->
            "An invalid action occurred. Please refresh or retry."



        // 5. UNEXPECTED FALLBACK
        else ->
            "An unexpected error occurred. Please try again."
    }