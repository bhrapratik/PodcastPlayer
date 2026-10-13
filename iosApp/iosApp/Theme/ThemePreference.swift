import SwiftUI

/// Mirrors Android's `ThemePreference` enum. Persistence uses `UserDefaults` here instead of
/// DataStore since that's the native iOS equivalent for a single small preference value.
enum ThemePreference: String, CaseIterable, Identifiable {
    case system, light, dark

    var id: String { rawValue }

    var label: String {
        switch self {
        case .system: "System"
        case .light: "Light"
        case .dark: "Dark"
        }
    }

    var detail: String {
        switch self {
        case .system: "Match your device setting"
        case .light: "Always use light theme"
        case .dark: "Always use dark theme"
        }
    }

    var systemImage: String {
        switch self {
        case .system: "circle.lefthalf.filled"
        case .light: "sun.max.fill"
        case .dark: "moon.fill"
        }
    }

    var colorScheme: ColorScheme? {
        switch self {
        case .system: nil
        case .light: .light
        case .dark: .dark
        }
    }
}

final class ThemeStore: ObservableObject {
    private static let storageKey = "theme_preference"

    @Published var preference: ThemePreference {
        didSet {
            UserDefaults.standard.set(preference.rawValue, forKey: Self.storageKey)
        }
    }

    init() {
        let stored = UserDefaults.standard.string(forKey: Self.storageKey)
        preference = stored.flatMap(ThemePreference.init(rawValue:)) ?? .system
    }
}
