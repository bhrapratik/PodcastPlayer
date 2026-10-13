import SwiftUI
import SharedLogic

struct PodcastDetailsView: View {
    let podcast: Podcast
    @EnvironmentObject private var viewModel: PodcastListViewModel

    var body: some View {
        List {
            Section {
                header
                    .listRowSeparator(.hidden)
                    .listRowBackground(Color.clear)
            }

            Section {
                episodesContent
            } header: {
                HStack {
                    Text("Episodes")
                    Spacer()
                    if !viewModel.uiState.episodes.isEmpty {
                        Text("\(viewModel.uiState.episodes.count) episodes")
                    }
                }
            }
        }
        .listStyle(.plain)
        .navigationTitle(podcast.title)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            viewModel.loadEpisodes(feedUrl: podcast.feedUrl)
        }
    }

    private var header: some View {
        VStack(spacing: 16) {
            PodcastArtwork(url: podcast.artworkUrl, size: 160, cornerRadius: 24)
                .shadow(color: .black.opacity(0.2), radius: 12, y: 6)

            VStack(spacing: 4) {
                Text(podcast.title)
                    .font(.title2.bold())
                    .multilineTextAlignment(.center)
                Text(podcast.author)
                    .font(.subheadline)
                    .foregroundStyle(Color.appOnSurfaceVariant)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 12)
    }

    @ViewBuilder
    private var episodesContent: some View {
        if viewModel.uiState.isLoadingEpisodes {
            ForEach(0..<4, id: \.self) { _ in EpisodeRowShimmer() }
        } else if let error = viewModel.uiState.episodeError {
            EmptyStateView(
                systemImage: "wifi.slash",
                title: "Couldn't load episodes",
                message: error,
                tone: .error
            )
        } else if viewModel.uiState.episodes.isEmpty {
            EmptyStateView(
                systemImage: "music.note.list",
                title: "No episodes yet",
                message: "This podcast has no episodes available right now."
            )
        } else {
            ForEach(viewModel.uiState.episodes, id: \.id) { episode in
                EpisodeRow(episode: episode)
            }
        }
    }
}

private struct EpisodeRow: View {
    let episode: PodcastEpisode
    @EnvironmentObject private var audioPlayer: PodcastAudioPlayer

    private var isCurrent: Bool { audioPlayer.uiState.currentEpisodeId == episode.id }
    private var isPlaying: Bool { isCurrent && audioPlayer.uiState.isPlaying }

    var body: some View {
        HStack(spacing: 12) {
            Button(action: togglePlay) {
                Image(systemName: isPlaying ? "pause.circle.fill" : "play.circle.fill")
                    .font(.system(size: 32))
                    .foregroundStyle(Color.brandPrimary)
            }
            .buttonStyle(.plain)
            .disabled(episode.audioUrl == nil)

            VStack(alignment: .leading, spacing: 2) {
                Text(episode.title)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(isCurrent ? Color.brandPrimary : Color.appOnSurface)
                    .lineLimit(2)
                if let date = episode.publishedDate {
                    Text(date)
                        .font(.caption)
                        .foregroundStyle(Color.appOnSurfaceVariant)
                }
            }
        }
        .padding(.vertical, 4)
    }

    private func togglePlay() {
        guard let audioUrl = episode.audioUrl else { return }
        if isCurrent && isPlaying {
            audioPlayer.pause()
        } else {
            audioPlayer.play(url: audioUrl, episodeId: episode.id, episodeTitle: episode.title)
        }
    }
}
