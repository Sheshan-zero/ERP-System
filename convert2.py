import os
import re

directory = 'c:/Users/USER/Desktop/ERP-System-ADBMS/src/main/java/com/erp/manufacturing'

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find the public record definition
    match = re.search(r'public\s+record\s+([A-Za-z0-9_]+)\s*\((.*?)\)\s*\{', content, re.DOTALL)
    if not match:
        return False

    class_name = match.group(1)
    params_str = match.group(2)
    
    # Split params safely by commas that are not inside generics
    params = []
    current_param = ''
    generic_depth = 0
    for char in params_str:
        if char == '<': generic_depth += 1
        elif char == '>': generic_depth -= 1
        elif char == ',' and generic_depth == 0:
            params.append(current_param.strip())
            current_param = ''
            continue
        current_param += char
    if current_param.strip():
        params.append(current_param.strip())

    fields = []
    for p in params:
        p = p.strip()
        if not p: continue
        parts = p.split()
        name = parts[-1]
        typ = ' '.join(parts[:-1])
        fields.append(f'    private {typ} {name};')

    fields_str = '\n'.join(fields)
    
    # Replace imports right after package
    if 'import lombok.Data;' not in content:
        content = re.sub(r'(package\s+.*?;)', r'\1\n\nimport lombok.Data;\nimport lombok.NoArgsConstructor;\nimport lombok.AllArgsConstructor;', content, count=1)
        
    # Replace the record with class
    new_class_def = f"@Data\n@NoArgsConstructor\n@AllArgsConstructor\npublic class {class_name} {{\n{fields_str}"
    
    content = re.sub(r'public\s+record\s+'+class_name+r'\s*\(.*?\)\s*\{', new_class_def, content, flags=re.DOTALL)
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
        
    print(f"Converted {class_name} in {filepath}")
    return True

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.java') and 'dto' in root:
            process_file(os.path.join(root, file))
