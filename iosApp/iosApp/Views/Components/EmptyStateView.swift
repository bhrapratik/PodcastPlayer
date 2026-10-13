import SwiftUI

enum EmptyStateTone {
    case neutral, error
}

/// Shared empty/error/placeholder visual, mirroring Android's `EmptyState` composable.
struct EmptyStateView: View {
    let systemImage: String
    let title: String
    let message: String
    var tone: EmptyStateTone = .neutral

    private var iconTint: Color { tone == .error ? .appError : .appOnSurfaceVariant }
    private var iconBackground: Color { tone == .error ? .appErrorContainer : .appSurfaceVariant }

    var body: some View {
        VStack(spacing: 8) {
            ZStack {
                Circle().fill(iconBackground).frame(width: 64, height: 64)
                Image(systemName: systemImage)
                    .font(.title2)
                    .foregroundStyle(iconTint)
            }

            Text(title)
                .font(.headline)
                .foregroundStyle(Color.appOnBackground)

            Text(message)
                .font(.subheadline)
                .foregroundStyle(Color.appOnSurfaceVariant)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 32)
        .padding(.horizontal, 16)
        .listRowSeparator(.hidden)
        .listRowBackground(Color.clear)
    }
}
