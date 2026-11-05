package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.example.R
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

private data class GuideStep(val text: String, val img: Int? = null)
private data class GuidePage(val question: String, val steps: List<GuideStep>)

private val guidePages = listOf(
    GuidePage("🏫 كيف أنتقل إلى قسم آخر؟", listOf(
        GuideStep("اضغط على الثلاث نقاط (☰) في أعلى الصفحة.", R.drawable.guide_menu),
        GuideStep("اختر «الصفحة الرئيسية والأقسام».", R.drawable.guide_d_home),
        GuideStep("اختر السنة الدراسية ثم اضغط على القسم الذي تريده.", R.drawable.guide_classes)
    )),
    GuidePage("👥 كيف أسجّل تلاميذ القسم؟", listOf(
        GuideStep("من الشريط السفلي اختر «الطلاب».", R.drawable.guide_bar),
        GuideStep("اضغط «+ طالب جديد» لتسجيل تلميذ واحدًا واحدًا.\nوإذا كانت عندك لائحة جاهزة في ملف، اضغط زر الملف 📄 في أعلى الصفحة لرفع كل التلاميذ مرة واحدة.", R.drawable.guide_reg)
    )),
    GuidePage("📥 كيف أسحب لائحة أو دفترًا؟", listOf(
        GuideStep("من صفحة «الطلاب» اضغط «سحب اللوائح»، ثم اختر اللائحة أو الدفتر الذي تريده.", R.drawable.guide_pull),
        GuideStep("احفظه أو أرسله بصيغة PDF.")
    )),
    GuidePage("📚 كيف أتنقل بين صفحات القسم؟", listOf(
        GuideStep("بعد دخول القسم يظهر شريط في أسفل الشاشة: الطلاب، النتائج، الكشوف، التخطيط السنوي، الجدول الزمني. اضغط على الصفحة التي تريدها.", R.drawable.guide_bar)
    )),
    GuidePage("🗓 رحلتي خلال السنة الدراسية (معلم أو مدير)", listOf(
        GuideStep("أنشئ أقسامك بعد تسجيل الدخول."),
        GuideStep("في بداية السنة: سجّل التلاميذ في صفحة «الطلاب»، واسحب منها الدفاتر الخاصة بالإدارة أو بالمعلم."),
        GuideStep("ثم ابدأ «التخطيط السنوي» لإعداد تحاضيرك."),
        GuideStep("عند امتحان الفصل الأول: سجّل النتائج، واسحب الكشوف واللوائح بالحجم الذي تختاره."),
        GuideStep("بعد ذلك فعّل الفصل الثاني، وتابع التخطيط السنوي حتى امتحان الفصل الثاني، ثم سجّل النتائج واسحب الكشوف واللوائح."),
        GuideStep("وبعده فعّل الفصل الثالث، وواصل التحضير من التخطيط السنوي حتى امتحان الفصل الثالث، ثم سجّل النتائج واسحب الكشوف واللوائح.")
    )),
    GuidePage("🎓 أنا طالب وأريد التحضير للمسابقات", listOf(
        GuideStep("لا تحتاج إلى إنشاء قسم. اضغط على الثلاث نقاط (☰).", R.drawable.guide_menu),
        GuideStep("اختر «دورة تحضير مسابقات المعلمين»، ثم فعّل الدورة وابدأ التحضير.", R.drawable.guide_d_course)
    )),
    GuidePage("🧑‍🏫 أنا مقدّم خدمة", listOf(
        GuideStep("يمكنك إنشاء قسم، والتسجيل في دورة التحضير للمسابقات معًا.")
    )),
    GuidePage("🤝 تبادلات المعلمين", listOf(
        GuideStep("هذه الخدمة خاصة بالمعلمين فقط، وليست للطلاب. اضغط على الثلاث نقاط (☰) ثم اختر «تبادلات المعلمين».", R.drawable.guide_d_exch)
    )),
    GuidePage("🔁 أين أجد هذا المرشد مرة أخرى؟", listOf(
        GuideStep("اضغط على الثلاث نقاط (☰).", R.drawable.guide_menu),
        GuideStep("اختر «❓ كيف أستخدم التطبيق؟».", R.drawable.guide_d_guide)
    ))
)

/** مرشد بسيط: قائمة أسئلة، وكل سؤال في صفحة مستقلة بخطوات وصور وأسهم. */
@Composable
fun UserGuideDialog(onDismiss: () -> Unit) {
    var page by remember { mutableStateOf<Int?>(null) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val current = page?.let { guidePages.getOrNull(it) }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(current?.question ?: "❓ كيف أستخدم التطبيق؟", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (current == null) {
                        Text("اختر السؤال الذي يشغلك:", fontSize = 14.sp)
                        guidePages.forEachIndexed { i, g ->
                            OutlinedButton(onClick = { page = i }, modifier = Modifier.fillMaxWidth()) {
                                Text(g.question, fontSize = 14.sp)
                            }
                        }
                    } else {
                        current.steps.forEachIndexed { i, st ->
                            Text("${i + 1}. ${st.text}", fontSize = 15.sp, lineHeight = 22.sp)
                            if (st.img != null) {
                                Image(
                                    painter = painterResource(st.img),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth().clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (current == null) Button(onClick = onDismiss) { Text("فهمت") }
                else Button(onClick = { page = null }) { Text("↩ الأسئلة") }
            },
            dismissButton = if (current != null) ({ TextButton(onClick = onDismiss) { Text("إغلاق") } }) else null
        )
    }
}
