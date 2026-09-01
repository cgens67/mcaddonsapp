package com.cgens67.mcaddons

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {
    val supportedLocales = listOf(
        "en" to "English",
        "es" to "Español",
        "fr" to "Français",
        "de" to "Deutsch",
        "ja" to "日本語",
        "zh" to "中文"
    )

    fun setLocale(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)?.applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCode)
            )
        }
    }

    fun getCurrentLocale(context: Context): String {
        val tags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)?.applicationLocales?.toLanguageTags()
        } else {
            AppCompatDelegate.getApplicationLocales().toLanguageTags()
        }
        
        return tags?.split(",")?.firstOrNull()?.takeIf { it.isNotBlank() } ?: "en"
    }
}
