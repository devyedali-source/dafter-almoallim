package com.example.data.exams

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExamsRoot(
    val version: Int = 1,
    val title: String = "",
    val levels: List<ExamLevel> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ExamLevel(
    val code: String = "",
    val exams: List<ExamTerm> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ExamTerm(
    val fasl: Int = 0,
    val semaine: Int = 0,
    val subjects: List<ExamSubject> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ExamSubject(
    val code: String = "",
    val name: String = "",
    val proposals: List<ExamProposal> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ExamProposal(
    val id: String = "",
    val title: String = "",
    val ctx: String = "",
    val task: String = "",
    val score: String = ""
)
