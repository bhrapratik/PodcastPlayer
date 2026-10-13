import AVFoundation
import Combine

struct PodcastPlayerUiState {
    var isPlaying = false
    var isBuffering = false
    var currentEpisodeId: String?
    var currentEpisodeTitle: String?
    var currentPosition: Double = 0 // seconds
    var duration: Double = 0 // seconds
}

/// Native AVPlayer-backed playback, mirroring the shape of Android's `PodcastPlayer`.
/// Foreground playback only for now - no background-audio capability or lock-screen
/// controls (that's the equivalent of Android's `PlaybackService` and is a separate ask).
final class PodcastAudioPlayer: ObservableObject {
    @Published private(set) var uiState = PodcastPlayerUiState()

    private let player = AVPlayer()
    private var timeObserverToken: Any?
    private var timeControlObservation: NSKeyValueObservation?
    private var endObserver: NSObjectProtocol?

    init() {
        try? AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)

        timeObserverToken = player.addPeriodicTimeObserver(
            forInterval: CMTime(seconds: 0.5, preferredTimescale: 600),
            queue: .main
        ) { [weak self] time in
            guard let self else { return }
            let duration = self.player.currentItem?.duration.seconds ?? 0
            self.uiState.currentPosition = time.seconds.isFinite ? time.seconds : 0
            self.uiState.duration = (duration.isFinite && duration > 0) ? duration : 0
        }

        timeControlObservation = player.observe(\.timeControlStatus, options: [.new]) { [weak self] player, _ in
            DispatchQueue.main.async {
                self?.uiState.isPlaying = player.timeControlStatus == .playing
                self?.uiState.isBuffering = player.timeControlStatus == .waitingToPlayAtSpecifiedRate
            }
        }
    }

    func play(url: String, episodeId: String, episodeTitle: String) {
        try? AVAudioSession.sharedInstance().setActive(true)

        if uiState.currentEpisodeId == episodeId {
            // Resume the current episode from its paused position.
            player.play()
            return
        }

        guard let mediaUrl = URL(string: url) else { return }

        removeEndObserver()
        let item = AVPlayerItem(url: mediaUrl)
        player.replaceCurrentItem(with: item)

        uiState.currentEpisodeId = episodeId
        uiState.currentEpisodeTitle = episodeTitle
        uiState.currentPosition = 0
        uiState.duration = 0

        endObserver = NotificationCenter.default.addObserver(
            forName: .AVPlayerItemDidPlayToEndTime,
            object: item,
            queue: .main
        ) { [weak self] _ in
            self?.uiState.isPlaying = false
        }

        player.play()
    }

    func pause() {
        player.pause()
    }

    func resume() {
        player.play()
    }

    private func removeEndObserver() {
        if let endObserver {
            NotificationCenter.default.removeObserver(endObserver)
        }
        endObserver = nil
    }

    deinit {
        if let timeObserverToken {
            player.removeTimeObserver(timeObserverToken)
        }
        removeEndObserver()
    }
}
