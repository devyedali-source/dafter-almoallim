package com.example.data

import android.content.Context
import org.json.JSONObject
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

/**
 * دليل المدارس: الولاية ← المقاطعة ← قائمة المدارس (من موقع النتائج الرسمي، مسابقة 2026).
 * أسماء الولايات هي نفسها أسماء التطبيق (مثل «كوركول» و«نواكشوط الشمالية»).
 * يُحمَّل مرة واحدة من assets/schools_directory.json.gz.
 */
object SchoolDirectory {

    private const val ASSET_PATH = "schools_directory.json.gz"
    private const val ASSET_PATH_RAW = "schools_directory.json"

    private val loadLock = Any()

    /** سبب فشل قراءة الدليل (للتشخيص)، أو null إن نجحت. */
    @Volatile
    var lastError: String? = null

    /** توحيد الكتابة: همزات وتاء مربوطة وألف مقصورة وتشكيل ومسافات. */
    private fun norm(x: String): String = x.trim()
        .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا')
        .replace('ة', 'ه').replace('ى', 'ي')
        .filter { it != 'ـ' && it !in '\u064B'..'\u065F' }
        .replace(Regex("\\s+"), " ")

    @Volatile
    private var cached: Map<String, Map<String, List<String>>>? = null

    private fun load(context: Context): Map<String, Map<String, List<String>>> {
        cached?.let { return it }
        synchronized(loadLock) {
            cached?.let { return it }
            val parsed: Map<String, Map<String, List<String>>> = try {
                // أدوات البناء في أندرويد تفكّ ضغط ملفات .gz وتحذف اللاحقة، فنجرّب الاسمين
                val text = try {
                    context.applicationContext.assets.open(ASSET_PATH).use { base ->
                        GZIPInputStream(base).use { gz ->
                            InputStreamReader(gz, Charsets.UTF_8).use { reader -> reader.readText() }
                        }
                    }
                } catch (e1: Throwable) {
                    context.applicationContext.assets.open(ASSET_PATH_RAW).use { base ->
                        InputStreamReader(base, Charsets.UTF_8).use { reader -> reader.readText() }
                    }
                }
                val out = LinkedHashMap<String, Map<String, List<String>>>()
                val root = JSONObject(text)
                for (w in root.keys()) {
                    val mObj = root.getJSONObject(w)
                    val inner = LinkedHashMap<String, List<String>>()
                    for (m in mObj.keys()) {
                        val arr = mObj.getJSONArray(m)
                        inner[m] = List(arr.length()) { i -> arr.getString(i) }
                    }
                    out[w] = inner
                }
                out
                .also { lastError = null }
            } catch (e: Throwable) {
                lastError = e.javaClass.simpleName + ": " + (e.message ?: "-")
                emptyMap()
            }
            if (parsed.isNotEmpty()) cached = parsed
            return parsed
        }
    }

    /** مقاطعات الولاية كما في الدليل (فارغة إن لم تُعرف الولاية). */
    fun moughataas(context: Context, wilaya: String): List<String> {
        val all = load(context)
        val key = if (all.containsKey(wilaya.trim())) wilaya.trim() else all.keys.firstOrNull { norm(it) == norm(wilaya) }
        return all[key]?.keys?.toList() ?: emptyList()
    }

    /** مدارس المقاطعة مرتبة أبجدياً (فارغة إن لم تُعرف). */
    fun schools(context: Context, wilaya: String, moughataa: String): List<String> {
        val all = load(context)
        val wKey = if (all.containsKey(wilaya.trim())) wilaya.trim() else all.keys.firstOrNull { norm(it) == norm(wilaya) }
        val m = all[wKey] ?: return emptyList()
        val mKey = if (m.containsKey(moughataa.trim())) moughataa.trim() else m.keys.firstOrNull { norm(it) == norm(moughataa) }
        return m[mKey] ?: emptyList()
    }

    /** هل الاسم موجود في دليل هذه المقاطعة؟ */
    fun contains(context: Context, wilaya: String, moughataa: String, school: String): Boolean =
        schools(context, wilaya, moughataa).contains(school.trim())
}
