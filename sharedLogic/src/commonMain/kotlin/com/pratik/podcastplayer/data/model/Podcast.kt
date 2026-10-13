package com.pratik.podcastplayer.data.model

data class Podcast(
    val id: Long,
    val title: String,
    val author: String,
    val artworkUrl: String?,
    val feedUrl: String?
)