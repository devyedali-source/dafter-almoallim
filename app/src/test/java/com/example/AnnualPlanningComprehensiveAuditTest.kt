package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.ClassSection
import com.example.data.planning.AnnualPlanningProgressManager
import com.example.data.planning.AnnualPlanningRepository
import com.example.data.planning.LessonProposalRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AnnualPlanningComprehensiveAuditTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Application>()
    }

    @Test
    fun testLevel1_FullPath_Subjects_Weeks_Proposals() {
        val level = 1
        val lessons = AnnualPlanningRepository.getLessonsForLevel(level)
        assertTrue("Level 1 must contain lessons", lessons.isNotEmpty())

        // Check coverage of all 38 weeks
        val weeks = AnnualPlanningRepository.getWeeksForLevel(level)
        assertEquals(38, weeks.size)

        for (w in 1..38) {
            val subjects = AnnualPlanningRepository.getSubjectsForWeek(level, w)
            assertTrue("Week $w in Level 1 must have subjects", subjects.isNotEmpty())
            assertFalse("Level 1 must NOT have Français", subjects.contains("Français"))

            for (sub in subjects) {
                val subLessons = AnnualPlanningRepository.getLessonsForWeekAndSubject(level, w, sub)
                assertTrue("Lessons for Level $level Week $w Subject $sub must not be empty", subLessons.isNotEmpty())
                for (lesson in subLessons) {
                    assertEquals(level, lesson.level)
                    assertTrue(w in lesson.week..lesson.weekEnd)

                    if (LessonProposalRepository.isActualLesson(lesson)) {
                        val proposal = LessonProposalRepository.getProposalForLesson(lesson, w)
                        assertNotNull(proposal)
                        assertEquals(lesson.subject, proposal.subject)
                        assertTrue(proposal.procedureSteps.isNotEmpty())
                        assertTrue(proposal.durationText.isNotBlank())
                    }
                }
            }
        }
    }

    @Test
    fun testLevel2_FullPath_Subjects_Weeks_Proposals() {
        val level = 2
        val lessons = AnnualPlanningRepository.getLessonsForLevel(level)
        assertTrue("Level 2 must contain lessons", lessons.isNotEmpty())

        val weeks = AnnualPlanningRepository.getWeeksForLevel(level)
        assertEquals(38, weeks.size)

        for (w in 1..38) {
            val subjects = AnnualPlanningRepository.getSubjectsForWeek(level, w)
            assertTrue("Week $w in Level 2 must have subjects", subjects.isNotEmpty())
            assertFalse("Level 2 must NOT have Français", subjects.contains("Français"))

            for (sub in subjects) {
                val subLessons = AnnualPlanningRepository.getLessonsForWeekAndSubject(level, w, sub)
                assertTrue("Lessons for Level $level Week $w Subject $sub must not be empty", subLessons.isNotEmpty())
                for (lesson in subLessons) {
                    assertEquals(level, lesson.level)
                    assertTrue(w in lesson.week..lesson.weekEnd)

                    if (LessonProposalRepository.isActualLesson(lesson)) {
                        val proposal = LessonProposalRepository.getProposalForLesson(lesson, w)
                        assertNotNull(proposal)
                        assertEquals(lesson.subject, proposal.subject)
                        assertTrue(proposal.procedureSteps.isNotEmpty())
                    }
                }
            }
        }
    }

    @Test
    fun testLevel3_FullPath_Subjects_Weeks_Francais_Proposals() {
        val level = 3
        val lessons = AnnualPlanningRepository.getLessonsForLevel(level)
        assertTrue("Level 3 must contain lessons", lessons.isNotEmpty())

        val weeks = AnnualPlanningRepository.getWeeksForLevel(level)
        assertEquals(38, weeks.size)

        var foundFrancais = false
        for (w in 1..38) {
            val subjects = AnnualPlanningRepository.getSubjectsForWeek(level, w)
            assertTrue("Week $w in Level 3 must have subjects", subjects.isNotEmpty())

            if (subjects.contains("Français")) {
                foundFrancais = true
            }

            for (sub in subjects) {
                val subLessons = AnnualPlanningRepository.getLessonsForWeekAndSubject(level, w, sub)
                assertTrue("Lessons for Level $level Week $w Subject $sub must not be empty", subLessons.isNotEmpty())
                for (lesson in subLessons) {
                    assertEquals(level, lesson.level)
                    assertTrue(w in lesson.week..lesson.weekEnd)

                    if (LessonProposalRepository.isActualLesson(lesson)) {
                        val proposal = LessonProposalRepository.getProposalForLesson(lesson, w)
                        assertNotNull(proposal)
                        assertEquals(lesson.subject, proposal.subject)
                        assertTrue(proposal.procedureSteps.isNotEmpty())
                    }
                }
            }
        }
        assertTrue("Level 3 MUST contain Français", foundFrancais)
    }

    @Test
    fun testLevel4_FullPath_Subjects_Weeks_Francais_Proposals() {
        val level = 4
        val lessons = AnnualPlanningRepository.getLessonsForLevel(level)
        assertTrue("Level 4 must contain lessons", lessons.isNotEmpty())

        val weeks = AnnualPlanningRepository.getWeeksForLevel(level)
        assertEquals(38, weeks.size)

        var foundFrancais = false
        for (w in 1..38) {
            val subjects = AnnualPlanningRepository.getSubjectsForWeek(level, w)
            assertTrue("Week $w in Level 4 must have subjects", subjects.isNotEmpty())

            if (subjects.contains("Français")) {
                foundFrancais = true
            }

            for (sub in subjects) {
                val subLessons = AnnualPlanningRepository.getLessonsForWeekAndSubject(level, w, sub)
                assertTrue("Lessons for Level $level Week $w Subject $sub must not be empty", subLessons.isNotEmpty())
                for (lesson in subLessons) {
                    assertEquals(level, lesson.level)
                    assertTrue(w in lesson.week..lesson.weekEnd)

                    if (LessonProposalRepository.isActualLesson(lesson)) {
                        val proposal = LessonProposalRepository.getProposalForLesson(lesson, w)
                        assertNotNull(proposal)
                        assertEquals(lesson.subject, proposal.subject)
                        assertTrue(proposal.procedureSteps.isNotEmpty())
                    }
                }
            }
        }
        assertTrue("Level 4 MUST contain Français", foundFrancais)
    }

    @Test
    fun testLevel5_FullPath_Subjects_Weeks_Francais_Proposals() {
        val level = 5
        val lessons = AnnualPlanningRepository.getLessonsForLevel(level)
        assertTrue("Level 5 must contain lessons", lessons.isNotEmpty())

        val weeks = AnnualPlanningRepository.getWeeksForLevel(level)
        assertEquals(38, weeks.size)

        var foundFrancais = false
        for (w in 1..38) {
            val subjects = AnnualPlanningRepository.getSubjectsForWeek(level, w)
            assertTrue("Week $w in Level 5 must have subjects", subjects.isNotEmpty())

            if (subjects.contains("Français")) {
                foundFrancais = true
            }

            for (sub in subjects) {
                val subLessons = AnnualPlanningRepository.getLessonsForWeekAndSubject(level, w, sub)
                assertTrue("Lessons for Level $level Week $w Subject $sub must not be empty", subLessons.isNotEmpty())
                for (lesson in subLessons) {
                    assertEquals(level, lesson.level)
                    assertTrue(w in lesson.week..lesson.weekEnd)

                    if (LessonProposalRepository.isActualLesson(lesson)) {
                        val proposal = LessonProposalRepository.getProposalForLesson(lesson, w)
                        assertNotNull(proposal)
                        assertEquals(lesson.subject, proposal.subject)
                        assertTrue(proposal.procedureSteps.isNotEmpty())
                    }
                }
            }
        }
        assertTrue("Level 5 MUST contain Français", foundFrancais)
    }

    @Test
    fun testClassToLevelBinding_SourceOfTruth_And_NoFallback() {
        // Direct level property is strictly authoritative
        for (lvl in 1..5) {
            val sec = ClassSection(id = lvl * 100L, name = "قسم غير مسمى", level = lvl)
            assertEquals("Direct level $lvl must be preserved", lvl, AnnualPlanningRepository.resolveLevel(sec))
        }

        // Parsing from Mauritanian class names when level property is 0
        assertEquals(1, AnnualPlanningRepository.resolveLevel(ClassSection(name = "1AP")))
        assertEquals(2, AnnualPlanningRepository.resolveLevel(ClassSection(name = "2AP")))
        assertEquals(3, AnnualPlanningRepository.resolveLevel(ClassSection(name = "3AP")))
        assertEquals(4, AnnualPlanningRepository.resolveLevel(ClassSection(name = "4AP")))
        assertEquals(5, AnnualPlanningRepository.resolveLevel(ClassSection(name = "5AP")))

        assertEquals(1, AnnualPlanningRepository.resolveLevel(ClassSection(name = "السنة الأولى أ")))
        assertEquals(2, AnnualPlanningRepository.resolveLevel(ClassSection(name = "السنة الثانية ب")))
        assertEquals(3, AnnualPlanningRepository.resolveLevel(ClassSection(name = "السنة الثالثة 1")))
        assertEquals(4, AnnualPlanningRepository.resolveLevel(ClassSection(name = "السنة الرابعة")))
        assertEquals(5, AnnualPlanningRepository.resolveLevel(ClassSection(name = "السنة الخامسة 2")))

        assertEquals(1, AnnualPlanningRepository.resolveLevel(ClassSection(name = "1AF")))
        assertEquals(3, AnnualPlanningRepository.resolveLevel(ClassSection(name = "3AF")))
        assertEquals(5, AnnualPlanningRepository.resolveLevel(ClassSection(name = "5AF")))

        // Unknown level returns 0 (No fallback to Level 1)
        val unknownSec = ClassSection(name = "فصل الأمل", level = 0)
        assertEquals(0, AnnualPlanningRepository.resolveLevel(unknownSec))
    }

    @Test
    fun testMultiWeekProgression_DifferentPhases() {
        // Find a 4-week unit in Arabic Level 1 (W2..W5)
        val l1Lessons = AnnualPlanningRepository.getLessonsForLevel(1)
        val multiWeekItem = l1Lessons.first { it.week == 2 && it.weekEnd == 5 && it.subject == "اللغة العربية" }

        val propW2 = LessonProposalRepository.getProposalForLesson(multiWeekItem, 2)
        val propW3 = LessonProposalRepository.getProposalForLesson(multiWeekItem, 3)
        val propW4 = LessonProposalRepository.getProposalForLesson(multiWeekItem, 4)
        val propW5 = LessonProposalRepository.getProposalForLesson(multiWeekItem, 5)

        assertNotNull(propW2)
        assertNotNull(propW3)
        assertNotNull(propW4)
        assertNotNull(propW5)

        // Subphases must be differentiated
        assertNotEquals(propW2.currentWeekSubPhase, propW3.currentWeekSubPhase)
        assertNotEquals(propW3.currentWeekSubPhase, propW4.currentWeekSubPhase)
        assertNotEquals(propW4.currentWeekSubPhase, propW5.currentWeekSubPhase)

        assertTrue(propW2.currentWeekSubPhase.contains("مرحلة 1 من 4"))
        assertTrue(propW3.currentWeekSubPhase.contains("مرحلة 2 من 4"))
        assertTrue(propW4.currentWeekSubPhase.contains("مرحلة 3 من 4"))
        assertTrue(propW5.currentWeekSubPhase.contains("مرحلة 4 من 4"))
    }

    @Test
    fun testSpecialStations_Recognition() {
        val l1Lessons = AnnualPlanningRepository.getLessonsForLevel(1)

        val diagItem = l1Lessons.first { it.subject.contains("التهيئة والتقويم التشخيصي") }
        val examItem = l1Lessons.first { it.subject.contains("اختبارات وعطلة الفصل الأول") }
        val regularItem = l1Lessons.first { it.subject == "التربية الإسلامية" && it.week == 2 }

        // Diagnostic is special station
        assertTrue(LessonProposalRepository.isSpecialStation(diagItem))
        // Exam/Vacation is special station
        assertTrue(LessonProposalRepository.isSpecialStation(examItem))
        // Regular Islamic lesson is NOT special station
        assertFalse(LessonProposalRepository.isSpecialStation(regularItem))

        // Diagnostic is an actual lesson (instructional)
        assertTrue(LessonProposalRepository.isActualLesson(diagItem))
        // Exam/Vacation is administrative (not a daily instructional lesson)
        assertFalse(LessonProposalRepository.isActualLesson(examItem))
    }

    @Test
    fun testMultiClassStateIsolation() {
        val classAId = 101L
        val classBId = 202L
        val classCId = 303L

        // Clear existing test prefs
        AnnualPlanningProgressManager.getInstance(context)

        // Toggle week 3 for Class A
        AnnualPlanningProgressManager.toggleWeekCompletion(context, classAId, 3)
        // Toggle week 7 for Class B
        AnnualPlanningProgressManager.toggleWeekCompletion(context, classBId, 7)
        // Set last visited week for Class C
        AnnualPlanningProgressManager.setLastVisitedWeek(context, classCId, 15)

        val progA = AnnualPlanningProgressManager.getProgress(context, classAId)
        val progB = AnnualPlanningProgressManager.getProgress(context, classBId)
        val progC = AnnualPlanningProgressManager.getProgress(context, classCId)

        assertTrue("Class A must contain completed week 3", progA.completedWeeks.contains(3))
        assertFalse("Class A must NOT contain week 7", progA.completedWeeks.contains(7))

        assertTrue("Class B must contain completed week 7", progB.completedWeeks.contains(7))
        assertFalse("Class B must NOT contain week 3", progB.completedWeeks.contains(3))

        assertEquals(15, progC.lastVisitedWeek)
        assertNull(progA.lastVisitedWeek)
    }
}
