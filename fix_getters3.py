import re
import os

directory = 'c:/Users/USER/Desktop/ERP-System-ADBMS/src/main/java/com/erp/manufacturing'

def to_camel_case(match):
    var_name = match.group(1)
    field_name = match.group(2)
    getter_name = 'get' + field_name[0].upper() + field_name[1:]
    return f"{var_name}.{getter_name}()"

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            new_content = re.sub(r'\b(usage|assignment|itemRequest)\.(?!get)([a-zA-Z0-9_]+)\(\)', to_camel_case, content)
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed getters in {file}")
