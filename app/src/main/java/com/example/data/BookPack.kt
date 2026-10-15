package com.example.data

import android.content.Context
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * صور صفحات الكتب مخزنة في حزمة منفصلة اسمها "books" (fast-follow):
 * تنزّلها Google Play تلقائيًا بعد تثبيت التطبيق، وتستأنف التنزيل إذا انقطع الإنترنت.
 * لا يمسّ هذا بيانات المعلمين أو الطلاب.
 */
object BookPack {
    const val PACK = "books"

    private fun manager(context: Context) =
        AssetPackManagerFactory.getInstance(context.applicationContext)

    /** مجلد صور الكتب إن كانت الحزمة منزّلة، وإلا null. */
    private fun root(context: Context): File? = try {
        manager(context).getPackLocation(PACK)?.assetsPath()
            ?.let { File(it, "books") }
            ?.takeIf { it.isDirectory }
    } catch (e: Exception) {
        null
    }

    /** يفتح صفحة كتاب، أو يعيد null إذا لم تصل الحزمة بعد. */
    fun open(context: Context, book: String, page: Int): InputStream? {
        val name = page.toString().padStart(3, '0') + ".webp"
        val file = root(context)?.let { File(it, "$book/$name") }
        if (file != null && file.isFile) {
            return try { FileInputStream(file) } catch (e: Exception) { null }
        }
        // احتياط: نسخ بُنيت والصور داخل assets مباشرة
        return try { context.assets.open("books/$book/$name") } catch (e: Exception) { null }
    }

    /** يطلب من Google Play تنزيل الحزمة إن لم تكن منزّلة بعد. */
    fun requestDownload(context: Context) {
        try {
            if (root(context) == null) manager(context).fetch(listOf(PACK))
        } catch (e: Exception) {
            // لا شيء: سيُعاد المحاولة لاحقًا
        }
    }
}
