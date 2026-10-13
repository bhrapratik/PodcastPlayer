package com.pratik.podcastplayer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PodcastSearchResponse(
    val resultCount: Int,
    val results: List<PodcastDto>
)

@Serializable
data class PodcastDto(
    @SerialName("collectionId")
    val id: Long,

    @SerialName("collectionName")
    val title: String,

    @SerialName("artistName")
    val author: String,

    @SerialName("artworkUrl600")
    val artworkUrl: String? = null,

    @SerialName("feedUrl")
    val feedUrl: String? = null,

    val genres: List<String> = emptyList(),

    @SerialName("trackCount")
    val episodeCount: Int? = null
) {
    fun toDomain(): Podcast = Podcast(
        id = id,
        title = title,
        author = author,
        artworkUrl = artworkUrl,
        feedUrl = feedUrl
    )
}