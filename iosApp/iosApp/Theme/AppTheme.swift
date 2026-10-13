import SwiftUI

/// Brand colors mirrored from the Android app's `Color.kt` / `Theme.kt`, expressed as
/// dynamic SwiftUI colors that automatically resolve against the active (or overridden)
/// color scheme.
extension Color {
    private static func dynamic(light: UInt32, dark: UInt32) -> Color {
        Color(uiColor: UIColor { traits in
            traits.userInterfaceStyle == .dark ? UIColor(rgb: dark) : UIColor(rgb: light)
        })
    }

    static let brandPrimary = dynamic(light: 0x7655E8, dark: 0xB8A5FF)
    static let brandOnPrimary = dynamic(light: 0xFFFFFF, dark: 0x27105F)
    static let brandPrimaryContainer = dynamic(light: 0xDCD1FF, dark: 0x49318A)
    static let brandOnPrimaryContainer = dynamic(light: 0x25104F, dark: 0xE9DDFF)

    static let appBackground = dynamic(light: 0xF8F7FC, dark: 0x121118)
    static let appOnBackground = dynamic(light: 0x191625, dark: 0xF4F0FF)

    static let appSurface = dynamic(light: 0xFFFFFF, dark: 0x1C1A24)
    static let appOnSurface = dynamic(light: 0x191625, dark: 0xF4F0FF)
    static let appSurfaceVariant = dynamic(light: 0xF0ECF7, dark: 0x292633)
    static let appOnSurfaceVariant = dynamic(light: 0x716D7D, dark: 0xBDB7CB)

    static let appOutline = dynamic(light: 0xE5E0EF, dark: 0x484352)

    static let appError = dynamic(light: 0xBA1A1A, dark: 0xFFB4AB)
    static let appErrorContainer = dynamic(light: 0xF9DEDC, dark: 0x8C1D18)
}

private extension UIColor {
    convenience init(rgb: UInt32) {
        self.init(
            red: CGFloat((rgb >> 16) & 0xFF) / 255,
            green: CGFloat((rgb >> 8) & 0xFF) / 255,
            blue: CGFloat(rgb & 0xFF) / 255,
            alpha: 1
        )
    }
}
