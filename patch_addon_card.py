import os

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'r') as f:
    content = f.read()

card_def = """fun AddonCard(
    addon: AddonItem,
    downloadState: DownloadState,
    onDownloadClick: () -> Unit
)"""
new_card_def = """fun AddonCard(
    addon: AddonItem,
    downloadState: DownloadState,
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit
)"""

content = content.replace(card_def, new_card_def)

card_modifier = """        modifier = Modifier.fillMaxWidth()
    ) {"""
new_card_modifier = """        modifier = modifier.fillMaxWidth()
    ) {"""

content = content.replace(card_modifier, new_card_modifier)

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'w') as f:
    f.write(content)
