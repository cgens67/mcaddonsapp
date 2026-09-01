package com.cgens67.mcaddons

import android.app.LocaleManager as AndroidLocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import androidx.compose.material3.MaterialTheme

data class LanguageItem(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val completionStatus: CompletionStatus = CompletionStatus.COMPLETE,
    val isSystemDefault: Boolean = false,
    val flag: String = "",
    val lastUpdated: String = ""
)

enum class CompletionStatus(@StringRes val labelRes: Int?, val color: @Composable () -> Color) {
    COMPLETE(null, { Color.Transparent }),
    INCOMPLETE(R.string.status_incomplete, { MaterialTheme.colorScheme.tertiary }),
    BETA(R.string.status_beta, { MaterialTheme.colorScheme.primary }),
    EXPERIMENTAL(R.string.status_experimental, { MaterialTheme.colorScheme.secondary })
}

sealed interface LanguageChangeState {
    object Idle : LanguageChangeState
    object Changing : LanguageChangeState
    object Success : LanguageChangeState
    data class Error(val message: String) : LanguageChangeState
}

class LocaleManager private constructor(private val context: Context) {

    companion object {
        private const val PREF_NAME = "locale_preferences"
        private const val PREF_LANGUAGE_KEY = "selected_language"
        const val SYSTEM_DEFAULT = "system_default"
        private const val ANIMATION_DELAY = 200L

        @Volatile
        private var instance: LocaleManager? = null

        fun getInstance(context: Context): LocaleManager {
            return instance ?: synchronized(this) {
                instance ?: LocaleManager(context.applicationContext).also { instance = it }
            }
        }

        private val LANGUAGE_METADATA = mapOf(
            "en" to LanguageMetadata("🇺🇸", CompletionStatus.COMPLETE, "v1.5.0"),
            "es" to LanguageMetadata("🇪🇸", CompletionStatus.COMPLETE, "v1.5.0"),
            "fr" to LanguageMetadata("🇫🇷", CompletionStatus.COMPLETE, "v1.5.0"),
            "de" to LanguageMetadata("🇩🇪", CompletionStatus.COMPLETE, "v1.5.0"),
            "ja" to LanguageMetadata("🇯🇵", CompletionStatus.COMPLETE, "v1.5.0"),
            "zh" to LanguageMetadata("🇨🇳", CompletionStatus.COMPLETE, "v1.5.0")
        )

        private data class LanguageMetadata(
            val flag: String,
            val completionStatus: CompletionStatus,
            val lastUpdated: String = "v1.5.0"
        )
    }

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val _currentLanguage = MutableStateFlow(getSelectedLanguageCode())
    private val _changeState = MutableStateFlow<LanguageChangeState>(LanguageChangeState.Idle)

    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()
    val changeState: StateFlow<LanguageChangeState> = _changeState.asStateFlow()

    private var _cachedLanguages: List<LanguageItem>? = null

    fun getSelectedLanguageCode(): String {
        return sharedPreferences.getString(PREF_LANGUAGE_KEY, SYSTEM_DEFAULT) ?: SYSTEM_DEFAULT
    }

    private fun getSystemLanguageCode(): String {
        val localeList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConfigurationCompat.getLocales(context.resources.configuration)
        } else {
            LocaleListCompat.create(Locale.getDefault())
        }
        return if (localeList.isEmpty) "en" else localeList[0]?.language ?: "en"
    }

    fun getAvailableLanguages(): List<LanguageItem> {
        return _cachedLanguages ?: run {
            val languages = mutableListOf<LanguageItem>()

            val systemLocale = Locale(getSystemLanguageCode())
            val systemDisplayName = systemLocale.displayLanguage.replaceFirstChar { it.uppercase() }

            languages.add(
                LanguageItem(
                    code = SYSTEM_DEFAULT,
                    displayName = context.getString(R.string.system_language, systemDisplayName),
                    nativeName = systemDisplayName,
                    completionStatus = CompletionStatus.COMPLETE,
                    isSystemDefault = true,
                    flag = "🌐",
                    lastUpdated = ""
                )
            )

            LANGUAGE_METADATA.keys.forEach { localeCode ->
                val locale = Locale(localeCode)
                val displayName = locale.getDisplayLanguage(Locale.ENGLISH).replaceFirstChar { it.uppercase() }
                val nativeName = locale.getDisplayLanguage(locale).replaceFirstChar { it.uppercase() }
                val metadata = LANGUAGE_METADATA[localeCode]!!

                languages.add(
                    LanguageItem(
                        code = localeCode,
                        displayName = displayName,
                        nativeName = nativeName,
                        completionStatus = metadata.completionStatus,
                        isSystemDefault = false,
                        flag = metadata.flag,
                        lastUpdated = metadata.lastUpdated
                    )
                )
            }

            _cachedLanguages = languages
            languages
        }
    }

    suspend fun updateLanguage(languageCode: String): Boolean {
        if (_changeState.value is LanguageChangeState.Changing) return false
        return try {
            _changeState.value = LanguageChangeState.Changing
            delay(ANIMATION_DELAY)

            sharedPreferences.edit().putString(PREF_LANGUAGE_KEY, languageCode).apply()
            _currentLanguage.value = languageCode
            
            _changeState.value = LanguageChangeState.Success
            delay(400) // UI time to show success state
            true
        } catch (e: Exception) {
            _changeState.value = LanguageChangeState.Error(e.message ?: "Error")
            false
        }
    }

    fun applyLocaleToApp(context: Context) {
        val code = getSelectedLanguageCode()
        val tag = if (code == SYSTEM_DEFAULT) "" else code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(AndroidLocaleManager::class.java)?.applicationLocales =
                LocaleList.forLanguageTags(tag)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(tag)
            )
        }
    }

    fun resetChangeState() {
        _changeState.value = LanguageChangeState.Idle
    }
}
