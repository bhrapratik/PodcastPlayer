package com.pratik.podcastplayer.data.repository

import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.data.model.PodcastEpisode
import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    fun searchPodcasts(query: String): Flow<List<Podcast>>

    fun getEpisodes(feedUrl: String): Flow<List<PodcastEpisode>>
}