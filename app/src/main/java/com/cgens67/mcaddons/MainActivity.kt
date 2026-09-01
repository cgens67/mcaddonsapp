package com.cgens67.mcaddons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi

import androidx.compose.foundation.isSystemInDarkTheme
import coil.compose.AsyncImage
import com.cgens67.mcaddons.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themePreferences = ThemePreferences(this)
        enableEdgeToEdge()
        setContent {
            val selectedTheme by themePreferences.themeFlow.collectAsState()
            val darkTheme = when (selectedTheme) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }
            
            MyApplicationTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentScreen by remember { mutableStateOf("home") }

                    val screenOrder = mapOf(
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
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddonScreen(
    viewModel: AddonViewModel = viewModel(),
    onNavigateToAbout: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.app_name), 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge
                    ) 
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchAddons() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings))
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.about_creators))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_addons)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
            
                val categories = listOf(
                    "All" to stringResource(R.string.filter_all),
                    "Texture Pack" to stringResource(R.string.filter_texture_pack),
                    "Addon" to stringResource(R.string.filter_addon),
                    "World" to stringResource(R.string.filter_world)
                )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(categories) { (id, name) ->
                    FilterChip(
                        selected = uiState.selectedCategory == id,
                        onClick = { viewModel.selectCategory(id) },
                        label = { Text(name) }
                    )
                }
            }
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.error, uiState.error ?: ""), color = MaterialTheme.colorScheme.error)
                }
            } else if (uiState.filteredAddons.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_addons_found), 
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.filteredAddons, key = { it.id }) { addon ->
                        AddonCard(
                            addon = addon,
                            downloadState = uiState.downloadStates[addon.id] ?: DownloadState.Idle,
                            onDownloadClick = { viewModel.downloadAndInstall(addon) },
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = tween(300))
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddonCard(
    addon: AddonItem,
    downloadState: DownloadState,
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.animateContentSize(animationSpec = tween(300))) {
            if (!addon.thumbnailUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = addon.thumbnailUrl,
                    contentDescription = addon.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = addon.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = addon.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${addon.sizeMb ?: 0.0} MB • ${addon.category}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    when (downloadState) {
                        is DownloadState.Downloading -> {
                            Button(
                                onClick = { },
                                enabled = false
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SizedCircularProgressIndicator(size = 16.dp, strokeWidth = 2.dp)
                                    Text(stringResource(R.string.downloading))
                                }
                            }
                        }
                        is DownloadState.Downloaded -> {
                            FilledTonalButton(
                                onClick = onDownloadClick
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.open_in_minecraft))
                            }
                        }
                        is DownloadState.Idle -> {
                            Button(
                                onClick = onDownloadClick
                            ) {
                                Text(stringResource(R.string.download_and_install))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SizedCircularProgressIndicator(size: androidx.compose.ui.unit.Dp, strokeWidth: androidx.compose.ui.unit.Dp) {
    CircularProgressIndicator(
        modifier = Modifier.size(size),
        strokeWidth = strokeWidth
    )
}
