import SwiftUI

/// Podcast/episode artwork with a branded gradient + music note fallback when there's no
/// artwork URL or the image fails to load.
struct PodcastArtwork: View {
    let url: String?
    var size: CGFloat = 52
    var cornerRadius: CGFloat = 12

    var body: some View {
        ZStack {
            LinearGradient(
                colors: [.brandPrimaryContainer, .brandPrimary.opacity(0.45)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )

            if let url, let imageUrl = URL(string: url) {
                AsyncImage(url: imageUrl) { phase in
                    if case .success(let image) = phase {
                        image.resizable().aspectRatio(contentMode: .fill)
                    } else {
                        fallbackIcon
                    }
                }
            } else {
                fallbackIcon
            }
        }
        .frame(width: size, height: size)
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
    }

    private var fallbackIcon: some View {
        Image(systemName: "music.note")
            .font(.system(size: size * 0.4))
            .foregroundStyle(Color.brandOnPrimaryContainer)
    }
}
