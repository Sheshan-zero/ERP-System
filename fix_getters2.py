import re

filepath = 'c:/Users/USER/Desktop/ERP-System-ADBMS/src/main/java/com/erp/manufacturing/reporting/ReportExportService.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

def to_camel_case(match):
    var_name = match.group(1)
    field_name = match.group(2)
    getter_name = 'get' + field_name[0].upper() + field_name[1:]
    return f"{var_name}.{getter_name}()"

new_content = re.sub(r'\b(invoice|summary)\.(?!get)([a-zA-Z0-9_]+)\(\)', to_camel_case, content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(new_content)
