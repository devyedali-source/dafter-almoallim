# -*- coding: utf-8 -*-
"""
This script appends the remaining Level 5 subjects to generate_level5.py:
1. التربية المدنية
2. Français (Lecture, Expression orale, Grammaire, Conjugaison, Orthographe, Vocabulaire, Production d'écrits)
3. التربية البدنية والفنية (التربية البدنية والرياضية، والتربية الفنية والنشيد)
"""

code_to_append = '''

# ----------------- التربية المدنية (LEVEL 5) -----------------
add_item("l5_civ_w02_w05", 2, 5, "التربية المدنية", "رموز السيادة والمؤسسات الدستورية (W02–W05)",
        "رموز السيادة الوطنية في الجمهورية الإسلامية الموريتانية والمؤسسات الدستورية",
        "التعرف على رموز السيادة الوطنية (العلم الوطني بألوانه ورموزه، النشيد الوطني، الشعار، العملة الوطنية الأوقية، الختم الرسمي) ودور رئيس الجمهورية والبرلمان والقضاء.",
        "يميز بين مختلف رموز السيادة الوطنية ويشرح دلالاتها ويعدد مهام السلطات التنفيذية والتشريعية والقضائية.",
        "تأمل العلم الوطني وتحليله، الاستماع للنشيد الوطني وحفظه وترديده باحترام، فحص نماذج العملة الوطنية، ورسم المخطط الهيكلي للمؤسسات الدستورية.")

add_item("l5_civ_w07_w10", 7, 10, "التربية المدنية", "حقوق وواجبات المواطن (W07–W10)",
        "حقوق المواطن وواجباته في المجتمع ودولة القانون",
        "معرفة الحقوق الأساسية للمواطن (حق الحياة، التعليم، الرعاية الصحية، المساواة، حرية التعبير، الأمن) والواجبات الملزمة (احترام القوانين، الدفاع عن الوطن، أداء الضرائب، الإخلاص للوطن).",
        "يحدد حقوق وواجبات المواطن في الدستور الموريتاني، ويمارس السلوك المدني القويم داخل المدرسة وخارجها.",
        "مناقشة نصوص مقتبسة من الدستور الموريتاني حول الحقوق والواجبات، جدول مقارنة الحقوق والواجبات، وتمثيل مواقف تدل على الوعي المدني.")

add_item("l5_civ_w14_w17", 14, 17, "التربية المدنية", "الديمقراطية والانتخابات (W14–W17)",
        "الديمقراطية والمواطنة والعملية الانتخابية النزيهة",
        "فهم مفهوم الديمقراطية ومبادئ الانتخابات (التصويت السري، بطاقة الناخب، صندوق الاقتراع، فرز الأصوات، قبول نتائج الأغلبية واحترام رأي الأقلية).",
        "يشرح مراحل العملية الانتخابية وأهمية المشاركة الإيجابية في بناء الوطن واختيار الممثلين بنزاهة.",
        "تنظيم عملية انتخابية تمثيلية لاختيار مندوب القسم (صندوق اقتراع، بطاقات تصويت، لجنة فرز، وإعلان النتائج بروح رياضية وديمقراطية).")

add_item("l5_civ_w19_w23", 19, 23, "التربية المدنية", "المؤسسات الإدارية والخدمية (W19–W23)",
        "المؤسسات الإدارية والخدمية الوطنية والبلدية ودورها في خدمة المواطن",
        "التعرف على المؤسسات الإدارية واللامركزية (البلدية والمجلس البلدي، الولاية، المقاطعة، المستشفيات والمراكز الصحية، البريد، مراكز الحالة المدنية، الحماية المدنية).",
        "يوضح دور البلدية والمؤسسات العمومية في تقديم الخدمات للمواطنين (النظافة، الإنارة، الصحة، استخراج الوثائق الرسمية).",
        "استمارة استخراج وثيقة إدارية (شهادة ميلاد/سجل سكاني)، محاكاة مراجعة مكتب خدمة عمومية، وكتابة تقرير عن خدمات البلدية.")

add_item("l5_civ_w27_w31", 27, 31, "التربية المدنية", "السلامة المرورية والوقاية (W27–W31)",
        "قواعد السلامة المرورية والوقاية من حوادث السير والسلوك في الطرقات",
        "التعرف على إشارات المرور (المنع، الخطر، الإلزام، الإرشاد) وقواعد عبور المشاة واستخدام وسائل النقل العامة بأمان واحترام شرطة المرور.",
        "يطبق قواعد المرور بأمان (النظر يميناً ويساراً قبل العبور، استعمال ممر الراجلين، عدم اللعب في الطرقات، ربط حزام الأمان).",
        "لوحات إشارات المرور، ورشة تطبيقية بساحة المدرسة لعبور ممر المشاة، وتصميم ملصقات توعية للوقاية من حوادث السير.")

add_item("l5_civ_w33_w36", 33, 36, "التربية المدنية", "حماية الممتلكات والتراث (W33–W36)",
        "حماية الممتلكات العامة والمحافظة على البيئة والتراث الوطني",
        "إدراك أهمية المحافظة على الممتلكات العامة (المدارس، الحدائق، المستشفيات، وسائل النقل) وحماية التراث الموريتاني العريق وغرس ثقافة التطوع.",
        "يعبر عن التزامه بصيانة الممتلكات العامة والمشاركة في الأعمال التطوعية وغرس الأشجار والمحافظة على نظافة المحيط المدرسي والحي.",
        "حملة نظافة وتشجير في فناء المدرسة، إعداد ميثاق حماية الممتلكات العامة والمرافق المدرسية، ومعرض صور للتراث الوطني الموريتاني.")

# ----------------- اللغة الفرنسية (FRANÇAIS - LEVEL 5) -----------------

# 1. Lecture / Compréhension (W02–W36)
add_item("l5_fr_lec_w02_w05", 2, 5, "Français", "Lecture / Compréhension (W02–W05)",
        "Lecture et compréhension de textes narratifs et descriptifs sur la rentrée scolaire et la vie de classe",
        "Lire de manière fluide et expressive un texte narratif ou descriptif, identifier les personnages, le cadre spatio-temporel et les événements principaux.",
        "Lit à voix haute avec intonation, dégage l'idée générale et répond aux questions de compréhension explicites et implicites.",
        "Textes de lecture du manuel de 5e AF, lecture silencieuse suivie de questions, lecture magistrale et individuelle, explication du vocabulaire contextuel.",
        is_french=True)

add_item("l5_fr_lec_w07_w10", 7, 10, "Français", "Lecture / Compréhension (W07–W10)",
        "Lecture et compréhension de textes informatifs et documentaires sur l'environnement et la faune mauritanienne",
        "Comprendre un texte documentaire, repérer les informations essentielles relatives à la faune, la flore et la protection de la nature en Mauritanie.",
        "Identifie le type de texte (informatif/documentaire), retrouve des informations précises et résume les points clés.",
        "Articles documentaires illustrés (Banc d'Arguin, le désert, les oasis), questionnaires de lecture et tableaux d'extraction d'informations.",
        is_french=True)

add_item("l5_fr_lec_w14_w17", 14, 17, "Français", "Lecture / Compréhension (W14–W17)",
        "Lecture et compréhension de contes et récits traditionnels mauritaniens et africains",
        "Découvrir la structure du conte traditionnel (situation initiale, élément perturbateur, péripéties, dénouement, situation finale) et sa portée morale.",
        "Analyse le schéma narratif d'un conte, caractérise les héros et les opposants, et dégage la morale de l'histoire.",
        "Contes mauritaniens transcrits, lecture dramatisée avec jeu de rôles, fiches de lecture et identification des étapes du récit.",
        is_french=True)

add_item("l5_fr_lec_w19_w23", 19, 23, "Français", "Lecture / Compréhension (W19–W23)",
        "Lecture et compréhension de textes injonctifs et prescriptifs (recettes, notices, règles de sécurité)",
        "Comprendre le fonctionnement d'un texte injonctif, repérer les étapes chronologiques, les verbes d'action à l'impératif ou à l'infinitif.",
        "Suit des instructions précises tirées d'un texte prescriptif pour réaliser une tâche, et reconstitue un texte dans l'ordre chronologique.",
        "Notices d'expériences scientifiques simples, recettes de cuisine, chartes de classe, et exercices de remise en ordre de consignes.",
        is_french=True)

add_item("l5_fr_lec_w27_w31", 27, 31, "Français", "Lecture / Compréhension (W27–W31)",
        "Lecture et compréhension de textes sur les métiers, les technologies et les moyens de communication",
        "Lire et analyser des textes traitant du monde du travail, de l'artisanat mauritanien, des transports et des outils de communication modernes.",
        "Comprend le vocabulaire spécifique aux métiers et aux technologies, et exprime un avis personnel argumenté sur le thème abordé.",
        "Textes illustrés sur les métiers traditionnels et modernes en Mauritanie, débats guidés et fiches de synthèse de lecture.",
        is_french=True)

add_item("l5_fr_lec_w33_w36", 33, 36, "Français", "Lecture / Compréhension (W33–W36)",
        "Lecture et compréhension de textes poétiques, dialogues et extraits de pièces de théâtre",
        "Sensibiliser à la musicalité du poème, aux rimes, aux strophes et à l'expression théâtrale à travers des dialogues expressifs.",
        "Déclame un poème avec émotion et respect du rythme, lit un dialogue en incarnant un personnage avec les mimiques appropriées.",
        "Recueil de poèmes d'auteurs francophones et mauritaniens, répétitions de courtes scènes théâtrales et récitation poétique en groupe.",
        is_french=True)

# 2. Expression Orale / Communication (W02–W36)
add_item("l5_fr_exp_w02_w05", 2, 5, "Français", "Expression orale / Communication (W02–W05)",
        "Se présenter, décrire son environnement scolaire et raconter ses vacances",
        "Prendre la parole avec assurance devant ses pairs pour raconter un événement vécu, décrire son école et exprimer ses sentiments.",
        "Construit des phrases correctes à l'oral en utilisant un lexique adapté et une prononciation intelligible.",
        "Jeux de présentation, récits d'expériences vécues à l'oral, dialogues deux par deux et utilisation d'images séquentielles.",
        is_french=True)

add_item("l5_fr_exp_w07_w10", 7, 10, "Français", "Expression orale / Communication (W07–W10)",
        "Décrire un animal, un paysage mauritanien et donner son avis sur la protection de la nature",
        "Prendre la parole pour décrire les caractéristiques d'un animal ou d'un lieu (l'oasis, le fleuve, le littoral) et sensibiliser à l'écologie.",
        "Utilise des adjectifs qualificatifs et des tournures comparatives pour décrire avec précision un élément naturel.",
        "Photos du paysage et de la faune mauritanienne, jeux de devinettes 'Qui suis-je ?', et mini-débats sur la propreté de l'école.",
        is_french=True)

add_item("l5_fr_exp_w14_w17", 14, 17, "Français", "Expression orale / Communication (W14–W17)",
        "Raconter un conte, relater une anecdote ou un fait divers entendu",
        "Restituer oralement la trame d'une histoire ou d'un conte traditionnel en respectant l'enchaînement chronologique des événements.",
        "Emploie les connecteurs logiques et temporels (d'abord, ensuite, soudain, enfin) pour structurer son discours oral.",
        "Raconter à tour de rôle une partie d'un conte, jeu du témoin d'un événement, et exercices d'éloquence en classe.",
        is_french=True)

add_item("l5_fr_exp_w19_w23", 19, 23, "Français", "Expression orale / Communication (W19–W23)",
        "Donner des ordres, formuler des consignes, conseiller et interdire",
        "Communiquer à l'oral pour guider un camarade, lui expliquer les règles d'un jeu ou lui donner des conseils de santé et de sécurité.",
        "Utilise l'impératif présent, les formules d'obligation (il faut, vous devez) et d'interdiction (il est interdit de, ne pas).",
        "Jeu du robot (guidage d'un camarade les yeux fermés), explications de règles de jeux collectifs, et mises en situation.",
        is_french=True)

add_item("l5_fr_exp_w27_w31", 27, 31, "Français", "Expression orale / Communication (W27–W31)",
        "Parler d'un métier d'avenir, expliquer une utilisation technologique et justifier son choix",
        "Participer à une discussion collective sur les métiers d'avenir en Mauritanie (médecin, ingénieur, enseignant, artisan) et justifier ses préférences.",
        "Articule des arguments simples avec 'parce que', 'car', 'à mon avis', 'je pense que' pour défendre une idée à l'oral.",
        "Interview fictive d'un professionnel, table ronde des métiers, et présentations orales individuelles avec support visuel.",
        is_french=True)

add_item("l5_fr_exp_w33_w36", 33, 36, "Français", "Expression orale / Communication (W33–W36)",
        "Dramatiser un dialogue, réciter un poème et participer à un débat d'opinion",
        "S'exprimer avec expressivité, moduler sa voix, utiliser le langage corporel pour jouer un rôle ou défendre une opinion dans un débat structuré.",
        "Adapte son registre de langue à la situation de communication et écoute respectueusement la parole d'autrui.",
        "Saynètes théâtrales courtes, déclamation poétique, tournois de débats contradictoires sur des thèmes citoyens.",
        is_french=True)

# 3. Grammaire (W02–W36)
add_item("l5_fr_gra_w02_w05", 2, 5, "Français", "Grammaire (W02–W05)",
        "La phrase : types de phrases (déclarative, interrogative, exclamative, impérative) et formes (affirmative, négative)",
        "Identifier et transformer les différents types et formes de phrases à l'écrit et à l'oral.",
        "Distingue le type d'une phrase selon sa ponctuation et son sens, et transforme une phrase affirmative en phrase négative (ne... pas, ne... jamais, ne... plus).",
        "Exercices de transformation de phrases, ponctuation de textes non ponctués, et création de phrases interrogatives avec inversion ou est-ce que.",
        is_french=True)

add_item("l5_fr_gra_w07_w10", 7, 10, "Français", "Grammaire (W07–W10)",
        "Les constituants de la phrase : Groupe Nominal Sujet (GNS) et Groupe Verbal (GV), les déterminants et le nom",
        "Identifier les constituants fondamentaux de la phrase simple : le GNS, le GV, et distinguer les noms propres, noms communs et déterminants.",
        "Isole le GNS et le GV dans une phrase, identifie le noyau nominal et son déterminant (articles définis, indéfinis, possessifs, démonstratifs).",
        "Analyse grammaticale en arbre ou en encadrés, substitution de pronoms personnels au GNS, et accords déterminant-nom.",
        is_french=True)

add_item("l5_fr_gra_w14_w17", 14, 17, "Français", "Grammaire (W14–W17)",
        "Les expansions du nom : l'adjectif qualificatif (épithète et attribut) et le complément du nom",
        "Reconnaître et employer les expansions du nom pour enrichir des descriptions dans des phrases complexes.",
        "Distingue l'adjectif qualificatif épithète de l'attribut du sujet, et identifie le complément du nom introduit par une préposition (de, à, en).",
        "Exercices d'enrichissement et de réduction de groupes nominaux, identification des fonctions dans des extraits de textes, et accords en genre et nombre.",
        is_french=True)

add_item("l5_fr_gra_w19_w23", 19, 23, "Français", "Grammaire (W19–W23)",
        "Les compléments d'objet : Complément d'Objet Direct (COD) et Complément d'Objet Indirect (COI) et pronoms compléments",
        "Identifier le COD et le COI dans le groupe verbal et les remplacer par les pronoms personnels appropriés (le, la, les, lui, leur).",
        "Pose les questions 'qui ? / quoi ?' pour trouver le COD et 'à qui ? / à quoi ? / de qui ? / de quoi ?' pour le COI, et effectue la pronominalisation.",
        "Schémas d'analyse fonctionnelle, exercices de pronominalisation de COD/COI, et production de phrases avec compléments d'objet.",
        is_french=True)

add_item("l5_fr_gra_w27_w31", 27, 31, "Français", "Grammaire (W27–W31)",
        "Les compléments circonstanciels (CC) : de temps (CCT), de lieu (CCL) et de manière (CCM)",
        "Identifier les compléments circonstanciels de temps, de lieu et de manière, et reconnaître leur mobilité et supprimabilité.",
        "Distingue les différents CC en posant les questions 'quand ?', 'où ?' et 'comment ?', et les déplace dans la phrase sans en altérer le sens.",
        "Repérage des CC dans des paragraphes, déplacement et suppression des CC, et enrichissement de phrases de base.",
        is_french=True)

add_item("l5_fr_gra_w33_w36", 33, 36, "Français", "Grammaire (W33–W36)",
        "La phrase complexe : juxtaposition, coordination (mais, où, et, donc, or, ni, car) et notion de proposition relative avec 'qui' et 'que'",
        "Comprendre la différence entre phrase simple et phrase complexe, relier des propositions par des conjonctions de coordination ou pronoms relatifs.",
        "Relie deux propositions indépendantes à l'aide de conjonctions de coordination appropriées, et utilise 'qui' ou 'que' pour éviter les répétitions.",
        "Exercices de liaison de phrases simples en phrases complexes, identification des connecteurs logiques, et réécriture de textes.",
        is_french=True)

# 4. Conjugaison (W02–W36)
add_item("l5_fr_con_w02_w05", 2, 5, "Français", "Conjugaison (W02–W05)",
        "Le présent de l'indicatif des verbes des 1er, 2e et 3e groupes usuels (être, avoir, aller, faire, dire, venir, prendre, pouvoir, vouloir)",
        "Maîtriser la conjugaison au présent de l'indicatif des verbes réguliers et des principaux verbes irréguliers du 3e groupe.",
        "Conjugue sans erreur les verbes au présent de l'indicatif avec toutes les personnes grammaticales.",
        "Tableaux de conjugaison systématiques, transformation de textes au présent, dictées flash de formes verbales.",
        is_french=True)

add_item("l5_fr_con_w07_w10", 7, 10, "Français", "Conjugaison (W07–W10)",
        "Le futur simple de l'indicatif des verbes des 1er, 2e et 3e groupes usuels",
        "Connaître les terminaisons régulières du futur simple (-ai, -as, -a, -ons, -ez, -ont) et les radicaux particuliers des verbes fréquents.",
        "Conjugue les verbes usuels au futur simple et les emploie pour exprimer des actions à venir ou des prévisions.",
        "Exercices de transposition temporelle (du présent au futur), rédaction de projets futurs, et grilles d'évaluation de conjugaison.",
        is_french=True)

add_item("l5_fr_con_w14_w17", 14, 17, "Français", "Conjugaison (W14–W17)",
        "L'imparfait de l'indicatif : formation et valeurs pour les 1er, 2e et 3e groupes",
        "Comprendre la valeur de l'imparfait (description, habitude, durée dans le passé) et maîtriser ses terminaisons uniformes (-ais, -ais, -ait, -ions, -iez, -aient).",
        "Forme l'imparfait de tous les verbes à partir du radical de la 1ère personne du pluriel du présent et l'utilise dans des descriptions au passé.",
        "Transposition de récits au passé, repérage de l'imparfait dans des contes, et exercices à trous.",
        is_french=True)

add_item("l5_fr_con_w19_w23", 19, 23, "Français", "Conjugaison (W19–W23)",
        "Le passé composé de l'indicatif : auxiliaires (être/avoir) et participe passé des 1er, 2e et 3e groupes",
        "Maîtriser la structure du passé composé (auxiliaire être ou avoir au présent + participe passé) et la terminaison des participes passés (-é, -i, -u, -is, -it).",
        "Choisit l'auxiliaire adéquat et conjugue correctement les verbes au passé composé en accordant le participe passé avec être.",
        "Exercices d'association auxiliaire-participe, transformation de récits vécus au passé composé, et cartes mémo de conjugaison.",
        is_french=True)

add_item("l5_fr_con_w27_w31", 27, 31, "Français", "Conjugaison (W27–W31)",
        "Le passé simple de l'indicatif (3es personnes du singulier et du pluriel) pour le récit",
        "Reconnaître le passé simple dans les textes littéraires et contes (terminaisons en -a/-èrent pour le 1er groupe, -it/-irent pour les 2e et 3e groupes).",
        "Identifie le passé simple à la lecture et emploie les formes de la 3e personne du singulier et du pluriel pour faire progresser un récit.",
        "Chasse aux verbes au passé simple dans les contes, fiches de conjugaison comparée imparfait / passé simple.",
        is_french=True)

add_item("l5_fr_con_w33_w36", 33, 36, "Français", "Conjugaison (W33–W36)",
        "L'impératif présent et le conditionnel présent (formules de politesse) des verbes usuels",
        "Connaître les trois personnes de l'impératif présent (tu, nous, vous) pour donner des ordres et utiliser le conditionnel de politesse (je voudrais, pourriez-vous).",
        "Formule des consignes précises à l'impératif et utilise le conditionnel pour exprimer un souhait ou une demande polie.",
        "Écriture de notices de jeu à l'impératif, jeux de rôles en situation de demande polie au conditionnel.",
        is_french=True)

# 5. Orthographe (W02–W36)
add_item("l5_fr_ort_w02_w05", 2, 5, "Français", "Orthographe (W02–W05)",
        "L'accord dans le groupe nominal : genre (masculin/féminin) et nombre (singulier/pluriel régulier et particulier)",
        "Appliquer rigoureusement les règles d'accord en genre et en nombre au sein du groupe nominal (déterminant + nom + adjectif).",
        "Accorde correctement l'adjectif qualificatif avec le nom qu'il qualifie et maîtrise les pluriels en -s, -x, -aux, -eux.",
        "Dictées de mots, exercices de mise au pluriel et au féminin de groupes nominaux, et repérage d'erreurs d'accord dans des phrases.",
        is_french=True)

add_item("l5_fr_ort_w07_w10", 7, 10, "Français", "Orthographe (W07–W10)",
        "L'accord sujet-verbe : sujet simple, sujet inversé, et plusieurs sujets",
        "Accorder correctement le verbe avec son sujet, quelle que soit la place du sujet (devant, éloigné, inversé ou pluriel composé).",
        "Repère le sujet de chaque verbe et applique la terminaison verbale correspondante sans se laisser piéger par les mots intercalés.",
        "Exercices d'accord sujet-verbe avec pièges (pronoms intercalés, sujets inversés), dictées préparées et auto-correction avec grille.",
        is_french=True)

add_item("l5_fr_ort_w14_w17", 14, 17, "Français", "Orthographe (W14–W17)",
        "Les homophones grammaticaux fondamentaux : a / à, ont / on, et / est, son / sont",
        "Distinguer les homophones grammaticaux usuels par le raisonnement et le test de substitution (remplacement par avait, étaient, etc.).",
        "Orthographie sans faute les homophones a/à, on/ont, et/est, son/sont dans des phrases contextuelles.",
        "Règles d'astuces mnémotechniques, exercices de choix multiples, dictées à trous et phrases à justifier oralement.",
        is_french=True)

add_item("l5_fr_ort_w19_w23", 19, 23, "Français", "Orthographe (W19–W23)",
        "Les homophones grammaticaux : ce / se, ces / ses, c'est / s'est, ou / où",
        "Distinguer les homophones démonstratifs, possessifs, pronominaux et spatiaux par des tests syntaxiques rigoureux.",
        "Choisit la graphie correcte selon la nature grammaticale du mot dans la phrase (démonstratif, possessif, pronom ou adverbe).",
        "Tableaux synthétiques de comparaison, exercices ciblés d'homophonie et dictées de phrases complexes.",
        is_french=True)

add_item("l5_fr_ort_w27_w31", 27, 31, "Français", "Orthographe (W27–W31)",
        "L'accord du participe passé : avec l'auxiliaire être et avec l'auxiliaire avoir sans COD antéposé, et distinction infinitif en -er / participe en -é",
        "Maîtriser l'accord du participe passé employé avec être et la règle de distinction entre verbe à l'infinitif en -er et participe en -é (test de remplacement par mordre/mordu).",
        "Applique le test de substitution pour choisir entre -é et -er, et accorde le participe passé avec le sujet lorsqu'il est employé avec être.",
        "Exercices de choix -é/-er, dictées de phrases au passé composé, et justifications orthographiques écrites.",
        is_french=True)

add_item("l5_fr_ort_w33_w36", 33, 36, "Français", "Orthographe (W33–W36)",
        "Mots invariables, adverbes en -ment, et ponctuation de dialogue (guillemets, tirets, deux-points)",
        "Mémoriser l'orthographe lexicale des mots invariables courants et maîtriser la ponctuation spécifique des dialogues.",
        "Écrit correctement les mots invariables fréquents (toujours, jamais, soudain, beaucoup) et dispose correctement les signes de ponctuation dans un dialogue.",
        "Dictées de mots invariables, transformation de récits en dialogues ponctués, et jeux d'orthographe.",
        is_french=True)

# 6. Vocabulaire / Lexique (W02–W36)
add_item("l5_fr_voc_w02_w05", 2, 5, "Français", "Vocabulaire / Lexique (W02–W05)",
        "L'utilisation du dictionnaire : ordre alphabétique, mot-repère, article de dictionnaire, abréviations et sens multiples d'un mot",
        "Utiliser efficacement le dictionnaire pour vérifier l'orthographe d'un mot et choisir la définition convenant au contexte.",
        "Cherche rapidement un mot dans le dictionnaire, identifie sa classe grammaticale (n.m., n.f., v., adj.) et sélectionne la bonne acception.",
        "Chronométrage de recherche dans le dictionnaire, analyse d'articles de dictionnaire projetés, et jeux de mots croisés.",
        is_french=True)

add_item("l5_fr_voc_w07_w10", 7, 10, "Français", "Vocabulaire / Lexique (W07–W10)",
        "Les familles de mots, la dérivation : radicaux, préfixes (in-, im-, dé-, re-, pré-) et suffixes (-able, -tion, -ment, -eur)",
        "Comprendre comment se forment les mots par dérivation et enrichir son vocabulaire grâce aux familles de mots.",
        "Identifie le radical d'un mot, ajoute un préfixe pour créer un antonyme ou un suffixe pour changer la nature du mot.",
        "Arbres généalogiques de familles de mots, tableaux de dérivation préfixe/suffixe, et création de néologismes amusants.",
        is_french=True)

add_item("l5_fr_voc_w14_w17", 14, 17, "Français", "Vocabulaire / Lexique (W14–W17)",
        "Synonymes et antonymes (contraires) et niveaux de langue (familier, courant, soutenu)",
        "Enrichir son expression écrite en utilisant des synonymes pour éviter les répétitions et maîtriser les antonymes et registres de langue.",
        "Trouve des synonymes et des contraires adaptés au contexte et classe des mots selon leur niveau de langue.",
        "Jeux de cartes synonymes/contraires, réécriture d'un texte familier en registre courant, et enrichissement de textes pauvres.",
        is_french=True)

add_item("l5_fr_voc_w19_w23", 19, 23, "Français", "Vocabulaire / Lexique (W19–W23)",
        "Sens propre et sens figuré, expressions imagées et proverbes",
        "Distinguer le sens propre (concret) du sens figuré (abstrait/imagé) et comprendre le sens des expressions idiomatiques courantes.",
        "Explique le sens d'une expression imagée dans son contexte et produit des phrases illustrant le sens propre et le sens figuré.",
        "Dessins humoristiques illustrant le sens propre vs sens figuré d'expressions courantes (ex: avoir le cœur sur la main), et devinettes.",
        is_french=True)

add_item("l5_fr_voc_w27_w31", 27, 31, "Français", "Vocabulaire / Lexique (W27–W31)",
        "Les champs lexicaux : le travail, les métiers, la santé, la nature et le milieu urbain / rural",
        "Regrouper des mots sous un même champ lexical thématique pour enrichir la rédaction de textes descriptifs et informatifs.",
        "Constitue des réseaux de mots autour d'un thème donné et mobilise ce vocabulaire thématique dans ses écrits.",
        "Cartes heuristiques (mind maps) thématiques, brainstorming collectif et constitution de boîtes à mots pour la rédaction.",
        is_french=True)

add_item("l5_fr_voc_w33_w36", 33, 36, "Français", "Vocabulaire / Lexique (W33–W36)",
        "Les termes génériques et termes spécifiques, et les homonymes lexicaux",
        "Classer les mots en catégories hyperonymes/hyponymes (termes génériques/spécifiques) et distinguer les homonymes lexicaux (mer/mère/maire, vers/vert/verre).",
        "Utilise les termes génériques pour résumer et les termes spécifiques pour préciser une description sans ambiguïté.",
        "Jeux de classement lexical en arborescence, phrases d'homonymes lexicaux à compléter, et énigmes sémantiques.",
        is_french=True)

# 7. Production d'écrits / Rédaction (W02–W36)
add_item("l5_fr_red_w02_w05", 2, 5, "Français", "Production d\\'écrits (W02–W05)",
        "Rédiger un paragraphe narratif sur un souvenir ou un événement scolaire",
        "Produire un court texte narratif cohérent de 5 à 8 lignes en respectant la chronologie, la ponctuation et les règles d'accord.",
        "Planifie son texte (début, milieu, fin), rédige un premier jet, et l'améliore à l'aide d'une grille de relecture.",
        "Grille d'écriture guidée, rédaction sur brouillon, relecture en binôme et mise au propre sur le cahier de rédaction.",
        is_french=True)

add_item("l5_fr_red_w07_w10", 7, 10, "Français", "Production d\\'écrits (W07–W10)",
        "Rédiger un texte descriptif : portrait d'une personne ou description d'un lieu/animal",
        "Décrire un personnage (aspect physique, moral, vêtements) ou un lieu mauritanien en utilisant des adjectifs variés et des comparaisons.",
        "Organise sa description selon un ordre logique (du général au particulier ou de haut en bas) avec un vocabulaire riche.",
        "Boîte à mots descriptifs, fiches de portraits modèles, production individuelle et exposition des meilleurs textes.",
        is_french=True)

add_item("l5_fr_red_w14_w17", 14, 17, "Français", "Production d\\'écrits (W14–W17)",
        "Écrire la suite ou la fin d'un conte traditionnel mauritanien",
        "Imaginer et rédiger la suite d'un conte à partir d'un début donné, en introduisant des péripéties et un dénouement moral satisfaisant.",
        "Respecte la cohérence narrative, utilise les temps du passé (imparfait/passé composé ou passé simple) et les connecteurs temporels.",
        "Amorce de conte distribuée, schéma narratif à compléter, rédaction guidée et lecture publique des productions en classe.",
        is_french=True)

add_item("l5_fr_red_w19_w23", 19, 23, "Français", "Production d\\'écrits (W19–W23)",
        "Rédiger un texte injonctif : une recette de cuisine ou une règle de jeu / de vie",
        "Rédiger un texte prescriptif clair comprenant un titre, la liste des éléments nécessaires (ingrédients/matériel) et les étapes numérotées.",
        "Emploie l'infinitif ou l'impératif pour les verbes d'action et ordonne rigoureusement les étapes avec des indicateurs temporels.",
        "Modèles de recettes mauritaniennes traditionnelles, rédaction collective puis individuelle d'une notice, validation par mise en pratique.",
        is_french=True)

add_item("l5_fr_red_w27_w31", 27, 31, "Français", "Production d\\'écrits (W27–W31)",
        "Rédiger une lettre amicale ou une carte postale à un correspondant",
        "Maîtriser la mise en page d'une lettre amicale (lieu et date, formule d'appel, corps de la lettre, formule de politesse et signature).",
        "Communique des nouvelles personnelles de manière claire et courtoise en respectant la structure conventionnelle de la correspondance.",
        "Silhouette de lettre amicale, rédaction d'une lettre à un ami d'une autre ville de Mauritanie, enveloppe et adresse postale.",
        is_french=True)

add_item("l5_fr_red_w33_w36", 33, 36, "Français", "Production d\\'écrits (W33–W36)",
        "Rédiger un court dialogue inséré dans un récit ou une scène de théâtre",
        "Écrire un échange de répliques entre deux personnages en utilisant la ponctuation correcte du dialogue (guillemets, tirets) et des verbes de parole.",
        "Varie les verbes déclaratifs (dit, répondit, s'exclama, demanda) et caractérise les émotions des personnages à travers leurs répliques.",
        "Mise en scène préalable par le jeu de rôle, écriture du dialogue par paires, relecture critique et lecture théâtralisée.",
        is_french=True)

# ----------------- التربية البدنية والفنية (LEVEL 5) -----------------

# التربية البدنية والرياضية (EPS)
add_item("l5_eps_w02_w05", 2, 5, "التربية البدنية والرياضية", "ألعاب القوى والجري السريع والتتابع (W02–W05)",
        "تقنيات الجري السريع والانطلاق الصحيح وسباق التتابع وتمرير العصا",
        "التحكم في وضعيات الانطلاق السريع (على خط الانطلاق) والتسارع والمحافظة على المسار وتمرير عصا التتابع ضمن المنطقة المحددة.",
        "ينفذ الانطلاق السريع والاستجابة الفورية للإشارة، ويتعاون مع زميله لتسليم وتسلّم عصا التتابع دون إسقاطها.",
        "تمارين إحماء، سباقات سرعة 30م و50م، سباقات تتابع في مضمار المدرسة مع التركيز على الروح الرياضية.")

add_item("l5_eps_w07_w10", 7, 10, "التربية البدنية والرياضية", "ألعاب القوى: الوثب والرمي (W07–W10)",
        "تقنيات الوثب الطويل والوثب العالي ورمي الجلة/الكرة الحديدية الخفيفة",
        "إتقان مراحل الوثب (الاقتراب، الارتقاء، الطيران، الهبوط السليم) ومراحل الرمي (الوقوف، الاندفاع، إطلاق الأداة بزاوية مناسبة).",
        "يحقق وثبة متوازنة مع هبوط آمن على القدمين معاً، ويرمي الأداة بطريقة صحيحة تضمن السلامة وتحقيق المسافة المطلوبة.",
        "حفرة الوثب أو بساط آمن، كرات رمي تعليمية مناسبة لسن 5 أساسي، قياس المسافات المسجلة وتشجيع المنافسة الشريفة.")

add_item("l5_eps_w14_w17", 14, 17, "التربية البدنية والرياضية", "الجمباز والحركات الإيقاعية والتوازن (W14–W17)",
        "الحركات الأرضية الجمبازية: الدحرجة الأمامية والخلفية والوقوف على القدم الواحدة والاتزان",
        "تنمية المرونة والرشاقة والتوازن الحركي من خلال تنفيذ الدحرجة الأمامية المستقيمة والدحرجة الخلفية وحركات التوازن على عارضة منخفضة.",
        "ينفذ السلسلة الحركية الجمبازية بسلاسة وتناسق مع التحكم في وضعية الظهر والرقبة لتفادي الإصابات.",
        "أفرشة جمباز آمنة، محطات تدريبية متدرجة الصعوبة، مساعدة المعلم في تأمين الحركة، وتقييم الوضعيات الصحيحة.")

add_item("l5_eps_w19_w23", 19, 23, "التربية البدنية والرياضية", "الألعاب الجماعية والتعاونية: كرة اليد (W19–W23)",
        "المهارات الأساسية في كرة اليد: التمرير، الاستقبال، التنطيط والتصويب على المرمى وقواعد اللعب النظيف",
        "إتقان التمريرات المتنوعة (صدرية، مرتدة، كرباجية) والتنطيط القانوني (3 خطوات) والتعاون الهجومي والدفاعي ضمن الفريق.",
        "يطبق قواعد كرة اليد الأساسية، ويتعاون بفعالية مع زملائه في التمرير والتمركز، ويحترم قرارات الحكم والخصم.",
        "مباريات مصغرة (3 ضد 3 أو 5 ضد 5)، تدريبات تمرير وتصويب على أهداف محددة، وتطبيق أدوار التحكيم بين التلاميذ.")

add_item("l5_eps_w27_w31", 27, 31, "التربية البدنية والرياضية", "الألعاب الجماعية: كرة القدم المصغرة وقواعد اللعب (W27–W31)",
        "المهارات الفنية في كرة القدم المصغرة: التحكم بالكرة، التمرير القصير، الترويض والتسديد الجماعي",
        "تطوير المهارات التكتيكية والحركية الجماعية، التموضع السليم في الملعب، احترام الزملاء والمنافسين وتجنب الأخطاء والسلوك غير الرياضي.",
        "يتحكم في الكرة بالقدمين، يمرر لزملائه بدقة، يشارك في الدفاع والهجوم بانسجام، ويلتزم بالروح الرياضية العالية.",
        "مباريات تدريبية في ساحة المدرسة، تمارين المربعات للتمرير السريع، ومنافسات دوري الفصول الداخلية.")

add_item("l5_eps_w33_w36", 33, 36, "التربية البدنية والرياضية", "الألعاب التقليدية الموريتانية والأنشطة الترفيهية (W33–W36)",
        "الألعاب الشعبية والتراثية الموريتانية (الظمبة، كرور، كش، صيد السمك) وأنشطة الاسترخاء والمحافظة على الصحة",
        "إحياء الألعاب التقليدية والتراثية الموريتانية التي تنمي الذكاء الحركي وسرعة البديهة والتعاون الجماعي والاعتزاز بالموروث الوطني.",
        "يمارس الألعاب التقليدية الموريتانية وفق قواعدها الأصيلة، ويدرك أهمية النشاط البدني اليومي في تعزيز الصحة البدنية والنفسية.",
        "ميدان ألعاب تراثية بساحة المدرسة، تنظيم مهرجان مصغر للألعاب الشعبية، وحصص استرخاء وتنفس ختامية.")

# التربية الفنية والنشيد (Dessin & Chant)
add_item("l5_art_w02_w05", 2, 5, "التربية الفنية والنشيد", "الرسم والألوان: الطبيعة والبيئة الموريتانية (W02–W05)",
        "تقنيات الرسم والتلوين: المنظور البسيط، تدرج الألوان الدافئة والباردة، ورسم معالم البيئة الموريتانية",
        "استخدام الألوان المائية والخشبية للتعبير الفني عن مناظر البيئة الموريتانية (الكثبان، الواحات، شاطئ المحيط) مع مراعاة الظل والضوء.",
        "يمزج الألوان بمهارة للحصول على درجات لونية متنوعة، ويوزع عناصر اللوحة بتوازن وجمالية على مساحة الورقة.",
        "كراسة الرسم، أقلام تلوين وصباغة مائية، نماذج لوحات طبيعية موريتانية، ومعرض للرسومات المتميزة بالقسم.")

add_item("l5_art_w07_w10", 7, 10, "التربية الفنية والنشيد", "الخط العربي والزخرفة الإسلامية (W07–W10)",
        "قواعد الخط العربي (خط النسخ وخط الرقعة) وعناصر الزخرفة الهندسية والنباتية الإسلامية",
        "إتقان قواعد كتابة الحروف بخطي النسخ والرقعة مع ضبط المسافات وتناسق الحروف، وتصميم زخارف هندسية متناظرة ومستوحاة من التراث.",
        "يكتب نصوصاً وحكماً بخط نسخ جميل ومقروء، ويرسم أشكالاً زخرفية دقيقة باستخدام المسطرة والفرجار.",
        "كراسة تحسين الخط، أقلام الخط العربي المشطوفة، شبكات هندسية للزخرفة، وتصميم بطاقات تهنئة مزخرفة.")

add_item("l5_art_w14_w17", 14, 17, "التربية الفنية والنشيد", "الأناشيد الوطنية والتربوية: النشيد الوطني الموريتاني (W14–W17)",
        "حفظ وأداء النشيد الوطني الموريتاني وأناشيد حب الوطن والعلم بإيقاع صحيح وأداء جماعي منضبط",
        "التحكم في مخارج الحروف والنبر الصوتي والتنفس السليم أثناء الأداء الفردي والجماعي للأناشيد الوطنية والتربوية الهادفة.",
        "يردد النشيد الوطني بدقة لحنية ووقار، ويحفظ الأناشيد المقررة ويتفاعل مع معانيها الوطنية والإنسانية النبيلة.",
        "تسجيلات صوتية نموذجية، تدريب على النطق الموسيقي والإيقاعي، كورال مدرسي لأداء الأناشيد في تحية العلم.")

add_item("l5_art_w19_w23", 19, 23, "التربية الفنية والنشيد", "الأشغال اليدوية وتدوير المواد (W19–W23)",
        "الأعمال اليدوية والتشكيل المجسم باستخدام الورق المقوى والمواد القابلة للتدوير من البيئة المحلية",
        "تنمية الابتكار والمهارات اليدوية من خلال قص وتشكيل ولصق خامات بسيطة (علب كرتون، أغطية، قماش) لإنتاج مجسمات وأدوات تعليمية نافعة.",
        "يصنع مجسمات وأشكالاً فنية مجسمة بأمان وإتقان، ويدرك قيمة إعادة التدوير في حماية البيئة والتوفير.",
        "مقصات آمنة، غراء، ورق ملون وخامات تدوير، ورشة صناعة مجسمات بيئية ومدرسية، وعرض المنتجات في ركن الوسائل.")

add_item("l5_art_w27_w31", 27, 31, "التربية الفنية والنشيد", "الرسم التعبيري والتراث الموريتاني (W27–W31)",
        "رسم مظاهر الحياة التقليدية والصناعات التراثية الموريتانية (الخيمة، المحظرة، الصناعة التقليدية، الزي الوطني)",
        "التعبير الفني عن ثراء الثقافة الموريتانية وتنوعها والاعتزاز بالهوية الوطنية من خلال لوحات تعبيرية أصيلة.",
        "يرسم موضوعاً متكاملاً حول التراث الموريتاني بعناصره المميزة وشخصياته وزخارفه التراثية بألوان متناسقة.",
        "معرض صور فوتوغرافية للتراث الموريتاني، الرسم بالألوان الزيتية أو الشمعية، ومسابقة أفضل لوحة تراثية.")

add_item("l5_art_w33_w36", 33, 36, "التربية الفنية والنشيد", "المسرح المدرسي والأناشيد الختامية (W33–W36)",
        "تقنيات التمثيل والإلقاء المسرحي للأطفال وأناشيد النجاح وحفل ختام السنة الدراسية",
        "تنمية التعبير الجسدي واللفظي والجرأة الأدبية من خلال تقمص أدوار مسرحية تربوية وترديد أناشيد النجاح والاحتفاء بالتميز المدرسي.",
        "يؤدي دوره المسرحي بثقة ووضوح صوتي، ويشارك بانسجام في الكورال الختامي للسنة الدراسية.",
        "نصوص مسرحية قصيرة هادفة، ملابس وأكسسوارات مسرحية بسيطة، تدريبات على الأداء، وحفل مدرسي ختامي.")

'''

with open("generate_level5.py", "a", encoding="utf-8") as f:
    f.write(code_to_append)

print("Appended remaining Level 5 subjects successfully.")
