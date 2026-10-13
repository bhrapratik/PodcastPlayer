package com.pratik.podcastplayer.di

import com.pratik.podcastplayer.player.PodcastPlayer
import com.pratik.podcastplayer.settings.ThemePreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { PodcastPlayer(androidContext()) }
    single { ThemePreferenceStore(androidContext()) }
}