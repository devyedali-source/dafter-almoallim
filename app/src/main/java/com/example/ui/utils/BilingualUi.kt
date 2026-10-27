package com.example.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

/**
 * واجهة ثنائية اللغة: العربية هي الأساس، والفرنسية تحتها بحجم أصغر (لمعلمي الفرنسية غير الناطقين بالعربية).
 * القاعدة: لا تُترجم الأرقام ولا الأسماء ولا البيانات، فقط عبارات الواجهة.
 */
object BiFr {
    /** زر «Traduire»: إذا كان مفعّلاً تظهر الفرنسية تحت العربية في الواجهات والكشوف، وإلا تبقى كما كانت. */
    var enabled by mutableStateOf(false)

    private val emojiRegex = Regex("[\\u2190-\\u2BFF\\uFE0F\\u200D]|[\\uD83C-\\uD83E][\\uDC00-\\uDFFF]")

    private fun norm(s: String): String =
        s.replace(emojiRegex, "").replace(Regex("\\s+"), " ").trim()

    private val extra: Map<String, String> = mapOf(
        "الرقم (التسلسلي) يظهر دائماً. الاسم:" to "Le numéro (de série) s'affiche toujours. Nom :",
        "الملاحظات الصحية" to "Remarques de santé",
        "باقي الأعمدة:" to "Autres colonnes :",
        "سحب اللائحة" to "Exporter la liste",
        "طالب جديد" to "Nouvel élève",
        "ما الذي تريد إظهاره في اللائحة؟" to "Que voulez-vous afficher dans la liste ?",
        "هاتف الوكيل 1" to "Téléphone du tuteur 1",
        "هاتف الوكيل 2" to "Téléphone du tuteur 2",
        // الشريط السفلي
        "الطلاب" to "Élèves",
        "النتائج" to "Résultats",
        "الكشوف" to "Relevés",
        "التخطيط السنوي" to "Planif. annuelle",
        "الجدول الزمني" to "Emploi du temps",
        // الصفحة الرئيسية
        "إنشاء قسم" to "Créer classe",
        "دفتر المعلم" to "Cahier de l'enseignant",
        "دفتر المعلم - اختيار السنة الدراسية" to "Cahier de l'enseignant – Choix de l'année scolaire",
        "اختر السنة الدراسية للعمل عليها" to "Choisissez l'année scolaire de travail",
        "العام الدراسي" to "Année scolaire",
        "العام الدراسي:" to "Année scolaire :",
        "السنة الدراسية" to "Année scolaire",
        "السنة الدراسية:" to "Année scolaire :",
        "جارية" to "En cours",
        "أقسام دراسية مسجلة" to "classes enregistrées",
        "تطبيقي المدرسي" to "Mon application scolaire",
        "لوحة تسيير مدير الأقسام" to "Tableau de gestion des classes",
        // عناوين الأقسام الخارجية
        "تبادل المعلمين (بين الولايات)" to "Échanges d'enseignants (entre wilayas)",
        "تبادلات المعلمين (بين الولايات)" to "Échanges d'enseignants (entre wilayas)",
        "حول التطبيق" to "À propos de l'application",
        "التشريع المدرسي" to "Législation scolaire",
        "دورة تحضير مسابقات المعلمين" to "Préparation aux concours des enseignants",
        // القائمة الجانبية
        "القائمة الجانبية للتطبيق" to "Menu de l'application",
        "الصفحة الرئيسية والأقسام" to "Accueil et classes",
        "تصميم اللوائح والكشوف" to "Design des listes et relevés",
        "كتبي" to "Koutoubi",
        "لائحة الطلاب" to "Liste des élèves",
        "المنشدين" to "Groupes de l'hymne",
        "الكناسة" to "Nettoyage de la classe",
        "سجل معلم الفرنسية" to "Registre du maître de français",
        "الطلاب المسجلين كلياً" to "Total des élèves inscrits",
        "ذكور" to "Garçons",
        "إناث" to "Filles",
        "أقسام" to "Classes",
        "عدد الطلاب لكل قسم:" to "Élèves par classe :",
        "طالب" to "élève",
        "طلاب" to "élèves",
        "المستوى الدراسي من 1 إلى 6" to "Niveau scolaire de 1 à 6",
        "رجوع إلى الدروس" to "Retour aux leçons",
        "رجوع إلى التخصّصات" to "Retour aux spécialités",
        "رجوع إلى المواد" to "Retour aux matières",
        "رجوع إلى قائمة الدروس" to "Retour à la liste des leçons",
        "الخروج من الاختبار" to "Quitter le test",
        "إجابة خاطئة" to "Mauvaise réponse",
        "إجابة صحيحة" to "Bonne réponse",
        "اختر الولاية أولاً:" to "Choisissez d'abord la wilaya :",
        "اختر الولاية المستهدفة للتحويل:" to "Choisissez la wilaya visée par la mutation :",
        "استمارة إعلان التبادل" to "Formulaire d'annonce d'échange",
        "اسم المدرسة:" to "Nom de l'école :",
        "البلدية:" to "Commune :",
        "السؤال الموالي" to "Question suivante",
        "الولاية:" to "Wilaya :",
        "تحديد المقاطعة المستهدفة للتحويل (داخل الولاية)" to "Choisir la moughataa visée (dans la wilaya)",
        "تحديد الولاية المرغوب التحويل إليها" to "Choisir la wilaya de mutation souhaitée",
        "تعديل إعلان التبادل" to "Modifier l'annonce d'échange",
        "عرض المعلمين الراغبين في الانتقال والتحويل إلى هذه الولاية" to "Afficher les enseignants qui souhaitent être mutés vers cette wilaya",
        "عرض المعلمين الراغبين في التحويل من وإلى هذه المقاطعة داخل نفس الولاية" to "Afficher les enseignants qui souhaitent être mutés depuis ou vers cette moughataa, dans la même wilaya",
        "عرض النتيجة" to "Voir le résultat",
        "لم تقم بنشر أي إعلان تبادل بعد" to "Vous n'avez encore publié aucune annonce d'échange",
        "مكان العمل:" to "Lieu de travail :",
        "نشر إعلان التبادل الآن" to "Publier l'annonce maintenant",
        "يرجى اكتمال معلومات مكان عملك الحالية (المقاطعة، البلدية، مكان العمل، واسم المدرسة) للتواصل عبر الواتساب." to "Veuillez compléter les informations de votre lieu de travail actuel (moughataa, commune, lieu de travail et nom de l'école) pour être contacté sur WhatsApp.",
        "يرجى اكتمال معلومات مكان عملك الحالية (الولاية، المقاطعة، البلدية، مكان العمل، واسم المدرسة) للتواصل عبر الواتساب." to "Veuillez compléter les informations de votre lieu de travail actuel (wilaya, moughataa, commune, lieu de travail et nom de l'école) pour être contacté sur WhatsApp.",
        "يرجى تسجيل الدخول" to "Veuillez vous connecter",
        "يرجى تسجيل الدخول بحسابك لعرض وإدارة منشوراتك الخاصة (التعديل والحذف)." to "Connectez-vous pour afficher et gérer vos annonces (modifier et supprimer).",
        "يريدون دخول المقاطعة" to "Veulent entrer dans la moughataa",
        "يريدون دخول الولاية" to "Veulent entrer dans la wilaya",
        "يريدون مغادرة المقاطعة" to "Veulent quitter la moughataa",
        "يريدون مغادرة الولاية" to "Veulent quitter la wilaya",
        "يمكنك الذهاب إلى تبويب \"استمارة الإعلان\" ونشر أول إعلان لك مجاناً ليصل لكافة المعلمين في موريتانيا." to "Allez à l'onglet « Formulaire d'annonce » et publiez gratuitement votre première annonce pour toucher tous les enseignants de Mauritanie.",
        "المحاكي الشامل" to "Simulateur complet",
        "حفظ التعديلات" to "Enregistrer les modifications",
        "تبادل مقاعد ودي دون دفع" to "Échange amiable de postes, sans paiement",
        "بيع مقعد مكان عمله" to "Vente de son poste actuel",
        "بحث عن شراء مقعد" to "Recherche d'achat d'un poste",
        "معلم مقاطعي" to "Enseignant de moughataa",
        "المقابل المالي: للنقاش في الخاص" to "Contrepartie financière : à discuter en privé",
        "مكان العمل الحالي:" to "Lieu de travail actuel :",
        "الولايات والمقاطعات المرغوب العمل بها:" to "Wilayas et moughataas souhaitées :",
        "التخصص:" to "Spécialité :",
        "تواصل عبر الواتساب" to "Contacter sur WhatsApp",
        "حذف" to "Supprimer",
        "الإعلانات التي قمت بنشرها على المنصة" to "Annonces que vous avez publiées sur la plateforme",
        "منشوراتي" to "Mes annonces",
        "العروض" to "Offres",
        "استمارة الإعلان" to "Formulaire d'annonce",
        "تصفية وفلترة العروض" to "Filtrer les offres",
        "نوع العرض:" to "Type d'offre :",
        "الكل" to "Tous",
        "الولاية الحالية أو المرغوبة" to "Wilaya actuelle ou souhaitée",
        "جميع الولايات" to "Toutes les wilayas",
        "عدد العروض المتاحة للتبادل بين الولايات:" to "Nombre d'offres d'échange entre wilayas :",
        "تطبيق دفتر المعلم غير مسؤول عن التفاهم بين الأشخاص فقط يوفر تواصل بين المعلمين" to "L'application n'est pas responsable des accords entre personnes ; elle met seulement les enseignants en relation.",
        "حدد نوع العرض والمشاركة:" to "Choisissez le type d'offre :",
        "تخصص المعلم / مقدم الخدمة:" to "Spécialité de l'enseignant / prestataire :",
        "اختر التخصص" to "Choisissez la spécialité",
        "معلم عربية" to "Enseignant d'arabe",
        "معلم فرنسية" to "Enseignant de français",
        "معلم مزدوج" to "Enseignant bilingue",
        "معلم رئيسي عربي" to "Enseignant principal d'arabe",
        "معلم رئيسي مزدوج" to "Enseignant principal bilingue",
        "معلم عربي" to "Enseignant d'arabe",
        "عن الدورة" to "À propos du cours",
        "مفتوح" to "Ouvert",
        "مخصص للوافدين الجدد الذين لم يدرجو نتائج الفصل الأول والثانى" to "Réservé aux nouveaux élèves sans résultats des 1er et 2e trimestres",
        "الرجاء اختيار قسم أولاً" to "Veuillez d'abord choisir une classe",
        "قم باختيار قسم دراسي لتفعيل واجهة الخيارات الإضافية." to "Choisissez une classe pour activer les options supplémentaires.",
        "اختر الطالب..." to "Choisissez l'élève...",
        "البدء في رصد الدرجات" to "Commencer la saisie des notes",
        "الفصل الدراسي" to "Trimestre",
        "حفظ النتائج" to "Enregistrer les résultats",
        "رجوع" to "Retour",
        "رصد مواد لطالب واحد" to "Saisie des matières d'un seul élève",
        "رصد نتيجة مادة للقسم" to "Saisie d'une matière pour toute la classe",
        "رصد وإدخال درجات الطلاب" to "Saisie des notes des élèves",
        "طريقة رصد العلامات" to "Mode de saisie des notes",
        "يرجى تحديد الفصل الدراسي وطريقة الإدخال ثم المادة لبدء واجهة رصد العلامات." to "Veuillez choisir le trimestre, le mode de saisie puis la matière pour commencer la saisie des notes.",
        "المادة الدراسية المطلوبة" to "Matière demandée",
        "الطالب المستهدف" to "Élève concerné",
        "اختر المادة الدراسية..." to "Choisissez la matière...",
        "اختر المادة الدراسية" to "Choisissez la matière",
        "اختر الفصل الدراسي..." to "Choisissez le trimestre...",
        "الفصل الأول" to "1er trimestre",
        "الفصل الثاني" to "2e trimestre",
        "الفصل الثالث والأخير" to "3e et dernier trimestre",
        "الفصل الثالث" to "3e trimestre",
        "الفصل الأخير فقط" to "Dernier trimestre seulement",
        "شريط تقدم تسجيل النتائج" to "Progression de la saisie des notes",
        "اضغط على الشكل الذي يعجبك. التصميم يغيّر الزخرفة فقط ولا يغيّر أي معلومة. «الوضع الأصلي» هو التلقائي ويمكن الرجوع إليه في أي وقت." to "Touchez le modèle qui vous plaît. Le design ne change que la décoration, jamais les informations. « Mode d'origine » est le choix automatique et vous pouvez y revenir à tout moment.",
        "رأسية اللوائح (تُطبَّق على كل اللوائح)" to "En-tête des listes (appliqué à toutes les listes)",
        "تصميم الكشوف (يُطبَّق على كل الكشوف)" to "Design des relevés (appliqué à tous les relevés)",
        "تصميم شهادات التفوق والتكريم" to "Design des certificats d'excellence et d'honneur",
        "الوضع الأصلي (التلقائي)" to "Mode d'origine (automatique)",
        "إطار أزرق بزوايا مزخرفة" to "Cadre bleu à coins ornés",
        "زوايا زرقاء مائلة" to "Coins bleus inclinés",
        "شريط أخضر بفواصل" to "Bande verte à séparateurs",
        "أمواج زرقاء ومعين" to "Vagues bleues et losanges",
        "إطار ذهبي مع أغصان" to "Cadre doré avec rameaux",
        "موج أزرق خفيف" to "Vague bleue légère",
        "إطار ذهبي مزخرف" to "Cadre doré orné",
        "موج أزرق مع كتاب" to "Vague bleue avec livre",
        "أخضر خفيف مع قبعة وكتب" to "Vert clair avec toque et livres",
        "إطار بنفسجي خفيف" to "Cadre violet léger",
        "أخضر وذهبي مع أغصان" to "Vert et or avec rameaux",
        "ذهبي كلاسيكي" to "Or classique",
        "أخضر وطني" to "Vert national",
        "أزرق عصري" to "Bleu moderne",
        "بنفسجي أنيق" to "Violet élégant",
        "بسيط للطباعة بالأبيض والأسود" to "Simple, pour impression noir et blanc",
        "تم" to "Terminé",
        "تسجيل الخروج" to "Se déconnecter",
        // الجدول الزمني
        "الجدول الزمني للقسم" to "Emploi du temps de la classe",
        "رسمي" to "Officiel",
        "شخصي" to "Personnel",
        "الفترة الصباحية الأولى" to "Première période du matin",
        "الفترة الصباحية الثانية" to "Deuxième période du matin",
        "الفترة المسائية" to "Période de l'après-midi",
        "مشاركة PDF" to "Partager en PDF",
        "العودة للجدول الرسمي" to "Retour à l'emploi du temps officiel",
        "عدّل التاريخ الهجري بعد انتهاء كل اجتماع للجنة الأهلة ليبقى التاريخ الهجري صحيحًا ومحليًا دون إنترنت" to
            "Ajustez la date hégirienne après chaque réunion du comité des croissants pour qu'elle reste exacte, en local et sans internet",
        "استراحة مبرمجة بيداغوجياً" to "Récréation programmée",
        "لا توجد حصص مبرمجة في هذه الفترة" to "Aucune séance programmée dans cette période",
        "لا توجد حصص مبرمجة في الفترة المسائية" to "Aucune séance programmée l'après-midi",
        "15 دقيقة" to "15 min",
        "الإثنين" to "Lundi",
        "الثلاثاء" to "Mardi",
        "الأربعاء" to "Mercredi",
        "الخميس" to "Jeudi",
        "الجمعة" to "Vendredi",
        "السبت" to "Samedi",
        "الأحد" to "Dimanche"
    )

    private val subjects: Map<String, String> = mapOf(
        "التربية الإسلامية" to "Éducation islamique",
        "القرآن الكريم" to "Coran",
        "الأخلاق" to "Morale",
        "الحديث الشريف" to "Hadith",
        "السيرة النبوية" to "Biographie du Prophète",
        "العبادات" to "Cultes",
        "العقيدة" to "Dogme",
        "العقيدة / الأخلاق" to "Dogme / Morale",
        "التاريخ" to "Histoire",
        "الجغرافيا" to "Géographie",
        "التاريخ والجغرافيا" to "Histoire et géographie",
        "التربية البدنية" to "Éducation physique",
        "التربية العلمية والتكنولوجية" to "Éducation scientifique et technologique",
        "التقنيات" to "Techniques",
        "تقنيات الإعلام والاتصال" to "TIC",
        "التربية الفنية" to "Éducation artistique",
        "الأعمال اليدوية والرسم" to "Travaux manuels et dessin",
        "الرسم / الأعمال اليدوية" to "Dessin / Travaux manuels",
        "التربية المدنية" to "Éducation civique",
        "التربية على المواطنة" to "Éducation à la citoyenneté",
        "السلوك المدني" to "Comportement civique",
        "السلوك المدني / المهارات الحياتية" to "Comportement civique / Compétences de vie",
        "المهارات الحياتية" to "Compétences de vie",
        "الرياضيات" to "Mathématiques",
        "الحساب" to "Calcul",
        "القياس" to "Mesure",
        "الهندسة" to "Géométrie",
        "العلوم" to "Sciences",
        "البيئة" to "Environnement",
        "البيئة / التجريب" to "Environnement / Expérimentation",
        "الصحة" to "Santé",
        "الصحة والتغذية" to "Santé et nutrition",
        "قواعد السلامة" to "Règles de sécurité",
        "اللغة العربية" to "Langue arabe",
        "الإملاء والخط العربي" to "Dictée et calligraphie arabe",
        "التعبير" to "Expression",
        "القراءة" to "Lecture",
        "القراءة والفهم" to "Lecture et compréhension",
        "الكتابة" to "Écriture",
        "قواعد النحو والصرف" to "Grammaire arabe"
    )


    /** أسماء الولايات والمقاطعات بالفرنسية (حسب القائمة الرسمية). */
    private val geo: Map<String, String> = mapOf(
        "الحوض الشرقي" to "Hodh Ech Chargui", "الحوض الغربي" to "Hodh El Gharbi", "لعصابه" to "Assaba",
        "كوركول" to "Gorgol", "لبراكنه" to "Brakna", "اترارزه" to "Trarza", "آدرار" to "Adrar",
        "داخلت نواذيبو" to "Dakhlet Nouadhibou", "تكانت" to "Tagant", "كيدي ماغا" to "Guidimagha",
        "تيرس زمور" to "Tiris Zemmour", "إنشيري" to "Inchiri",
        "نواكشوط الشمالية" to "Nouakchott-Nord", "نواكشوط الغربية" to "Nouakchott-Ouest", "نواكشوط الجنوبية" to "Nouakchott-Sud",
        "النعمة" to "Néma", "تمبدغة" to "Timbédra", "آمرج" to "Amourj", "باسكنو" to "Bassikounou", "جكني" to "Djigueni",
        "ولاته" to "Oualata", "عدل بكرو" to "Adel Bagrou", "انبيكت لحواش" to "N'Beiket Lehwach",
        "لعيون" to "Aïoun", "الطينطان" to "Tintane", "كوبني" to "Koubenni", "تامشكط" to "Tamchekett", "اطويل" to "Touil",
        "كيفه" to "Kiffa", "كرو" to "Guerou", "كنكوصة" to "Kankossa", "بومديد" to "Boumdeid", "باركيول" to "Barkéol",
        "كيهيدي" to "Kaédi", "مونغل" to "Monguel", "مقامة" to "Maghama", "إمبود" to "M'Bout", "لكصيبة" to "Lexeiba",
        "ألاك" to "Aleg", "بوكى" to "Boghé", "بابابى" to "Bababé", "مبان" to "M'Bagne", "مقطع لحجار" to "Magta Lahjar", "مال" to "Male",
        "روصو" to "Rosso", "بوتلميت" to "Boutilimit", "المذرذرة" to "Méderdra", "واد الناقة" to "Ouad Naga",
        "اركيز" to "R'Kiz", "كرمسين" to "Keur Macène", "انتيكان" to "Tékane",
        "أطار" to "Atar", "شنقيط" to "Chinguetti", "وادان" to "Ouadane", "أوجفت" to "Aoujeft",
        "نواذيبو" to "Nouadhibou", "الشامي" to "Chami",
        "تجكجة" to "Tidjikja", "المجرية" to "Moudjéria", "تيشيت" to "Tichitt",
        "سيلبابي" to "Sélibaby", "ولد ينجة" to "Ould Yengé", "غابو" to "Ghabou", "ومبو" to "Wompou",
        "ازويرات" to "Zouérate", "فديرك" to "F'Déirick", "بير أم اكرين" to "Bir Moghrein",
        "أكجوجت" to "Akjoujt", "بنشاب" to "Bénichab",
        "دار النعيم" to "Dar Naïm", "تيارت" to "Teyarett", "توجنين" to "Toujounine",
        "تفرغ زينة" to "Tevragh Zeina", "لكصر" to "Ksar", "السبخة" to "Sebkha",
        "عرفات" to "Arafat", "الميناء" to "El Mina", "الرياض" to "Riyad"
    )

    /** الاسم الفرنسي لولاية أو مقاطعة إن عُرف. */
    fun geoFr(ar: String): String? {
        val t = ar.trim()
        geo[t]?.let { return it }
        // أرقام لاحقة مثل «لكصيبه 1»: نترجم الاسم ونُبقي الرقم
        val m = Regex("^(.*?)\\s*([0-9٠-٩]+)$").find(t)
        val base = m?.groupValues?.get(1)?.trim() ?: t
        val suffix = m?.groupValues?.get(2)
        val hit = geoNorm[geoKey(base)] ?: return null
        return if (suffix != null) "$hit $suffix" else hit
    }

    private fun geoKey(s: String): String =
        s.trim().replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا').replace('ة', 'ه').replace('ى', 'ي')

    private val geoAliases: Map<String, String> = mapOf("غوركل" to "Gorgol")

    private val geoNorm: Map<String, String> by lazy {
        val m = HashMap<String, String>()
        for ((k, v) in geo) m[geoKey(k)] = v
        for ((k, v) in geoAliases) m[geoKey(k)] = v
        m
    }

    /** الاسم الفرنسي لمادة إن عُرف. */
    fun subjectFr(ar: String): String? = subjects[norm(ar)]

    /** «الحوض الشرقي - Hodh Ech Chargui»؛ وإن لم يوجد اسم فرنسي يبقى العربي وحده. */
    fun geoLabel(ar: String): String {
        if (!enabled) return ar
        val f = geo[ar.trim()] ?: return ar
        return "$ar - $f"
    }

    /** الترجمة الفرنسية لعبارة واجهة عربية، أو null إن لم تتوفر (فيظهر العربي وحده). */
    fun of(ar: String): String? {
        val k = norm(ar)
        if (k.isEmpty()) return null
        // نص فيه حروف لاتينية أصلاً (مثل Français) لا يُترجم
        if (k.any { it in 'a'..'z' || it in 'A'..'Z' }) return null
        val direct = extra[k] ?: subjects[k] ?: UiFrench.map[ar] ?: UiFrench.map[k]
        if (direct != null) return direct
        // العبارة قد تنتهي برمز تعبيري (🏫 🎨 ...): نبحث بدونه
        val bare = k.filter { it.isLetterOrDigit() || it.isWhitespace() || it in "()[]:،,.-/'؟?!" }
            .trim().replace(Regex("\\s+"), " ")
        if (bare.isEmpty() || bare == k) return null
        return extra[bare] ?: subjects[bare] ?: UiFrench.map[bare]
    }

    /** اسم القسم بحروف لاتينية: «الطلحاية1 - س4 (4AP) [2026-2027]» ← «El Talhaya 1 - Cl. 4 (4AP)» (بدون تكرار السنة). */
    fun className(ar: String): String {
        var s = ar.replace(Regex("\\s*\\[.*?\\]"), "").trim()
        s = s.replace(Regex("(?<![\\p{L}])س\\s*(\\d)"), "Cl. $1")
        return com.example.ui.BiHtml.translitName(s)
    }

    /** نفس of لكن لنصوص فيها متغيرات {0} {1} ... */
    fun f(ar: String, vararg args: Any?): String? {
        var out = of(ar) ?: return null
        args.forEachIndexed { i, a -> out = out.replace("{" + i + "}", a?.toString() ?: "") }
        return out
    }

    /** اسم المادة مع فرعها بالفرنسية: «Éducation islamique (Coran)» */
    fun subjectLine(subject: String, branch: String): String? {
        val s = subjects[norm(subject)] ?: return null
        val b = branch.trim()
        if (b.isEmpty() || b == subject.trim()) return s
        val fb = subjects[norm(b)] ?: return s
        return "$s ($fb)"
    }

    private val frMonths = listOf(
        "janvier", "février", "mars", "avril", "mai", "juin",
        "juillet", "août", "septembre", "octobre", "novembre", "décembre"
    )
    private val frDays = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche")
    private val frHijriMonths = listOf(
        "Mouharram", "Safar", "Rabi' al-awwal", "Rabi' ath-thani",
        "Joumada al-oula", "Joumada ath-thania", "Rajab", "Chaabane",
        "Ramadan", "Chawwal", "Dhou al-qi'da", "Dhou al-hijja"
    )

    /** «Lundi 26 octobre 2026» (الأرقام كما هي). */
    fun gregorian(d: LocalDate): String =
        "${frDays[d.dayOfWeek.value - 1]} ${d.dayOfMonth} ${frMonths[d.monthValue - 1]} ${d.year}"

    /** «15 Joumada al-oula 1448 H». */
    fun hijri(day: Int, month: Int, year: Int): String =
        "$day ${frHijriMonths.getOrElse(month - 1) { month.toString() }} $year H"
}

/**
 * نص عربي أساسي وتحته الفرنسية بحجم أصغر. إن لم توجد ترجمة يظهر العربي وحده.
 * [fr] يُمرَّر يدوياً للنصوص الديناميكية (التواريخ مثلاً).
 */
@Composable
fun BiText(
    ar: String,
    modifier: Modifier = Modifier,
    fr: String? = BiFr.of(ar),
    fontSize: TextUnit = 14.sp,
    frSize: TextUnit = fontSize * 0.72f,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    center: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(
            text = ar,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (center) TextAlign.Center else null
        )
        if (BiFr.enabled && !fr.isNullOrBlank()) {
            Text(
                text = fr,
                fontSize = frSize,
                fontWeight = FontWeight.Medium,
                color = if (color == Color.Unspecified) color else color.copy(alpha = color.alpha * 0.85f),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (center) TextAlign.Center else null
            )
        }
    }
}


/** مثل BiText لكن يرث حجم الخط من السياق إن لم يُحدَّد (للتحويل التلقائي للأزرار والنصوص). */
@Composable
fun BiTextA(
    ar: String,
    modifier: Modifier = Modifier,
    fr: String? = BiFr.of(ar),
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    center: Boolean = false
) {
    val base = if (fontSize != TextUnit.Unspecified) fontSize
        else androidx.compose.material3.LocalTextStyle.current.fontSize.let { if (it == TextUnit.Unspecified) 14.sp else it }
    BiText(
        ar = ar, modifier = modifier, fr = fr, fontSize = base, frSize = base * 0.72f,
        fontWeight = fontWeight, color = color, maxLines = maxLines, center = center
    )
}
