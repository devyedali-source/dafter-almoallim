package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.ClassSection
import com.example.ui.CoverDesign
import com.example.ui.CoverInfo
import com.example.ui.utils.uiT
import java.io.ByteArrayOutputStream

/** يقرأ الصورة المختارة ويصغّرها (حتى 800 بكسل) ويحوّلها إلى JPEG بصيغة Base64. */
private fun loadPhotoB64(ctx: Context, uri: Uri): String? = try {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    var s = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / s > 800) s *= 2
    val bmp = ctx.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = s })
    }
    if (bmp == null) null else {
        val bo = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, bo)
        Base64.encodeToString(bo.toByteArray(), Base64.NO_WRAP)
    }
} catch (e: Exception) {
    null
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun CoverPreview(section: ClassSection, title: String, info: CoverInfo, design: Int) {
    val context = LocalContext.current
    val html = remember(section, title, info, design) { CoverDesign.previewHtml(context, section, title, info, design) }
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val heightDp = maxWidth * (CoverDesign.H_MM.toFloat() / CoverDesign.W_MM.toFloat())
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(heightDp),
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(android.graphics.Color.WHITE)
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.javaScriptEnabled = false
                    setOnTouchListener { _, _ -> true }
                    loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                }
            },
            update = { it.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null) }
        )
    }
}

/**
 * واجهة الغلاف: الخطوة 1 معلومات الغلاف (الاسم، الدليل المالي، الهاتف، صورة اختيارية)،
 * الخطوة 2 اختيار تصميم الغلاف. المدرسة والقسم والسنة تؤخذ من إعدادات القسم.
 * onResult يستلم شريحة HTML الخاصة بالغلاف لتُدرج في أول الدفتر.
 */
@Composable
fun CoverDialog(
    section: ClassSection,
    bookTitle: String,
    onDismiss: () -> Unit,
    onResult: (String) -> Unit
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(0) }
    var teacher by remember { mutableStateOf(CoverDesign.lastInfo.teacher) }
    var finance by remember { mutableStateOf(CoverDesign.lastInfo.finance) }
    var phone by remember { mutableStateOf(CoverDesign.lastInfo.phone) }
    var photo by remember { mutableStateOf(CoverDesign.lastInfo.photoB64) }
    var design by remember { mutableStateOf(0) }
    var photoError by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val b = loadPhotoB64(context, uri)
            photoError = b == null
            if (b != null) photo = b
        }
    }
    val info = CoverInfo(teacher, finance, phone, photo)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.96f).fillMaxHeight(0.94f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text(uiT("غلاف الدفتر") + " — " + bookTitle, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(6.dp))
                if (step == 0) {
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                        Text(
                            uiT("المدرسة والقسم والسنة الدراسية تؤخذ تلقائيًا من إعدادات القسم. اترك أي خانة فارغة إن أردت كتابتها بالقلم."),
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = teacher, onValueChange = { teacher = it },
                            label = { Text(uiT("اسم المعلم(ة)")) }, singleLine = true, modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = finance, onValueChange = { finance = it },
                            label = { Text(uiT("الدليل المالي")) }, singleLine = true, modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = phone, onValueChange = { phone = it },
                            label = { Text(uiT("رقم الهاتف")) }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { picker.launch("image/*") }) {
                                Text(uiT(if (photo == null) "إضافة صورة (اختياري)" else "تغيير الصورة"))
                            }
                            if (photo != null) {
                                TextButton(onClick = { photo = null }) { Text(uiT("إزالة الصورة")) }
                                Text("✓", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }
                        if (photoError) Text(uiT("تعذّر قراءة الصورة."), color = Color(0xFFC62828), fontSize = 12.sp)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = onDismiss) { Text(uiT("إلغاء")) }
                        Button(onClick = { step = 1 }) { Text(uiT("التالي: اختيار التصميم")) }
                    }
                } else {
                    Text(uiT("اضغط على الغلاف الذي يعجبك."), fontSize = 12.sp)
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CoverDesign.names.forEachIndexed { i, n ->
                            val selected = design == i
                            val shape = RoundedCornerShape(12.dp)
                            Column(
                                Modifier.fillMaxWidth().clip(shape)
                                    .border(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else Color(0xFFBBBBBB), shape)
                                    .clickable { design = i }.padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = selected, onClick = { design = i })
                                    Text(uiT(n), fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                                }
                                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(Color.White)) {
                                    CoverPreview(section, bookTitle, info, i)
                                    Box(Modifier.matchParentSize().clickable { design = i })
                                }
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { step = 0 }) { Text(uiT("رجوع")) }
                        Button(onClick = {
                            CoverDesign.lastInfo = info
                            onResult(CoverDesign.body(context, section, bookTitle, info, design))
                        }) { Text(uiT("اعتماد الغلاف وسحب الدفتر")) }
                    }
                }
            }
        }
    }
}
