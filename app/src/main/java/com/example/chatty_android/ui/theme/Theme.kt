package com.example.chatty_android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

fun createLightScheme(themeColor: ThemeColor): ColorScheme {
    val p = getPalette(themeColor)
    return lightColorScheme(
        primary = p.primary,
        onPrimary = RiceWhite,
        primaryContainer = p.primaryLight,
        onPrimaryContainer = InkBlack,
        secondary = Gold,
        onSecondary = InkBlack,
        secondaryContainer = GoldLight,
        onSecondaryContainer = InkBlack,
        tertiary = p.primaryDark,
        onTertiary = RiceWhite,
        tertiaryContainer = p.primaryLight,
        onTertiaryContainer = InkBlack,
        background = RiceWhite,
        onBackground = InkBlack,
        surface = RiceWhiteLight,
        onSurface = InkBlack,
        surfaceVariant = RiceWhiteDark,
        onSurfaceVariant = InkBlackLight,
        error = ChineseRed,
        onError = RiceWhite,
    )
}

fun createDarkScheme(themeColor: ThemeColor): ColorScheme {
    val p = getPalette(themeColor)
    return darkColorScheme(
        primary = p.primaryLight,
        onPrimary = RiceWhite,
        primaryContainer = p.primaryDark,
        onPrimaryContainer = RiceWhiteLight,
        secondary = GoldLight,
        onSecondary = InkBlack,
        secondaryContainer = GoldDark,
        onSecondaryContainer = RiceWhite,
        tertiary = p.primaryLight,
        onTertiary = InkBlack,
        tertiaryContainer = p.primaryDark,
        onTertiaryContainer = RiceWhite,
        background = InkBlack,
        onBackground = RiceWhite,
        surface = InkBlackLight,
        onSurface = RiceWhite,
        surfaceVariant = InkBlack,
        onSurfaceVariant = RiceWhiteDark,
        error = ChineseRedLight,
        onError = RiceWhite,
    )
}

@Composable
fun ChattyAndroidTheme(
    themeColor: ThemeColor = ThemeColor.CHINESE_RED,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) createDarkScheme(themeColor) else createLightScheme(themeColor)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
