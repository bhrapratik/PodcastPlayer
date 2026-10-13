package com.pratik.podcastplayer.data.model

data class PodcastEpisode(
    val id: String,
    val title: String,
    val description: String?,
    val audioUrl: String?,
    val artworkUrl: String?,
    val publishedDate: String?,
    val duration: String?
)