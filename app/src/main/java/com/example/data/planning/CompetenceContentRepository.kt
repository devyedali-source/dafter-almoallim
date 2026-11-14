package com.example.data.planning

import android.content.Context
import org.json.JSONObject
import java.util.zip.GZIPInputStream

/**
 * محتوى أسابيع الإدماج والتقويم والعلاج لكل كفاية (مقترح تربوي مساعد، وليس نصًا رسميًا).
 * المفتاح: "<المستوى>|<رمز المادة>_<رمز المجال>_w<الأسبوع>" وهو معرّف المحطة بلا اللاحقة _synth.
 */
data class CompetenceWeekContent(
    val isEnd: Boolean,
    val competenceNumber: String,
    val situation: String,
    val remediation: List<String>,
    val units: String,
    val questions: List<Pair<String, String>>,
    val keyQuestions: Set<Int>
)

object CompetenceContentRepository {
    @Volatile private var cache: JSONObject? = null

    private fun load(context: Context): JSONObject? {
        cache?.let { return it }
        return try {
            context.applicationContext.assets.open("planning/competence_content.json.gz").use { raw ->
                GZIPInputStream(raw).bufferedReader(Charsets.UTF_8).use { JSONObject(it.readText()) }
            }.also { cache = it }
        } catch (e: Exception) { null }
    }

    /** يُرجع محتوى المحطة أو null إن لم يوجد (تبقى المقترحات العامة القديمة كما هي). */
    fun find(context: Context, level: Int, stationId: String): CompetenceWeekContent? {
        val root = load(context) ?: return null
        val o = root.optJSONObject("$level|${stationId.removeSuffix("_synth")}") ?: return null
        return try {
            val rem = o.optJSONArray("rem")
            val qs = o.optJSONArray("qs")
            val key = o.optJSONArray("key")
            CompetenceWeekContent(
                isEnd = o.optBoolean("end"),
                competenceNumber = o.optString("comp"),
                situation = o.optString("sit"),
                remediation = (0 until (rem?.length() ?: 0)).map { rem!!.getString(it) },
                units = o.optString("units"),
                questions = (0 until (qs?.length() ?: 0)).map { i ->
                    val p = qs!!.getJSONArray(i); p.getString(0) to p.getString(1)
                },
                keyQuestions = (0 until (key?.length() ?: 0)).map { key!!.getInt(it) }.toSet()
            )
        } catch (e: Exception) { null }
    }
}
