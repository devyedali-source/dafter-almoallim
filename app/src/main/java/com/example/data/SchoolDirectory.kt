package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

/**
 * دليل المدارس: الولاية ← المقاطعة ← قائمة المدارس (من موقع النتائج الرسمي، مسابقة 2026).
 * أسماء الولايات هي نفسها أسماء التطبيق (مثل «كوركول» و«نواكشوط الشمالية»).
 * يُحمَّل مرة واحدة من assets/geo/schools_directory.json.gz.
 */
object SchoolDirectory {

    private const val ASSET_PATH = "geo/schools_directory.json.gz"

    private val loadLock = Any()

    @Volatile
    private var cached: Map<String, Map<String, List<String>>>? = null

    private fun load(context: Context): Map<String, Map<String, List<String>>> {
        cached?.let { return it }
        synchronized(loadLock) {
            cached?.let { return it }
            val parsed: Map<String, Map<String, List<String>>> = try {
                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val type = Types.newParameterizedType(
                    Map::class.java,
                    String::class.java,
                    Types.newParameterizedType(
                        Map::class.java,
                        String::class.java,
                        Types.newParameterizedType(List::class.java, String::class.java)
                    )
                )
                val adapter = moshi.adapter<Map<String, Map<String, List<String>>>>(type)
                context.applicationContext.assets.open(ASSET_PATH).use { base ->
                    GZIPInputStream(base).use { gz ->
                        InputStreamReader(gz, Charsets.UTF_8).use { reader ->
                            adapter.fromJson(reader.readText())
                        }
                    }
                } ?: emptyMap()
            } catch (e: Throwable) {
                emptyMap()
            }
            cached = parsed
            return parsed
        }
    }

    /** مقاطعات الولاية كما في الدليل (فارغة إن لم تُعرف الولاية). */
    fun moughataas(context: Context, wilaya: String): List<String> =
        load(context)[wilaya.trim()]?.keys?.toList() ?: emptyList()

    /** مدارس المقاطعة مرتبة أبجدياً (فارغة إن لم تُعرف). */
    fun schools(context: Context, wilaya: String, moughataa: String): List<String> =
        load(context)[wilaya.trim()]?.get(moughataa.trim()) ?: emptyList()

    /** هل الاسم موجود في دليل هذه المقاطعة؟ */
    fun contains(context: Context, wilaya: String, moughataa: String, school: String): Boolean =
        schools(context, wilaya, moughataa).contains(school.trim())
}
