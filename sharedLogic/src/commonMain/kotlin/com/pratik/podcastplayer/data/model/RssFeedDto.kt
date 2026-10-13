package com.pratik.podcastplayer.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("rss", "", "")
data class RssFeedDto(
    @XmlSerialName("version", "", "")
    @XmlElement(false)
    val version: String? = null,
    @XmlElement(true) val channel: RssChannelDto,
)

@Serializable
@XmlSerialName("channel", "", "")
data class RssChannelDto(
    @XmlElement(true) val item: List<RssItemDto> = emptyList()
)

@Serializable
@XmlSerialName("item", "", "")
data class RssItemDto(
    @XmlElement(true) val title: String = "",

    @XmlElement(true) val description: String? = null,

    @XmlElement(true) val guid: String? = null,

    @XmlElement(true) val link: String? = null,

    @XmlElement(true) val pubDate: String? = null,

    @XmlElement(true) val enclosure: RssEnclosureDto? = null
)

@Serializable
@XmlSerialName("enclosure", "", "")
data class RssEnclosureDto(
    @XmlSerialName("url", "", "") @XmlElement(false) val url: String? = null,

    @XmlSerialName("type", "", "") @XmlElement(false) val type: String? = null
)