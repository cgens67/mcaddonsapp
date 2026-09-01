import re

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'val categories = listOf(stringResource(R.string.filter_all), stringResource(R.string.filter_texture_pack), stringResource(R.string.filter_addon), stringResource(R.string.filter_world))',
    '''val categories = listOf(
                    "All" to stringResource(R.string.filter_all),
                    "Texture Pack" to stringResource(R.string.filter_texture_pack),
                    "Addon" to stringResource(R.string.filter_addon),
                    "World" to stringResource(R.string.filter_world)
                )'''
)

content = content.replace(
    'items(categories) { category ->',
    'items(categories) { (id, name) ->'
)

content = content.replace(
    'selected = uiState.selectedCategory == category,',
    'selected = uiState.selectedCategory == id,'
)

content = content.replace(
    'onClick = { viewModel.selectCategory(category) },',
    'onClick = { viewModel.selectCategory(id) },'
)

content = content.replace(
    'label = { Text(category) }',
    'label = { Text(name) }'
)

with open('app/src/main/java/com/cgens67/mcaddons/MainActivity.kt', 'w') as f:
    f.write(content)
