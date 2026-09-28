package com.example.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.models.Student
import com.example.data.models.Subject
import com.example.data.models.sortedByOfficialOrder
import com.example.ui.StudentPerformance
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object PdfGenerator {

    /**
     * Generates a beautifully formatted PDF for the Class Ledger (کشف نقاط القسم)
     * and shares it using the Android Share Intent.
     */
    fun shareClassLedgerPdf(
        context: Context,
        className: String,
        academicYear: String,
        subjects: List<Subject>,
        performances: List<StudentPerformance>,
        termName: String
    ) {
        val sortedSubjects = subjects.sortedByOfficialOrder()
        val pdfDocument = PdfDocument()
        val paint = Paint()
        
        // A4 page size proportions: 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842
        
        val margin = 35
        val contentWidth = pageWidth - (margin * 2)
        
        val rowHeight = 22
        val headerHeight = 28
        val itemsPerPage = 28
        
        val totalStudents = performances.size
        var currentStudentIndex = 0
        var pageNum = 1
        
        if (totalStudents == 0) {
            Toast.makeText(context, "لا توجد بيانات طلاب لتصديرها!", Toast.LENGTH_SHORT).show()
            return
        }

        while (currentStudentIndex < totalStudents) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            
            // Draw standard modern decorative light gray background
            paint.color = 0xFFF9FAFC.toInt()
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)
            
            // Header Top Bar Accent (Emerald Green Bento Vibe)
            paint.color = 0xFF0D9488.toInt()
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 12f, paint)

            // Draw Header Text block
            paint.color = 0xFF1E293B.toInt() // Dark Slate Charcoal
            paint.isAntiAlias = true
            paint.textSize = 18f
            paint.isFakeBoldText = true
            
            val appTitle = "دفتر المعلم الذكي 📝 - كشف النقاط الإجمالي"
            val rectAppTitle = Rect()
            paint.getTextBounds(appTitle, 0, appTitle.length, rectAppTitle)
            canvas.drawText(appTitle, (pageWidth - rectAppTitle.width()) / 2f, 40f, paint)
            
            paint.textSize = 11f
            paint.isFakeBoldText = false
            paint.color = 0xFF475569.toInt() // Cool Gray
            
            val detailsText = "الصف: $className   |   الفصل الدراسي: $termName   |   السنة الدراسية: $academicYear"
            val rectDetailsText = Rect()
            paint.getTextBounds(detailsText, 0, detailsText.length, rectDetailsText)
            canvas.drawText(detailsText, (pageWidth - rectDetailsText.width()) / 2f, 60f, paint)
            
            // Thin elegant rule below header
            paint.color = 0xFFE2E8F0.toInt()
            canvas.drawLine(margin.toFloat(), 72f, (pageWidth - margin).toFloat(), 72f, paint)

            // Now draw the table
            var currentY = 95f
            
            // Draw table columns headers
            // Columns: Name (right alignment), Average, Rank, Sub1, Sub2, Sub3...
            // Let's allocate width: 
            // - Name: 160 points
            // - Average: 50 points
            // - Rank: 40 points
            // - Subjects space: remaining (approx 275 points) divided by number of subjects (up to 6)
            val nameWidth = 160
            val avgWidth = 50
            val rankWidth = 40
            
            val maxSubToShow = minOf(sortedSubjects.size, 6)
            val subWidth = if (maxSubToShow > 0) (contentWidth - nameWidth - avgWidth - rankWidth) / maxSubToShow else 0
            
            // Draw Table Header Background: Slate Cool Gray
            paint.color = 0xFFE2E8F0.toInt()
            canvas.drawRoundRect(
                margin.toFloat(), 
                currentY, 
                (pageWidth - margin).toFloat(), 
                currentY + headerHeight, 
                6f, 6f, paint
            )
            
            paint.color = 0xFF1E293B.toInt()
            paint.isFakeBoldText = true
            paint.textSize = 10f
            
            // Row text drawing
            var colX = (pageWidth - margin).toFloat()
            
            // Col 1: Name (draw aligned right)
            canvas.drawText("اسم الطالب والطالبة", colX - nameWidth + 10f, currentY + 18f, paint)
            colX -= nameWidth
            
            // Col 2: Average
            canvas.drawText("المعدل", colX - avgWidth + 10f, currentY + 18f, paint)
            colX -= avgWidth
            
            // Col 3: Rank
            canvas.drawText("الرتبة", colX - rankWidth + 8f, currentY + 18f, paint)
            colX -= rankWidth
            
            // Draw subjects headers
            for (i in 0 until maxSubToShow) {
                val sub = sortedSubjects[i]
                val subNameTrunc = if (sub.name.length > 9) sub.name.substring(0, 8) + ".." else sub.name
                canvas.drawText(subNameTrunc, colX - subWidth + 3f, currentY + 18f, paint)
                colX -= subWidth
            }
            
            currentY += headerHeight + 5f
            
            // Page items loop
            var itemInPageCount = 0
            while (currentStudentIndex < totalStudents && itemInPageCount < itemsPerPage) {
                val perf = performances[currentStudentIndex]
                
                // Zebra striping backgrounds: alternating rows
                if (itemInPageCount % 2 == 0) {
                    paint.color = 0xFFF1F5F9.toInt() // light zebra row
                } else {
                    paint.color = 0xFFFFFFFF.toInt() // clean white row
                }
                
                canvas.drawRoundRect(
                    margin.toFloat(),
                    currentY,
                    (pageWidth - margin).toFloat(),
                    currentY + rowHeight,
                    4f, 4f, paint
                )
                
                // Write scores/data
                paint.color = 0xFF334155.toInt() // Text secondary slate
                paint.isFakeBoldText = false
                paint.textSize = 9f
                
                var rX = (pageWidth - margin).toFloat()
                
                // 1. Draw Student Name (Arabic RTL)
                val stdName = perf.student.name
                val rectN = Rect()
                paint.getTextBounds(stdName, 0, stdName.length, rectN)
                // Draw name right aligned
                canvas.drawText(stdName, rX - nameWidth + 10f, currentY + 14f, paint)
                rX -= nameWidth
                
                // 2. Draw Average
                paint.isFakeBoldText = true
                val avgStr = String.format(Locale.US, "%.2f", perf.averageScore)
                canvas.drawText(avgStr, rX - avgWidth + 12f, currentY + 14f, paint)
                paint.isFakeBoldText = false
                rX -= avgWidth
                
                // 3. Draw Rank
                val rnkStr = "${perf.rank}"
                canvas.drawText(rnkStr, rX - rankWidth + 14f, currentY + 14f, paint)
                rX -= rankWidth
                
                // 4. Draw grades of subjects
                for (i in 0 until maxSubToShow) {
                    val sub = sortedSubjects[i]
                    val gradeValue = perf.gradesMap[sub.id]
                    val gradeStr = if (gradeValue != null) com.example.ui.HtmlReportHelper.formatCleanNumber(gradeValue) else "-"
                    canvas.drawText(gradeStr, rX - subWidth + 10f, currentY + 14f, paint)
                    rX -= subWidth
                }
                
                currentY += rowHeight + 3f
                currentStudentIndex++
                itemInPageCount++
            }
            
            // Draw Footer with elegant styling
            paint.color = 0xFF94A3B8.toInt()
            paint.textSize = 8.5f
            val footerText = "تطبيق دفتر المعلم 🇲🇷  |  الصفحة $pageNum  |  تم التوليد فورياً ومحلياً 🟢"
            val rFooter = Rect()
            paint.getTextBounds(footerText, 0, footerText.length, rFooter)
            canvas.drawText(footerText, (pageWidth - rFooter.width()) / 2f, pageHeight - 20f, paint)
            
            pdfDocument.finishPage(page)
            pageNum++
        }
        
        saveAndSharePdf(context, pdfDocument, "كشف_نقاط_${className.replace(" ", "_")}.pdf")
    }

    /**
     * Generates a single student report certificate (كشف درجات الطالب الفردي) in PDF format
     */
    fun shareSingleStudentReportPdf(
        context: Context,
        className: String,
        academicYear: String,
        student: Student,
        subjects: List<Subject>,
        gradesMap: Map<Long, Double>,
        termName: String,
        average: Double,
        rank: Int,
        totalStudentsCount: Int,
        outOfTen: Boolean,
        observations: String = ""
    ) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40
        
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        
        // Background Base: Warm clean background with soft layout
        paint.color = 0xFFFDFDFD.toInt()
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)
        
        // Elegant gold/teal header borders
        paint.color = 0xFF0F766E.toInt() // Teal Primary
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 15f, paint)
        paint.color = 0xFFD97706.toInt() // Amber accent line below it
        canvas.drawRect(0f, 15f, pageWidth.toFloat(), 20f, paint)

        // Mauritania crest visual / decorative text
        paint.color = 0xFF0F766E.toInt()
        paint.textSize = 20f
        paint.isFakeBoldText = true
        paint.isAntiAlias = true
        
        val docTitle = "الجمهورية الإسلامية الموريتانية 🇲🇷"
        val rDocTitle = Rect()
        paint.getTextBounds(docTitle, 0, docTitle.length, rDocTitle)
        canvas.drawText(docTitle, (pageWidth - rDocTitle.width()) / 2f, 55f, paint)

        paint.textSize = 10f
        paint.color = 0xFF475569.toInt()
        paint.isFakeBoldText = false
        val ministryTitle = "وزارة التربية وإصلاح النظام التعليمي"
        val rMinistry = Rect()
        paint.getTextBounds(ministryTitle, 0, ministryTitle.length, rMinistry)
        canvas.drawText(ministryTitle, (pageWidth - rMinistry.width()) / 2f, 72f, paint)
        
        // Card Body with Rounded Edges for Student Information
        paint.color = 0xFFF1F5F9.toInt()
        canvas.drawRoundRect(
            margin.toFloat(), 
            95f, 
            (pageWidth - margin).toFloat(), 
            215f, 
            12f, 12f, paint
        )
        
        // Text details inside student info card
        paint.color = 0xFF1E293B.toInt()
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("بطاقة النتائج والدرجات الفصلية 🔖", margin + 15f, 125f, paint)
        
        paint.textSize = 10.5f
        paint.isFakeBoldText = false
        paint.color = 0xFF334155.toInt()
        
        // Left Column Coordinates
        val leftColX = margin + 20f
        // Right Column Coordinates
        val rightColX = pageWidth - margin - 230f
        
        canvas.drawText("اسم التلميذ(ة):  ${student.name}", rightColX, 155f, paint)
        if (student.frenchName.isNotBlank()) {
            canvas.drawText("الاسم بالفرنسية: ${student.frenchName}", rightColX, 175f, paint)
        } else {
            canvas.drawText("الجنس: ${student.gender}", rightColX, 175f, paint)
        }
        canvas.drawText("القسم الدراسي:  $className", rightColX, 195f, paint)
        
        canvas.drawText("الفصل الدراسي:   $termName", leftColX, 155f, paint)
        canvas.drawText("السنة الدراسية:   $academicYear", leftColX, 175f, paint)
        canvas.drawText("الرقم الوطني / المدرسي:  ${student.nationalId.ifBlank { student.schoolId }}", leftColX, 195f, paint)

        // Divider
        paint.color = 0xFFCBD5E1.toInt()
        canvas.drawLine(margin.toFloat(), 230f, (pageWidth - margin).toFloat(), 230f, paint)

        // Column headers for grades table
        // Headers: Subject | Max Points | Grade | Result Level / Observation
        var tableY = 250f
        val tableHeight = 32f
        
        paint.color = 0xFF0F766E.toInt() // Header row: dark teal
        canvas.drawRoundRect(
            margin.toFloat(), 
            tableY, 
            (pageWidth - margin).toFloat(), 
            tableY + tableHeight, 
            8f, 8f, paint
        )
        
        paint.color = Color.WHITE
        paint.textSize = 11f
        paint.isFakeBoldText = true
        
        val colName = "المادة الدراسية"
        val colMax = "الدرجة القصوى"
        val colScore = "النقاط المحصل عليها"
        val colPercent = "النسبة"
        
        canvas.drawText(colName, (pageWidth - margin - 150f), tableY + 20f, paint)
        canvas.drawText(colMax, (pageWidth - margin - 240f), tableY + 20f, paint)
        canvas.drawText(colScore, (pageWidth - margin - 370f), tableY + 20f, paint)
        canvas.drawText(colPercent, margin + 25f, tableY + 20f, paint)
        
        tableY += tableHeight + 5f
        
        // Draw subjects rows
        subjects.forEachIndexed { idx, sub ->
            if (idx % 2 == 0) {
                paint.color = 0xFFF8FAFC.toInt()
            } else {
                paint.color = Color.WHITE
            }
            
            canvas.drawRoundRect(
                margin.toFloat(), 
                tableY, 
                (pageWidth - margin).toFloat(), 
                tableY + 24f, 
                4f, 4f, paint
            )
            
            paint.color = 0xFF1E293B.toInt()
            paint.textSize = 10f
            paint.isFakeBoldText = false
            
            canvas.drawText(sub.name, (pageWidth - margin - 150f), tableY + 16f, paint)
            
            val maxPts = sub.maxPoints
            canvas.drawText("$maxPts", (pageWidth - margin - 230f), tableY + 16f, paint)
            
            val doubleScore = gradesMap[sub.id]
            val dispScore = if (doubleScore != null) com.example.ui.HtmlReportHelper.formatCleanNumber(doubleScore) else "غائب"
            paint.isFakeBoldText = true
            canvas.drawText(dispScore, (pageWidth - margin - 350f), tableY + 16f, paint)
            paint.isFakeBoldText = false
            
            val percentText = if (doubleScore != null) {
                val ratio = (doubleScore / maxPts) * 100
                String.format(Locale.US, "%d%%", ratio.toInt())
            } else {
                "-"
            }
            canvas.drawText(percentText, margin + 30f, tableY + 16f, paint)
            
            tableY += 28f
        }
        
        // Big performance summary card at the bottom
        tableY += 10f
        paint.color = 0xFFFFFBEB.toInt() // Warm amber highlight container
        canvas.drawRoundRect(
            margin.toFloat(), 
            tableY, 
            (pageWidth - margin).toFloat(), 
            tableY + 105f, 
            12f, 12f, paint
        )
        
        paint.color = 0xFFD97706.toInt() // Amber accent border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(
            margin.toFloat(), 
            tableY, 
            (pageWidth - margin).toFloat(), 
            tableY + 105f, 
            12f, 12f, paint
        )
        paint.style = Paint.Style.FILL
        
        // Summary items
        paint.color = 0xFF1E293B.toInt()
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("ملخص الفصل والنتائج العامة 🏆", margin + 15f, tableY + 30f, paint)
        
        paint.textSize = 11.5f
        paint.isFakeBoldText = false
        paint.color = 0xFF451A03.toInt() // Amber Text color
        
        val scaleSuffix = if (outOfTen) "/10" else "/20"
        val averageText = "المعدل الإجمالي للفصل:   "
        val averageNumText = String.format(Locale.US, "%.2f", average) + " " + scaleSuffix
        
        canvas.drawText(averageText, margin + 20f, tableY + 60f, paint)
        paint.isFakeBoldText = true
        canvas.drawText(averageNumText, margin + 150f, tableY + 60f, paint)
        paint.isFakeBoldText = false
        
        val rankText = "الرتبة في القسم الدراسي:  "
        val rankNumText = "$rank  من أصل  $totalStudentsCount  تلاميذ"
        canvas.drawText(rankText, margin + 20f, tableY + 85f, paint)
        paint.isFakeBoldText = true
        canvas.drawText(rankNumText, margin + 150f, tableY + 85f, paint)
        paint.isFakeBoldText = false
        
        // Right side summary stats (Success indicator)
        val successColor = if (average >= (if (outOfTen) 5.0 else 10.0)) 0xFF15803D.toInt() else 0xFFB91C1C.toInt()
        paint.color = successColor
        paint.isFakeBoldText = true
        paint.textSize = 15f
        val verdict = if (average >= (if (outOfTen) 5.0 else 10.0)) "ناجح(ة) مؤهل للتفوق 🟢" else "لم يوفق هذا الفصل 🔴"
        canvas.drawText(verdict, pageWidth - margin - 200f, tableY + 65f, paint)
        paint.isFakeBoldText = false

        // Custom observation message
        if (observations.isNotBlank()) {
            paint.textSize = 9.5f
            paint.color = 0xFF64748B.toInt()
            canvas.drawText("ملاحظة الأستاذ: $observations", margin + 20f, tableY + 125f, paint)
        }

        // Draw Signatures Block
        val sigY = pageHeight - 110f
        paint.color = 0xFF1E293B.toInt()
        paint.textSize = 10f
        paint.isFakeBoldText = true
        
        canvas.drawText("توقيع المعلم والأستاذ", margin + 40f, sigY, paint)
        canvas.drawText("توقيع الإدارة المدرسية", (pageWidth / 2f) - 40f, sigY, paint)
        canvas.drawText("توقيع ولي الأمر والوصي", (pageWidth - margin - 120f), sigY, paint)
        
        paint.color = 0xFFCBD5E1.toInt()
        canvas.drawLine(margin + 20f, sigY + 45f, margin + 150f, sigY + 45f, paint)
        canvas.drawLine((pageWidth / 2f) - 60f, sigY + 45f, (pageWidth / 2f) + 60f, sigY + 45f, paint)
        canvas.drawLine((pageWidth - margin - 140f), sigY + 45f, (pageWidth - margin - 20f), sigY + 45f, paint)

        // Footer standard brand info
        paint.color = 0xFF94A3B8.toInt()
        paint.textSize = 8.5f
        val footerLabel = "تم استخدام برنامج دفتر المعلم الذكي الموريتاني لإعداد النتائج والطباعة تلقائياً وبأمان كامل 🇲🇷"
        val rFL = Rect()
        paint.getTextBounds(footerLabel, 0, footerLabel.length, rFL)
        canvas.drawText(footerLabel, (pageWidth - rFL.width()) / 2f, pageHeight - 20f, paint)
        
        pdfDocument.finishPage(page)
        
        saveAndSharePdf(context, pdfDocument, "بطاقة_نتائج_${student.name.replace(" ", "_")}.pdf")
    }

    private fun saveAndSharePdf(context: Context, pdfDocument: PdfDocument, filename: String) {
        try {
            // Save inside clean temporary external/cache files
            val pdfDir = File(context.cacheDir, "generated_pdfs")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }
            val pdfFile = File(pdfDir, filename)
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            
            // Generate content:// Uri using the registered FileProvider
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)
            
            // Build ACTION_SEND share sheet
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(shareIntent, "مشاركة وطباعة التقرير PDF")
            // grant read permissions explicitly for intent chooser
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(chooser)
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "فشل توليد وتصدير ملف الـ PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
