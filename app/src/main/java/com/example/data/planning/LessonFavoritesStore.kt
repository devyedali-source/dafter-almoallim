package com.example.data.planning

import android.content.Context

/** العناوين المفضلة للمعلم: محلية على هذا الجهاز، لكل قسم. */
object LessonFavoritesStore {
    private const val PREFS = "lesson_favorites_v1"
    private fun key(classId: Long) = "class_$classId"

    fun load(context: Context, classId: Long): Set<String> = try {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getStringSet(key(classId), emptySet())?.toSet() ?: emptySet()
    } catch (e: Exception) { emptySet() }

    /** يقلب حالة العنوان ويرجع المجموعة الجديدة */
    fun toggle(context: Context, classId: Long, lessonId: String): Set<String> {
        val cur = load(context, classId).toMutableSet()
        if (!cur.add(lessonId)) cur.remove(lessonId)
        try { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putStringSet(key(classId), cur).apply() } catch (e: Exception) { }
        return cur
    }
}
