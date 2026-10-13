package com.pratik.podcastplayer.presentation.podcast

import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.data.model.PodcastEpisode

data class PodcastUiState(
    val isLoading: Boolean = false,
    val podcasts: List<Podcast> = emptyList(),
    val error: String? = null,
    val episodes: List<PodcastEpisode> = emptyList(),
    val isLoadingEpisodes: Boolean = false,
    val episodeError: String? = null
)
