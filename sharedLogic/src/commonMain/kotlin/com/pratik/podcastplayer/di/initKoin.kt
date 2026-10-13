package com.pratik.podcastplayer.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module


fun initKoin(
    additionalModules: List<Module> = emptyList(),
    appDeclaration: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        appDeclaration?.invoke(this)
        modules(listOf(appModule) + additionalModules)
    }
}
