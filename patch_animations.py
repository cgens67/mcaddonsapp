import os

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'r') as f:
    content = f.read()

# Add imports
imports = """
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
"""

content = content.replace('import androidx.compose.foundation.isSystemInDarkTheme', imports + '\nimport androidx.compose.foundation.isSystemInDarkTheme')

# Replace the screen switching logic with AnimatedContent
old_screen_logic = """                    if (currentScreen == "about") {
                        BackHandler {
                            currentScreen = "home"
                        }
                        AboutScreen(
                            onNavigateBack = { currentScreen = "home" }
                        )
                    } else if (currentScreen == "settings") {
                        BackHandler {
                            currentScreen = "home"
                        }
                        SettingsScreen(
                            onNavigateBack = { currentScreen = "home" },
                            onNavigateToAppearance = { currentScreen = "appearance_settings" },
                            onNavigateToLanguage = { currentScreen = "language_settings" }
                        )
                    } else if (currentScreen == "appearance_settings") {
                        BackHandler {
                            currentScreen = "settings"
                        }
                        AppearanceSettingsScreen(
                            selectedTheme = selectedTheme,
                            onThemeSelected = { themePreferences.setTheme(it) },
                            onNavigateBack = { currentScreen = "settings" }
                        )
                    } else if (currentScreen == "language_settings") {
                        BackHandler {
                            currentScreen = "settings"
                        }
                        LanguageSettingsScreen(
                            onNavigateBack = { currentScreen = "settings" }
                        )
                    } else {
                        AddonScreen(
                            onNavigateToAbout = { currentScreen = "about" },
                            onNavigateToSettings = { currentScreen = "settings" }
                        )
                    }"""

new_screen_logic = """                    val screenOrder = mapOf(
                        "home" to 0,
                        "settings" to 1,
                        "about" to 1,
                        "appearance_settings" to 2,
                        "language_settings" to 2
                    )

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val initialOrder = screenOrder[initialState] ?: 0
                            val targetOrder = screenOrder[targetState] ?: 0
                            if (targetOrder > initialOrder) {
                                (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300))).togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut(animationSpec = tween(300)))
                            } else {
                                (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn(animationSpec = tween(300))).togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300)))
                            }
                        },
                        label = "screen_transition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            "about" -> {
                                BackHandler { currentScreen = "home" }
                                AboutScreen(onNavigateBack = { currentScreen = "home" })
                            }
                            "settings" -> {
                                BackHandler { currentScreen = "home" }
                                SettingsScreen(
                                    onNavigateBack = { currentScreen = "home" },
                                    onNavigateToAppearance = { currentScreen = "appearance_settings" },
                                    onNavigateToLanguage = { currentScreen = "language_settings" }
                                )
                            }
                            "appearance_settings" -> {
                                BackHandler { currentScreen = "settings" }
                                AppearanceSettingsScreen(
                                    selectedTheme = selectedTheme,
                                    onThemeSelected = { themePreferences.setTheme(it) },
                                    onNavigateBack = { currentScreen = "settings" }
                                )
                            }
                            "language_settings" -> {
                                BackHandler { currentScreen = "settings" }
                                LanguageSettingsScreen(
                                    onNavigateBack = { currentScreen = "settings" }
                                )
                            }
                            else -> {
                                AddonScreen(
                                    onNavigateToAbout = { currentScreen = "about" },
                                    onNavigateToSettings = { currentScreen = "settings" }
                                )
                            }
                        }
                    }"""

content = content.replace(old_screen_logic, new_screen_logic)

# Add animateContentSize to AddonCard
content = content.replace('Column {', 'Column(modifier = Modifier.animateContentSize(animationSpec = tween(300))) {', 1)

# Ensure opt-in for animation if required. Also we want animateItemPlacement for list items.
# Let's find LazyColumn items in AddonScreen
if 'items(uiState.filteredAddons, key = { it.id }) { addon ->' in content:
    print("Found LazyColumn items")
else:
    print("Did not find LazyColumn items exactly as expected. Let's try replacement with regex.")
    
import re
# We need to add modifier to AddonCard call inside AddonScreen
addon_card_call = """                        AddonCard(
                            addon = addon,
                            downloadState = uiState.downloadStates[addon.id] ?: DownloadState.Idle,
                            onDownloadClick = { viewModel.downloadAndInstall(addon) }
                        )"""

new_addon_card_call = """                        AddonCard(
                            addon = addon,
                            downloadState = uiState.downloadStates[addon.id] ?: DownloadState.Idle,
                            onDownloadClick = { viewModel.downloadAndInstall(addon) },
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = tween(300))
                        )"""
if addon_card_call in content:
    content = content.replace(addon_card_call, new_addon_card_call)
else:
    # fallback, maybe use regex
    content = re.sub(
        r'(AddonCard\(\s*addon = addon,\s*downloadState = uiState\.downloadStates\[addon\.id\] \?: DownloadState\.Idle,\s*onDownloadClick = \{ viewModel\.downloadAndInstall\(addon\) \})',
        r'\1,\n                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = tween(300))',
        content
    )

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'w') as f:
    f.write(content)
