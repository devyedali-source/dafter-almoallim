package com.example.ui.utils

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import java.text.Normalizer

/** تلميذ مقروء من لائحة PDF (اللائحة الرسمية التي يسحبها التطبيق). */
data class ImportedStudent(
    val number: Int,
    val name: String,
    val frenchName: String = "",
    val schoolId: String = "",
    val nationalId: String = "",
    val phone1: String = "",
    val phone2: String = "",
    val gender: String = ""
)

/**
 * يقرأ لائحة التلاميذ من ملف PDF نصي (غير ممسوح ضوئياً) بالاعتماد على مواضع الحروف في الصفحة،
 * دون إنترنت. لا يغيّر أي بيانات؛ يُرجع القائمة فقط ليراجعها المستخدم قبل الحفظ.
 */
object StudentPdfImporter {

    private class Glyph(val t: String, val x0: Float, val x1: Float, val y: Float)

    private class GlyphStripper : PDFTextStripper() {
        val glyphs = ArrayList<Glyph>()
        override fun processTextPosition(text: TextPosition) {
            val u = text.unicode ?: return
            if (u.isBlank()) return
            val x0 = text.xDirAdj
            glyphs.add(Glyph(Normalizer.normalize(u, Normalizer.Form.NFKC), x0, x0 + text.widthDirAdj, text.yDirAdj))
        }
        override fun writeString(text: String?, textPositions: MutableList<TextPosition>?) { /* لا نحتاج النص المجمّع */ }
    }

    private val arabic = Regex("[\\u0600-\\u06FF]")
    private val latin = Regex("[A-Za-z]")

    fun read(context: Context, uri: Uri): List<ImportedStudent> {
        PDFBoxResourceLoader.init(context.applicationContext)
        val out = ArrayList<ImportedStudent>()
        context.contentResolver.openInputStream(uri)?.use { ins ->
            val doc = PDDocument.load(ins)
            try {
                for (p in 1..doc.numberOfPages) {
                    val st = GlyphStripper()
                    st.startPage = p
                    st.endPage = p
                    st.getText(doc)
                    val pageW = doc.getPage(p - 1).mediaBox.width
                    out.addAll(parsePage(st.glyphs, pageW))
                }
            } finally {
                doc.close()
            }
        }
        return out
    }

    private fun parsePage(all: List<Glyph>, pageW: Float): List<ImportedStudent> {
        // عمود الرقم: أرقام عند الحافة اليمنى للجدول
        val numCol = all.filter { it.x1 >= pageW * 0.94f && it.x1 <= pageW * 0.965f && it.t.all { c -> c.isDigit() } }
            .sortedBy { it.y }
        val numRows = ArrayList<MutableList<Glyph>>()
        for (g in numCol) {
            if (numRows.isNotEmpty() && Math.abs(g.y - numRows.last().first().y) < 3f) numRows.last().add(g)
            else numRows.add(mutableListOf(g))
        }
        data class NR(val v: Int, val y: Float)
        var rows = numRows.mapNotNull { r ->
            val s = r.sortedByDescending { it.x1 }.reversed().joinToString("") { it.t }
            s.toIntOrNull()?.let { NR(it, r.map { g -> g.y }.average().toFloat()) }
        }
        // نبقي الأرقام المتتابعة فقط (تجاهل أرقام شاردة في الترويسة)
        val vals = rows.map { it.v }.toSet()
        rows = rows.filter { (it.v - 1) in vals || (it.v + 1) in vals }
        // عند التكرار نأخذ الأدنى في الصفحة
        rows = rows.groupBy { it.v }.map { e -> e.value.maxByOrNull { it.y }!! }.sortedBy { it.y }
        if (rows.isEmpty()) return emptyList()

        val spacing = if (rows.size > 1) rows.zipWithNext { a, b -> b.y - a.y }.sorted()[(rows.size - 1) / 2] else 24f
        val body = all.filter { it.x1 < pageW * 0.94f }
        val result = ArrayList<ImportedStudent>()
        rows.forEachIndexed { i, r ->
            val lo = if (i > 0) (rows[i - 1].y + r.y) / 2 else r.y - spacing / 2
            val hi = if (i + 1 < rows.size) (r.y + rows[i + 1].y) / 2 else r.y + spacing / 2
            val band = body.filter { it.y >= lo && it.y < hi }.sortedByDescending { it.x1 }
            if (band.isEmpty()) return@forEachIndexed

            // تقسيم إلى خلايا حسب الفراغات الكبيرة، ومسافات بين الكلمات حسب الفراغات الصغيرة
            val cells = ArrayList<StringBuilder>()
            val cellX = ArrayList<Float>()
            var prev: Glyph? = null
            for (g in band) {
                val gap = if (prev == null) 0f else prev.x0 - g.x1
                if (prev == null || gap > 9f) {
                    cells.add(StringBuilder(g.t)); cellX.add(g.x1)
                } else {
                    if (gap > 1.0f) cells.last().append(' ')
                    cells.last().append(g.t)
                }
                prev = g
            }
            var name = ""
            var french = ""
            val digits = ArrayList<String>()
            cells.forEachIndexed { idx, sb ->
                val raw = sb.toString()
                when {
                    name.isEmpty() && arabic.containsMatchIn(raw) && cellX[idx] > pageW * 0.75f -> name = raw.trim()
                    latin.containsMatchIn(raw) -> french = raw.reversed().trim()
                    raw.all { it.isDigit() || it == ' ' } -> {
                        val d = raw.reversed().filter { it.isDigit() }
                        if (d.length in 6..14) digits.add(d)
                    }
                }
            }
            if (name.isBlank()) return@forEachIndexed

            // الرقم الوطني 9–10 أرقام؛ الرقم المدرسي قبله (6–8)؛ الهواتف 8 أرقام بعده
            val nIdx = digits.indexOfFirst { it.length in 9..10 }
            var school = ""
            var national = ""
            val phones = ArrayList<String>()
            if (nIdx >= 0) {
                national = digits[nIdx]
                if (nIdx >= 1 && digits[nIdx - 1].length in 6..8) school = digits[nIdx - 1]
                for (k in nIdx + 1 until digits.size) if (digits[k].length == 8) phones.add(digits[k])
            } else {
                if (digits.isNotEmpty() && digits[0].length in 6..8) school = digits[0]
                for (k in 1 until digits.size) if (digits[k].length == 8) phones.add(digits[k])
            }
            result.add(
                ImportedStudent(
                    number = r.v, name = name, frenchName = french,
                    schoolId = school, nationalId = national,
                    phone1 = phones.getOrElse(0) { "" }, phone2 = phones.getOrElse(1) { "" }
                )
            )
        }
        return result
    }
}
