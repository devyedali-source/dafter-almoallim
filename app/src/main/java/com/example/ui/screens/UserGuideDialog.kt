package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val GUIDE_PREFS = "teacher_settings_prefs"
private const val GUIDE_SEEN_KEY = "user_guide_seen_v1"

fun isUserGuideSeen(context: Context): Boolean =
    try { context.getSharedPreferences(GUIDE_PREFS, Context.MODE_PRIVATE).getBoolean(GUIDE_SEEN_KEY, false) } catch (e: Exception) { true }

fun markUserGuideSeen(context: Context) {
    try { context.getSharedPreferences(GUIDE_PREFS, Context.MODE_PRIVATE).edit().putBoolean(GUIDE_SEEN_KEY, true).apply() } catch (e: Exception) { }
}

private data class GuideItem(val title: String, val steps: List<String>)

private val guideItems = listOf(
    GuideItem(
        "🏫 كيف أنتقل إلى قسم آخر؟",
        listOf(
            "اضغط على الثلاث نقاط (☰) في أعلى الصفحة.",
            "اختر «الصفحة الرئيسية والأقسام».",
            "اختر السنة الدراسية ثم اضغط على القسم الذي تريده."
        )
    ),
    GuideItem(
        "👥 كيف أسجّل تلاميذ القسم؟",
        listOf(
            "من الشريط السفلي اختر «الطلاب».",
            "اضغط «+ طالب جديد» لتسجيل تلميذ واحدًا واحدًا.",
            "وإذا كانت عندك لائحة جاهزة في ملف، اضغط زر الملف 📄 في أعلى الصفحة واختر الملف لرفع كل التلاميذ مرة واحدة."
        )
    ),
    GuideItem(
        "📥 كيف أسحب لائحة أو دفترًا؟",
        listOf(
            "من صفحة «الطلاب» اضغط «سحب اللوائح».",
            "اختر اللائحة أو الدفتر الذي تريده، ثم احفظه أو أرسله بصيغة PDF."
        )
    ),
    GuideItem(
        "📚 كيف أتنقل بين صفحات القسم؟",
        listOf(
            "بعد دخول القسم يظهر شريط في أسفل الشاشة: الطلاب، النتائج، الكشوف، التخطيط السنوي، الجدول الزمني.",
            "اضغط على الصفحة التي تريدها."
        )
    ),
    GuideItem(
        "🔁 أين أجد هذا المرشد مرة أخرى؟",
        listOf(
            "اضغط الثلاث نقاط (☰) ثم «❓ كيف أستخدم التطبيق؟»."
        )
    )
)

/** مرشد بسيط لمن يجد صعوبة في استخدام التطبيق: أسئلة شائعة بخطوات مرقّمة. */
@Composable
fun UserGuideDialog(onDismiss: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("❓ كيف أستخدم التطبيق؟", fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    guideItems.forEach { item ->
                        OutlinedCard(Modifier.fillMaxWidth(), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                item.steps.forEachIndexed { i, st ->
                                    Text("${i + 1}. $st", fontSize = 14.sp, lineHeight = 21.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = onDismiss) { Text("فهمت") } }
        )
    }
}
