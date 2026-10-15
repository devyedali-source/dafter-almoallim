package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ClassSection
import com.example.data.models.Student
import com.example.data.planning.LessonPreparationRepository
import com.example.ui.TeacherViewModel
import com.example.ui.theme.*

private val CmpGreen = Color(0xFF2E9E4F)
private val CmpRed = Color(0xFFD32F2F)
private val CmpLine = Color(0xFFB0B0C0)

/** صفحة تقييم الكفايات: دفتر متابعة القسم + جدول دعم المجموعات (قابلة للمشاركة PDF) */
@Composable
fun CompetencyAssessmentScreen(
    viewModel: TeacherViewModel,
    section: ClassSection,
    students: List<Student>,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    BackHandler { onClose() }

    val catalog = remember(section.level) {
        try { LessonPreparationRepository.getCompetencyCatalog(ctx, section.level) } catch (e: Exception) { emptyList() }
    }
    val all by viewModel.competencies.collectAsState()
    val sorted = remember(students) { students.sortedBy { it.id } }

    var discIdx by remember { mutableStateOf(0) }
    var chunkIdx by remember { mutableStateOf(0) }
    var tab by remember { mutableStateOf(0) } // 0 = دفتر المتابعة ، 1 = جدول الدعم

    val classEntries = remember(all, section.id) { all.filter { it.classId == section.id } }
    val marks = remember(classEntries) {
        classEntries.filter { it.studentId > 0 }.associate { "${it.studentId}|${it.entryKey}" to it.value }
    }
    val notes = remember(classEntries) {
        classEntries.filter { it.studentId == 0L && it.note.isNotBlank() }.associate { it.entryKey to it.note }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = BentoBg) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع") }
                Text("تقييم الكفايات — ${section.name}", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f))
                val disc = catalog.getOrNull(discIdx)
                Button(
                    onClick = {
                        if (disc != null) {
                            val html = com.example.ui.HtmlReportHelper.generateCompetencyHtml(
                                ctx, section, students, disc, marks, notes, viewModel
                            )
                            com.example.ui.HtmlReportHelper.shareHtmlAsPdf(
                                ctx, html, "تقييم_الكفايات_${disc.label}.pdf", isLandscape = true
                            )
                        }
                    },
                    enabled = disc != null && students.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                ) { Text("مشاركة PDF") }
            }

            if (catalog.isEmpty()) {
                Text("لا توجد كفايات متاحة لهذا المستوى.", color = BentoPrimaryDesc, modifier = Modifier.padding(16.dp))
                return@Column
            }
            if (students.isEmpty()) {
                Text("أضف تلاميذ إلى القسم أولاً.", color = BentoPrimaryDesc, modifier = Modifier.padding(16.dp))
                return@Column
            }

            val disc = catalog[discIdx.coerceIn(0, catalog.lastIndex)]
            val chunks = disc.spheres.chunked(3)

            Text("المادة", fontSize = 12.sp, color = BentoPrimaryDesc)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                catalog.forEachIndexed { i, d ->
                    FilterChip(selected = i == discIdx, onClick = { discIdx = i; chunkIdx = 0 }, label = { Text(d.label) })
                }
            }
            if (chunks.size > 1) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    chunks.forEachIndexed { i, c ->
                        FilterChip(
                            selected = i == chunkIdx.coerceIn(0, chunks.lastIndex),
                            onClick = { chunkIdx = i },
                            label = { Text(c.joinToString(" · ") { it.label }, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
            }
            val chunk = chunks.getOrNull(chunkIdx.coerceIn(0, (chunks.size - 1).coerceAtLeast(0))).orEmpty()

            Spacer(Modifier.height(4.dp))
            TabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("دفتر المتابعة") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("جدول الدعم") })
            }
            Spacer(Modifier.height(6.dp))

            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                if (tab == 0) {
                    Text("اضغط على الخانة: + (امتلك) ← − (لم يمتلك) ← فارغة", fontSize = 11.sp, color = BentoPrimaryDesc)
                    Spacer(Modifier.height(4.dp))
                    Column(Modifier.horizontalScroll(rememberScrollState())) {
                        // رأس المجالات
                        Row {
                            CmpCell("الرقم", 36.dp, bold = true)
                            CmpCell("اسم التلميذ", 130.dp, bold = true)
                            chunk.forEach { sp -> CmpCell(sp.label, 132.dp, bold = true, bg = BentoLightLavender) }
                        }
                        Row {
                            CmpCell("", 36.dp)
                            CmpCell("", 130.dp)
                            chunk.forEach { _ -> for (k in 1..3) CmpCell("ك$k", 44.dp, bold = true) }
                        }
                        sorted.forEachIndexed { i, st ->
                            Row {
                                CmpCell("${i + 1}", 36.dp)
                                CmpCell(st.name, 130.dp, align = TextAlign.Start)
                                chunk.forEach { sp ->
                                    for (k in 1..3) {
                                        val key = "${disc.code}|${sp.code}|$k"
                                        val v = marks["${st.id}|$key"] ?: 0
                                        val has = sp.cbs.containsKey(k)
                                        Box(
                                            Modifier.width(44.dp).height(36.dp)
                                                .border(0.5.dp, CmpLine)
                                                .background(if (has) Color.White else Color(0xFFEDEDED))
                                                .clickable(enabled = has) {
                                                    val next = when (v) { 0 -> 1; 1 -> -1; else -> 0 }
                                                    viewModel.saveCompetencyEntry(section.id, st.id, key, next)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            when {
                                                v > 0 -> Text("+", color = CmpGreen, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                                v < 0 -> Text("−", color = CmpRed, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                                !has -> Text("/", color = Color.Gray, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    chunk.forEach { sp ->
                        sp.cbs.forEach { (k, t) ->
                            Text("${sp.label} — الكفاية $k: $t", fontSize = 11.sp, color = BentoPrimaryDesc, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                } else {
                    Text("المجموعات تُحسب تلقائياً من الخانات المعلّمة بـ (−) وتظهر بأرقام التلاميذ.", fontSize = 11.sp, color = BentoPrimaryDesc)
                    Spacer(Modifier.height(6.dp))
                    for (k in 1..3) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Text("المجموعة التي لم تمتلك الكفاية $k", fontWeight = FontWeight.Bold)
                                chunk.forEach { sp ->
                                    val nums = sorted.mapIndexedNotNull { i, st ->
                                        if ((marks["${st.id}|${disc.code}|${sp.code}|$k"] ?: 0) < 0) (i + 1).toString() else null
                                    }
                                    Text(
                                        "${sp.label}: " + (if (nums.isEmpty()) "—" else nums.joinToString(" - ")),
                                        fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp)
                                    )
                                }
                                val noteKey = "SUP|${disc.code}|$k"
                                var text by remember(noteKey, notes[noteKey]) { mutableStateOf(notes[noteKey].orEmpty()) }
                                OutlinedTextField(
                                    value = text,
                                    onValueChange = {
                                        text = it
                                        viewModel.saveCompetencyEntry(section.id, 0L, noteKey, 0, it)
                                    },
                                    label = { Text("الدعم المدرسي") },
                                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CmpCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    bold: Boolean = false,
    bg: Color = Color.White,
    align: TextAlign = TextAlign.Center
) {
    Box(
        Modifier.width(width).height(36.dp).border(0.5.dp, CmpLine).background(bg).padding(horizontal = 3.dp),
        contentAlignment = if (align == TextAlign.Start) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            text, fontSize = 11.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            textAlign = align, maxLines = 2, overflow = TextOverflow.Ellipsis
        )
    }
}
