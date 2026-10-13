package com.pratik.podcastplayer.data.repository

import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.data.model.PodcastEpisode
import com.pratik.podcastplayer.network.PodcastApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PodcastRepositoryImpl(
    private val apiService: PodcastApiService,
    private val rssFeedParser: RssFeedParser
) : PodcastRepository {

    override fun searchPodcasts(query: String): Flow<List<Podcast>> = flow {
        val podcasts = apiService.searchPodcasts(query).map { it.toDomain() }
        emit(podcasts)
    }

    override fun getEpisodes(feedUrl: String): Flow<List<PodcastEpisode>> = flow {
        val xmlContent = apiService.getEpisodes(feedUrl)
        val episodes = rssFeedParser.parse(xmlContent)

        emit(episodes)
    }
}