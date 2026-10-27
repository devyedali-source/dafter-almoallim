package com.example.ui.utils

import com.example.data.models.Student

/** مجموعات النظافة: البنات والأولاد في مجموعات منفصلة، ولا مجموعة تجمعهما. */
object CleaningGroups {
    data class Result(val groups: List<List<Student>>, val labels: List<String>)

    fun split(students: List<Student>, count: Int = 5): Result {
        val sorted = students.sortedBy { it.id }
        val girls = sorted.filter { it.gender == "أنثى" }
        val boys = sorted.filter { it.gender != "أنثى" }
        val girlGroups = when {
            girls.isEmpty() -> 0
            boys.isEmpty() -> count
            else -> Math.round(count.toDouble() * girls.size / sorted.size).toInt().coerceIn(1, count - 1)
        }
        val boyGroups = count - girlGroups
        val boyLists = List(boyGroups) { mutableListOf<Student>() }
        boys.forEachIndexed { i, s -> boyLists[i % boyGroups].add(s) }
        val girlLists = List(girlGroups) { mutableListOf<Student>() }
        girls.forEachIndexed { i, s -> girlLists[i % girlGroups].add(s) }
        return Result(
            groups = boyLists + girlLists,
            labels = List(boyGroups) { "أولاد" } + List(girlGroups) { "بنات" }
        )
    }
}
