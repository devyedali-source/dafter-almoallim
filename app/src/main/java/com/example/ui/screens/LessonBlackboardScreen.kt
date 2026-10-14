package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.planning.LessonProposalData

private val BoardBlack = Color(0xFF050505)
private val ChalkWhite = Color(0xFFFAFAF5)
private val BoardFrame = Color(0xFF6B4A2B)

/** نقاط فارغة يكتب فيها المعلم التاريخ بنفسه، كما في ورقة التحضير. */
private const val BOARD_DOTS = "................"

/**
 * شاشة السبورة: خلفية سوداء وكتابة بيضاء غليظة بالطبشور. تظهر كل السبورة دفعة واحدة دون تمرير:
 * الترويسة، الدرس (ما يكتبه التلاميذ)، الإدماج (إن وجد)، والتطبيق. يصغَّر الخط تلقائياً ليتسع المحتوى.
 * لا تغيّر اتجاه الهاتف: في الوضع العمودي تُرتَّب الأقسام فوق بعضها، وفي الأفقي جنباً إلى جنب.
 */
@Composable
fun LessonBlackboardScreen(
    data: LessonProposalData,
    isFrench: Boolean,
    subjectFallback: String,
    domainFallback: String,
    topicFallback: String,
    onClose: () -> Unit
) {
    val board = data.board ?: return
    BackHandler(onBack = onClose)

    val direction = if (isFrench) LayoutDirection.Ltr else LayoutDirection.Rtl
    val subject = data.subject.ifBlank { subjectFallback }
    val domain = data.domain.ifBlank { domainFallback }
    val topic = data.topic.ifBlank { topicFallback }

    val headerText = listOf(
        (if (isFrench) "Date (hégire) : " else "التاريخ الهجري: ") + BOARD_DOTS,
        (if (isFrench) "Date : " else "التاريخ الميلادي: ") + BOARD_DOTS,
        (if (isFrench) "Matière : " else "المادة: ") + subject,
        (if (isFrench) "Domaine : " else "المجال: ") + domain,
        (if (isFrench) "Thème : " else "الموضوع: ") + topic
    ).joinToString("\n")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoardBlack)
            .systemBarsPadding()
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .border(3.dp, BoardFrame, RoundedCornerShape(6.dp))
                    .padding(4.dp)
            ) {
                val landscape = maxWidth > maxHeight
                val sections: List<Triple<String?, String, Float>> = buildList {
                    add(Triple(null, headerText, 0.9f))
                    add(Triple(if (isFrench) "Leçon" else "الدرس", board.lesson, 2f))
                    if (board.hasIntegration) add(Triple(if (isFrench) "Intégration" else "الإدماج", board.integration, 1.1f))
                    add(Triple(if (isFrench) "Application" else "التطبيق", board.application, 1.3f))
                }
                if (landscape) {
                    Row(Modifier.fillMaxSize()) {
                        sections.forEachIndexed { i, (title, text, w) ->
                            if (i > 0) BoardVDivider()
                            BoardSection(title, text, Modifier.weight(w).fillMaxHeight())
                        }
                    }
                } else {
                    Column(Modifier.fillMaxSize()) {
                        sections.forEachIndexed { i, (title, text, w) ->
                            if (i > 0) BoardHDivider()
                            BoardSection(title, text, Modifier.weight(w).fillMaxWidth())
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .size(34.dp)
                .background(Color.White.copy(alpha = 0.18f), CircleShape)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✕", color = ChalkWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun BoardSection(title: String?, text: String, modifier: Modifier) {
    Column(modifier = modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
        if (title != null) {
            Text(
                text = title,
                style = ChalkStyle,
                fontSize = 20.sp,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
        }
        AutoFitChalkText(text, Modifier.fillMaxWidth().weight(1f))
    }
}

/** نص يصغَّر خطه تدريجياً حتى يتسع كله في المساحة المتاحة دون تمرير. */
@Composable
private fun AutoFitChalkText(text: String, modifier: Modifier) {
    BoxWithConstraints(modifier = modifier) {
        var size by remember(text, maxWidth, maxHeight) { mutableStateOf(26f) }
        var ready by remember(text, maxWidth, maxHeight) { mutableStateOf(false) }
        Text(
            text = text,
            style = ChalkStyle,
            fontSize = size.sp,
            lineHeight = (size * 1.45f).sp,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent { if (ready) drawContent() },
            onTextLayout = { r ->
                if (r.didOverflowHeight && size > 8f) {
                    size = (size * 0.93f).coerceAtLeast(8f)
                } else {
                    ready = true
                }
            }
        )
    }
}

@Composable
private fun BoardVDivider() {
    Box(Modifier.width(2.dp).fillMaxHeight().background(ChalkWhite.copy(alpha = 0.85f)))
}

@Composable
private fun BoardHDivider() {
    Box(Modifier.fillMaxWidth().height(2.dp).background(ChalkWhite.copy(alpha = 0.85f)))
}

private val ChalkStyle = TextStyle(
    color = ChalkWhite,
    fontWeight = FontWeight.ExtraBold,
    shadow = Shadow(color = ChalkWhite.copy(alpha = 0.35f), offset = Offset(0f, 0f), blurRadius = 4f)
)
