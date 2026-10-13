package com.pratik.podcastplayer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform