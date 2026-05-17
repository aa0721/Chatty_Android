package com.example.chatty_android.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeColor(val label: String) {
    CHINESE_RED("中国红"),
    NAVY_BLUE("藏青"),
    JADE_GREEN("玉绿")
}

data class ThemePalette(
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color
)

fun getPalette(themeColor: ThemeColor): ThemePalette = when (themeColor) {
    ThemeColor.CHINESE_RED -> ThemePalette(
        primary = ChineseRed,
        primaryLight = ChineseRedLight,
        primaryDark = ChineseRedDark
    )
    ThemeColor.NAVY_BLUE -> ThemePalette(
        primary = NavyBlue,
        primaryLight = NavyBlueLight,
        primaryDark = NavyBlueDark
    )
    ThemeColor.JADE_GREEN -> ThemePalette(
        primary = JadeGreen,
        primaryLight = JadeGreenLight,
        primaryDark = JadeGreenDark
    )
}

// 中国红
val ChineseRed = Color(0xFFC8102E)
val ChineseRedLight = Color(0xFFE84848)
val ChineseRedDark = Color(0xFF8B0000)

// 藏青
val NavyBlue = Color(0xFF1B3A5C)
val NavyBlueLight = Color(0xFF2C5F8A)
val NavyBlueDark = Color(0xFF0F2440)

// 墨黑
val InkBlack = Color(0xFF212121)
val InkBlackLight = Color(0xFF424242)

// 米白
val RiceWhite = Color(0xFFF8F3EC)
val RiceWhiteLight = Color(0xFFFFFFFF)
val RiceWhiteDark = Color(0xFFE8E0D0)

// 金色
val Gold = Color(0xFFC9A96E)
val GoldLight = Color(0xFFD4BC8B)
val GoldDark = Color(0xFFA68B4B)

// 玉绿
val JadeGreen = Color(0xFF5B8C5A)
val JadeGreenLight = Color(0xFF7CB87B)
val JadeGreenDark = Color(0xFF3D6B4F)
