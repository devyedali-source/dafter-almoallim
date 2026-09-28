import json

def gen_kt():
    out = []
    out.append('''package com.example.data.planning

import com.example.data.models.ClassSection

object AnnualPlanningRepository {

    fun resolveLevel(classSection: ClassSection?): Int {
        if (classSection == null) return 0
        if (classSection.level in 1..5) {
            return classSection.level
        }
        val parsed = parseLevelFromClassName(classSection.getFormattedName())
        if (parsed in 1..5) {
            return parsed
        }
        val parsedFromName = parseLevelFromClassName(classSection.name)
        if (parsedFromName in 1..5) {
            return parsedFromName
        }
        val parsedFromSectionName = parseLevelFromClassName(classSection.sectionName)
        if (parsedFromSectionName in 1..5) {
            return parsedFromSectionName
        }
        return 0
    }

    fun parseLevelFromClassName(className: String): Int {
        val name = className.trim()
        if (name.isBlank()) return 0
        return when {
            name.contains("الخامسة") || name.contains("الخامس") || name.contains("س5") || name.contains("س 5") || name.contains("5AP") || name.contains("5 AP") || name.contains("5أساس") -> 5
            name.contains("الرابعة") || name.contains("الرابع") || name.contains("س4") || name.contains("س 4") || name.contains("4AP") || name.contains("4 AP") || name.contains("4أساس") -> 4
            name.contains("الثالثة") || name.contains("الثالث") || name.contains("س3") || name.contains("س 3") || name.contains("3AP") || name.contains("3 AP") || name.contains("3أساس") -> 3
            name.contains("الثانية") || name.contains("الثاني") || name.contains("س2") || name.contains("س 2") || name.contains("2AP") || name.contains("2 AP") || name.contains("2أساس") -> 2
            name.contains("الأولى") || name.contains("الأول") || name.contains("س1") || name.contains("س 1") || name.contains("1AP") || name.contains("1 AP") || name.contains("1أساس") -> 1

            Regex("""(?:سنة|س|صف|مستوى|المستوى|قسم|القسم|السنة)\\s*[:\\-_]?\\s*5""").containsMatchIn(name) -> 5
            Regex("""(?:سنة|س|صف|مستوى|المستوى|قسم|القسم|السنة)\\s*[:\\-_]?\\s*4""").containsMatchIn(name) -> 4
            Regex("""(?:سنة|س|صف|مستوى|المستوى|قسم|القسم|السنة)\\s*[:\\-_]?\\s*3""").containsMatchIn(name) -> 3
            Regex("""(?:سنة|س|صف|مستوى|المستوى|قسم|القسم|السنة)\\s*[:\\-_]?\\s*2""").containsMatchIn(name) -> 2
            Regex("""(?:سنة|س|صف|مستوى|المستوى|قسم|القسم|السنة)\\s*[:\\-_]?\\s*1""").containsMatchIn(name) -> 1

            else -> 0
        }
    }

    fun getLevelTitle(level: Int): String {
        return when (level) {
            1 -> "السنة الأولى"
            2 -> "السنة الثانية"
            3 -> "السنة الثالثة"
            4 -> "السنة الرابعة"
            5 -> "السنة الخامسة"
            else -> "مستوى غير محدد"
        }
    }

    fun getLessonsForLevel(level: Int): List<AnnualLessonItem> {
        return allLessons.filter { it.level == level }
    }

    fun getWeeksForLevel(level: Int): List<Int> {
        return (1..38).toList()
    }

    fun getSubjectsForWeek(level: Int, week: Int): List<String> {
        return allLessons
            .filter { it.level == level && week in it.week..it.weekEnd }
            .map { it.subject }
            .distinct()
    }

    fun getLessonsForWeekAndSubject(level: Int, week: Int, subject: String): List<AnnualLessonItem> {
        return allLessons.filter {
            it.level == level && subject == it.subject && week in it.week..it.weekEnd
        }
    }

    fun searchLessons(level: Int, query: String): List<AnnualLessonItem> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return allLessons.filter {
            it.level == level && (
                it.title.lowercase().contains(q) ||
                it.subject.lowercase().contains(q) ||
                it.domain.lowercase().contains(q) ||
                it.skills.lowercase().contains(q) ||
                it.competency.lowercase().contains(q)
            )
        }
    }

    // Comprehensive official primary education curriculum dataset for all 5 levels (38 weeks each)
    private val allLessons: List<AnnualLessonItem> = listOf(
''')

    # Now write helper to append items
    return out

print("generator script template ready")
