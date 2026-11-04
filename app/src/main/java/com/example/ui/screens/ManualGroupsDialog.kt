package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.data.models.Student
import com.example.ui.utils.ManualGroups

private const val MAX_GROUPS = 12

/**
 * يسمح للمستخدم، إن أراد، بإنشاء مجموعات (المنشدين / الكناسة) وإضافة التلاميذ إليها بنفسه.
 * إذا تركه مغلقًا يبقى التقسيم تلقائيًا كما كان.
 */
@Composable
fun ManualGroupsDialog(
    type: String,
    title: String,
    classId: Long,
    students: List<Student>,
    onGenerate: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sorted = remember(students) { students.sortedBy { it.id } }
    val saved = remember { ManualGroups.load(context, type, classId) }
    var enabled by remember { mutableStateOf(saved?.enabled ?: false) }
    val names = remember { mutableStateListOf<String>() }
    val members = remember { mutableStateListOf<SnapshotStateList<Long>>() }
    var addingTo by remember { mutableStateOf<Int?>(null) }
    var movingId by remember { mutableStateOf<Long?>(null) }
    var movingFrom by remember { mutableStateOf(-1) }
    var cancelGroup by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        val valid = sorted.map { it.id }.toSet()
        val src = saved?.groups.orEmpty()
        if (src.isEmpty()) {
            names.add(ManualGroups.defaultName(type, 0)); members.add(mutableStateListOf())
            names.add(ManualGroups.defaultName(type, 1)); members.add(mutableStateListOf())
        } else {
            src.forEach { g ->
                names.add(g.name)
                members.add(mutableStateListOf<Long>().also { l -> l.addAll(g.ids.filter { it in valid }) })
            }
        }
    }

    fun persist() {
        ManualGroups.save(
            context, type, classId, enabled,
            names.indices.map { i -> ManualGroups.Group(names[i], members[i].toMutableList()) }
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("أريد التحكم في المجموعات بنفسي", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Switch(checked = enabled, onCheckedChange = { enabled = it })
                    }
                    if (!enabled) {
                        Text("المجموعات تلقائية: يوزّع التطبيق التلاميذ بنفسه.", fontSize = 13.sp)
                    } else {
                        Text("أنشئ مجموعاتك وأضف التلاميذ إلى كل مجموعة. التلميذ غير المضاف لا يظهر في اللائحة.", fontSize = 13.sp)
                        val assigned = members.flatMap { it.toList() }.toSet()
                        val left = sorted.count { it.id !in assigned }
                        Text("غير موزّعين: $left", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        names.indices.forEach { i ->
                            OutlinedCard(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        OutlinedTextField(
                                            value = names[i],
                                            onValueChange = { names[i] = it },
                                            label = { Text("اسم المجموعة") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(onClick = { cancelGroup = i }) { Text("إلغاء المجموعة") }
                                    }
                                    members[i].toList().forEach { id ->
                                        val idx = sorted.indexOfFirst { it.id == id }
                                        val st = sorted.getOrNull(idx)
                                        if (st != null) {
                                            Row(
                                                Modifier.fillMaxWidth().clickable { movingId = id; movingFrom = i },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("${idx + 1} - ${st.name}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                                                Text("↔ نقل", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                                TextButton(onClick = { members[i].remove(id) }) { Text("إخراج") }
                                            }
                                        }
                                    }
                                    Text("العدد: ${members[i].size}", fontSize = 12.sp)
                                    Button(onClick = { addingTo = i }, modifier = Modifier.fillMaxWidth()) { Text("➕ إضافة طلاب") }
                                }
                            }
                        }
                        if (names.size < MAX_GROUPS) {
                            OutlinedButton(onClick = {
                                names.add(ManualGroups.defaultName(type, names.size)); members.add(mutableStateListOf())
                            }, modifier = Modifier.fillMaxWidth()) { Text("➕ إضافة مجموعة") }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { persist(); onDismiss(); onGenerate() }) { Text("حفظ وسحب") }
            },
            dismissButton = { TextButton(onClick = { persist(); onDismiss() }) { Text("حفظ فقط") } }
        )

        val mv = movingId
        if (mv != null && movingFrom in members.indices) {
            val stName = sorted.firstOrNull { it.id == mv }?.name ?: ""
            AlertDialog(
                onDismissRequest = { movingId = null },
                title = { Text("نقل: $stName") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("اختر المجموعة التي تريد نقله إليها:", fontSize = 13.sp)
                        names.indices.filter { it != movingFrom }.forEach { j ->
                            Button(onClick = {
                                members[movingFrom].remove(mv); members[j].add(mv); movingId = null
                            }, modifier = Modifier.fillMaxWidth()) { Text(names[j].ifBlank { ManualGroups.defaultName(type, j) }) }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { movingId = null }) { Text("إلغاء") } }
            )
        }
        val cg = cancelGroup
        if (cg != null && cg in names.indices) {
            AlertDialog(
                onDismissRequest = { cancelGroup = null },
                title = { Text("إلغاء المجموعة؟") },
                text = { Text("تُلغى المجموعة فقط، ويبقى جميع تلاميذها في القسم دون مجموعة، ويمكنك إضافتهم لمجموعة أخرى.") },
                confirmButton = { TextButton(onClick = { names.removeAt(cg); members.removeAt(cg); cancelGroup = null }) { Text("نعم، ألغِ المجموعة") } },
                dismissButton = { TextButton(onClick = { cancelGroup = null }) { Text("رجوع") } }
            )
        }
        val target = addingTo
        if (target != null && target < members.size) {
            val picked = remember(target) { mutableStateListOf<Long>() }
            val taken = members.flatMap { it.toList() }.toSet()
            val free = sorted.filter { it.id !in taken }
            AlertDialog(
                onDismissRequest = { addingTo = null },
                title = { Text("اختر الطلاب (${names[target]})") },
                text = {
                    if (free.isEmpty()) Text("كل التلاميذ موزّعون على مجموعات.")
                    else LazyColumn(Modifier.heightIn(max = 380.dp)) {
                        items(free, key = { it.id }) { st ->
                            val no = sorted.indexOfFirst { it.id == st.id } + 1
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = st.id in picked,
                                    onCheckedChange = { c -> if (c) picked.add(st.id) else picked.remove(st.id) }
                                )
                                Text("$no - ${st.name}", fontSize = 14.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { members[target].addAll(picked); addingTo = null }) { Text("إضافة") }
                },
                dismissButton = { TextButton(onClick = { addingTo = null }) { Text("إلغاء") } }
            )
        }
    }
}
