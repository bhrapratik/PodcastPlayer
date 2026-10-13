import SwiftUI

/// Loading placeholders using SwiftUI's native `.redacted(reason: .placeholder)` instead of a
/// hand-rolled shimmer animation, to keep the loading state feeling like a standard iOS list.
struct PodcastRowShimmer: View {
    var body: some View {
        HStack(spacing: 14) {
            RoundedRectangle(cornerRadius: 12)
                .fill(Color.appSurfaceVariant)
                .frame(width: 52, height: 52)

            VStack(alignment: .leading, spacing: 6) {
                Text("Loading podcast title")
                    .font(.headline)
                Text("Loading author")
                    .font(.subheadline)
            }
        }
        .redacted(reason: .placeholder)
        .padding(.vertical, 4)
    }
}

struct EpisodeRowShimmer: View {
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "play.circle.fill")
                .font(.system(size: 32))

            VStack(alignment: .leading, spacing: 4) {
                Text("Loading episode title")
                    .font(.subheadline.weight(.semibold))
                Text("Loading date")
                    .font(.caption)
            }
        }
        .redacted(reason: .placeholder)
        .padding(.vertical, 4)
    }
}
