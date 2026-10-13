package com.pratik.podcastplayer.network

import com.pratik.podcastplayer.data.model.PodcastDto
import com.pratik.podcastplayer.data.model.PodcastEpisode
import com.pratik.podcastplayer.data.model.PodcastSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

class PodcastApiService(
    private val httpClient: HttpClient
) {
    suspend fun searchPodcasts(
        query: String,
        limit: Int = 20
    ): List<PodcastDto> {
        val response: PodcastSearchResponse =
            httpClient.get("${NetworkClient.baseUrl()}/search") {
                parameter("term", query)
                parameter("entity", "podcast")
                parameter("limit", limit)
            }.body()

        return response.results
    }


    suspend fun getEpisodes(
        feedUrl: String,
    ): String {
        val xmlContent = httpClient.get(feedUrl).bodyAsText()

        return xmlContent
    }
}