package com.example.ui.utils

/** صفحة أناشيد ومحفوظات السنة الدراسية: نصوص فقط بتصميم خاص، بلا غلاف ولا رأسية. */
object HymnsPdf {
    // (اللون الأساسي، اللون الفاتح للصفوف)
    private val COLORS = listOf(
        "#1B5E20" to "#EEF6EE", "#0D47A1" to "#EEF3FA", "#880E4F" to "#FBEFF4",
        "#E65100" to "#FFF3E8", "#4A148C" to "#F4EFF9", "#006064" to "#E8F5F6"
    )

    private fun esc(t: String) = t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    /** عدد الحروف دون علامات التشكيل (لتقدير عرض الشطر). */
    private fun letters(t: String) = t.count { it.code !in 0x064B..0x0652 && it != 'ٰ' }

    fun html(level: Int): String {
        val css = "@page{size:A4 portrait;margin:12mm;}" +
            "*{box-sizing:border-box;}" +
            "body{font-family:'Amiri','Scheherazade New','Noto Naskh Arabic','Droid Naskh Shift Alt','Noto Sans Arabic','Arial',sans-serif;direction:rtl;margin:0;background:#fff;color:#1b1b1b;-webkit-print-color-adjust:exact;print-color-adjust:exact;}" +
            ".po{border:2.5px solid var(--c);border-radius:16px;margin:0 0 8mm;padding:0 0 3mm;page-break-inside:avoid;break-inside:avoid;overflow:hidden;box-shadow:inset 0 0 0 4px #fff,inset 0 0 0 5.5px var(--t);}" +
            ".ti{background:var(--c);color:#fff;text-align:center;font-weight:900;font-size:25px;line-height:1.9;padding:2px 12px;margin-bottom:2.5mm;border-bottom:4px double rgba(255,255,255,.55);}" +
            ".ln{display:flex;align-items:center;justify-content:center;font-weight:700;line-height:2.5;padding:0 6px;}" +
            ".ln:nth-child(even){background:var(--t);}" +
            ".ln .h{text-align:justify;text-align-last:justify;}" +
            ".ln .d{width:34px;text-align:center;color:var(--c);font-size:13px;}" +
            ".ln.one{display:block;text-align:center;}" +
            ".ln.rf{color:#B71C1C;font-weight:900;background:#FDECEC;}"
        val sb = StringBuilder()
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>أناشيد ومحفوظات السنة $level</title><style>$css</style></head><body>")
        HymnsData.poems(level).forEachIndexed { i, (title, body) ->
            val (c, t) = COLORS[i % COLORS.size]
            val rows = body.lines().map { it.trim() }.filter { it.isNotEmpty() }
            val maxChars = rows.flatMap { r -> r.removePrefix("~").split("|").map { letters(it.trim()) + it.trim().count { ch -> ch == ' ' } } }.maxOrNull() ?: 20
            val size = minOf(22.0, 600.0 / maxOf(maxChars, 1)).coerceAtLeast(15.0)
            val w = (maxChars * 0.5 * size).toInt()
            sb.append("<div class=\"po\" style=\"--c:$c;--t:$t;font-size:${"%.1f".format(java.util.Locale.US, size)}px\"><div class=\"ti\">✦ ${esc(title)} ✦</div>")
            rows.forEach { raw ->
                val refrain = raw.startsWith("~")
                val line = raw.removePrefix("~").trim()
                val cls = if (refrain) " rf" else ""
                if (line.contains("|")) {
                    val (a, b) = line.split("|", limit = 2).map { it.trim() }
                    sb.append("<div class=\"ln$cls\"><span class=\"h\" style=\"width:${w}px\">${esc(a)}</span><span class=\"d\">❖</span><span class=\"h\" style=\"width:${w}px\">${esc(b)}</span></div>")
                } else {
                    sb.append("<div class=\"ln one$cls\">${esc(line)}</div>")
                }
            }
            sb.append("</div>")
        }
        sb.append("</body></html>")
        return sb.toString()
    }
}
