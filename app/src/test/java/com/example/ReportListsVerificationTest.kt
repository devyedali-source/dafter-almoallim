package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.ClassSection
import com.example.data.models.Student
import com.example.data.models.Subject
import com.example.ui.HtmlReportHelper
import com.example.ui.StudentPerformance
import com.example.ui.TeacherViewModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ReportListsVerificationTest {

    private lateinit var context: Context
    private lateinit var viewModel: TeacherViewModel
    private val activeClass = ClassSection(
        id = 1L,
        name = "القسم الثاني",
        wilaya = "كوركول",
        moughataa = "كيهيدي",
        schoolName = "مدرسة السلام",
        level = 2,
        sectionName = "ب",
        academicYear = "2025 - 2026",
        isActivated = true
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = TeacherViewModel(application)
    }

    private fun generateMockStudents(count: Int): List<Student> {
        return (1..count).map { i ->
            Student(
                id = i.toLong(),
                name = "طالب رقم $i",
                classId = 1L,
                frenchName = "Student Number $i",
                schoolId = "SCH-$i",
                nationalId = "NAT-000$i",
                healthNotes = if (i % 10 == 0) "بحاجة لرعاية" else "",
                parentPhone1 = "2222333$i",
                parentPhone2 = "4444555$i",
                gender = if (i % 2 == 0) "ذكر" else "أنثى"
            )
        }
    }

    private fun generateMockSubjects(): List<Subject> {
        return listOf(
            Subject(id = 1L, name = "التربية الإسلامية", level = 2, maxPoints = 20, classId = 1L),
            Subject(id = 2L, name = "اللغة العربية", level = 2, maxPoints = 20, classId = 1L),
            Subject(id = 3L, name = "الرياضيات", level = 2, maxPoints = 20, classId = 1L),
            Subject(id = 4L, name = "اللغة الفرنسية", level = 2, maxPoints = 20, classId = 1L)
        )
    }

    private fun generateMockPerformances(students: List<Student>, subjects: List<Subject>): List<StudentPerformance> {
        return students.mapIndexed { index, student ->
            StudentPerformance(
                student = student,
                averageScore = 12.5 + (index % 5) * 0.5,
                rank = index + 1,
                gradesMap = subjects.associate { it.id to 10.0 + (index % 10) }
            )
        }
    }

    @Test
    fun testReportGenerationWithVaryingStudentCounts() {
        // Test with 1 student, 35 students (typical class size), and 80 students (large/max class size)
        val studentCounts = listOf(1, 35, 80)

        for (count in studentCounts) {
            val students = generateMockStudents(count)
            val subjects = generateMockSubjects()
            val performances = generateMockPerformances(students, subjects)

            // 1. Class Student List (A4 Portrait List with footer)
            val studentDirectoryHtml = HtmlReportHelper.generateStudentDirectoryHtml(
                context = context,
                className = activeClass.name,
                students = students,
                activeClass = activeClass
            )
            assertNotNull(studentDirectoryHtml)
            assertTrue(studentDirectoryHtml.startsWith("<!DOCTYPE html>"))
            assertTrue(studentDirectoryHtml.contains("مدرسة السلام"))
            assertTrue(studentDirectoryHtml.contains("لائحة التلاميذ"))
            // Check Cairo font imported
            assertTrue(studentDirectoryHtml.contains("https://fonts.googleapis.com/css2?family=Cairo"))
            // Verify our paged-media bottom rule is generated
            assertTrue(studentDirectoryHtml.contains("@bottom-right"))

            // 2. French Grades Entry List (A4 Portrait List with footer)
            val frenchGradesHtml = HtmlReportHelper.generateFrenchGradesEntryHtml(
                context = context,
                className = activeClass.name,
                students = students,
                activeClass = activeClass,
                frenchMaxPoints = 20
            )
            assertNotNull(frenchGradesHtml)
            assertTrue(frenchGradesHtml.contains("لائحة إدراج نتائج الفرنسية"))
            assertTrue(frenchGradesHtml.contains("@bottom-center"))

            // 3. Detailed Term Ledger (A4 Landscape List with footer)
            val detailedLedgerHtml = HtmlReportHelper.generateDetailedTermLedgerHtml(
                context = context,
                className = activeClass.name,
                performances = performances,
                subjects = subjects,
                activeTermId = 1,
                activeClass = activeClass,
                viewModel = viewModel
            )
            assertNotNull(detailedLedgerHtml)
            assertTrue(detailedLedgerHtml.contains("دفتر كشف الدرجات التفصيلي"))
            assertTrue(detailedLedgerHtml.contains("landscape"))
            assertTrue(detailedLedgerHtml.contains("@bottom-left"))

            // 4. Annual Averages (A4 Landscape List with footer)
            val annualAveragesHtml = HtmlReportHelper.generateAnnualAveragesHtml(
                context = context,
                className = activeClass.name,
                students = students,
                activeClass = activeClass,
                viewModel = viewModel
            )
            assertNotNull(annualAveragesHtml)
            assertTrue(annualAveragesHtml.contains("دفتر كشف المعدلات السنوي ورتب التجاوز"))
            assertTrue(annualAveragesHtml.contains("@bottom-center"))

            // 5. Hymns Groups (5 groups, Portrait)
            val hymnsHtml = HtmlReportHelper.generateHymnsGroupsHtml(
                context = context,
                className = activeClass.name,
                students = students,
                activeClass = activeClass
            )
            assertNotNull(hymnsHtml)
            assertTrue(hymnsHtml.contains("مجموعات النشيد المدرسي"))

            // 6. Cleaning Groups (5 groups, Portrait)
            val cleaningHtml = HtmlReportHelper.generateCleaningGroupsHtml(
                context = context,
                className = activeClass.name,
                students = students,
                activeClass = activeClass
            )
            assertNotNull(cleaningHtml)
            assertTrue(cleaningHtml.contains("جدول النظافة والكناسة الدوري"))
        }
    }
}
