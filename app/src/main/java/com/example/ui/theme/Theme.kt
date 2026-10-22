package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = BentoPrimary,
    secondary = BentoSecondary,
    tertiary = BentoSolidViolet,
    background = BentoBg,
    surface = BentoLightLavender,
    onBackground = BentoText,
    onSurface = BentoText
  )


private val LightColorScheme =
  lightColorScheme(
    primary = BentoPrimary,
    onPrimary = Color.White,
    secondary = BentoSecondary,
    onSecondary = Color.White,
    primaryContainer = BentoSoftPurpleContainer,
    onPrimaryContainer = BentoSecondary,
    secondaryContainer = BentoSecondaryContainer,
    onSecondaryContainer = BentoSecondary,
    tertiary = BentoSolidViolet,
    background = BentoBg,
    surface = BentoLightLavender,
    onBackground = BentoText,
    onSurface = BentoText,
    surfaceVariant = BentoLightLavender,
    onSurfaceVariant = BentoPrimaryDesc,
    outline = BentoGrayOutline
  )


@Composable
fun MyApplicationTheme(
  // التطبيق مصمَّم بألوان فاتحة فقط؛ لا نتّبع الوضع الليلي حتى لا تختفي الكتابات والأزرار
  darkTheme: Boolean = false,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
