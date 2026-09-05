package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
  val bg: Color,
  val secondaryBg: Color,
  val card: Color,
  val cardElevated: Color,
  val cardHover: Color,
  val border: Color,
  val borderSubtle: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val textMuted: Color,
  val isDark: Boolean,
)

val DarkCustomColors = CustomColors(
  bg = DarkBg,
  secondaryBg = DarkSecondaryBg,
  card = DarkCard,
  cardElevated = DarkCardElevated,
  cardHover = DarkCardHover,
  border = DarkBorder,
  borderSubtle = DarkBorderSubtle,
  textPrimary = TextPrimaryDark,
  textSecondary = TextSecondaryDark,
  textMuted = TextMutedDark,
  isDark = true,
)

val LightCustomColors = CustomColors(
  bg = LightBg,
  secondaryBg = LightSecondaryBg,
  card = LightCard,
  cardElevated = LightCardElevated,
  cardHover = LightCardHover,
  border = LightBorder,
  borderSubtle = LightBorderSubtle,
  textPrimary = TextPrimaryLight,
  textSecondary = TextSecondaryLight,
  textMuted = TextMutedLight,
  isDark = false,
)

val LocalCustomColors = staticCompositionLocalOf { DarkCustomColors }

private val DarkExecutiveColorScheme = darkColorScheme(
  primary = AccentBlue,
  onPrimary = TextPrimaryDark,
  primaryContainer = DarkCardElevated,
  onPrimaryContainer = TextPrimaryDark,
  secondary = AccentIndigo,
  onSecondary = TextPrimaryDark,
  secondaryContainer = DarkCard,
  onSecondaryContainer = TextSecondaryDark,
  tertiary = AccentCyan,
  onTertiary = TextPrimaryDark,
  background = DarkBg,
  onBackground = TextPrimaryDark,
  surface = DarkCard,
  onSurface = TextPrimaryDark,
  surfaceVariant = DarkCardElevated,
  onSurfaceVariant = TextSecondaryDark,
  outline = DarkBorder,
  outlineVariant = DarkBorderSubtle,
  error = StatusDanger,
  onError = TextPrimaryDark,
)

private val LightExecutiveColorScheme = lightColorScheme(
  primary = AccentIndigo,
  onPrimary = Color.White,
  primaryContainer = LightCardElevated,
  onPrimaryContainer = TextPrimaryLight,
  secondary = AccentIndigo,
  onSecondary = Color.White,
  secondaryContainer = LightSecondaryBg,
  onSecondaryContainer = TextSecondaryLight,
  tertiary = AccentCyan,
  onTertiary = Color.White,
  background = LightBg,
  onBackground = TextPrimaryLight,
  surface = LightCard,
  onSurface = TextPrimaryLight,
  surfaceVariant = LightCardElevated,
  onSurfaceVariant = TextSecondaryLight,
  outline = LightBorder,
  outlineVariant = LightBorderSubtle,
  error = StatusDanger,
  onError = Color.White,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  selectedFont: PersianFont = PersianFont.YEKAN,
  content: @Composable () -> Unit,
) {
  val customColors = if (darkTheme) DarkCustomColors else LightCustomColors
  val colorScheme = if (darkTheme) DarkExecutiveColorScheme else LightExecutiveColorScheme

  // Keep fallback globals in sync
  TextPrimary = customColors.textPrimary
  TextSecondary = customColors.textSecondary
  TextMuted = customColors.textMuted

  val typography = remember(selectedFont) {
    getTypographyWithFont(selectedFont.getFontFamily())
  }

  CompositionLocalProvider(LocalCustomColors provides customColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = typography,
      content = content
    )
  }
}

