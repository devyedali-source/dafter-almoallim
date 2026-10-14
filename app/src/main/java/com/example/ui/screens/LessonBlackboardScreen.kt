package com.example.ui.screens

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
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
 * شاشة السبورة: خلفية سوداء وكتابة بيضاء غليظة بالطبشور، مقسمة إلى ثلاثة أقسام
 * (الترويسة، الدرس، التطبيق)، وإلى أربعة عند وجود الإدماج (الربع الثالث).
 * تُعرض بالوضع الأفقي، وتقبل التقريب بإصبعين.
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
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context as? android.app.Activity
        val previous = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose { activity?.requestedOrientation = previous }
    }
    BackHandler(onBack = onClose)

    val direction = if (isFrench) LayoutDirection.Ltr else LayoutDirection.Rtl
    val subject = data.subject.ifBlank { subjectFallback }
    val domain = data.domain.ifBlank { domainFallback }
    val topic = data.topic.ifBlank { topicFallback }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoardBlack)
            .systemBarsPadding()
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            ZoomableSheet {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .border(3.dp, BoardFrame, RoundedCornerShape(6.dp))
                        .padding(4.dp)
                ) {
                    // 1) الترويسة (الثلث الأول من جهة اليمين)
                    BoardColumn(modifier = Modifier.weight(1f)) {
                        BoardLine(if (isFrench) "Date (hégire) :" else "التاريخ الهجري:", BOARD_DOTS)
                        BoardLine(if (isFrench) "Date :" else "التاريخ الميلادي:", BOARD_DOTS)
                        BoardLine(if (isFrench) "Matière :" else "المادة:", subject)
                        BoardLine(if (isFrench) "Domaine :" else "المجال:", domain)
                        BoardLine(if (isFrench) "Thème :" else "الموضوع:", topic)
                    }
                    BoardDivider()
                    // 2) الدرس أو القواعد أو نص القراءة
                    BoardColumn(modifier = Modifier.weight(1f)) {
                        BoardTitle(if (isFrench) "Leçon" else "الدرس")
                        BoardBody(board.lesson)
                    }
                    BoardDivider()
                    // 3) الإدماج (إن وجد): ليفهم التلاميذ الربط بالدرس السابق
                    if (board.hasIntegration) {
                        BoardColumn(modifier = Modifier.weight(1f)) {
                            BoardTitle(if (isFrench) "Intégration" else "الإدماج")
                            BoardBody(board.integration)
                        }
                        BoardDivider()
                    }
                    // 4) التطبيق على الدرس (يكتبه التلاميذ في دفتر التمارين)
                    BoardColumn(modifier = Modifier.weight(1f)) {
                        BoardTitle(if (isFrench) "Application" else "التطبيق")
                        BoardBody(board.application)
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
private fun BoardColumn(modifier: Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        content()
    }
}

@Composable
private fun BoardDivider() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .fillMaxHeight()
            .background(ChalkWhite.copy(alpha = 0.85f))
    )
}

private val ChalkStyle = TextStyle(
    color = ChalkWhite,
    fontWeight = FontWeight.ExtraBold,
    shadow = Shadow(color = ChalkWhite.copy(alpha = 0.35f), offset = Offset(0f, 0f), blurRadius = 4f)
)

@Composable
private fun BoardTitle(text: String) {
    Text(
        text = text,
        style = ChalkStyle,
        fontSize = 26.sp,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BoardBody(text: String) {
    Text(
        text = text,
        style = ChalkStyle,
        fontSize = 22.sp,
        lineHeight = 36.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BoardLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = ChalkStyle, fontSize = 20.sp, textDecoration = TextDecoration.Underline)
        Text(text = value, style = ChalkStyle, fontSize = 22.sp, lineHeight = 32.sp)
    }
}
