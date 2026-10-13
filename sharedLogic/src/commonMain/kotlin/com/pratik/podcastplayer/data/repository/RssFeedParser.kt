package com.pratik.podcastplayer.data.repository

import com.pratik.podcastplayer.data.model.PodcastEpisode
import com.pratik.podcastplayer.data.model.RssFeedDto
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

class RssFeedParser {

    private val xml = XML {
        defaultPolicy {
            ignoreUnknownChildren()
        }
    }

    fun parse(xmlContent: String): List<PodcastEpisode> {
        val feed = xml.decodeFromString<RssFeedDto>(xmlContent)

        return feed.channel.item.mapIndexed { index, item ->
            PodcastEpisode(
                id = item.guid
                    ?.takeIf { it.isNotBlank() }
                    ?: item.link
                    ?: "${item.title}-$index",
                title = item.title,
                description = item.description,
                audioUrl = item.enclosure?.url,
                artworkUrl = null,
                publishedDate = item.pubDate,
                duration = null
            )
        }
    }
}