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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.scaleIn
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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
            val selectedLoadingStyle by themePreferences.loadingStyleFlow.collectAsState()
            val selectedImportTarget by themePreferences.importTargetFlow.collectAsState()
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
                            val spec = tween<androidx.compose.ui.unit.IntOffset>(durationMillis = 400, easing = FastOutSlowInEasing)
                            val fadeSpec = tween<Float>(durationMillis = 400, easing = LinearEasing)
                            
                            if (targetOrder > initialOrder) {
                                (slideInHorizontally(animationSpec = spec) { width -> width } + fadeIn(animationSpec = fadeSpec))
                                    .togetherWith(slideOutHorizontally(animationSpec = spec) { width -> -width / 3 } + fadeOut(animationSpec = fadeSpec))
                            } else {
                                (slideInHorizontally(animationSpec = spec) { width -> -width / 3 } + fadeIn(animationSpec = fadeSpec))
                                    .togetherWith(slideOutHorizontally(animationSpec = spec) { width -> width } + fadeOut(animationSpec = fadeSpec))
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
                                    selectedImportTarget = selectedImportTarget,
                                    onImportTargetSelected = { themePreferences.setImportTarget(it) },
                                    onNavigateBack = { currentScreen = "home" },
                                    onNavigateToAppearance = { currentScreen = "appearance_settings" }
                                )
                            }
                            "appearance_settings" -> {
                                BackHandler { currentScreen = "settings" }
                                AppearanceSettingsScreen(
                                    selectedTheme = selectedTheme,
                                    onThemeSelected = { themePreferences.setTheme(it) },
                                    selectedLoadingStyle = selectedLoadingStyle,
                                    onLoadingStyleSelected = { themePreferences.setLoadingStyle(it) },
                                    onNavigateBack = { currentScreen = "settings" }
                                )
                            }

                            else -> {
                                AddonScreen(
                                    loadingStyle = selectedLoadingStyle,
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

enum class ContentDisplayState {
    Loading,
    Error,
    Empty,
    Content
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddonScreen(
    viewModel: AddonViewModel = viewModel(),
    loadingStyle: String = ThemePreferences.LOADING_STYLE_PICKAXE,
    onNavigateToAbout: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.app_name), 
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
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior
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
            
            val displayState = when {
                uiState.isLoading -> ContentDisplayState.Loading
                uiState.error != null -> ContentDisplayState.Error
                uiState.filteredAddons.isEmpty() -> ContentDisplayState.Empty
                else -> ContentDisplayState.Content
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refreshAddons() },
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = displayState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(450, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(350, easing = FastOutSlowInEasing))
                    },
                    label = "content_fade_transition",
                    modifier = Modifier.fillMaxSize()
                ) { targetDisplayState ->
                    when (targetDisplayState) {
                        ContentDisplayState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                AppLoadingIndicator(
                                    loadingStyle = loadingStyle,
                                    labelText = stringResource(R.string.loading)
                                )
                            }
                        }
                        ContentDisplayState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.error, uiState.error ?: ""),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        ContentDisplayState.Empty -> {
                            EmptyStateAnimation(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            )
                        }
                        ContentDisplayState.Content -> {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(uiState.filteredAddons, key = { it.id }) { addon ->
                                    AddonCard(
                                        addon = addon,
                                        loadingStyle = loadingStyle,
                                        downloadState = uiState.downloadStates[addon.id] ?: DownloadState.Idle,
                                        onDownloadClick = { viewModel.downloadAndInstall(addon) },
                                        modifier = Modifier.animateItem(
                                            fadeInSpec = tween(400, easing = FastOutSlowInEasing),
                                            fadeOutSpec = tween(200, easing = FastOutSlowInEasing),
                                            placementSpec = tween(400, easing = FastOutSlowInEasing)
                                        )
                                    )
                                }
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
fun AddonCard(
    addon: AddonItem,
    downloadState: DownloadState,
    loadingStyle: String = ThemePreferences.LOADING_STYLE_PICKAXE,
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
                                    if (loadingStyle == ThemePreferences.LOADING_STYLE_PICKAXE) {
                                        PickaxeLoadingIndicator(size = 18.dp)
                                    } else {
                                        SizedCircularProgressIndicator(size = 16.dp, strokeWidth = 2.dp)
                                    }
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


@Composable
fun EmptyStateAnimation(modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + scaleIn(initialScale = 0.8f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .offset(y = floatAnim.dp)
                    .padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.no_addons_found),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_addons_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
