package com.pratik.podcastplayer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pratik.podcastplayer.settings.ThemePreference

@Composable
fun ProfileScreen(
    selectedTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val versionName = appVersionName()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Column {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Customize your listening experience.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )
        }

        SettingsSection(title = "Appearance") {
            ThemeOption.entries.forEachIndexed { index, option ->
                ThemeOptionRow(
                    icon = option.icon,
                    label = option.preference.label,
                    description = option.description,
                    selected = selectedTheme == option.preference,
                    onClick = { onThemeSelected(option.preference) }
                )

                if (index != ThemeOption.entries.lastIndex) {
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        SettingsSection(title = "About") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(colors.primaryContainer, colors.tertiaryContainer)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = colors.onPrimaryContainer
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Podcast Player",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Version $versionName",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Podcast search and episode data are powered by the iTunes Search API.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    icon: ImageVector,
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(if (selected) colors.primaryContainer else colors.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (selected) colors.primary else colors.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) colors.onPrimaryContainer else colors.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = colors.primary
            )
        }
    }
}

private enum class ThemeOption(
    val preference: ThemePreference,
    val icon: ImageVector,
    val description: String
) {
    System(
        preference = ThemePreference.System,
        icon = Icons.Default.SettingsBrightness,
        description = "Match your device setting"
    ),
    Light(
        preference = ThemePreference.Light,
        icon = Icons.Default.LightMode,
        description = "Always use light theme"
    ),
    Dark(
        preference = ThemePreference.Dark,
        icon = Icons.Default.DarkMode,
        description = "Always use dark theme"
    )
}

@Composable
private fun appVersionName(): String {
    val context = LocalContext.current
    return remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }
}
