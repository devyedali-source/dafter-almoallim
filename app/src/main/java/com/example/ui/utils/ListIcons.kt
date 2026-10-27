package com.example.ui.utils

/** رسوم صغيرة (SVG) تميّز اللوائح عن بعضها: الكناسة والإنشاد. تظهر جيداً بالأبيض والأسود وبالألوان. */
object ListIcons {
    private const val WRAP_START = "<div style=\"text-align:center;margin:2px 0 6px 0;\">"
    private const val WRAP_END = "</div>"

    /** مكنسة ودلو ولمعان */
    val cleaning: String = WRAP_START + """
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130 72" width="108" height="60" fill="none" stroke="#222" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
  <line x1="16" y1="8" x2="60" y2="48" stroke-width="4.5"/>
  <path d="M52 42 L80 50 L70 68 L44 58 Z" fill="#f2c94c"/>
  <path d="M52 50 L60 62 M59 47 L67 60 M66 47 L73 58" stroke-width="1.8"/>
  <path d="M92 34 L118 34 L113 66 L97 66 Z" fill="#cfe8ff"/>
  <ellipse cx="105" cy="34" rx="13" ry="4" fill="#eef7ff"/>
  <path d="M94 34 Q105 12 116 34" stroke-width="2"/>
  <path d="M100 46 q5 4 10 0" stroke-width="1.6"/>
  <path d="M30 44 l2 5 5 2 -5 2 -2 5 -2 -5 -5 -2 5 -2z" fill="#fff4b3" stroke-width="1.6"/>
  <path d="M82 14 l1.6 4 4 1.6 -4 1.6 -1.6 4 -1.6 -4 -4 -1.6 4 -1.6z" fill="#fff4b3" stroke-width="1.4"/>
</svg>""" + WRAP_END

    /** ميكروفون ونغمات موسيقية للإنشاد */
    val hymn: String = WRAP_START + """
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130 72" width="108" height="60" fill="none" stroke="#222" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
  <rect x="53" y="6" width="24" height="36" rx="12" fill="#e9e9e9"/>
  <path d="M53 18 H77 M53 26 H77 M53 34 H77" stroke-width="1.5"/>
  <path d="M44 30 Q44 54 65 54 Q86 54 86 30"/>
  <line x1="65" y1="54" x2="65" y2="66"/>
  <line x1="52" y1="67" x2="78" y2="67"/>
  <ellipse cx="22" cy="50" rx="7" ry="5" fill="#222" transform="rotate(-20 22 50)"/>
  <path d="M28 48 V18 Q40 22 36 34"/>
  <ellipse cx="104" cy="42" rx="7" ry="5" fill="#222" transform="rotate(-20 104 42)"/>
  <path d="M110 40 V12 Q122 16 118 28"/>
  <path d="M16 18 q4 -4 8 0 M8 28 q4 -4 8 0" stroke-width="1.6"/>
</svg>""" + WRAP_END
}
