# -*- coding: utf-8 -*-
import generate_level5

def escape_kt_string(s):
    if s is None:
        return ""
    return str(s).replace('\\', '\\\\').replace('"', '\\"').replace('\n', ' ')

def main():
    items = generate_level5.items
    print(f"Generating Kotlin file for {len(items)} items...")

    lines = []
    lines.append("package com.example.data.planning")
    lines.append("")
    lines.append("// Auto-generated official Level 5 curriculum dataset from 'برنامج السنة الخامسة 2023.pdf'")
    lines.append("object Level5OfficialCurriculum {")
    lines.append("    val lessons: List<AnnualLessonItem> = listOf(")

    for i, it in enumerate(items):
        is_french_str = ", isFrench = true" if it.get("isFrench", False) else ""
        type_str = f', type = "{it.get("type", "TEACHING")}"'
        
        comma = "," if i < len(items) - 1 else ""
        item_kt = f"""        AnnualLessonItem(
            id = "{escape_kt_string(it['id'])}",
            level = {it['level']},
            week = {it['week']},
            weekEnd = {it['weekEnd']},
            subject = "{escape_kt_string(it['subject'])}",
            domain = "{escape_kt_string(it['domain'])}",
            title = "{escape_kt_string(it['title'])}",
            competency = "{escape_kt_string(it['competency'])}",
            skills = "{escape_kt_string(it['skills'])}",
            activities = "{escape_kt_string(it['activities'])}"{is_french_str}{type_str}
        ){comma}"""
        lines.append(item_kt)

    lines.append("    )")
    lines.append("}")
    lines.append("")

    content = "\n".join(lines)
    with open("app/src/main/java/com/example/data/planning/Level5OfficialCurriculum.kt", "w", encoding="utf-8") as f:
        f.write(content)

    print("Wrote Level5OfficialCurriculum.kt successfully!")

if __name__ == "__main__":
    main()
