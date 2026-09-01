package com.cgens67.mcaddons

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _themeFlow = MutableStateFlow(prefs.getString("selected_theme", "System") ?: "System")
    val themeFlow: StateFlow<String> = _themeFlow.asStateFlow()

    fun setTheme(theme: String) {
        prefs.edit { putString("selected_theme", theme) }
        _themeFlow.value = theme
    }
}
