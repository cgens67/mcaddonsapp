package com.cgens67.mcaddons

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePreferences(context: Context) {
    companion object {
        const val LOADING_STYLE_PICKAXE = "Pickaxe"
        const val LOADING_STYLE_DEFAULT = "Default"
    }

    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _themeFlow = MutableStateFlow(prefs.getString("selected_theme", "System") ?: "System")
    val themeFlow: StateFlow<String> = _themeFlow.asStateFlow()

    private val _loadingStyleFlow = MutableStateFlow(
        prefs.getString("selected_loading_style", LOADING_STYLE_PICKAXE) ?: LOADING_STYLE_PICKAXE
    )
    val loadingStyleFlow: StateFlow<String> = _loadingStyleFlow.asStateFlow()

    fun setTheme(theme: String) {
        prefs.edit { putString("selected_theme", theme) }
        _themeFlow.value = theme
    }

    fun setLoadingStyle(style: String) {
        prefs.edit { putString("selected_loading_style", style) }
        _loadingStyleFlow.value = style
    }
}
