package com.example.ui.utils

import android.content.Context
import com.example.data.models.Student
import org.json.JSONArray
import org.json.JSONObject

/**
 * مجموعات يحدّدها المستخدم بنفسه (فرقة الأناشيد والكناسة) بدل التقسيم التلقائي.
 * إذا لم يفعّل المستخدم الوضع اليدوي يبقى التقسيم التلقائي كما كان.
 * تُحفظ لكل قسم ولكل نوع ("hymns" أو "cleaning") في SharedPreferences.
 */
object ManualGroups {
    const val HYMNS = "hymns"
    const val CLEANING = "cleaning"

    data class Group(var name: String, val ids: MutableList<Long>)
    data class Config(val enabled: Boolean, val groups: List<Group>)

    private const val PREFS = "teacher_settings_prefs"
    private fun key(type: String, classId: Long) = "manual_groups_${type}_$classId"

    private val dayNames = listOf("الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")
    private val ordinals = listOf("الأولى", "الثانية", "الثالثة", "الرابعة", "الخامسة", "السادسة", "السابعة", "الثامنة", "التاسعة", "العاشرة", "الحادية عشرة", "الثانية عشرة")

    /** اسم افتراضي للمجموعة رقم [index] (يبدأ من 0). */
    fun defaultName(type: String, index: Int): String {
        val base = "المجموعة " + (ordinals.getOrNull(index) ?: "${index + 1}")
        return if (type == CLEANING && index < dayNames.size) "$base (يوم ${dayNames[index]})" else base
    }

    fun load(context: Context, type: String, classId: Long): Config? {
        return try {
            val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(key(type, classId), null) ?: return null
            val o = JSONObject(raw)
            val arr = o.optJSONArray("groups") ?: JSONArray()
            val groups = (0 until arr.length()).map { i ->
                val g = arr.getJSONObject(i)
                val ids = g.optJSONArray("ids") ?: JSONArray()
                Group(g.optString("name"), (0 until ids.length()).map { ids.getLong(it) }.toMutableList())
            }
            Config(o.optBoolean("enabled", false), groups)
        } catch (e: Exception) {
            null
        }
    }

    fun save(context: Context, type: String, classId: Long, enabled: Boolean, groups: List<Group>) {
        try {
            val arr = JSONArray()
            groups.forEach { g ->
                arr.put(JSONObject().put("name", g.name).put("ids", JSONArray(g.ids)))
            }
            val o = JSONObject().put("enabled", enabled).put("groups", arr)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(key(type, classId), o.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * المجموعات اليدوية جاهزة للطباعة (الاسم + التلاميذ)، أو null إذا لم يفعّل المستخدم الوضع اليدوي
     * فيبقى التقسيم التلقائي. التلاميذ المحذوفون من القسم يُتجاهلون.
     */
    fun resolve(context: Context, type: String, classId: Long, students: List<Student>): List<Pair<String, List<Student>>>? {
        val cfg = load(context, type, classId) ?: return null
        if (!cfg.enabled || cfg.groups.isEmpty()) return null
        val byId = students.associateBy { it.id }
        return cfg.groups.mapIndexed { i, g ->
            val name = g.name.trim().ifBlank { defaultName(type, i) }
            name to g.ids.mapNotNull { byId[it] }
        }
    }
}
