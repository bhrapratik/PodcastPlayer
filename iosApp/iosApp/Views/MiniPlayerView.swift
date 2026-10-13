import SwiftUI

struct MiniPlayerView: View {
    @EnvironmentObject private var audioPlayer: PodcastAudioPlayer

    private var progress: Double {
        let state = audioPlayer.uiState
        guard state.duration > 0 else { return 0 }
        return min(max(state.currentPosition / state.duration, 0), 1)
    }

    private var statusText: String {
        let state = audioPlayer.uiState
        if state.isBuffering { return "Loading…" }
        if state.duration > 0 {
            let remaining = formatted(state.duration - state.currentPosition)
            return state.isPlaying ? "Now playing · \(remaining) left" : "Paused · \(remaining) left"
        }
        return state.isPlaying ? "Now playing" : "Paused"
    }

    var body: some View {
        HStack(spacing: 12) {
            PodcastArtwork(url: nil, size: 44, cornerRadius: 12)

            VStack(alignment: .leading, spacing: 2) {
                Text(audioPlayer.uiState.currentEpisodeTitle ?? "")
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(Color.appOnSurface)
                    .lineLimit(1)
                Text(statusText)
                    .font(.caption)
                    .foregroundStyle(audioPlayer.uiState.isPlaying ? Color.brandPrimary : Color.appOnSurfaceVariant)
                    .lineLimit(1)
            }

            Spacer(minLength: 8)

            ZStack {
                Circle().stroke(Color.appOutline, lineWidth: 3)

                if audioPlayer.uiState.isBuffering {
                    ProgressView()
                } else {
                    Circle()
                        .trim(from: 0, to: progress)
                        .stroke(Color.brandPrimary, style: StrokeStyle(lineWidth: 3, lineCap: .round))
                        .rotationEffect(.degrees(-90))
                }

                Button {
                    audioPlayer.uiState.isPlaying ? audioPlayer.pause() : audioPlayer.resume()
                } label: {
                    Image(systemName: audioPlayer.uiState.isPlaying ? "pause.fill" : "play.fill")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(Color.brandOnPrimary)
                        .frame(width: 32, height: 32)
                        .background(Circle().fill(Color.brandPrimary))
                }
                .buttonStyle(.plain)
                .disabled(audioPlayer.uiState.isBuffering)
            }
            .frame(width: 44, height: 44)
        }
        .padding(10)
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 18))
        .padding(.horizontal, 12)
        .padding(.top, 6)
    }

    private func formatted(_ seconds: Double) -> String {
        let total = max(Int(seconds), 0)
        let h = total / 3600
        let m = (total % 3600) / 60
        let s = total % 60
        return h > 0 ? String(format: "%d:%02d:%02d", h, m, s) : String(format: "%d:%02d", m, s)
    }
}
