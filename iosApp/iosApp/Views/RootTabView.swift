import SwiftUI

struct RootTabView: View {
    @StateObject private var themeStore = ThemeStore()
    @StateObject private var podcastListViewModel = PodcastListViewModel()
    @StateObject private var audioPlayer = PodcastAudioPlayer()

    var body: some View {
        TabView {
            NavigationStack {
                DiscoverView()
            }
            .tabItem {
                Label("Discover", systemImage: "safari")
            }

            NavigationStack {
                ProfileView()
            }
            .tabItem {
                Label("Profile", systemImage: "person.crop.circle")
            }
        }
        .safeAreaInset(edge: .bottom, spacing: 0) {
            if audioPlayer.uiState.currentEpisodeId != nil {
                MiniPlayerView()
            }
        }
        .environmentObject(podcastListViewModel)
        .environmentObject(audioPlayer)
        .environmentObject(themeStore)
        .tint(.brandPrimary)
        .preferredColorScheme(themeStore.preference.colorScheme)
    }
}
