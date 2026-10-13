import SwiftUI
import SharedLogic

struct DiscoverView: View {
    @EnvironmentObject private var viewModel: PodcastListViewModel
    @State private var searchText: String = "technology"

    var body: some View {
        List {
            if viewModel.uiState.isLoading && viewModel.uiState.podcasts.isEmpty {
                ForEach(0..<4, id: \.self) { _ in
                    PodcastRowShimmer()
                }
            } else if let error = viewModel.uiState.error {
                EmptyStateView(
                    systemImage: "wifi.slash",
                    title: "Something went wrong",
                    message: error,
                    tone: .error
                )
            } else if viewModel.uiState.podcasts.isEmpty {
                EmptyStateView(
                    systemImage: "magnifyingglass",
                    title: "No podcasts found",
                    message: "Try another search term."
                )
            } else {
                ForEach(viewModel.uiState.podcasts, id: \.id) { podcast in
                    NavigationLink {
                        PodcastDetailsView(podcast: podcast)
                    } label: {
                        PodcastRow(podcast: podcast)
                    }
                }
            }
        }
        .listStyle(.plain)
        .navigationTitle("Discover")
        .searchable(text: $searchText, prompt: "Search podcasts")
        .onChange(of: searchText) { _, newValue in
            viewModel.search(query: newValue)
        }
        .onAppear {
            viewModel.search(query: searchText)
        }
    }
}

private struct PodcastRow: View {
    let podcast: Podcast

    var body: some View {
        HStack(spacing: 14) {
            PodcastArtwork(url: podcast.artworkUrl, size: 52)

            VStack(alignment: .leading, spacing: 2) {
                Text(podcast.title)
                    .font(.headline)
                    .foregroundStyle(Color.appOnSurface)
                    .lineLimit(2)
                Text(podcast.author)
                    .font(.subheadline)
                    .foregroundStyle(Color.appOnSurfaceVariant)
                    .lineLimit(1)
            }
        }
        .padding(.vertical, 4)
    }
}
