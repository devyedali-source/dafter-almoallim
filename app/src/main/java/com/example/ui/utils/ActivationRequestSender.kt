package com.example.ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import com.google.firebase.firestore.DocumentReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * يرسل طلب التفعيل إلى الخادم ولا يُكمل (فتح الواتساب) إلا بعد أن يؤكد الخادم الاستلام،
 * حتى لا يصل المدير رابط لطلب غير موجود. إذا لم يصل الطلب خلال مهلة قصيرة يُلغى ولا يبقى معلّقًا.
 */
object ActivationRequestSender {
    private const val TIMEOUT_MS = 12000L
    const val MSG_NO_NET = "لا يوجد اتصال بالإنترنت. لم يُرسل الطلب، أعد المحاولة عند توفر الشبكة."
    const val MSG_WEAK_NET = "الإنترنت ضعيف، لم يصل الطلب إلى الخادم. أعد المحاولة عند توفر الشبكة."

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val caps = cm.getNetworkCapabilities(cm.activeNetwork)
            caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            true
        }
    }

    /** يجب استدعاؤها من الخيط الرئيسي. */
    fun send(
        context: Context,
        docRef: DocumentReference,
        data: Map<String, Any?>,
        onSending: (Boolean) -> Unit,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        if (!isOnline(context)) {
            onFail(MSG_NO_NET)
            return
        }
        onSending(true)
        val done = AtomicBoolean(false)
        val handler = Handler(Looper.getMainLooper())
        val timeout = Runnable {
            if (done.compareAndSet(false, true)) {
                // نلغي الطلب المعلّق حتى لا يصل لاحقًا دون علم المستخدم
                try { docRef.delete() } catch (e: Exception) { }
                onSending(false)
                onFail(MSG_WEAK_NET)
            }
        }
        handler.postDelayed(timeout, TIMEOUT_MS)
        try {
            docRef.set(data).addOnCompleteListener { task ->
                if (done.compareAndSet(false, true)) {
                    handler.removeCallbacks(timeout)
                    onSending(false)
                    if (task.isSuccessful) onSuccess() else onFail(MSG_WEAK_NET)
                }
            }
        } catch (e: Exception) {
            if (done.compareAndSet(false, true)) {
                handler.removeCallbacks(timeout)
                onSending(false)
                onFail(MSG_WEAK_NET)
            }
        }
    }
}
