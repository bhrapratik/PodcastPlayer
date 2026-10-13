package com.pratik.podcastplayer.di

import com.pratik.podcastplayer.data.repository.PodcastRepository
import com.pratik.podcastplayer.data.repository.PodcastRepositoryImpl
import com.pratik.podcastplayer.data.repository.RssFeedParser
import com.pratik.podcastplayer.network.NetworkClient
import com.pratik.podcastplayer.network.PodcastApiService
import com.pratik.podcastplayer.presentation.podcast.PodcastViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { NetworkClient.httpClient }
    single { PodcastApiService(get()) }
    single { RssFeedParser() }
    single<PodcastRepository> {
        PodcastRepositoryImpl(apiService = get(), rssFeedParser = get())
    }

    viewModel {
        PodcastViewModel(get())
    }
}