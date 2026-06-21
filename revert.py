import os
import re

directory = 'c:/Users/USER/Desktop/ERP-System-ADBMS/src/main/java/com/erp/manufacturing'
files_to_fix = [
    'accounting/dto/GeneralLedgerEntryResponse.java',
    'notification/dto/NotificationRequest.java',
    'notification/dto/NotificationResponse.java'
]

def revert_file(filepath):
    if not os.path.exists(filepath):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # match imports and class definition
    match = re.search(r'import lombok\.Data;\s*import lombok\.NoArgsConstructor;\s*import lombok\.AllArgsConstructor;\s*@Data\s*@NoArgsConstructor\s*@AllArgsConstructor\s*public class ([A-Za-z0-9_]+) \{(.*?)\}', content, re.DOTALL)
    if not match:
        return

    class_name = match.group(1)
    body = match.group(2)

    # find all fields: private Type name;
    fields = re.findall(r'private\s+(.*?)\s+([A-Za-z0-9_]+);', body)
    
    record_params = []
    for typ, name in fields:
        record_params.append(f"        {typ} {name}")
        
    record_def = f"public record {class_name}(\n" + ",\n".join(record_params) + "\n) {\n}\n"
    
    content = content[:match.start()] + record_def
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Reverted {class_name}")

for file in files_to_fix:
    revert_file(os.path.join(directory, file))
