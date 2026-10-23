package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.ClassSection
import com.example.data.models.Student
import com.example.ui.TeacherViewModel
import com.example.ui.utils.ImportedStudent
import com.example.ui.utils.StudentPdfImporter
import com.example.ui.utils.uiT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private class ImportRow(val s: ImportedStudent, checked: Boolean, gender: String, name: String, val duplicate: Boolean) {
    var checked by mutableStateOf(checked)
    var gender by mutableStateOf(gender)
    var name by mutableStateOf(name)
}

private fun normName(s: String) = s.replace(Regex("\\s+"), "").replace("أ", "ا").replace("إ", "ا").replace("آ", "ا")

/** استيراد لائحة التلاميذ من ملف PDF نصي (لائحة التطبيق الرسمية) مع مراجعة قبل الحفظ. */
@Composable
fun ImportStudentsDialog(
    viewModel: TeacherViewModel,
    section: ClassSection,
    existing: List<Student>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var rows by remember { mutableStateOf<List<ImportRow>>(emptyList()) }
    val existingNames = remember(existing) { existing.map { normName(it.name) }.toSet() }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        loading = true
        error = null
        scope.launch {
            try {
                val list = withContext(Dispatchers.IO) { StudentPdfImporter.read(context, uri) }
                if (list.isEmpty()) {
                    error = "لم أجد تلاميذ في هذا الملف. تأكد أنه لائحة التلاميذ التي يسحبها التطبيق (PDF نصي وليس صورة)."
                    rows = emptyList()
                } else {
                    rows = list.map {
                        val dup = normName(it.name) in existingNames
                        ImportRow(it, !dup, "ذكر", it.name, dup)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                error = "تعذّرت قراءة الملف: ${e.localizedMessage ?: "خطأ غير معروف"}"
                rows = emptyList()
            } finally {
                loading = false
            }
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.96f).fillMaxHeight(0.92f),
            shape = RoundedCornerShape(18.dp),
            color = Color.White
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(uiT("استيراد لائحة التلاميذ من PDF"), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(uiT("القسم: ${section.name}"), fontSize = 12.sp, color = Color.Gray)
                Button(
                    onClick = { picker.launch(arrayOf("application/pdf")) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) { Text(uiT(if (rows.isEmpty()) "اختيار ملف اللائحة (PDF)" else "اختيار ملف آخر")) }

                if (loading) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text(uiT("جاري قراءة الملف..."))
                    }
                }
                error?.let { Text(it, color = Color(0xFFB71C1C), fontSize = 13.sp) }

                if (rows.isNotEmpty()) {
                    val selected = rows.count { it.checked }
                    Text(
                        uiT("وُجد ${rows.size} تلميذاً — المحدد للحفظ: $selected. راجع الأسماء وعدّلها إن لزم، وحدّد الجنس لكل تلميذ."),
                        fontSize = 12.5.sp
                    )
                    if (rows.any { it.duplicate }) {
                        Text(uiT("التلاميذ المكرَّرون (موجودون في القسم) غير محدَّدين."), fontSize = 12.sp, color = Color(0xFFB26A00))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { rows.forEach { it.gender = "ذكر" } }) { Text(uiT("الكل ذكور"), fontSize = 12.sp) }
                        OutlinedButton(onClick = { rows.forEach { it.gender = "أنثى" } }) { Text(uiT("الكل إناث"), fontSize = 12.sp) }
                    }
                    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        itemsIndexed(rows) { _, r ->
                            Column(
                                Modifier.fillMaxWidth()
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                                    .padding(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = r.checked, onCheckedChange = { r.checked = it })
                                    Text("${r.s.number}", fontSize = 12.sp, modifier = Modifier.width(24.dp))
                                    OutlinedTextField(
                                        value = r.name,
                                        onValueChange = { r.name = it },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    FilledTonalButton(
                                        onClick = { r.gender = if (r.gender == "ذكر") "أنثى" else "ذكر" },
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) { Text(if (r.gender == "ذكر") "👦" else "👧") }
                                }
                                val info = listOfNotNull(
                                    r.s.schoolId.takeIf { it.isNotBlank() }?.let { "مدرسي: $it" },
                                    r.s.nationalId.takeIf { it.isNotBlank() }?.let { "وطني: $it" },
                                    r.s.phone1.takeIf { it.isNotBlank() }?.let { "هاتف: $it" },
                                    if (r.duplicate) "موجود في القسم" else null
                                ).joinToString(" • ")
                                if (info.isNotBlank()) Text(info, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(start = 12.dp))
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(uiT("إلغاء")) }
                    Button(
                        onClick = {
                            val chosen = rows.filter { it.checked && it.name.isNotBlank() }
                                .map { it.s.copy(name = it.name.trim()) to it.gender }
                            viewModel.addStudents(section.id, chosen) { n ->
                                Toast.makeText(context, "تمت إضافة $n تلميذاً إلى القسم ✅", Toast.LENGTH_LONG).show()
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = rows.any { it.checked }
                    ) { Text(uiT("حفظ")) }
                }
            }
        }
    }
}
