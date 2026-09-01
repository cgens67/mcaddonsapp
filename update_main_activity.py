import re

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'r') as f:
    content = f.read()

# Remove onNavigateToLanguage from SettingsScreen
old_settings = """                                SettingsScreen(
                                    onNavigateBack = { currentScreen = "home" },
                                    onNavigateToAppearance = { currentScreen = "appearance_settings" },
                                    onNavigateToLanguage = { currentScreen = "language_settings" }
                                )"""
new_settings = """                                SettingsScreen(
                                    onNavigateBack = { currentScreen = "home" },
                                    onNavigateToAppearance = { currentScreen = "appearance_settings" }
                                )"""
content = content.replace(old_settings, new_settings)

# Remove language_settings case
old_lang_settings = """                            "language_settings" -> {
                                BackHandler { currentScreen = "settings" }
                                LanguageSettingsScreen(
                                    onNavigateBack = { currentScreen = "settings" }
                                )
                            }"""
content = content.replace(old_lang_settings, "")

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'w') as f:
    f.write(content)
