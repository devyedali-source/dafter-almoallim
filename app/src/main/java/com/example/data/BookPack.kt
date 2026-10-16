package com.example.data

import android.content.Context
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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

    /**
     * نسبة تنزيل حزمة الكتب (0..100) أثناء التنزيل، أو null إذا لا يوجد تنزيل جارٍ
     * (اكتمل، أو فشل، أو التطبيق غير مثبّت من Google Play فلا حزمة تُنزَّل).
     */
    fun downloadProgress(context: Context): Flow<Int?> = callbackFlow {
        val mgr = manager(context)
        fun percentOf(st: AssetPackState?): Int? {
            if (st == null) return null
            return when (st.status()) {
                AssetPackStatus.COMPLETED,
                AssetPackStatus.FAILED,
                AssetPackStatus.CANCELED -> null
                else -> {
                    val total = st.totalBytesToDownload()
                    if (total > 0) ((st.bytesDownloaded() * 100L) / total).toInt().coerceIn(0, 100) else 0
                }
            }
        }
        if (root(context) != null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = object : AssetPackStateUpdateListener {
            override fun onStateUpdate(state: AssetPackState) {
                if (state.name() == PACK) trySend(percentOf(state))
            }
        }
        try {
            mgr.registerListener(listener)
            requestDownload(context)
            mgr.getPackStates(listOf(PACK))
                .addOnSuccessListener { trySend(percentOf(it.packStates()[PACK])) }
                .addOnFailureListener { trySend(null) }
        } catch (e: Exception) {
            trySend(null)
        }
        awaitClose {
            try { mgr.unregisterListener(listener) } catch (e: Exception) { }
        }
    }.distinctUntilChanged()
}
