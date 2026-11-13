package com.example.ui.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * حالة إرسال طلب التفعيل داخل النافذة: تنبيه دائم بلزوم الإنترنت، مؤشر «جاري إنشاء الطلب» أثناء الإرسال،
 * ورسالة اعتذار ظاهرة داخل النافذة عند الفشل (حتى لا يظن المستخدم أن الزر لا يعمل).
 */
@Composable
fun ActivationSendStatus(sending: Boolean, error: String?) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE3F2FD), modifier = Modifier.fillMaxWidth()) {
            BiText(
                ar = "ℹ️ يلزم اتصال بالإنترنت لإرسال الطلب.",
                fr = "ℹ️ Une connexion Internet est nécessaire pour envoyer la demande.",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0)
            )
        }
        if (sending) {
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF3E0), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp, color = Color(0xFFE65100))
                    BiText(
                        ar = "جاري إنشاء الطلب... قد يطول الأمر إذا كان الإنترنت ضعيفًا، انتظر قليلًا.",
                        fr = "Création de la demande... Cela peut durer si la connexion est faible, patientez.",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        if (error != null && !sending) {
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFEBEE), modifier = Modifier.fillMaxWidth()) {
                BiText(
                    ar = "⚠️ $error",
                    fr = if (error == ActivationRequestSender.MSG_NO_NET) "⚠️ Pas de connexion Internet. La demande n'a pas été envoyée, réessayez quand le réseau est disponible."
                         else "⚠️ Connexion trop faible, la demande n'a pas atteint le serveur. Réessayez quand le réseau est disponible.",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828)
                )
            }
        }
    }
}
