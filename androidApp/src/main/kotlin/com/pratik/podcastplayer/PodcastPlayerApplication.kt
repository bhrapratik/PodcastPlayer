package com.pratik.podcastplayer

import android.app.Application
import com.pratik.podcastplayer.di.androidModule
import com.pratik.podcastplayer.di.initKoin
import org.koin.android.ext.koin.androidContext

class PodcastPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            additionalModules = listOf(
                androidModule,
            ),
            appDeclaration = {
                androidContext(this@PodcastPlayerApplication)
            },
        )
    }
}