import SwiftUI
import SharedLogic

/// Bridges the shared `PodcastViewModelBridge` (from `sharedLogic`'s iosMain) into a SwiftUI
/// `ObservableObject`. One instance is shared app-wide via `.environmentObject`, mirroring how
/// Android resolves a single `PodcastViewModel` across the Discover and Details screens.
@MainActor
final class PodcastListViewModel: ObservableObject {
    @Published var uiState = PodcastUiState(
        isLoading: true,
        podcasts: [],
        error: nil,
        episodes: [],
        isLoadingEpisodes: false,
        episodeError: nil
    )

    private let bridge = PodcastViewModelBridge()

    init() {
        bridge.start { [weak self] state in
            DispatchQueue.main.async {
                self?.uiState = state
            }
        }
    }

    func search(query: String) {
        bridge.onSearchQueryChanged(query: query)
    }

    func loadEpisodes(feedUrl: String?) {
        bridge.loadEpisodes(feedUrl: feedUrl)
    }
}
