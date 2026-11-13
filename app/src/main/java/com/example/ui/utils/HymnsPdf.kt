package com.example.ui.utils

/** صفحة أناشيد ومحفوظات السنة الدراسية: نصوص فقط بتصميم خاص، بلا غلاف ولا رأسية. */
object HymnsPdf {
    private val COLORS = listOf("#1565C0", "#2E7D32", "#C2185B", "#EF6C00", "#6A1B9A", "#00838F")

    private fun esc(t: String) = t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    fun html(level: Int): String {
        val css = "@page{size:A4 portrait;margin:12mm;}" +
            "*{box-sizing:border-box;}" +
            "body{font-family:'Amiri','Scheherazade New','Noto Naskh Arabic','Droid Naskh Shift Alt','Noto Sans Arabic','Arial',sans-serif;direction:rtl;margin:0;background:#fff;color:#1b1b1b;-webkit-print-color-adjust:exact;print-color-adjust:exact;}" +
            ".po{border:2px solid var(--c);border-radius:14px;margin:0 0 7mm;padding:0 0 3mm;page-break-inside:avoid;break-inside:avoid;overflow:hidden;}" +
            ".ti{background:var(--c);color:#fff;text-align:center;font-weight:900;font-size:23px;padding:4px 10px;margin-bottom:3mm;}" +
            ".ln{display:grid;grid-template-columns:1fr 14px 1fr;align-items:center;padding:0 8px;font-size:21px;font-weight:700;line-height:2.7;border-bottom:1px solid #e3e3e3;}" +
            ".ln .h{text-align:right;padding-right:12px;}" +
            ".ln .d{width:14px;text-align:center;color:var(--c);font-size:11px;}" +
            ".ln.one{display:block;text-align:center;}" +
            ".ln.rf{color:#C62828;font-weight:900;}"
        val sb = StringBuilder()
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>أناشيد ومحفوظات السنة $level</title><style>$css</style></head><body>")
        HymnsData.poems(level).forEachIndexed { i, (title, body) ->
            sb.append("<div class=\"po\" style=\"--c:${COLORS[i % COLORS.size]}\"><div class=\"ti\">${esc(title)}</div>")
            body.lines().map { it.trim() }.filter { it.isNotEmpty() }.forEach { raw ->
                val refrain = raw.startsWith("~")
                val line = raw.removePrefix("~").trim()
                val cls = if (refrain) " rf" else ""
                if (line.contains("|")) {
                    val (a, b) = line.split("|", limit = 2).map { it.trim() }
                    sb.append("<div class=\"ln$cls\"><span class=\"h\">${esc(a)}</span><span class=\"d\">❖</span><span class=\"h\">${esc(b)}</span></div>")
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
