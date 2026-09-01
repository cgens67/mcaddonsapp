import re

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'r') as f:
    content = f.read()

# Make transitions smoother
old_transition_spec = """                        transitionSpec = {
                            val initialOrder = screenOrder[initialState] ?: 0
                            val targetOrder = screenOrder[targetState] ?: 0
                            if (targetOrder > initialOrder) {
                                (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300))).togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut(animationSpec = tween(300)))
                            } else {
                                (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn(animationSpec = tween(300))).togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300)))
                            }
                        },"""

new_transition_spec = """                        transitionSpec = {
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
                        },"""

content = content.replace(old_transition_spec, new_transition_spec)

# Update AddonCard modifier in AddonScreen
old_addon_card_modifier = """                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = tween(300))"""

new_addon_card_modifier = """                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(400, easing = FastOutSlowInEasing),
                                fadeOutSpec = tween(200, easing = FastOutSlowInEasing),
                                placementSpec = tween(400, easing = FastOutSlowInEasing)
                            )"""

content = content.replace(old_addon_card_modifier, new_addon_card_modifier)

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'w') as f:
    f.write(content)
