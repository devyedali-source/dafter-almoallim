package com.example.data.planning.canonical

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CanonicalAnnualPlan(
    val schema_version: String = "1.0",
    val source: CanonicalSourceInfo? = null,
    val niveau: CanonicalNiveauInfo,
    val reperes_calendrier: List<CanonicalCalendarMarker> = emptyList(),
    val fasls: List<CanonicalFaslInfo> = emptyList(),
    val notes: List<String> = emptyList(),
    val matieres: List<CanonicalSubject> = emptyList(),
    val lesson_instances: List<CanonicalLessonInstance> = emptyList(),
    val reperes_locaux: List<CanonicalLocalMarker> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CanonicalSourceInfo(
    val document: String? = null,
    val editeur: String? = null,
    val pays: String? = null,
    val annee: Int? = null
)

@JsonClass(generateAdapter = true)
data class CanonicalNiveauInfo(
    val code: String,
    val label: String,
    val semaines_total: Int = 38
)

@JsonClass(generateAdapter = true)
data class CanonicalCalendarMarker(
    val semaine: Int,
    val type: String, // "accueil", "examen", "vacances", etc.
    val label: String? = null
)

@JsonClass(generateAdapter = true)
data class CanonicalLocalMarker(
    val semaine: Int? = null,
    val type: String? = null,
    val label: String? = null,
    val traitement: String? = null
)

@JsonClass(generateAdapter = true)
data class CanonicalFaslInfo(
    val numero: Int,
    val semaines: List<Int> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CanonicalSubject(
    val code: String,
    val nom: String,
    val objectif_integrateur: String? = null,
    val source_code: String? = null,
    val source_label: String? = null,
    val canonical_code: String? = null,
    val canonical_label: String? = null,
    val domaines: List<CanonicalDomain> = emptyList(),
    val reperes_locaux: List<CanonicalLocalMarker> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CanonicalDomain(
    val code: String,
    val nom: String,
    val source_domain_code: String? = null,
    val source_label: String? = null,
    val source_parent_subject: String? = null,
    val canonical_domain_code: String? = null,
    val canonical_label: String? = null,
    val competences: List<CanonicalCompetence> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CanonicalCompetence(
    val code: String,
    val enonce: String? = null,
    val habiletes: List<String> = emptyList(),
    val activites: List<String> = emptyList(),
    val unites: List<CanonicalUnit> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CanonicalUnit(
    val semaines: List<Int> = emptyList(),
    val type: String, // "lecon" or "integration"
    val titre: String,
    val habiletes: List<String> = emptyList(),
    val activites: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CanonicalLessonInstance(
    val niveau: String,
    val fasl: Int? = null,
    val matiere: String,
    val domaine: String,
    val competence: String,
    val semaine: Int,
    val titre_officiel: String,
    val lesson_unit_id: String,
    val partie: Int = 1,
    val nombre_parties: Int = 1,
    val est_suite: Boolean = false,
    val source_domaine: String? = null,
    val source_lesson_unit_id: String? = null
)
