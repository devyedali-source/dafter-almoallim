package com.example.data.calendar

import android.content.SharedPreferences
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

/**
 * التاريخ الهجري حسب موريتانيا، محليًا ودون إنترنت.
 *
 * الأساس تقويم أم القرى (المدمج في أندرويد). ولأن لجنة الأهلة في موريتانيا قد تبدأ الشهر قبل يوم
 * أو بعد يوم من أم القرى، يحفظ المستخدم لكل شهر «إزاحة» خاصة به (−2 .. +2 يوم) تخصّ ذلك الشهر وحده.
 * بداية الشهر الموريتاني = بداية الشهر في أم القرى + إزاحة ذلك الشهر؛ والشهر التالي يبدأ من حسابه هو،
 * فلا تنتقل إزاحة شهر إلى الشهر الذي بعده.
 */
object HijriMauritania {

    private const val KEY_SHIFTS = "hijri_month_shifts_v3"
    private const val KEY_OLD_OFFSET = "hijri_offset_days_v2"
    const val MAX_SHIFT = 2L
    private const val MAX_MONTH_DAYS = 30

    data class HDate(val year: Int, val month: Int, val day: Int)

    private fun key(year: Int, month: Int) = "$year-$month"

    private fun baseStart(year: Int, month: Int): LocalDate =
        LocalDate.from(HijrahDate.of(year, month, 1))

    private fun next(year: Int, month: Int) = if (month == 12) (year + 1) to 1 else year to (month + 1)
    private fun prev(year: Int, month: Int) = if (month == 1) (year - 1) to 12 else year to (month - 1)

    fun load(prefs: SharedPreferences): Map<String, Int> {
        val result = HashMap<String, Int>()
        try {
            val raw = prefs.getString(KEY_SHIFTS, "") ?: ""
            for (part in raw.split(";")) {
                val pieces = part.split(":")
                if (pieces.size != 2) continue
                val shift = pieces[1].toIntOrNull() ?: continue
                if (shift < -MAX_SHIFT || shift > MAX_SHIFT) continue
                result[pieces[0]] = shift
            }
            // ترحيل الإزاحة القديمة الثابتة (بعدد أيام) إلى إزاحة الشهر الحالي فقط
            val old = prefs.getLong(KEY_OLD_OFFSET, 0L)
            if (old != 0L) {
                try {
                    val today = LocalDate.now()
                    val adjusted = HijrahDate.from(today.plusDays(old))
                    val y = adjusted.get(ChronoField.YEAR)
                    val m = adjusted.get(ChronoField.MONTH_OF_YEAR)
                    val d = adjusted.get(ChronoField.DAY_OF_MONTH)
                    val shift = shiftFor(today, y, m, d)
                    if (shift != null && !result.containsKey(key(y, m))) {
                        result[key(y, m)] = shift
                    }
                } catch (e: Exception) {
                    // نتجاهل الإزاحة القديمة إن لم تُحوَّل
                }
                prefs.edit().remove(KEY_OLD_OFFSET).apply()
                save(prefs, result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun save(prefs: SharedPreferences, shifts: Map<String, Int>) {
        val text = shifts.entries
            .filter { it.value != 0 }
            .joinToString(";") { it.key + ":" + it.value }
        prefs.edit().putString(KEY_SHIFTS, text).apply()
    }

    private fun clampDate(d: LocalDate, lo: LocalDate, hi: LocalDate): LocalDate =
        if (d.isBefore(lo)) lo else if (d.isAfter(hi)) hi else d

    /**
     * يعيد التاريخ الهجري الموريتاني ليوم ميلادي معيّن.
     * الأشهر التي ضبطها المستخدم تبقى كما ضبطها؛ أما الأشهر المجاورة التي لم يضبطها فتُعدَّل بيومٍ إن لزم
     * ليبقى طول كل شهر 29 أو 30 يومًا (فلا يظهر يوم 31 ولا يتكرر يوم).
     */
    fun resolve(date: LocalDate, shifts: Map<String, Int>): HDate {
        val b = HijrahDate.from(date)
        val y = b.get(ChronoField.YEAR)
        val m = b.get(ChronoField.MONTH_OF_YEAR)

        // نافذة من ثمانية أشهر: ثلاثة قبل شهر اليوم، وشهر اليوم، وأربعة بعده
        var cursor = y to m
        repeat(3) { cursor = prev(cursor.first, cursor.second) }
        val months = ArrayList<Pair<Int, Int>>()
        repeat(8) {
            months.add(cursor)
            cursor = next(cursor.first, cursor.second)
        }
        val explicit = months.map { shifts.containsKey(key(it.first, it.second)) }
        val starts = months.map { (yy, mm) ->
            baseStart(yy, mm).plusDays((shifts[key(yy, mm)] ?: 0).toLong())
        }.toMutableList()
        val last = months.size - 1
        for (i in last - 1 downTo 0) {
            if (!explicit[i]) starts[i] = clampDate(starts[i], starts[i + 1].minusDays(30), starts[i + 1].minusDays(29))
        }
        for (i in 1..last) {
            if (!explicit[i]) starts[i] = clampDate(starts[i], starts[i - 1].plusDays(29), starts[i - 1].plusDays(30))
        }

        var chosen = 0
        for (i in months.indices) {
            if (!starts[i].isAfter(date)) chosen = i
        }
        var (cy, cm) = months[chosen]
        var day = ChronoUnit.DAYS.between(starts[chosen], date).toInt() + 1
        if (day > MAX_MONTH_DAYS) {
            val nx = next(cy, cm)
            cy = nx.first
            cm = nx.second
            day -= MAX_MONTH_DAYS
        }
        return HDate(cy, cm, day)
    }

    /**
     * الإزاحة اللازمة ليكون يوم [today] هو اليوم [day] من الشهر الهجري [month] سنة [year].
     * تعيد null إذا كان الفرق عن أم القرى أكبر من يومين.
     */
    fun shiftFor(today: LocalDate, year: Int, month: Int, day: Int): Int? {
        val desiredStart = today.minusDays((day - 1).toLong())
        val diff = ChronoUnit.DAYS.between(baseStart(year, month), desiredStart)
        if (diff < -MAX_SHIFT || diff > MAX_SHIFT) return null
        return diff.toInt()
    }

    fun keyOf(year: Int, month: Int): String = key(year, month)
}
