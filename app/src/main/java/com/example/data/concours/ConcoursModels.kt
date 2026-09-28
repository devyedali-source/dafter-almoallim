package com.example.data.concours

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConcoursRoot(
    val version: Int = 1,
    val title: String = "",
    val subjects: List<ConcoursSubject> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ConcoursSubject(
    val code: String = "",
    val name: String = "",
    val type: String = "exercices",
    val domains: List<ConcoursDomain> = emptyList(),
    val situations: List<ConcoursSituation> = emptyList(),
    val qa: List<ConcoursQA> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ConcoursDomain(
    val code: String = "",
    val name: String = "",
    val items: List<ConcoursItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ConcoursItem(
    val id: String = "",
    val txt: String = "",
    val q: String = "",
    val sol: String = ""
)

@JsonClass(generateAdapter = true)
data class ConcoursSituation(
    val id: String = "",
    val title: String = "",
    val ctx: String = "",
    val tasks: List<String> = emptyList(),
    val sol: String = ""
)

@JsonClass(generateAdapter = true)
data class ConcoursQA(
    val id: String = "",
    val q: String = "",
    val a: String = ""
)
