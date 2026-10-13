package com.pratik.podcastplayer.ui.podcast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.data.model.PodcastEpisode
import com.pratik.podcastplayer.ui.components.EmptyState
import com.pratik.podcastplayer.ui.components.EmptyStateTone
import com.pratik.podcastplayer.ui.components.EpisodeListItemShimmer

@Composable
fun PodcastDetailsScreen(
    podcast: Podcast,
    episodes: List<PodcastEpisode>,
    isLoadingEpisodes: Boolean,
    episodeError: String?,
    onBack: () -> Unit,
    playingEpisodeId: String?,
    onPlayClick: (PodcastEpisode) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding()
    ) {
        FilledTonalIconButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to podcasts"
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PodcastHeader(podcast)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Episodes",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.weight(1f))

                    if (episodes.isNotEmpty()) {
                        Text(
                            text = "${episodes.size} episodes",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            episodeListContent(
                episodes = episodes,
                playingEpisodeId = playingEpisodeId,
                isLoading = isLoadingEpisodes,
                error = episodeError,
                onPlayClick = onPlayClick
            )
        }
    }
}

@Composable
private fun PodcastHeader(podcast: Podcast) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(176.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(colors.primaryContainer, colors.tertiaryContainer)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!podcast.artworkUrl.isNullOrBlank()) {
                AsyncImage(
                    model = podcast.artworkUrl,
                    contentDescription = "${podcast.title} artwork",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = colors.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = podcast.title,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = podcast.author,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun LazyListScope.episodeListContent(
    episodes: List<PodcastEpisode>,
    playingEpisodeId: String?,
    isLoading: Boolean,
    error: String?,
    onPlayClick: (PodcastEpisode) -> Unit
) {
    when {
        isLoading -> {
            items(count = 4) {
                EpisodeListItemShimmer()
            }
        }

        error != null -> {
            item {
                EmptyState(
                    icon = Icons.Default.WifiOff,
                    title = "Couldn't load episodes",
                    message = error,
                    tone = EmptyStateTone.Error
                )
            }
        }

        episodes.isEmpty() -> {
            item {
                EmptyState(
                    icon = Icons.Default.MusicOff,
                    title = "No episodes yet",
                    message = "This podcast has no episodes available right now."
                )
            }
        }

        else -> {
            items(items = episodes, key = { it.id }) { episode ->
                EpisodeItem(
                    episode = episode,
                    isPlaying = playingEpisodeId == episode.id,
                    onPlayClick = onPlayClick
                )
            }
        }
    }
}

@Composable
private fun EpisodeItem(
    episode: PodcastEpisode,
    isPlaying: Boolean,
    onPlayClick: (PodcastEpisode) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val playable = !episode.audioUrl.isNullOrBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) colors.primaryContainer else colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { onPlayClick(episode) },
                enabled = playable,
                modifier = Modifier.size(44.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause episode" else "Play episode",
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isPlaying) colors.onPrimaryContainer else colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                episode.publishedDate?.let { date ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPlaying) colors.onPrimaryContainer else colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
