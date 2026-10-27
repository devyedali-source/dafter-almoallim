package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TeacherViewModel
import com.example.ui.utils.ClassListPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** زر «رفع لائحة PDF» لمدير الأقسام: يقرأ لائحة صدّرها التطبيق وينشئ القسم بتلاميذه بعد المراجعة. */
@Composable
fun ClassListImportButton(viewModel: TeacherViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var parsed by remember { mutableStateOf<ClassListPayload.Imported?>(null) }
    var busy by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        busy = true
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(context.applicationContext)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        com.tom_roush.pdfbox.pdmodel.PDDocument.load(input).use { doc ->
                            val text = com.tom_roush.pdfbox.text.PDFTextStripper().getText(doc)
                            ClassListPayload.parse(text)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            busy = false
            if (result == null || result.students.isEmpty()) {
                Toast.makeText(
                    context,
                    "⚠️ لا توجد بيانات قسم داخل هذا الملف. أعد تصدير اللائحة من النسخة الجديدة للتطبيق ثم ارفعها.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                parsed = result
            }
        }
    }

    ExtendedFloatingActionButton(
        onClick = { if (!busy) picker.launch(arrayOf("application/pdf")) },
        containerColor = Color(0xFF2E7D32),
        contentColor = Color.White,
        modifier = modifier,
        text = { Text(if (busy) "جاري قراءة الملف..." else "📄 رفع لائحة PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
        icon = {}
    )

    val classes by viewModel.classSections.collectAsState()
    var chosenId by remember { mutableStateOf<Long?>(null) }

    parsed?.let { data ->
        AlertDialog(
            onDismissRequest = { parsed = null },
            title = { Text("اختر القسم الذي تُضاف إليه اللائحة", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("في الملف ${data.students.size} تلميذًا (${data.schoolName} - ${getLevelArabicName(data.level)} ${data.sectionName})", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        data.students.take(4).joinToString("\n") { "• ${it.name}" } + if (data.students.size > 4) "\n..." else "",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                    if (classes.isEmpty()) {
                        Text("لا توجد أقسام عندك. أنشئ القسم أولًا ثم أعد الرفع.", color = Color(0xFFD32F2F))
                    }
                    classes.sortedBy { it.name }.forEach { c ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { chosenId = c.id }
                        ) {
                            RadioButton(selected = chosenId == c.id, onClick = { chosenId = c.id })
                            Text(c.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("الأسماء الموجودة في القسم أصلًا لا تُسجَّل مرتين.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(
                    enabled = chosenId != null,
                    onClick = {
                        val target = chosenId ?: return@TextButton
                        viewModel.importStudentsToClass(target, data.students) { added, skipped ->
                            Toast.makeText(context, "🎉 أُضيف $added تلميذًا" + if (skipped > 0) " وتُجووز $skipped مكرَّرًا" else "", Toast.LENGTH_LONG).show()
                        }
                        parsed = null
                        chosenId = null
                    }
                ) { Text("إضافة إلى القسم", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { parsed = null }) { Text("إلغاء") } }
        )
    }
}
