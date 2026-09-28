package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.example.data.models.ClassSection
import com.example.ui.TeacherViewModel
import com.example.ui.theme.*

val StampInkBlue = Color(0xFF153E90)
val StampInkBlueLight = Color(0xFFEBF2FF)

/**
 * Authentic Blue Official Stamp Icon evoking an official blue ink stamp seal on paper.
 */
@Composable
fun PaperStampSettingsIcon(
    modifier: Modifier = Modifier,
    tint: Color = StampInkBlue
) {
    Box(
        modifier = modifier.size(28.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().rotate(-6f)) {
            val w = size.width
            val h = size.height

            // 1. Outer circle (thick official ink stamp border)
            drawCircle(
                color = tint,
                radius = w * 0.44f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.08f)
            )

            // 2. Inner concentric circle (fine stamp ring)
            drawCircle(
                color = tint.copy(alpha = 0.9f),
                radius = w * 0.33f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.035f)
            )

            // 3. Center 5-pointed official star
            val starCenter = center
            val outerR = w * 0.12f
            val innerR = w * 0.05f
            val starPath = androidx.compose.ui.graphics.Path()
            for (i in 0 until 10) {
                val angle = Math.toRadians((i * 36 - 90).toDouble())
                val r = if (i % 2 == 0) outerR else innerR
                val x = (starCenter.x + r * Math.cos(angle)).toFloat()
                val y = (starCenter.y + r * Math.sin(angle)).toFloat()
                if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
            }
            starPath.close()
            drawPath(starPath, color = tint)

            // 4. Top and bottom official seal text simulation bars
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(w * 0.27f, h * 0.27f),
                end = androidx.compose.ui.geometry.Offset(w * 0.73f, h * 0.27f),
                strokeWidth = w * 0.04f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(w * 0.27f, h * 0.73f),
                end = androidx.compose.ui.geometry.Offset(w * 0.73f, h * 0.73f),
                strokeWidth = w * 0.04f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // 5. Left & right official stamp dots
            drawCircle(
                color = tint,
                radius = w * 0.035f,
                center = androidx.compose.ui.geometry.Offset(w * 0.18f, h * 0.5f)
            )
            drawCircle(
                color = tint,
                radius = w * 0.035f,
                center = androidx.compose.ui.geometry.Offset(w * 0.82f, h * 0.5f)
            )
        }
    }
}

/**
 * High-fidelity Jetpack Compose stamp preview matching the authentic blue ink stamp exactly
 * as rendered on grade slips and report sheets.
 */
@Composable
fun OfficialStampVisual(
    roleTitle: String,
    personName: String,
    schoolName: String,
    shape: String = "rectangle",
    modifier: Modifier = Modifier,
    scaleSize: Dp = 220.dp,
    financialId: String = ""
) {
    // The on-screen stamp is rendered from the exact same drawing used on paper
    // and in PDF reports (HtmlReportHelper.generateStampSvg), so the preview and
    // the printed stamp can never differ.
    val isCircle = shape.equals("circle", ignoreCase = true) ||
        roleTitle.trim() in listOf("مدير", "المدير")

    val targetWidth = if (scaleSize.isSpecified && scaleSize > 0.dp) scaleSize else 220.dp
    val targetHeight = if (isCircle) targetWidth else targetWidth / 1.55f

    val html = remember(roleTitle, personName, schoolName, shape, financialId) {
        val svg = com.example.ui.HtmlReportHelper.generateStampSvg(
            roleTitle = roleTitle,
            personName = personName,
            schoolName = schoolName,
            shape = shape,
            sizePx = 200,
            financialId = financialId
        )
        """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
        <style>
          html, body { margin:0; padding:0; background:transparent; overflow:hidden; width:100%; height:100%; }
          body { display:flex; align-items:center; justify-content:center; }
          svg { width:100% !important; height:100% !important; display:block; }
        </style>
        </head>
        <body>$svg</body>
        </html>
        """.trimIndent()
    }

    Box(
        modifier = modifier
            .width(targetWidth)
            .height(targetHeight)
            .rotate(if (isCircle) 1.5f else -2f),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.ui.viewinterop.AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                object : android.webkit.WebView(ctx) {
                    override fun onTouchEvent(event: android.view.MotionEvent): Boolean = false
                    override fun performClick(): Boolean = false
                }.apply {
                    settings.javaScriptEnabled = false
                    settings.useWideViewPort = false
                    settings.loadWithOverviewMode = false
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    isFocusable = false
                    isFocusableInTouchMode = false
                    overScrollMode = android.view.View.OVER_SCROLL_NEVER
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    webViewClient = android.webkit.WebViewClient()
                }
            },
            update = { webView ->
                if (webView.tag != html) {
                    webView.tag = html
                    webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
                }
            }
        )
    }
}

/**
 * Dedicated Section for configuring official stamps (Teacher and Principal)
 * Supports collapsing the form upon save until the user clicks Edit.
 */
@Composable
fun OfficialStampSettingsSection(
    viewModel: TeacherViewModel,
    activeClass: ClassSection,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedStampTab by remember { mutableIntStateOf(0) } // 0: Teacher Stamp, 1: Principal Stamp

    // Teacher Stamp States from ViewModel
    val teacherName by viewModel.teacherStampName.collectAsState()
    val teacherRole by viewModel.teacherStampRole.collectAsState()
    val teacherShowInReports by viewModel.showTeacherStampInReports.collectAsState()

    // Editing mode state (collapsed when already saved)
    var isEditingTeacher by remember(teacherName) { mutableStateOf(teacherName.isBlank()) }

    var inputTeacherName by remember(teacherName) { mutableStateOf(teacherName) }
    var inputTeacherRole by remember(teacherRole) {
        mutableStateOf(
            when (teacherRole.trim()) {
                "معلم", "المعلم" -> "المعلم"
                "مدير", "المدير" -> "المدير"
                else -> if (teacherRole.isBlank()) "المعلم" else teacherRole
            }
        )
    }
    var inputTeacherShowInReports by remember(teacherShowInReports) { mutableStateOf(teacherShowInReports) }

    // Principal Stamp States from ViewModel
    val principalName by viewModel.principalStampName.collectAsState()
    val principalRole by viewModel.principalStampRole.collectAsState()
    val principalFinancialId by viewModel.principalStampFinancialId.collectAsState()
    val principalShowInReports by viewModel.showPrincipalStampInReports.collectAsState()

    // Editing mode state for Principal
    var isEditingPrincipal by remember(principalName) { mutableStateOf(principalName.isBlank()) }

    var inputPrincipalName by remember(principalName) { mutableStateOf(principalName) }
    var inputPrincipalRole by remember(principalRole) {
        mutableStateOf(
            when (principalRole.trim()) {
                "معلم", "المعلم" -> "المعلم"
                "مدير", "المدير" -> "المدير"
                else -> if (principalRole.isBlank()) "المدير" else principalRole
            }
        )
    }
    var inputPrincipalShowInReports by remember(principalShowInReports) { mutableStateOf(principalShowInReports) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, StampInkBlue.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(StampInkBlueLight)
                        .border(1.dp, StampInkBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    PaperStampSettingsIcon(
                        modifier = Modifier.size(24.dp),
                        tint = StampInkBlue
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "إنشاء وتخصيص ختم وطابع المدرسة 🖋️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = StampInkBlue
                    )
                    Text(
                        text = "طابع رسمي للجمهورية الإسلامية الموريتانية مع اسم مدرسة القسم (${activeClass.schoolName.ifBlank { "الطلحايه 1" }}).",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoPrimaryDesc
                    )
                }
            }

            // Tab Selector: ختم المعلم / ختم المدير
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Teacher Tab
                val isTeacherTab = selectedStampTab == 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isTeacherTab) StampInkBlue else Color.Transparent)
                        .clickable { selectedStampTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "👨‍🏫 ختم المعلم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isTeacherTab) Color.White else Color.DarkGray
                        )
                        if (teacherName.isNotBlank()) {
                            Text(text = "✔", fontSize = 11.sp, color = if (isTeacherTab) Color(0xFF81C784) else Color(0xFF2E7D32))
                        }
                    }
                }

                // Principal Tab
                val isPrincipalTab = selectedStampTab == 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPrincipalTab) StampInkBlue else Color.Transparent)
                        .clickable { selectedStampTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "👔 ختم المدير",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isPrincipalTab) Color.White else Color.DarkGray
                        )
                        if (principalName.isNotBlank()) {
                            Text(text = "✔", fontSize = 11.sp, color = if (isPrincipalTab) Color(0xFF81C784) else Color(0xFF2E7D32))
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0))

            if (selectedStampTab == 0) {
                // ==================== TEACHER STAMP ====================
                if (!isEditingTeacher && teacherName.isNotBlank()) {
                    // --- SAVED / CONFIRMED VIEW (Fields hidden) ---
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Success Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, Color(0xFF81C784), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "طابع المعلم مسجل ومعتمد بنجاح ✔️",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                val displayRole = if (teacherRole in listOf("معلم", "المعلم")) "المعلم" else "المدير"
                                Text(
                                    text = "صاحب الطابع: $teacherName ($displayRole) - شكل مستطيل",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        // Prominent Visual Stamp Display
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "صورة الطابع الرسمية المعتمدة في كشوف الدرجات:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = StampInkBlue,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            OfficialStampVisual(
                                roleTitle = teacherRole,
                                personName = teacherName,
                                schoolName = activeClass.schoolName,
                                shape = "rectangle"
                            )
                        }

                        // Toggle visibility in reports
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (teacherShowInReports) StampInkBlueLight else Color(0xFFF8FAFC))
                                .border(1.dp, if (teacherShowInReports) StampInkBlue.copy(alpha = 0.4f) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable { viewModel.setTeacherStampShowInReports(!teacherShowInReports) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = teacherShowInReports,
                                onCheckedChange = { viewModel.setTeacherStampShowInReports(it) },
                                colors = CheckboxDefaults.colors(checkedColor = StampInkBlue)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "إظهار الطابع في كشوف الدرجات والنتائج التفصيلية ✔️",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (teacherShowInReports) StampInkBlue else Color.DarkGray
                                )
                                Text(
                                    text = if (teacherShowInReports) "الطابع مفعل ويظهر تلقائياً أسفل توقيع المعلم في الكشوف." else "الطابع مخفي حالياً من الكشوف المطبوعة.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Actions: Edit and Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    inputTeacherName = teacherName
                                    inputTeacherRole = if (teacherRole in listOf("معلم", "المعلم")) "المعلم" else "المدير"
                                    inputTeacherShowInReports = teacherShowInReports
                                    isEditingTeacher = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StampInkBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تعديل بيانات الطابع ✏️", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    inputTeacherName = ""
                                    inputTeacherShowInReports = false
                                    viewModel.updateTeacherStamp(
                                        name = "",
                                        shape = "rectangle",
                                        role = "المعلم",
                                        showInReports = false
                                    )
                                    isEditingTeacher = true
                                    Toast.makeText(context, "تم مسح بيانات طابع المعلم.", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Red)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مسح الطابع", fontSize = 11.5.sp, color = Color.Red)
                            }
                        }
                    }
                } else {
                    // --- EDIT / INPUT FORM (Visible when editing or empty) ---
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Role selection: المعلم / المدير
                        Text("الصفة في الطابع:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoText)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("المعلم" to "المعلم 👨‍🏫", "المدير" to "المدير 👔").forEach { (roleKey, label) ->
                                val isSel = inputTeacherRole == roleKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) StampInkBlue.copy(alpha = 0.15f) else Color(0xFFF8FAFC))
                                        .border(1.5.dp, if (isSel) StampInkBlue else Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                        .clickable { inputTeacherRole = roleKey }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.5.sp,
                                        color = if (isSel) StampInkBlue else Color.DarkGray
                                    )
                                }
                            }
                        }

                        // Name Field
                        OutlinedTextField(
                            value = inputTeacherName,
                            onValueChange = { inputTeacherName = it },
                            label = { Text("اسم صاحب الطابع (${inputTeacherRole})") },
                            placeholder = { Text("مثال: سيدى سالم") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = StampInkBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Live Visual Preview
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "معاينة حية لطابع المعلم (كما يظهر على كشوف الدرجات):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OfficialStampVisual(
                                roleTitle = inputTeacherRole,
                                personName = inputTeacherName,
                                schoolName = activeClass.schoolName,
                                shape = "rectangle"
                            )
                        }

                        // Checkbox for reports
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (inputTeacherShowInReports) StampInkBlueLight else Color(0xFFF8FAFC))
                                .border(1.dp, if (inputTeacherShowInReports) StampInkBlue.copy(alpha = 0.4f) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable { inputTeacherShowInReports = !inputTeacherShowInReports }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = inputTeacherShowInReports,
                                onCheckedChange = { inputTeacherShowInReports = it },
                                colors = CheckboxDefaults.colors(checkedColor = StampInkBlue)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "إظهار الطابع في كشوف الدرجات والنتائج التفصيلية ✔️",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (inputTeacherShowInReports) StampInkBlue else Color.DarkGray
                                )
                                Text(
                                    text = "يظهر أسفل توقيع المعلم في كافة الكشوف المطبوعة والنتائج التفصيلية.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (inputTeacherName.trim().isBlank()) {
                                        Toast.makeText(context, "يرجى كتابة اسم صاحب الطابع", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.updateTeacherStamp(
                                            name = inputTeacherName.trim(),
                                            shape = "rectangle",
                                            role = inputTeacherRole,
                                            showInReports = inputTeacherShowInReports
                                        )
                                        isEditingTeacher = false
                                        Toast.makeText(context, "تم تسجيل واعتماد ختم المعلم بنجاح! 💮", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StampInkBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("حفظ واعتماد الطابع 💾", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                            }

                            if (teacherName.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        inputTeacherName = teacherName
                                        inputTeacherRole = if (teacherRole in listOf("معلم", "المعلم")) "المعلم" else "المدير"
                                        inputTeacherShowInReports = teacherShowInReports
                                        isEditingTeacher = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(0.8f)
                                ) {
                                    Text("إلغاء", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // ==================== PRINCIPAL STAMP ====================
                if (!isEditingPrincipal && principalName.isNotBlank()) {
                    // --- SAVED / CONFIRMED VIEW (Fields hidden) ---
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Success Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, Color(0xFF81C784), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "طابع المدير مسجل ومعتمد بنجاح ✔️",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                val displayRole = if (principalRole in listOf("مدير", "المدير")) "المدير" else "المعلم"
                                Text(
                                    text = "صاحب الطابع: $principalName ($displayRole) - شكل دائرى",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        // Prominent Visual Stamp Display
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "صورة الطابع الرسمية المعتمدة في كشوف الدرجات:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = StampInkBlue,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            OfficialStampVisual(
                                roleTitle = principalRole,
                                personName = principalName,
                                schoolName = activeClass.schoolName,
                                shape = "circle",
                                financialId = principalFinancialId
                            )
                        }

                        // Toggle visibility in reports
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (principalShowInReports) StampInkBlueLight else Color(0xFFF8FAFC))
                                .border(1.dp, if (principalShowInReports) StampInkBlue.copy(alpha = 0.4f) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable { viewModel.setPrincipalStampShowInReports(!principalShowInReports) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = principalShowInReports,
                                onCheckedChange = { viewModel.setPrincipalStampShowInReports(it) },
                                colors = CheckboxDefaults.colors(checkedColor = StampInkBlue)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "إظهار الطابع في كشوف الدرجات والنتائج التفصيلية ✔️",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (principalShowInReports) StampInkBlue else Color.DarkGray
                                )
                                Text(
                                    text = if (principalShowInReports) "الطابع مفعل ويظهر تلقائياً أسفل توقيع المدير في الكشوف." else "الطابع مخفي حالياً من الكشوف المطبوعة.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Actions: Edit and Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    inputPrincipalName = principalName
                                    inputPrincipalRole = if (principalRole in listOf("مدير", "المدير")) "المدير" else "المعلم"
                                    inputPrincipalShowInReports = principalShowInReports
                                    isEditingPrincipal = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StampInkBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تعديل بيانات الطابع ✏️", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    inputPrincipalName = ""
                                    inputPrincipalShowInReports = false
                                    viewModel.updatePrincipalStamp(
                                        name = "",
                                        shape = "rectangle",
                                        role = "المدير",
                                        showInReports = false
                                    )
                                    isEditingPrincipal = true
                                    Toast.makeText(context, "تم مسح بيانات طابع المدير.", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Red)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مسح الطابع", fontSize = 11.5.sp, color = Color.Red)
                            }
                        }
                    }
                } else {
                    // --- EDIT / INPUT FORM FOR PRINCIPAL ---
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Role selection: المدير / المعلم
                        Text("الصفة في الطابع:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoText)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("المدير" to "المدير 👔", "المعلم" to "المعلم 👨‍🏫").forEach { (roleKey, label) ->
                                val isSel = inputPrincipalRole == roleKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) StampInkBlue.copy(alpha = 0.15f) else Color(0xFFF8FAFC))
                                        .border(1.5.dp, if (isSel) StampInkBlue else Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                        .clickable { inputPrincipalRole = roleKey }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.5.sp,
                                        color = if (isSel) StampInkBlue else Color.DarkGray
                                    )
                                }
                            }
                        }

                        // Name Field
                        OutlinedTextField(
                            value = inputPrincipalName,
                            onValueChange = { inputPrincipalName = it },
                            label = { Text("اسم صاحب الطابع (${inputPrincipalRole})") },
                            placeholder = { Text("مثال: عبد الله أحمد") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = StampInkBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Live Visual Preview
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "معاينة حية لطابع المدير (كما يظهر على كشوف الدرجات):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OfficialStampVisual(
                                roleTitle = inputPrincipalRole,
                                personName = inputPrincipalName,
                                schoolName = activeClass.schoolName,
                                shape = "circle",
                                financialId = principalFinancialId
                            )
                        }

                        // Checkbox for reports
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (inputPrincipalShowInReports) StampInkBlueLight else Color(0xFFF8FAFC))
                                .border(1.dp, if (inputPrincipalShowInReports) StampInkBlue.copy(alpha = 0.4f) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable { inputPrincipalShowInReports = !inputPrincipalShowInReports }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = inputPrincipalShowInReports,
                                onCheckedChange = { inputPrincipalShowInReports = it },
                                colors = CheckboxDefaults.colors(checkedColor = StampInkBlue)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "إظهار الطابع في كشوف الدرجات والنتائج التفصيلية ✔️",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (inputPrincipalShowInReports) StampInkBlue else Color.DarkGray
                                )
                                Text(
                                    text = "يظهر أسفل توقيع المدير في كافة الكشوف المطبوعة والنتائج التفصيلية.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (inputPrincipalName.trim().isBlank()) {
                                        Toast.makeText(context, "يرجى كتابة اسم صاحب الطابع", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.updatePrincipalStamp(
                                            name = inputPrincipalName.trim(),
                                            shape = "circle",
                                            role = inputPrincipalRole,
                                            showInReports = inputPrincipalShowInReports,
                                            financialId = principalFinancialId
                                        )
                                        isEditingPrincipal = false
                                        Toast.makeText(context, "تم تسجيل واعتماد ختم المدير بنجاح! 💮", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StampInkBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("حفظ واعتماد الطابع 💾", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                            }

                            if (principalName.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        inputPrincipalName = principalName
                                        inputPrincipalRole = if (principalRole in listOf("مدير", "المدير")) "المدير" else "المعلم"
                                        inputPrincipalShowInReports = principalShowInReports
                                        isEditingPrincipal = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(0.8f)
                                ) {
                                    Text("إلغاء", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
