package com.example.ui.utils

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * ترجمة تلقائية لنصوص الواجهة في الوضع الفرنسي: أي نص عربي مطابق لمفتاح في القاموس (أو لقالب فيه {0} {1})
 * يُعرض مترجماً، دون الحاجة إلى تغليف كل نص بـ uiT. في الوضع العربي يُعاد النص كما هو.
 */
object AutoTranslate {
    private val arabic = Regex("[\\u0600-\\u06FF]")
    private val cache = HashMap<String, String>()

    private class Pat(val regex: Regex, val template: String)

    private val patterns: List<Pat> by lazy {
        UiFrench.map.entries
            .filter { it.key.contains("{0}") && it.key.replace(Regex("\\{\\d+}"), "").trim().length >= 3 }
            .mapNotNull { (k, v) ->
                try {
                    val sb = StringBuilder("^")
                    var i = 0
                    val ph = Regex("\\{(\\d+)}")
                    var last = 0
                    for (m in ph.findAll(k)) {
                        sb.append(Regex.escape(k.substring(last, m.range.first)))
                        sb.append("(.+?)")
                        last = m.range.last + 1
                        i++
                    }
                    sb.append(Regex.escape(k.substring(last))).append("$")
                    Pat(Regex(sb.toString(), RegexOption.DOT_MATCHES_ALL), v)
                } catch (e: Exception) { null }
            }
            .sortedByDescending { it.regex.pattern.length }
    }

    @Synchronized
    fun tr(text: String): String {
        if (AppLocalization.lang != "fr") return text
        if (text.isEmpty() || !arabic.containsMatchIn(text)) return text
        cache[text]?.let { return it }
        var out = UiFrench.map[text]
        if (out == null) {
            for (p in patterns) {
                val m = p.regex.matchEntire(text) ?: continue
                var t = p.template
                for (gi in 1 until m.groupValues.size) t = t.replace("{" + (gi - 1) + "}", m.groupValues[gi])
                out = t
                break
            }
        }
        if (out == null) {
            // نص تحيط به رموز/إيموجي/نقطتان: نترجم لبّه ونُبقي الزوائد
            val m = Regex("^([^\\p{L}\\p{N}{]*)(.*?)([^\\p{L}\\p{N}}]*)$", RegexOption.DOT_MATCHES_ALL).matchEntire(text)
            if (m != null && m.groupValues[2].isNotEmpty() && (m.groupValues[1].isNotEmpty() || m.groupValues[3].isNotEmpty())) {
                val core = m.groupValues[2]
                val t = UiFrench.map[core]
                if (t != null) out = m.groupValues[1] + t + m.groupValues[3]
            }
        }
        val res = out ?: text
        if (cache.size > 4000) cache.clear()
        cache[text] = res
        return res
    }
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    androidx.compose.material3.Text(
        text = AutoTranslate.tr(text), modifier = modifier, color = color, fontSize = fontSize,
        fontStyle = fontStyle, fontWeight = fontWeight, fontFamily = fontFamily, letterSpacing = letterSpacing,
        textDecoration = textDecoration, textAlign = textAlign, lineHeight = lineHeight, overflow = overflow,
        softWrap = softWrap, maxLines = maxLines, minLines = minLines, onTextLayout = onTextLayout, style = style
    )
}

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    androidx.compose.material3.Text(
        text = text, modifier = modifier, color = color, fontSize = fontSize,
        fontStyle = fontStyle, fontWeight = fontWeight, fontFamily = fontFamily, letterSpacing = letterSpacing,
        textDecoration = textDecoration, textAlign = textAlign, lineHeight = lineHeight, overflow = overflow,
        softWrap = softWrap, maxLines = maxLines, minLines = minLines, onTextLayout = onTextLayout, style = style
    )
}
