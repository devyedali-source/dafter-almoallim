package com.example.ui

import android.content.Context
import android.util.Base64

/**
 * تصاميم رأسيات اللوائح وإطارات الكشوف وشهادات التفوق.
 * الزخرفة تُرسم بالـ CSS/SVG خلف النص فقط، فلا يتغير أي نص أو بيان.
 * القيمة 0 = الوضع الأصلي (التلقائي).
 * ألوان خفيفة جداً وخطوط رفيعة ليبقى النص واضحاً في الطباعة بالأبيض والأسود.
 */
object PrintDesign {
    private const val PREFS = "app_settings"
    private const val K_HEADER = "print_header_design_v1"
    private const val K_CARD = "print_card_design_v1"
    private const val K_CERT = "print_cert_design_v1"

    @Volatile var header: Int = 0
    @Volatile var card: Int = 0
    @Volatile var cert: Int = 1

    val headerNames = listOf(
        "الوضع الأصلي (التلقائي)",
        "إطار أزرق بزوايا مزخرفة",
        "زوايا زرقاء مائلة",
        "شريط أخضر بفواصل",
        "أمواج زرقاء ومعين",
        "إطار ذهبي مع أغصان"
    )
    val cardNames = listOf(
        "الوضع الأصلي (التلقائي)",
        "موج أزرق خفيف",
        "إطار ذهبي مزخرف",
        "موج أزرق مع كتاب",
        "أخضر خفيف مع قبعة وكتب",
        "إطار بنفسجي خفيف",
        "أخضر وذهبي مع أغصان"
    )
    val certNames = listOf(
        "ذهبي كلاسيكي",
        "أخضر وطني",
        "أزرق عصري",
        "بنفسجي أنيق",
        "بسيط للطباعة بالأبيض والأسود"
    )

    fun load(context: Context) {
        try {
            val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            header = p.getInt(K_HEADER, 0).coerceIn(0, headerNames.lastIndex)
            card = p.getInt(K_CARD, 0).coerceIn(0, cardNames.lastIndex)
            cert = p.getInt(K_CERT, 1).coerceIn(1, certNames.size)
        } catch (_: Exception) {
        }
    }

    fun save(context: Context, header: Int? = null, card: Int? = null, cert: Int? = null) {
        header?.let { this.header = it }
        card?.let { this.card = it }
        cert?.let { this.cert = it }
        try {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putInt(K_HEADER, this.header).putInt(K_CARD, this.card).putInt(K_CERT, this.cert).apply()
        } catch (_: Exception) {
        }
    }

    // ---------- أدوات الرسم ----------
    private const val B = "#185FA5"
    private const val BL = "#B5D4F4"
    private const val BX = "#E6F1FB"
    private const val G = "#3B6D11"
    private const val GL = "#C0DD97"
    private const val GX = "#EAF3DE"
    private const val Y = "#BA7517"
    private const val V = "#534AB7"

    private fun uri(svg: String): String {
        val full = if (svg.contains("xmlns")) svg else svg.replaceFirst("<svg", "<svg xmlns=\"http://www.w3.org/2000/svg\"")
        val b64 = Base64.encodeToString(full.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return "url(\"data:image/svg+xml;base64,$b64\")"
    }

    /** زاوية مزخرفة 28×28: corner 0=أعلى يسار 1=أعلى يمين 2=أسفل يسار 3=أسفل يمين */
    private fun cornerSvg(c: String, w: String, corner: Int, size: Int = 28): String {
        val tf = when (corner) {
            0 -> ""
            1 -> "translate(28,0) scale(-1,1)"
            2 -> "translate(0,28) scale(1,-1)"
            else -> "translate(28,28) scale(-1,-1)"
        }
        return "<svg viewBox=\"0 0 28 28\" width=\"$size\" height=\"$size\"><g transform=\"$tf\" fill=\"none\" stroke=\"$c\" stroke-width=\"$w\">" +
            "<path d=\"M1,27 V9 Q1,1 9,1 H27\"/><path d=\"M6,27 V13 Q6,6 13,6 H27\" stroke-width=\"0.7\"/><circle cx=\"12\" cy=\"12\" r=\"2.4\"/></g></svg>"
    }

    private fun fourCorners(c: String, w: String, size: Int, inset: Int): String {
        val s = "${size}px ${size}px"
        return listOf(
            "${uri(cornerSvg(c, w, 0, size))} left ${inset}px top ${inset}px/$s no-repeat",
            "${uri(cornerSvg(c, w, 1, size))} right ${inset}px top ${inset}px/$s no-repeat",
            "${uri(cornerSvg(c, w, 2, size))} left ${inset}px bottom ${inset}px/$s no-repeat",
            "${uri(cornerSvg(c, w, 3, size))} right ${inset}px bottom ${inset}px/$s no-repeat"
        ).joinToString(",")
    }

    private fun leafArc(cx: Double, cy: Double, r: Double, a0: Double, a1: Double, n: Int, col: String, flip: Boolean): String {
        val sb = StringBuilder()
        for (i in 0 until n) {
            val a = Math.toRadians(a0 + (a1 - a0) * i / (n - 1))
            val x = cx + r * Math.cos(a)
            val y = cy + r * Math.sin(a)
            val rot = Math.toDegrees(a) + if (flip) -90 else 90
            sb.append("<ellipse cx=\"%.1f\" cy=\"%.1f\" rx=\"2.2\" ry=\"5\" transform=\"rotate(%.0f %.1f %.1f)\" fill=\"$col\" fill-opacity=\"0.3\" stroke=\"$col\" stroke-width=\"0.6\"/>".format(java.util.Locale.US, x, y, rot, x, y))
        }
        return sb.toString()
    }

    private fun laurelRing(col: String): String =
        "<svg viewBox=\"0 0 90 90\" width=\"90\" height=\"90\">" +
            leafArc(45.0, 45.0, 36.0, 100.0, 250.0, 8, col, false) +
            leafArc(45.0, 45.0, 36.0, -70.0, 80.0, 8, col, true) + "</svg>"

    private fun laurelSide(col: String, left: Boolean): String {
        val a0 = if (left) -70.0 else 110.0
        val a1 = if (left) 70.0 else 250.0
        val cx = if (left) 0.0 else 22.0
        return "<svg viewBox=\"0 0 22 80\" width=\"22\" height=\"80\">" +
            leafArc(cx, 40.0, 34.0, a0, a1, 8, col, !left) + "</svg>"
    }

    // ---------- رأسيات اللوائح (تُطبَّق على .header-section) ----------
    fun listCss(design: Int = header): String {
        if (design <= 0) return ""
        val base = ".header-section{position:relative;padding:8px 14px 14px;margin-bottom:6px;"
        val css = when (design) {
            1 -> "$base border:1.6px solid $B;box-shadow:inset 0 0 0 4px #fff,inset 0 0 0 4.6px $B;background:${fourCorners(B, "1.4", 28, 2)};}"
            2 -> {
                val tl = uri("<svg viewBox=\"0 0 84 58\"><path d=\"M0,0 H84 L0,58Z\" fill=\"$BX\" stroke=\"$BL\"/><path d=\"M0,0 H48 L0,30Z\" fill=\"$BL\" fill-opacity=\"0.55\"/></svg>")
                val tr = uri("<svg viewBox=\"0 0 64 48\"><path d=\"M64,0 H0 L64,48Z\" fill=\"$BX\" stroke=\"$BL\"/></svg>")
                val bl = uri("<svg viewBox=\"0 0 70 36\"><path d=\"M0,36 H70 L0,0Z\" fill=\"$BX\" stroke=\"$BL\"/></svg>")
                val br = uri("<svg viewBox=\"0 0 70 32\"><path d=\"M70,32 H0 L70,0Z\" fill=\"$BX\" stroke=\"$BL\"/></svg>")
                "$base border-bottom:1.3px solid $B;background:$tl left top/84px 58px no-repeat,$tr right top/64px 48px no-repeat,$bl left bottom/70px 36px no-repeat,$br right bottom/70px 32px no-repeat;}"
            }
            3 -> "$base padding-bottom:20px;background:linear-gradient($Y,$Y) left bottom 12px/100% 1.8px no-repeat,linear-gradient($GL,$GL) left bottom/100% 12px no-repeat,linear-gradient($G,$G) left bottom/100% 1.2px no-repeat,linear-gradient(#F6FAEF,#F6FAEF);}" +
                ".header-section .header-column.center{border-left:1px solid $G;border-right:1px solid $G;}"
            4 -> {
                val tl = uri("<svg viewBox=\"0 0 150 52\" preserveAspectRatio=\"none\"><path d=\"M0,0 H150 C100,8 40,20 0,52Z\" fill=\"$BX\" stroke=\"$BL\"/><path d=\"M0,0 H110 C70,8 28,16 0,36Z\" fill=\"$BL\" fill-opacity=\"0.55\"/></svg>")
                val br = uri("<svg viewBox=\"0 0 150 52\" preserveAspectRatio=\"none\"><path d=\"M150,52 H0 C50,44 110,32 150,0Z\" fill=\"$BX\" stroke=\"$BL\"/><path d=\"M150,52 H40 C80,46 120,38 150,20Z\" fill=\"$BL\" fill-opacity=\"0.55\"/></svg>")
                val dm = uri("<svg viewBox=\"0 0 180 12\"><line x1=\"0\" y1=\"6\" x2=\"80\" y2=\"6\" stroke=\"$B\"/><line x1=\"100\" y1=\"6\" x2=\"180\" y2=\"6\" stroke=\"$B\"/><rect x=\"86\" y=\"2\" width=\"8\" height=\"8\" transform=\"rotate(45 90 6)\" fill=\"#fff\" stroke=\"$B\"/></svg>")
                "$base padding-bottom:18px;background:$tl left top/150px 52px no-repeat,$br right bottom/150px 52px no-repeat,$dm center bottom 3px/180px 12px no-repeat;}"
            }
            else -> {
                val lau = uri(laurelRing(Y))
                "$base border:1.3px solid $Y;box-shadow:inset 0 0 0 4px #fff,inset 0 0 0 4.6px $Y;background:${fourCorners(Y, "1.3", 28, 2)},linear-gradient(#FFFDF6,#FFFDF6);}" +
                    ".header-section .header-column.center{background:$lau center top/84px 84px no-repeat;}"
            }
        }
        return "<style>$css</style>"
    }

    // ---------- إطارات الكشوف (تُطبَّق على بطاقات الكشوف) ----------
    fun cardCss(design: Int = card): String {
        if (design <= 0) return ""
        val sel = ".report-card-container,.averages-card,.three-term-card"
        val css = when (design) {
            1 -> {
                val tl = uri("<svg viewBox=\"0 0 130 70\" preserveAspectRatio=\"none\"><path d=\"M0,0 H130 C80,8 30,24 0,70Z\" fill=\"$BX\" stroke=\"$BL\"/><path d=\"M0,0 H84 C50,8 20,20 0,44Z\" fill=\"$BL\" fill-opacity=\"0.5\"/></svg>")
                val br = uri("<svg viewBox=\"0 0 140 60\" preserveAspectRatio=\"none\"><path d=\"M140,60 H0 C60,52 120,36 140,0Z\" fill=\"$BX\" stroke=\"$BL\"/><path d=\"M140,60 H50 C90,54 125,44 140,28Z\" fill=\"$BL\" fill-opacity=\"0.5\"/></svg>")
                "$sel{border:1px solid $BL !important;border-radius:0 !important;background:$tl left top/130px 70px no-repeat,$br right bottom/140px 60px no-repeat,#fff !important;}"
            }
            2 -> "$sel{border:1.6px solid $Y !important;border-radius:0 !important;box-shadow:inset 0 0 0 4px #fff,inset 0 0 0 4.6px $Y !important;background:${fourCorners(Y, "1.6", 34, 2)},#fff !important;}"
            3 -> {
                val top = uri("<svg viewBox=\"0 0 200 26\" preserveAspectRatio=\"none\"><path d=\"M0,0 H200 V10 C140,26 60,26 0,8Z\" fill=\"$BX\" stroke=\"$BL\"/></svg>")
                val bot = uri("<svg viewBox=\"0 0 200 26\" preserveAspectRatio=\"none\"><path d=\"M0,26 H200 V16 C140,0 60,0 0,18Z\" fill=\"$BX\" stroke=\"$BL\"/></svg>")
                val book = uri("<svg viewBox=\"0 0 44 28\"><g fill=\"#fff\" stroke=\"$B\" stroke-width=\"1.2\"><path d=\"M2,3 C10,-1 18,1 22,5 V25 C18,21 10,19 2,25Z\"/><path d=\"M42,3 C34,-1 26,1 22,5 V25 C26,21 34,19 42,25Z\"/></g></svg>")
                "$sel{border:1px solid $BL !important;border-radius:0 !important;background:$book left 6px top 4px/44px 28px no-repeat,$top left top/100% 26px no-repeat,$bot left bottom/100% 26px no-repeat,#fff !important;}"
            }
            4 -> {
                val bot = uri("<svg viewBox=\"0 0 200 30\" preserveAspectRatio=\"none\"><path d=\"M0,30 H200 V12 C140,2 60,6 0,22Z\" fill=\"$GX\" stroke=\"$G\" stroke-width=\"0.8\"/></svg>")
                val tl = uri("<svg viewBox=\"0 0 110 60\" preserveAspectRatio=\"none\"><path d=\"M0,0 H110 C60,8 20,24 0,60Z\" fill=\"#F6FAEF\" stroke=\"$GL\"/></svg>")
                val cap = uri("<svg viewBox=\"0 0 60 56\"><g fill=\"#fff\" stroke=\"#444\" stroke-width=\"1.1\"><path d=\"M4,12 L28,3 L52,12 L28,21Z\"/><path d=\"M14,17 V28 C22,34 34,34 42,28 V17\"/><rect x=\"6\" y=\"36\" width=\"30\" height=\"6\"/><rect x=\"9\" y=\"42\" width=\"30\" height=\"6\"/></g><line x1=\"52\" y1=\"12\" x2=\"52\" y2=\"26\" stroke=\"$Y\" stroke-width=\"1.5\"/></svg>")
                "$sel{border:1px solid $GL !important;border-radius:0 !important;background:$cap left 4px top 3px/50px 47px no-repeat,$tl left top/110px 60px no-repeat,$bot left bottom/100% 30px no-repeat,#fff !important;}"
            }
            5 -> {
                val tl = uri("<svg viewBox=\"0 0 40 40\"><path d=\"M1,38 V13 Q1,1 13,1 H38\" fill=\"none\" stroke=\"$V\" stroke-width=\"2.4\"/></svg>")
                val br = uri("<svg viewBox=\"0 0 40 40\"><path d=\"M39,2 V27 Q39,39 27,39 H2\" fill=\"none\" stroke=\"$V\" stroke-width=\"2.4\"/></svg>")
                "$sel{border:1.8px solid #7F77DD !important;border-radius:12px !important;box-shadow:inset 0 0 0 5px #fff,inset 0 0 0 5.7px #AFA9EC !important;background:$tl left 2px top 2px/40px 40px no-repeat,$br right 2px bottom 2px/40px 40px no-repeat,#fff !important;}"
            }
            else -> {
                val ll = uri(laurelSide(G, true))
                val lr = uri(laurelSide(G, false))
                "$sel{border:1.6px solid $G !important;border-radius:0 !important;box-shadow:inset 0 0 0 4px #fff,inset 0 0 0 4.6px $Y !important;background:$ll left 2px center/22px 80px no-repeat,$lr right 2px center/22px 80px no-repeat,#fff !important;}"
            }
        }
        return "<style>$css</style>"
    }

    // ---------- شهادات التفوق ----------
    /** يرجع CSS الصفحة (.cert) حسب التصميم 1..5 */
    fun certificateCss(design: Int): String {
        val base = ".cert{position:relative;width:100%;height:190mm;box-sizing:border-box;page-break-after:always;break-after:page;page-break-inside:avoid;break-inside:avoid;overflow:hidden;text-align:center;color:#000;padding:12mm 16mm;}" +
            ".cert:last-child{page-break-after:auto;break-after:auto;}" +
            ".cert .top{display:flex;justify-content:space-between;align-items:flex-start;font-size:11px;font-weight:bold;line-height:1.6;}" +
            ".cert .top .c{flex:1}.cert .top .r{text-align:right}.cert .top .l{text-align:left}" +
            ".cert h1{font-size:34px;margin:6px 0 2px;font-weight:900;}" +
            ".cert .sub{font-size:14px;margin:0 0 10px;}" +
            ".cert .give{font-size:16px;margin:10px 0 4px;}" +
            ".cert .nm{font-size:30px;font-weight:900;margin:4px auto 8px;display:inline-block;padding:2px 28px;border-bottom:1.5px solid #000;}" +
            ".cert .txt{font-size:16px;line-height:2;max-width:200mm;margin:0 auto;}" +
            ".cert .rk{font-weight:900;font-size:20px;}" +
            ".cert .sigs{position:absolute;left:16mm;right:16mm;bottom:12mm;display:flex;justify-content:space-between;font-size:13px;font-weight:bold;}" +
            ".cert .sigs div{min-width:55mm;text-align:center;}" +
            ".cert img{max-height:58px;}"
        val frame = when (design) {
            1 -> ".cert{border:2px solid $Y;box-shadow:inset 0 0 0 5px #fff,inset 0 0 0 5.8px $Y;background:${fourCorners(Y, "1.8", 44, 3)},#FFFDF6;}.cert h1{color:#7a4a08}"
            2 -> ".cert{border:2px solid $G;box-shadow:inset 0 0 0 5px #fff,inset 0 0 0 5.8px $Y;background:${uri("<svg viewBox=\"0 0 200 14\" preserveAspectRatio=\"none\"><rect width=\"200\" height=\"14\" fill=\"$GX\"/><rect y=\"11\" width=\"200\" height=\"1.6\" fill=\"$Y\"/></svg>")} left bottom/100% 14mm no-repeat,${uri("<svg viewBox=\"0 0 200 14\" preserveAspectRatio=\"none\"><rect width=\"200\" height=\"14\" fill=\"$GX\"/><rect y=\"1.4\" width=\"200\" height=\"1.6\" fill=\"$Y\"/></svg>")} left top/100% 14mm no-repeat,#fff;}.cert h1{color:$G}"
            3 -> ".cert{border:1px solid $BL;background:${uri("<svg viewBox=\"0 0 200 60\" preserveAspectRatio=\"none\"><path d=\"M0,0 H200 C130,8 50,24 0,60Z\" fill=\"$BX\" stroke=\"$BL\"/></svg>")} left top/60% 60mm no-repeat,${uri("<svg viewBox=\"0 0 200 60\" preserveAspectRatio=\"none\"><path d=\"M200,60 H0 C60,52 140,36 200,0Z\" fill=\"$BX\" stroke=\"$BL\"/></svg>")} right bottom/60% 60mm no-repeat,#fff;}.cert h1{color:$B}"
            4 -> ".cert{border:2px solid #7F77DD;border-radius:14px;box-shadow:inset 0 0 0 6px #fff,inset 0 0 0 6.8px #AFA9EC;background:${uri("<svg viewBox=\"0 0 60 60\"><path d=\"M1,58 V16 Q1,1 16,1 H58\" fill=\"none\" stroke=\"$V\" stroke-width=\"2.6\"/></svg>")} left 4px top 4px/60px 60px no-repeat,${uri("<svg viewBox=\"0 0 60 60\"><path d=\"M59,2 V44 Q59,59 44,59 H2\" fill=\"none\" stroke=\"$V\" stroke-width=\"2.6\"/></svg>")} right 4px bottom 4px/60px 60px no-repeat,#fff;}.cert h1{color:$V}"
            else -> ".cert{border:2.5px solid #000;box-shadow:inset 0 0 0 5px #fff,inset 0 0 0 6px #000;background:#fff;}.cert h1{letter-spacing:1px}"
        }
        return "<style>$base$frame</style>"
    }
}
