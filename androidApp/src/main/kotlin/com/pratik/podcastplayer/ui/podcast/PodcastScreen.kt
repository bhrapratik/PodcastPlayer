
package com.pratik.podcastplayer.ui.podcast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pratik.podcastplayer.data.model.Podcast
import com.pratik.podcastplayer.presentation.podcast.PodcastUiState
import com.pratik.podcastplayer.ui.components.EmptyState
import com.pratik.podcastplayer.ui.components.EmptyStateTone
import com.pratik.podcastplayer.ui.components.PodcastListItemShimmer

@Composable
fun PodcastScreen(
    uiState: PodcastUiState,
    onSearch: (String) -> Unit,
    onPodcastClick: (Podcast) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var query by rememberSaveable { mutableStateOf("technology") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "PODCAST PLAYER",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Find your next favorite listen.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Search
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Search podcasts...")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        query = ""
                                        onSearch("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { onSearch(query) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            focusedTextColor = colors.onSurface,
                            unfocusedTextColor = colors.onSurface,
                            focusedPlaceholderColor = colors.onSurfaceVariant,
                            unfocusedPlaceholderColor = colors.onSurfaceVariant,
                            focusedLeadingIconColor = colors.primary,
                            unfocusedLeadingIconColor = colors.onSurfaceVariant,
                            cursorColor = colors.primary
                        )
                    )

                    Button(
                        onClick = { onSearch(query) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "Search podcasts",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Initial loading: skeleton rows instead of a bare spinner
            if (uiState.isLoading && uiState.podcasts.isEmpty()) {
                items(count = 4) {
                    PodcastListItemShimmer()
                }
            }

            // Refreshing existing results
            if (uiState.isLoading && uiState.podcasts.isNotEmpty()) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                        color = colors.primary,
                        trackColor = colors.surfaceVariant
                    )
                }
            }

            // Error state
            uiState.error?.let { error ->
                item {
                    EmptyState(
                        icon = Icons.Default.WifiOff,
                        title = "Something went wrong",
                        message = error,
                        tone = EmptyStateTone.Error
                    )
                }
            }

            // Empty state
            if (!uiState.isLoading &&
                uiState.error == null &&
                uiState.podcasts.isEmpty()
            ) {
                item {
                    EmptyState(
                        icon = Icons.Default.SearchOff,
                        title = "No podcasts found",
                        message = "Try another search term."
                    )
                }
            }

            // Podcast list
            if (uiState.podcasts.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Podcasts for you",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.weight(1f))

                        Text(
                            text = "${uiState.podcasts.size} results",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                items(
                    items = uiState.podcasts,
                    key = { it.id }
                ) { podcast ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPodcastClick(podcast)
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        color = colors.primaryContainer,
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if(!podcast.artworkUrl.isNullOrBlank()){
                                    AsyncImage(
                                        model = podcast.artworkUrl,
                                        contentDescription = "${podcast.title} artwork",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else{
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = colors.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = podcast.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = podcast.author,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Open podcast",
                                tint = colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
