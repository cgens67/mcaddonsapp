import os
import xml.etree.ElementTree as ET

def add_string(filepath, key, value):
    tree = ET.parse(filepath)
    root = tree.getroot()
    # Check if exists
    for elem in root.findall('string'):
        if elem.get('name') == key:
            return
    new_elem = ET.Element('string', {'name': key})
    new_elem.text = value
    root.append(new_elem)
    ET.indent(tree, space="    ", level=0)
    tree.write(filepath, encoding='utf-8', xml_declaration=True)

# English
add_string('app/src/main/res/values/strings.xml', 'no_addons_desc', 'Try adjusting your filters or search query.')
# Spanish
if os.path.exists('app/src/main/res/values-es/strings.xml'):
    add_string('app/src/main/res/values-es/strings.xml', 'no_addons_desc', 'Intenta ajustar tus filtros o la consulta de búsqueda.')
# French
if os.path.exists('app/src/main/res/values-fr/strings.xml'):
    add_string('app/src/main/res/values-fr/strings.xml', 'no_addons_desc', 'Essayez d\'ajuster vos filtres ou votre requête de recherche.')
# German
if os.path.exists('app/src/main/res/values-de/strings.xml'):
    add_string('app/src/main/res/values-de/strings.xml', 'no_addons_desc', 'Versuchen Sie, Ihre Filter oder Suchanfrage anzupassen.')
# Japanese
if os.path.exists('app/src/main/res/values-ja/strings.xml'):
    add_string('app/src/main/res/values-ja/strings.xml', 'no_addons_desc', 'フィルターまたは検索クエリを調整してみてください。')
# Chinese
if os.path.exists('app/src/main/res/values-zh/strings.xml'):
    add_string('app/src/main/res/values-zh/strings.xml', 'no_addons_desc', '尝试调整您的过滤器或搜索查询。')

