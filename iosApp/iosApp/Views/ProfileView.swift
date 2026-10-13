import SwiftUI

struct ProfileView: View {
    @EnvironmentObject private var themeStore: ThemeStore

    var body: some View {
        List {
            Section("Appearance") {
                ForEach(ThemePreference.allCases) { option in
                    Button {
                        themeStore.preference = option
                    } label: {
                        themeRow(option)
                    }
                    .buttonStyle(.plain)
                }
            }

            Section("About") {
                HStack(spacing: 14) {
                    PodcastArtwork(url: nil, size: 44, cornerRadius: 12)

                    VStack(alignment: .leading, spacing: 2) {
                        Text("Podcast Player")
                            .font(.subheadline.weight(.semibold))
                        Text("Version \(appVersion)")
                            .font(.caption)
                            .foregroundStyle(Color.appOnSurfaceVariant)
                    }
                }

                Text("Podcast search and episode data are powered by the iTunes Search API.")
                    .font(.caption)
                    .foregroundStyle(Color.appOnSurfaceVariant)
            }
        }
        .navigationTitle("Profile")
    }

    private func themeRow(_ option: ThemePreference) -> some View {
        HStack(spacing: 14) {
            Image(systemName: option.systemImage)
                .foregroundStyle(Color.brandPrimary)
                .frame(width: 28)

            VStack(alignment: .leading, spacing: 2) {
                Text(option.label)
                    .foregroundStyle(Color.appOnSurface)
                Text(option.detail)
                    .font(.caption)
                    .foregroundStyle(Color.appOnSurfaceVariant)
            }

            Spacer()

            if themeStore.preference == option {
                Image(systemName: "checkmark")
                    .foregroundStyle(Color.brandPrimary)
            }
        }
    }

    private var appVersion: String {
        Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
    }
}
