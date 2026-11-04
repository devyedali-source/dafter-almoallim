package com.example.data.security

import com.google.firebase.auth.FirebaseAuth

/**
 * منع لقطات الشاشة وتسجيلها (FLAG_SECURE) يبقى مفعّلًا للجميع،
 * ويُستثنى منه حساب مدير الأقسام وحده.
 */
object ScreenSecurity {
    private const val CAPTURE_ALLOWED_EMAIL = "elyedalimoctar@gmail.com"

    /** هل المستخدم الحالي هو مدير الأقسام المسموح له بالتقاط الشاشة وتسجيلها؟ */
    fun isCaptureAllowedForCurrentUser(): Boolean {
        return try {
            val email = FirebaseAuth.getInstance().currentUser?.email?.trim()?.lowercase()
            email == CAPTURE_ALLOWED_EMAIL
        } catch (e: Exception) {
            false
        }
    }

    // عدّاد: عدة شاشات قد تطلب المنع معًا (التخطيط السنوي + شاشة الدرس). المنع لا يُرفع إلا عند خروج آخرها.
    private var holders = 0

    fun acquire(activity: android.app.Activity?) {
        synchronized(this) {
            holders++
            activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun release(activity: android.app.Activity?) {
        synchronized(this) {
            if (holders > 0) holders--
            if (holders == 0) activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
