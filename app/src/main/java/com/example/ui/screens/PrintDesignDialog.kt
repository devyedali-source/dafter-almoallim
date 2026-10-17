package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PrintDesign

/** إعدادات تصميم رأسيات اللوائح وإطارات الكشوف وشهادات التفوق. */
@Composable
fun PrintDesignDialog(onDismiss: () -> Unit) {
    val context: Context = LocalContext.current
    var header by remember { mutableStateOf(PrintDesign.header) }
    var card by remember { mutableStateOf(PrintDesign.card) }
    var cert by remember { mutableStateOf(PrintDesign.cert) }

    @Composable
    fun Section(title: String, names: List<String>, selected: Int, firstIndex: Int, onPick: (Int) -> Unit) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 10.dp, bottom = 2.dp))
        names.forEachIndexed { i, n ->
            val idx = i + firstIndex
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onPick(idx) }.padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selected == idx, onClick = { onPick(idx) })
                Text(n, fontSize = 13.sp)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تصميم اللوائح والكشوف 🎨", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "التصميم يغيّر الزخرفة فقط، ولا يغيّر أي معلومة في الرأسية أو الجدول. «الوضع الأصلي» هو التلقائي ويمكن الرجوع إليه في أي وقت.",
                    fontSize = 12.sp
                )
                Section("رأسية اللوائح (تُطبَّق على كل اللوائح)", PrintDesign.headerNames, header, 0) {
                    header = it; PrintDesign.save(context, header = it)
                }
                Section("تصميم الكشوف (يُطبَّق على كل الكشوف)", PrintDesign.cardNames, card, 0) {
                    card = it; PrintDesign.save(context, card = it)
                }
                Section("تصميم شهادات التفوق والتكريم", PrintDesign.certNames, cert, 1) {
                    cert = it; PrintDesign.save(context, cert = it)
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("تم") } }
    )
}
