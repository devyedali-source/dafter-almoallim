# -*- coding: utf-8 -*-
with open('generate_complete_planning.py', 'r', encoding='utf-8') as f:
    text = f.read()

# Replace the f-string issue
text = text.replace("{',\\n'.join(items)}", '\"\"\" + ",\n".join(items) + \"\"\"')

with open('generate_complete_planning.py', 'w', encoding='utf-8') as f:
    f.write(text)

