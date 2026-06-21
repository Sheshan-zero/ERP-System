import os
import re

directory = 'c:/Users/USER/Desktop/ERP-System-ADBMS/src/main/java/com/erp/manufacturing'

def to_camel_case(match):
    var_name = match.group(1)
    field_name = match.group(2)
    # Don't modify if it's a known non-getter method like toString(), hashCode(), etc.
    if field_name in ['toString', 'hashCode', 'equals', 'builder', 'stream', 'map', 'toList', 'forEach']:
        return match.group(0)
    
    getter_name = 'get' + field_name[0].upper() + field_name[1:]
    return f"{var_name}.{getter_name}()"

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Match variable.method() where variable is a common DTO instance name
            # We use a negative lookahead to prevent matching methods starting with "get", "set", "is"
            new_content = re.sub(r'\b(request|item|payment|supplier|product|production|sales|component|order|dto)\.(?!get|set|is)([a-zA-Z0-9_]+)\(\)', to_camel_case, content)
            
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed getters in {file}")
