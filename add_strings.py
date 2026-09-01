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

add_string('app/src/main/res/values/strings.xml', 'status_incomplete', 'Incomplete')
add_string('app/src/main/res/values/strings.xml', 'status_beta', 'Beta')
add_string('app/src/main/res/values/strings.xml', 'status_experimental', 'Experimental')
add_string('app/src/main/res/values/strings.xml', 'system_language', 'System Default (%s)')
add_string('app/src/main/res/values/strings.xml', 'search_language_placeholder', 'Search languages...')
add_string('app/src/main/res/values/strings.xml', 'clear_search', 'Clear search')
add_string('app/src/main/res/values/strings.xml', 'applying', 'Applying...')
add_string('app/src/main/res/values/strings.xml', 'restarting', 'Restarting...')
add_string('app/src/main/res/values/strings.xml', 'no_results_found', 'No results found')
add_string('app/src/main/res/values/strings.xml', 'try_another_term', 'Try another search term')
add_string('app/src/main/res/values/strings.xml', 'last_updated_version', 'Updated: %s')
add_string('app/src/main/res/values/strings.xml', 'changing_language', 'Changing language...')
add_string('app/src/main/res/values/strings.xml', 'configure_app_language', 'Configure app language')

