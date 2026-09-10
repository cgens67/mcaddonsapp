package com.cgens67.mcaddons

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private object AboutDimensions {
    val HorizontalHeroBreakpoint = 600.dp
    val MaxHeroContentWidth = 600.dp
}

private object AboutSpacing {
    val none = 0.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var showTranslationDialog by remember { mutableStateOf(false) }
    var showLicenseDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_addons)
                        )
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = AboutSpacing.md,
                end = AboutSpacing.md,
                top = innerPadding.calculateTopPadding() + AboutSpacing.sm,
                bottom = innerPadding.calculateBottomPadding() + AboutSpacing.xl
            ),
            verticalArrangement = Arrangement.spacedBy(AboutSpacing.md)
        ) {
            item(key = "identity") {
                AboutIdentityCard()
            }

            item(key = "project_info_header") {
                AboutSectionHeader(title = stringResource(R.string.preferences))
            }

            item(key = "project_information") {
                AboutProjectInformationSection(
                    onOpenTranslations = { showTranslationDialog = true },
                    onOpenLicenses = { showLicenseDialog = true }
                )
            }

            item(key = "lead_developer_header") {
                AboutSectionHeader(title = stringResource(R.string.about_team))
            }

            item(key = "lead_developers") {
                Column(verticalArrangement = Arrangement.spacedBy(AboutSpacing.sm)) {
                    LeadDeveloperCard(
                        name = "Joseph Lim",
                        role = "Lead Concept Designer & Core Architect",
                        description = stringResource(R.string.joseph_lim_desc),
                        badgeIcon = Icons.Filled.Verified
                    )
                    LeadDeveloperCard(
                        name = "Loo Chin Siang",
                        role = "Lead Developer & Engine Architect",
                        description = stringResource(R.string.loo_chin_siang_desc),
                        badgeIcon = Icons.Filled.Code
                    )
                }
            }

            item(key = "app_highlights_header") {
                AboutSectionHeader(title = stringResource(R.string.app_highlights))
            }

            item(key = "app_highlights") {
                AppHighlightsCard()
            }

            item(key = "footer") {
                AboutFooter()
            }
        }
    }

    if (showTranslationDialog) {
        TranslationContributorsDialog(onDismissRequest = { showTranslationDialog = false })
    }

    if (showLicenseDialog) {
        DependencyLicensesDialog(onDismissRequest = { showLicenseDialog = false })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AboutIdentityCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AboutSpacing.lg)
        ) {
            val isHorizontal = maxWidth >= AboutDimensions.HorizontalHeroBreakpoint
            if (isHorizontal) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AboutSpacing.lg)
                ) {
                    SurfaceAppIcon()
                    Column(modifier = Modifier.weight(1f)) {
                        AboutIdentityTextContent()
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AboutSpacing.md)
                ) {
                    SurfaceAppIcon()
                    AboutIdentityTextContent(centered = true)
                }
            }
        }
    }
}

@Composable
private fun SurfaceAppIcon(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        val iconTint = MaterialTheme.colorScheme.onPrimaryContainer
        val iconColorFilter = remember(iconTint) { ColorFilter.tint(iconTint) }
        Image(
            painter = painterResource(R.drawable.about_splash),
            contentDescription = null,
            colorFilter = iconColorFilter,
            modifier = Modifier
                .padding(AboutSpacing.sm)
                .size(64.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AboutIdentityTextContent(
    centered: Boolean = false,
    modifier: Modifier = Modifier
) {
    val alignment = if (centered) Alignment.CenterHorizontally else Alignment.Start
    val textAlign = if (centered) TextAlign.Center else TextAlign.Start

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(AboutSpacing.xs)
    ) {
        Text(
            text = stringResource(R.string.mc_addons),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = textAlign,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        FlowRow(
            horizontalArrangement = if (centered) Arrangement.Center else Arrangement.spacedBy(AboutSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(AboutSpacing.xs),
            modifier = Modifier.padding(vertical = AboutSpacing.xxs)
        ) {
            AboutMetadataBadge(text = "v1.0")
            AboutMetadataBadge(text = "Bedrock")
            AboutMetadataBadge(text = "Release")
        }

        Spacer(modifier = Modifier.height(AboutSpacing.xxs))

        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = textAlign
        )

        Spacer(modifier = Modifier.height(AboutSpacing.xs))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(AboutSpacing.xs))

        LinkChipRow(centered = centered)
    }
}

@Composable
private fun AboutMetadataBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Badge(
        modifier = modifier.heightIn(min = 28.dp),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinkChipRow(
    centered: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (centered) Arrangement.Center else Arrangement.spacedBy(AboutSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AboutSpacing.xs)
    ) {
        AssistChip(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cgens67/mcaddons"))
                context.startActivity(Intent.createChooser(intent, "Open GitHub"))
            },
            label = { Text(stringResource(R.string.github)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Code,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        )

        AssistChip(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://minecraft.net"))
                context.startActivity(Intent.createChooser(intent, "Open Website"))
            },
            label = { Text(stringResource(R.string.website)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        )

        AssistChip(
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "MC Addons")
                    putExtra(Intent.EXTRA_TEXT, "Download texture packs, addons, and worlds with MC Addons!")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share MC Addons"))
            },
            label = { Text(stringResource(R.string.share)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        )
    }
}

@Composable
private fun AboutSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = AboutSpacing.sm, top = AboutSpacing.xs, bottom = AboutSpacing.xxs)
    )
}

@Composable
private fun AboutProjectInformationSection(
    onOpenTranslations: () -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column {
            AboutActionListItem(
                icon = Icons.Filled.Translate,
                title = stringResource(R.string.about_contributor_translation),
                subtitle = "Community language localizations",
                onClick = onOpenTranslations
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = AboutSpacing.md),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            AboutActionListItem(
                icon = Icons.Filled.Info,
                title = stringResource(R.string.about_license),
                subtitle = "Third-party open source notices",
                onClick = onOpenLicenses
            )
        }
    }
}

@Composable
private fun AboutActionListItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(AboutSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AboutSpacing.md)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LeadDeveloperCard(
    name: String,
    role: String,
    description: String,
    badgeIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AboutSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AboutSpacing.md)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AboutSpacing.xs)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = "Verified Member",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = role,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(AboutSpacing.xxs))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AppHighlightsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(AboutSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AboutSpacing.sm)
        ) {
            Text(
                text = stringResource(R.string.app_highlights_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
            )
        }
    }
}

@Composable
private fun AboutFooter(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AboutSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.crafted_with),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(AboutSpacing.xs))
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Love",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(AboutSpacing.xs))
        Text(
            text = stringResource(R.string.by_authors),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TranslationContributorsDialog(
    onDismissRequest: () -> Unit
) {
    val languages = listOf(
        Triple("English", "en", "Full (Default)"),
        Triple("Español", "es", "Community Translated"),
        Triple("Deutsch", "de", "Community Translated"),
        Triple("Français", "fr", "Community Translated"),
        Triple("Português (Brasil)", "pt-BR", "Community Translated"),
        Triple("Русский", "ru", "Community Translated"),
        Triple("中文 (简体)", "zh-CN", "Community Translated"),
        Triple("日本語", "ja", "Community Translated"),
        Triple("Italiano", "it", "Community Translated")
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.about_contributor_translation),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.close_dialog)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(AboutSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AboutSpacing.sm)
            ) {
                item {
                    Text(
                        text = "We deeply appreciate all community volunteers and contributors helping localize MC Addons across the globe.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = AboutSpacing.xs)
                    )
                }

                items(languages.size) { index ->
                    val (name, code, status) = languages[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AboutSpacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AboutSpacing.md)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Language,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = code,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Text(
                                    text = status,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DependencyLicensesDialog(
    onDismissRequest: () -> Unit
) {
    val libraries = listOf(
        Pair("Jetpack Compose", "Apache License 2.0 • Android Open Source Project"),
        Pair("Material 3 Expressive", "Apache License 2.0 • Google LLC"),
        Pair("KotlinX Coroutines", "Apache License 2.0 • JetBrains s.r.o."),
        Pair("KotlinX Serialization", "Apache License 2.0 • JetBrains s.r.o."),
        Pair("Coil Image Loader", "Apache License 2.0 • Coil Contributors"),
        Pair("AndroidX Core KTX", "Apache License 2.0 • Android Open Source Project"),
        Pair("Supabase Kt", "MIT License • Jan-Lukas Göbel & Contributors"),
        Pair("Ktor Client", "Apache License 2.0 • JetBrains s.r.o.")
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.about_license),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.close_dialog)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(AboutSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AboutSpacing.sm)
            ) {
                item {
                    Text(
                        text = "MC Addons is built upon open source software and libraries:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = AboutSpacing.xs)
                    )
                }

                items(libraries.size) { index ->
                    val (libName, license) = libraries[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AboutSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(AboutSpacing.xxs)
                        ) {
                            Text(
                                text = libName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = license,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
