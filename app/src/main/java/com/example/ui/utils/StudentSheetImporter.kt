package com.example.ui.utils

import android.content.Context
import android.net.Uri
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.text.Normalizer
import java.util.zip.ZipInputStream

/**
 * استيراد لائحة التلاميذ من أي ملف: PDF (لائحة التطبيق) أو Excel بصيغة xlsx أو CSV/نص.
 * الأعمدة في Excel وCSV تُعرَف من عناوينها (الاسم، الاسم بالفرنسية، الرقم المدرسي، الرقم الوطني، هاتف، الجنس)
 * مهما اختلف ترتيبها. لا إنترنت ولا تغيير في أي بيانات؛ القائمة تُراجَع قبل الحفظ.
 */
object StudentFileImporter {

    fun read(context: Context, uri: Uri): List<ImportedStudent> {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalStateException("تعذّر فتح الملف")
        if (bytes.size < 4) throw IllegalStateException("الملف فارغ")
        return when {
            bytes[0] == '%'.code.toByte() && bytes[1] == 'P'.code.toByte() && bytes[2] == 'D'.code.toByte() ->
                StudentPdfImporter.read(context, uri)
            bytes[0] == 'P'.code.toByte() && bytes[1] == 'K'.code.toByte() ->
                fromRows(readXlsx(bytes))
            (bytes[0].toInt() and 0xFF) == 0xD0 && (bytes[1].toInt() and 0xFF) == 0xCF ->
                throw IllegalStateException("هذا ملف Excel قديم (xls). افتحه واحفظه بصيغة xlsx أو csv ثم أعد المحاولة.")
            else -> fromRows(readCsv(bytes))
        }
    }

    // ---------- xlsx ----------

    private fun readXlsx(bytes: ByteArray): List<List<String>> {
        val entries = HashMap<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                val n = e.name
                if (n == "xl/sharedStrings.xml" || (n.startsWith("xl/worksheets/sheet") && n.endsWith(".xml"))) {
                    entries[n] = zin.readBytes()
                }
                e = zin.nextEntry
            }
        }
        val shared = entries["xl/sharedStrings.xml"]?.let { parseSharedStrings(it) } ?: emptyList()
        val sheetName = entries.keys.filter { it.startsWith("xl/worksheets/") }.sorted().firstOrNull()
            ?: throw IllegalStateException("لا توجد ورقة بيانات في الملف")
        return parseSheet(entries[sheetName]!!, shared)
    }

    private fun parseSharedStrings(data: ByteArray): List<String> {
        val out = ArrayList<String>()
        val p = Xml.newPullParser()
        p.setInput(ByteArrayInputStream(data), "UTF-8")
        var inSi = false
        var inT = false
        val cur = StringBuilder()
        var ev = p.eventType
        while (ev != XmlPullParser.END_DOCUMENT) {
            when (ev) {
                XmlPullParser.START_TAG -> when (p.name) {
                    "si" -> { inSi = true; cur.setLength(0) }
                    "t" -> inT = inSi
                }
                XmlPullParser.TEXT -> if (inT) cur.append(p.text)
                XmlPullParser.END_TAG -> when (p.name) {
                    "t" -> inT = false
                    "si" -> { inSi = false; out.add(cur.toString()) }
                }
            }
            ev = p.next()
        }
        return out
    }

    private fun colIndex(ref: String): Int {
        var n = 0
        for (ch in ref) {
            if (ch in 'A'..'Z') n = n * 26 + (ch - 'A' + 1) else break
        }
        return n - 1
    }

    private fun parseSheet(data: ByteArray, shared: List<String>): List<List<String>> {
        val rows = ArrayList<List<String>>()
        val p = Xml.newPullParser()
        p.setInput(ByteArrayInputStream(data), "UTF-8")
        var row: MutableMap<Int, String>? = null
        var cellCol = 0
        var cellType = ""
        var inV = false
        var inT = false
        val txt = StringBuilder()
        var ev = p.eventType
        while (ev != XmlPullParser.END_DOCUMENT) {
            when (ev) {
                XmlPullParser.START_TAG -> when (p.name) {
                    "row" -> row = HashMap()
                    "c" -> {
                        cellCol = colIndex(p.getAttributeValue(null, "r") ?: "A1")
                        cellType = p.getAttributeValue(null, "t") ?: ""
                        txt.setLength(0)
                    }
                    "v" -> { inV = true; txt.setLength(0) }
                    "t" -> { inT = true; if (cellType == "inlineStr") txt.setLength(0) }
                }
                XmlPullParser.TEXT -> if (inV || inT) txt.append(p.text)
                XmlPullParser.END_TAG -> when (p.name) {
                    "v" -> {
                        inV = false
                        val raw = txt.toString().trim()
                        val v = when (cellType) {
                            "s" -> raw.toIntOrNull()?.let { shared.getOrNull(it) } ?: ""
                            "n", "" -> plainNumber(raw)
                            else -> raw
                        }
                        row?.put(cellCol, v)
                    }
                    "t" -> {
                        inT = false
                        if (cellType == "inlineStr") row?.put(cellCol, txt.toString())
                    }
                    "row" -> {
                        val r = row
                        if (r != null) {
                            val max = (r.keys.maxOrNull() ?: -1) + 1
                            rows.add(List(max) { r[it] ?: "" })
                        }
                        row = null
                    }
                }
            }
            ev = p.next()
        }
        return rows
    }

    private fun plainNumber(raw: String): String {
        if (raw.isEmpty()) return ""
        return try {
            val bd = BigDecimal(raw)
            if (bd.stripTrailingZeros().scale() <= 0) bd.toBigInteger().toString() else raw
        } catch (e: Exception) { raw }
    }

    // ---------- CSV ----------

    private fun readCsv(bytes: ByteArray): List<List<String>> {
        var text = String(bytes, Charsets.UTF_8)
        if (text.contains('�')) text = String(bytes, charset("windows-1256"))
        text = text.removePrefix("﻿")
        val firstLine = text.lineSequence().firstOrNull { it.isNotBlank() } ?: return emptyList()
        val delim = listOf(',', ';', '\t').maxByOrNull { d -> firstLine.count { it == d } } ?: ','
        val rows = ArrayList<List<String>>()
        var cur = ArrayList<String>()
        val sb = StringBuilder()
        var inQ = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                inQ && c == '"' && i + 1 < text.length && text[i + 1] == '"' -> { sb.append('"'); i++ }
                c == '"' -> inQ = !inQ
                !inQ && c == delim -> { cur.add(sb.toString()); sb.setLength(0) }
                !inQ && (c == '\n' || c == '\r') -> {
                    if (c == '\r' && i + 1 < text.length && text[i + 1] == '\n') i++
                    cur.add(sb.toString()); sb.setLength(0)
                    if (cur.any { it.isNotBlank() }) rows.add(cur)
                    cur = ArrayList()
                }
                else -> sb.append(c)
            }
            i++
        }
        cur.add(sb.toString())
        if (cur.any { it.isNotBlank() }) rows.add(cur)
        return rows
    }

    // ---------- تعرّف الأعمدة ----------

    private fun norm(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFKD)
            .replace(Regex("[\\u064B-\\u065F\\u0670\\u0640]"), "")
            .replace(Regex("\\p{Mn}"), "")
            .lowercase()
            .replace(Regex("[\\s_\\-.:/()\\[\\]]"), "")
            .replace("أ", "ا").replace("إ", "ا").replace("آ", "ا").replace("ى", "ي").replace("ة", "ه")

    private data class Cols(
        val name: Int, val french: Int, val school: Int, val national: Int,
        val phone1: Int, val phone2: Int, val gender: Int
    )

    private fun detect(header: List<String>): Cols? {
        val h = header.map { norm(it) }
        fun find(pred: (String) -> Boolean, exclude: Set<Int> = emptySet()) =
            h.indices.firstOrNull { it !in exclude && pred(h[it]) } ?: -1
        val french = find({ it.contains("فرنسي") || it.contains("francais") || it.contains("french") || it.contains("nomfr") || it.contains("nomlatin") })
        val school = find({ it.contains("مدرسي") || it.contains("matricule") || it.contains("schoolid") || it.contains("رقمالتلميذ") || it.contains("massar") || it.contains("مسار") })
        val national = find({ it.contains("وطني") || it.contains("nni") || it.contains("national") || it.contains("هويه") || it.contains("تعريف") })
        val gender = find({ it.contains("الجنس") || it.contains("جنس") || it.contains("sexe") || it.contains("gender") })
        val name = find({
            (it.contains("اسم") || it.contains("nom") || it.contains("name") || it.contains("التلميذ") || it.contains("الطالب")) &&
                !it.contains("فرنسي") && !it.contains("francais") && !it.contains("french") && !it.contains("nomfr")
        }, setOf(french, school, national, gender))
        if (name < 0) return null
        val phones = h.indices.filter { h[it].contains("هاتف") || h[it].contains("tel") || h[it].contains("phone") || h[it].contains("جوال") || h[it].contains("واتس") }
        val p1 = phones.firstOrNull { h[it].contains("1") || h[it].contains("اول") } ?: phones.firstOrNull() ?: -1
        val p2 = phones.firstOrNull { it != p1 && (h[it].contains("2") || h[it].contains("ثاني")) } ?: phones.firstOrNull { it != p1 } ?: -1
        return Cols(name, french, school, national, p1, p2, gender)
    }

    private fun digits(s: String) = s.filter { it.isDigit() }

    private fun fromRows(rows: List<List<String>>): List<ImportedStudent> {
        if (rows.isEmpty()) throw IllegalStateException("الملف فارغ")
        var headerIdx = -1
        var cols: Cols? = null
        for (i in 0 until minOf(rows.size, 15)) {
            val c = detect(rows[i])
            if (c != null) { headerIdx = i; cols = c; break }
        }
        if (cols == null) throw IllegalStateException("لم أجد عمود «الاسم» في الملف. تأكد أن الصف الأول فيه عناوين الأعمدة (مثل: الاسم الكامل، الرقم الوطني).")
        val out = ArrayList<ImportedStudent>()
        for (i in headerIdx + 1 until rows.size) {
            val r = rows[i]
            fun cell(ix: Int) = if (ix >= 0 && ix < r.size) r[ix].trim() else ""
            val name = cell(cols.name).replace(Regex("\\s+"), " ")
            if (name.isBlank() || name.all { it.isDigit() }) continue
            out.add(
                ImportedStudent(
                    number = out.size + 1,
                    name = name,
                    frenchName = cell(cols.french),
                    schoolId = digits(cell(cols.school)),
                    nationalId = digits(cell(cols.national)),
                    phone1 = digits(cell(cols.phone1)),
                    phone2 = digits(cell(cols.phone2)),
                    gender = cell(cols.gender)
                )
            )
        }
        return out
    }
}
