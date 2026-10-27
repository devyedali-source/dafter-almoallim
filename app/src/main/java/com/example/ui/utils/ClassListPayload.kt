package com.example.ui.utils

import android.util.Base64
import com.example.data.models.ClassSection
import com.example.data.models.Student
import org.json.JSONArray
import org.json.JSONObject

/**
 * بيانات دقيقة مخفية داخل ملف «لائحة التلاميذ» PDF، تُستعمل لاستيراد القسم في حساب آخر.
 * تُكتب بحروف لاتينية فقط (Base64) لأن النص العربي يتلف عند استخراجه من PDF.
 */
object ClassListPayload {
    private const val START = "DFTR1~"
    private const val END = "~DFTREND"

    data class Imported(
        val schoolName: String,
        val level: Int,
        val sectionName: String,
        val wilaya: String,
        val moughataa: String,
        val academicYear: String,
        val students: List<Student>
    )

    /** يعيد قطعة HTML شبه غير مرئية (خط 1px بلون أبيض تقريباً) تحمل البيانات. */
    fun toHtml(
        cls: ClassSection,
        students: List<Student>,
        withIds: Boolean,
        withPhones: Boolean,
        withHealth: Boolean
    ): String {
        return try {
            val arr = JSONArray()
            students.sortedBy { it.id }.forEach { s ->
                arr.put(JSONObject().apply {
                    put("n", s.name)
                    put("f", s.frenchName)
                    put("g", s.gender)
                    if (withIds) { put("s", s.schoolId); put("a", s.nationalId) }
                    if (withPhones) { put("p1", s.parentPhone1); put("p2", s.parentPhone2) }
                    if (withHealth) put("h", s.healthNotes)
                })
            }
            val root = JSONObject().apply {
                put("school", cls.schoolName)
                put("level", cls.level)
                put("section", cls.sectionName)
                put("wilaya", cls.wilaya)
                put("moughataa", cls.moughataa)
                put("year", cls.academicYear)
                put("students", arr)
            }
            val b64 = Base64.encodeToString(
                root.toString().toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
            )
            val full = START + b64 + END
            val chunks = full.chunked(60).joinToString(" ")
            "<div dir=\"ltr\" style=\"font-size:1px;line-height:1px;color:#fcfcfc;margin-top:4px;word-break:break-all;\">$chunks</div>"
        } catch (e: Exception) {
            ""
        }
    }

    /** يقرأ النص المستخرج من PDF ويعيد القسم والتلاميذ، أو null إن لم توجد البيانات. */
    fun parse(extractedText: String): Imported? {
        return try {
            val flat = extractedText.filter { !it.isWhitespace() }
            val a = flat.indexOf(START)
            if (a < 0) return null
            val b = flat.indexOf(END, a + START.length)
            if (b < 0) return null
            val b64 = flat.substring(a + START.length, b)
            val json = String(
                Base64.decode(b64, Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE),
                Charsets.UTF_8
            )
            val o = JSONObject(json)
            val arr = o.getJSONArray("students")
            val list = mutableListOf<Student>()
            for (i in 0 until arr.length()) {
                val s = arr.getJSONObject(i)
                val name = s.optString("n").trim()
                if (name.isBlank()) continue
                list.add(
                    Student(
                        name = name,
                        classId = 0L,
                        frenchName = s.optString("f"),
                        schoolId = s.optString("s"),
                        nationalId = s.optString("a"),
                        healthNotes = s.optString("h"),
                        parentPhone1 = s.optString("p1"),
                        parentPhone2 = s.optString("p2"),
                        gender = s.optString("g", "ذكر").ifBlank { "ذكر" }
                    )
                )
            }
            Imported(
                schoolName = o.optString("school"),
                level = o.optInt("level", 1),
                sectionName = o.optString("section"),
                wilaya = o.optString("wilaya"),
                moughataa = o.optString("moughataa"),
                academicYear = o.optString("year"),
                students = list
            )
        } catch (e: Exception) {
            null
        }
    }
}
