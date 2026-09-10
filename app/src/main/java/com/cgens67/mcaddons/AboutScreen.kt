package com.cgens67.mcaddons

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private object AboutDimensions {
    val HorizontalHeroBreakpoint = 600.dp
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

            item(key = "lead_developer_header") {
                AboutSectionHeader(title = stringResource(R.string.about_team))
            }

            item(key = "lead_developers") {
                Column(verticalArrangement = Arrangement.spacedBy(AboutSpacing.sm)) {
                    LeadDeveloperCard(
                        name = "Loo Chin Siang",
                        role = stringResource(R.string.author_role),
                        badgeIcon = Icons.Filled.Code
                    )
                    LeadDeveloperCard(
                        name = "Joseph Lim",
                        role = stringResource(R.string.author_role),
                        badgeIcon = null
                    )
                }
            }

        }
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
        color = Color(0xFF4CAF50),
        contentColor = Color.White
    ) {
        Image(
            painter = painterResource(R.drawable.about_splash),
            contentDescription = null,
            colorFilter = remember { ColorFilter.tint(Color.White) },
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
            AboutMetadataBadge(text = "v${BuildConfig.VERSION_NAME}")
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jlmcpackaddons.vercel.app"))
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
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Download texture packs, addons from Joseph Lim and Loo Chin Siang!\nhttps://jlmcpackaddons.vercel.app"
                    )
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
private fun LeadDeveloperCard(
    name: String,
    role: String,
    badgeIcon: ImageVector?,
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
                    if (badgeIcon != null) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = "Badge",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = role,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
