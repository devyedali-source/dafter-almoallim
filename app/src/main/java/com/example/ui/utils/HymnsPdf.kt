package com.example.ui.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

/** أناشيد ومحفوظات كل سنة دراسية (ملف PDF جاهز داخل التطبيق) للسحب والمشاركة. */
object HymnsPdf {
    fun share(context: Context, level: Int) {
        try {
            val lv = level.coerceIn(1, 6)
            val out = File(context.cacheDir, "أناشيد_ومحفوظات_السنة_$lv.pdf")
            context.assets.open("hymns/hymns_level_$lv.pdf").use { i -> out.outputStream().use { o -> i.copyTo(o) } }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", out)
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(send, "مشاركة الملف عبر الواتساب والتطبيقات 📤")
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذّر فتح ملف الأناشيد: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
