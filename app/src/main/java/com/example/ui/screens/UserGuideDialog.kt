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
private data class GuidePage(val question: String, val steps: List<GuideStep>, val section: String = "general")

private val reportsPages = listOf(
    GuidePage("⚙️ ماذا يفعل زر الإعدادات؟", listOf(
        GuideStep("في أعلى صفحة «الكشوف» اضغط زر الإعدادات ⚙️.", R.drawable.guide_k_settings),
        GuideStep("تظهر «إعدادات القسم والملاحظات»، وفيها: تعديل مواد القسم والدرجات القصوى، واختيار الدرجات من 10، وتعديل مجالات التقييم والملاحظات لكل فصل.")
    ), "reports"),
    GuidePage("🔏 ماذا يفعل زر الطابع؟", listOf(
        GuideStep("اضغط زر الطابع تحت زر الإعدادات.", R.drawable.guide_k_stamp),
        GuideStep("من نافذة «إنشاء وختم الطابع الرسمي» تنشئ الطابع والتوقيع، فيظهران على الكشوف واللوائح التي تسحبها.")
    ), "reports"),
    GuidePage("🎨 كيف أغيّر شكل الكشوف واللوائح؟", listOf(
        GuideStep("اضغط زر 🎨 تحت زر الطابع.", R.drawable.guide_k_design),
        GuideStep("اختر رأسية اللوائح وتصميم الكشوف وتصميم شهادات التفوق. التصميم يغيّر الزخرفة فقط ولا يغيّر أي معلومة.")
    ), "reports"),
    GuidePage("📅 كيف أختار الفصل؟", listOf(
        GuideStep("اضغط على خانة «الفصل الدراسي» واختر الفصل الأول أو الثاني أو الثالث.", R.drawable.guide_k_term),
        GuideStep("الكشوف التي تظهر تحتها تتغيّر حسب الفصل الذي اخترته.")
    ), "reports"),
    GuidePage("🖨️ ما أنواع الكشوف واللوائح؟", listOf(
        GuideStep("تحت عبارة «اختر التقرير أو الكشف المطلوب» ثلاثة أنواع:", R.drawable.guide_k_types),
        GuideStep("🖨️ كشوف: كشف لكل ورقة، أو كشفان في الورقة، أو ثلاثة كشوف في الورقة (توفيرًا للورق)."),
        GuideStep("📊 لائحة النتائج التفصيلية: لائحة واحدة بنتائج القسم كله."),
        GuideStep("👤 كشف فردي: تختار تلميذًا وتسحب كشفه وحده."),
        GuideStep("اضغط النوع الذي تريده فيجهّز التطبيق الملف لتحفظه أو ترسله بصيغة PDF.")
    ), "reports")
)

private val syncPages = listOf(
    GuidePage("📱 غيّرت هاتفي، كيف أجد بياناتي؟", listOf(
        GuideStep("ادخل بنفس البريد الإلكتروني (حساب Google) الذي سجّلت به أول مرة، فتعود إليك بياناتك.", R.drawable.guide_s_login)
    ), "sync")
)

private val homePages = listOf(
    GuidePage("🏫 كيف أنتقل بين الأقسام؟", listOf(
        GuideStep("في الصفحة الرئيسية اضغط على السنة الدراسية التي تعمل فيها.", R.drawable.guide_h_years),
        GuideStep("تظهر أقسام هذه السنة. اضغط على القسم الذي تريده لتدخل إليه.", R.drawable.guide_h_classes),
        GuideStep("وإن كنت داخل قسم وتريد الخروج إلى قسم آخر: اضغط الثلاث نقاط (☰) ثم «الصفحة الرئيسية والأقسام»، ثم اختر السنة، ثم القسم.", R.drawable.guide_d_home)
    ), "home"),
    GuidePage("➕ كيف أنشئ قسمًا؟", listOf(
        GuideStep("افتح السنة الدراسية، ثم اضغط زر «إنشاء قسم» في أسفل الصفحة.", R.drawable.guide_h_classes),
        GuideStep("اختر الولاية، ثم المقاطعة، ثم المستوى الدراسي من 1 إلى 6، ثم العام الدراسي.", R.drawable.guide_h_create),
        GuideStep("اضغط «إنشاء القسم»، فيظهر القسم في قائمة أقسام السنة.")
    ), "home"),
    GuidePage("🗂️ كيف أسحب دفاتر المدير؟", listOf(
        GuideStep("افتح السنة الدراسية، ثم اضغط زر «🗂️ وثائق المدير» في أعلى الصفحة (يظهر إذا كان عندك قسم واحد على الأقل).", R.drawable.guide_h_classes),
        GuideStep("اختر الدفتر الذي تريده: دفتر غياب المعلمين، أو دفتر أثاث القسم، أو دفتر محاضر اجتماع المعلمين، أو دفتر زيارات المدرسة.", R.drawable.guide_h_docs),
        GuideStep("تُسحب الدفاتر فارغة بغلاف جاهز، لتملأها يدويًا بعد الطباعة. أما دفتر غياب المعلمين فتكتب أسماء المعلمين قبل سحبه.")
    ), "home")
)

private val studentsPages = listOf(
    GuidePage("🧭 ما أزرار صفحة الطلاب؟", listOf(
        GuideStep("في الأعلى: «سحب اللوائح» لسحب اللوائح والدفاتر، وزر الملف 📄 لرفع كل التلاميذ من ملف، وزر البحث 🔍 للبحث عن تلميذ بالاسم.", R.drawable.guide_reg),
        GuideStep("في الأسفل: «+ طالب جديد» لتسجيل تلميذ واحدًا واحدًا."),
        GuideStep("بجانب كل تلميذ زر «عرض» لفتح بطاقته.")
    ), "students"),
    GuidePage("👥 كيف أسجّل تلاميذ القسم؟", listOf(
        GuideStep("إذا كان عندك ملف جاهز (Excel / PDF / CSV): اضغط زر الملف 📄 ثم «حمّل التلاميذ» واختر الملف، فيدخل كل التلاميذ مرة واحدة.", R.drawable.guide_reg),
        GuideStep("إذا لم يكن عندك ملف: اضغط «+ طالب جديد» وسجّل كل تلميذ بنفسك.")
    ), "students"),
    GuidePage("📥 ماذا أسحب من «سحب اللوائح»؟", listOf(
        GuideStep("اضغط «سحب اللوائح» فتظهر قائمة.", R.drawable.guide_pull),
        GuideStep("اللوائح والدفاتر: لائحة الطلاب، دفتر الغياب، دفتر المتابعة، المنشدين، الكناسة، سجل معلم الفرنسية."),
        GuideStep("في «المنشدين» و«الكناسة» يمكنك أن تنشئ المجموعات بنفسك، أو تتركها تلقائية.")
    ), "students")
)

private val resultsPages = listOf(
    GuidePage("📝 كيف أرصد نتيجة مادة للقسم كله؟", listOf(
        GuideStep("① اختر الفصل الدراسي. ② اختر «رصد نتيجة مادة للقسم». ③ اختر المادة. ④ اضغط «البدء في رصد الدرجات».", R.drawable.guide_p_res),
        GuideStep("اكتب درجة كل تلميذ في خانته، ثم اضغط «حفظ النتائج 💾»."),
        GuideStep("الدرجات يجب أن تكون بين 0 و20.")
    ), "results"),
    GuidePage("👤 كيف أرصد مواد لتلميذ واحد؟", listOf(
        GuideStep("اختر الفصل، ثم «رصد مواد لطالب واحد».", R.drawable.guide_p_res),
        GuideStep("اختر التلميذ، ثم اضغط «البدء في رصد الدرجات»، واكتب درجاته في كل المواد واحفظ.")
    ), "results"),
    GuidePage("📊 كيف أسحب الكشوف بعد الرصد؟", listOf(
        GuideStep("من الشريط السفلي اختر «الكشوف»، ثم اختر الفصل ونوع الكشف الذي تريده. (في صفحة الكشوف مرشد خاص بها.)")
    ), "results")
)

private val planningPages = listOf(
    GuidePage("📅 كيف أفتح فصلًا في التخطيط السنوي؟", listOf(
        GuideStep("تظهر الفصول الثلاثة، ويظهر عدد التحاضير في كل فصل. اضغط على الفصل لفتحه.", R.drawable.guide_p_terms),
        GuideStep("الفصل المكتوب عليه «مقفل» يحتاج إلى تفعيل قبل أن تستعمله.")
    ), "planning"),
    GuidePage("📖 كيف أستعمل أسابيع الفصل ودروسه؟", listOf(
        GuideStep("داخل الفصل ترى الأسابيع، وكل أسبوع فيه المواد. اضغط «عرض المجالات» ثم «عرض الدروس».", R.drawable.guide_p_week),
        GuideStep("☑ علامة «تم التدريس» تضعها بعد أن تدرّس الدرس."),
        GuideStep("📖 زر «فتح دفتر التحضير» يفتح ورقة تحضير هذا الدرس.")
    ), "planning"),
    GuidePage("📈 ما شريط تقدم البرنامج؟", listOf(
        GuideStep("يبيّن نسبة ما أنجزته من البرنامج، ويرتفع كلما علّمت أسبوعًا.", R.drawable.guide_p_prog),
        GuideStep("«دليل الألوان» يشرح معنى كل لون: دروس وتدريس، منجز، إدماج وتقويم وعلاج، استقبال وتشخيص.")
    ), "planning")
)

private val timetablePages = listOf(
    GuidePage("🗓 ماذا أفعل في الجدول الزمني؟", listOf(
        GuideStep("يعرض الجدول الرسمي لقسمك حسب اليوم والفترات: الصباحية الأولى، الصباحية الثانية، المسائية.", R.drawable.guide_p_tt),
        GuideStep("«مشاركة PDF» يسحب الجدول ملفًا لتحفظه أو ترسله.")
    ), "timetable"),
    GuidePage("🔀 كيف أبدّل مكان حصة؟", listOf(
        GuideStep("اضغط «تعديل جدولي»، ثم اختر اليوم والحصة التي تريد تبديل مكانها.", R.drawable.guide_p_tt),
        GuideStep("التعديل خاص بجدولك أنت فقط ولا يغيّر الجدول الرسمي."),
        GuideStep("اضغط «العودة للجدول الرسمي» لإلغاء تعديلاتك.")
    ), "timetable")
)

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
fun UserGuideDialog(onDismiss: () -> Unit, section: String = "general") {
    val pages = remember(section) {
        when (section) {
            "reports" -> reportsPages
            "students" -> studentsPages
            "home" -> homePages
            "sync" -> syncPages
            "results" -> resultsPages
            "planning" -> planningPages
            "timetable" -> timetablePages
            else -> guidePages
        }
    }
    val guideTitle = when (section) {
        "reports" -> "❓ مرشد صفحة الكشوف"
        "students" -> "❓ مرشد صفحة الطلاب"
        "home" -> "❓ مرشد الصفحة الرئيسية والأقسام"
        "sync" -> "❓ مرشد المزامنة وحفظ البيانات"
        "results" -> "❓ مرشد صفحة النتائج"
        "planning" -> "❓ مرشد التخطيط السنوي"
        "timetable" -> "❓ مرشد الجدول الزمني"
        else -> "❓ كيف أستخدم التطبيق؟"
    }
    var page by remember { mutableStateOf<Int?>(null) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val current = page?.let { pages.getOrNull(it) }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(current?.question ?: guideTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (current == null) {
                        Text("اختر السؤال الذي يشغلك:", fontSize = 14.sp)
                        pages.forEachIndexed { i, g ->
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
