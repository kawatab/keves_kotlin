/*
 * ScmChar.kt
 *
 * Copyright 2021 Yasuhiro Yamakawa <kawatab@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.kawatab.keveskotlin.objects

class ScmChar private constructor(private val charArray: CharArray) : ScmObject() {
    constructor(value: Char) : this(charArrayOf(value))
    constructor(high: Char, low: Char) : this(charArrayOf(high, low))
    constructor(value: Int) : this(
        if (value < 0x10000) charArrayOf(value.toChar())
        else charArrayOf(
            ((value - 0x10000) / 0x400 + Char.MIN_HIGH_SURROGATE.toInt()).toChar(),
            ((value - 0x10000) % 0x400 + Char.MIN_LOW_SURROGATE.toInt()).toChar()
        )
    )

    // val char: Char get() = charArray[0]

    override fun toStringForWrite(): String = "#\\${toStringForDisplay()}"

    override fun toStringForDisplay(): String =
        when (charArray[0]) {
            '\u0007' -> "alarm"
            '\u0008' -> "backspace"
            '\u007F' -> "delete"
            '\u001B' -> "escape"
            '\u000A' -> "newline" // the linefeed character
            '\u0000' -> "null" // the null character
            '\u000D' -> "return" // the return character
            ' ' -> "space" // the preferred way to write a space
            '\u0009' -> "tab" // the tab character
            else -> toString()
        }

    override fun toString(): String = String(charArray)
    override fun eqvQ(other: ScmObject?): Boolean = other is ScmChar && this.toUtf32() == other.toUtf32()
    override fun equalQ(other: ScmObject?): Boolean = eqvQ(other)

    fun toUtf32(): Int =
        if (charArray.size == 1) charArray[0].toInt()
        else 0x10000 + (charArray[0].toInt() - Char.MIN_HIGH_SURROGATE.toInt()) * 0x400 +
                (charArray[1].toInt() - Char.MIN_LOW_SURROGATE.toInt())

    fun isAlphabetic(): Boolean = charArray[0].isLetter()
    fun isNumeric(): Boolean = digitToInt() >= 0
    fun isWhitespace(): Boolean = charArray[0].isWhitespace()
    fun isUpperCase(): Boolean = charArray[0].isUpperCase()
    fun isLowerCase(): Boolean = charArray[0].isLowerCase()

    fun toUpperCase(): Int = toString().toUpperCase().codePointAt(0)
    fun toLowerCase(): Int = toString().toLowerCase().codePointAt(0)
    fun toFoldCase(): Int = toString().toLowerCase().codePointAt(0)

    fun digitToInt(): Int =
        when (toUtf32()) {
            in 0x0030..0x0039 -> toUtf32() - 0x0030 // DIGIT
            in 0x0660..0x0669 -> toUtf32() - 0x0660 // ARABIC-INDIC DIGIT
            in 0x06F0..0x06F9 -> toUtf32() - 0x06F0 // EXTENDED ARABIC-INDIC DIGIT
            in 0x07C0..0x07C9 -> toUtf32() - 0x07C0 // NKO DIGIT
            in 0x0966..0x096F -> toUtf32() - 0x0966 // DEVANAGARI DIGIT
            in 0x09E6..0x09EF -> toUtf32() - 0x09E6 // BENGALI DIGIT
            in 0x0A66..0x0A6F -> toUtf32() - 0x0A66 // GURMUKHI DIGIT
            in 0x0AE6..0x0AEF -> toUtf32() - 0x0AE6 // GUJARATI DIGIT
            in 0x0B66..0x0B6F -> toUtf32() - 0x0B66 // ORIYA DIGIT
            in 0x0BE6..0x0BEF -> toUtf32() - 0x0BE6 // TAMIL DIGIT
            in 0x0C66..0x0C6F -> toUtf32() - 0x0C66 // TELUGU DIGIT
            in 0x0CE6..0x0CEF -> toUtf32() - 0x0CE6 // KANNADA DIGIT
            in 0x0D66..0x0D6F -> toUtf32() - 0x0D66 // MALAYALAM DIGIT
            in 0x0DE6..0x0DEF -> toUtf32() - 0x0DE6 // SINHALA LITH DIGIT
            in 0x0E50..0x0E59 -> toUtf32() - 0x0E50 // THAI DIGIT
            in 0x0ED0..0x0ED9 -> toUtf32() - 0x0ED0 // LAO DIGIT
            in 0x0F20..0x0F29 -> toUtf32() - 0x0F20 // TIBETAN DIGIT
            in 0x1040..0x1049 -> toUtf32() - 0x1040 // MYANMAR DIGIT
            in 0x1090..0x1099 -> toUtf32() - 0x1090 // MYANMAR SHAN DIGIT
            in 0x17E0..0x17E9 -> toUtf32() - 0x17E0 // KHMER DIGIT
            in 0x1810..0x1819 -> toUtf32() - 0x1810 // MONGOLIAN DIGIT
            in 0x1946..0x194F -> toUtf32() - 0x1946 // LIMBU DIGIT
            in 0x19D0..0x19D9 -> toUtf32() - 0x19D0 // NEW TAI LUE DIGIT
            in 0x1A80..0x1A89 -> toUtf32() - 0x1A80 // TAI THAM HORA DIGIT
            in 0x1A90..0x1A99 -> toUtf32() - 0x1A90 // TAI THAM THAM DIGIT
            in 0x1B50..0x1B59 -> toUtf32() - 0x1B50 // BALINESE DIGIT
            in 0x1BB0..0x1BB9 -> toUtf32() - 0x1BB0 // SUNDANESE DIGIT
            in 0x1C40..0x1C49 -> toUtf32() - 0x1C40 // LEPCHA DIGIT
            in 0x1C50..0x1C59 -> toUtf32() - 0x1C50 // OL CHIKI DIGIT
            in 0xA620..0xA629 -> toUtf32() - 0xA620 // VAI DIGIT
            in 0xA8D0..0xA8D9 -> toUtf32() - 0xA8D0 // SAURASHTRA DIGIT
            in 0xA900..0xA909 -> toUtf32() - 0xA900 // KAYAH LI DIGIT
            in 0xA9D0..0xA9D9 -> toUtf32() - 0xA9D0 // JAVANESE DIGIT
            in 0xA9F0..0xA9F9 -> toUtf32() - 0xA9F0 // MYANMAR TAI LAING DIGIT
            in 0xAA50..0xAA59 -> toUtf32() - 0xAA50 // CHAM DIGIT
            in 0xABF0..0xABF9 -> toUtf32() - 0xABF0 // MEETEI MAYEK DIGIT
            in 0xFF10..0xFF19 -> toUtf32() - 0xFF10 // FULLWIDTH DIGIT
            in 0x104A0..0x104A9 -> toUtf32() - 0x104A0 // OSMANYA DIGIT
            in 0x10D30..0x10D39 -> toUtf32() - 0x10D30 // HANIFI ROHINGYA DIGIT
            in 0x11066..0x1106F -> toUtf32() - 0x11066 // BRAHMI DIGIT
            in 0x110F0..0x110F9 -> toUtf32() - 0x110F0 // SORA SOMPENG DIGIT
            in 0x11136..0x1113F -> toUtf32() - 0x11136 // CHAKMA DIGIT
            in 0x111D0..0x111D9 -> toUtf32() - 0x111D0 // SHARADA DIGIT
            in 0x112F0..0x112F9 -> toUtf32() - 0x112F0 // KHUDAWADI DIGIT
            in 0x11450..0x11459 -> toUtf32() - 0x11450 // NEWA DIGIT
            in 0x114D0..0x114D9 -> toUtf32() - 0x114D0 // TIRHUTA DIGIT
            in 0x11650..0x11659 -> toUtf32() - 0x11650 // MODI DIGIT
            in 0x116C0..0x116C9 -> toUtf32() - 0x116C0 // TAKRI DIGIT
            in 0x11730..0x11739 -> toUtf32() - 0x11730 // AHOM DIGIT
            in 0x118E0..0x118E9 -> toUtf32() - 0x118E0 // WARANG CITI DIGIT
            in 0x11950..0x11959 -> toUtf32() - 0x11950 // DIVES AKURU DIGIT
            in 0x11C50..0x11C59 -> toUtf32() - 0x11C50 // BHAIKSUKI DIGIT
            in 0x11D50..0x11D59 -> toUtf32() - 0x11D50 // MASARAM GONDI DIGIT
            in 0x11DA0..0x11DA9 -> toUtf32() - 0x11DA0 // GUNJALA GONDI DIGIT
            in 0x16A60..0x16A69 -> toUtf32() - 0x16A60 // MRO DIGIT
            in 0x16B50..0x16B59 -> toUtf32() - 0x16B50 // PAHAWH HMONG DIGIT
            in 0x1D7CE..0x1D7D7 -> toUtf32() - 0x1D7CE // MATHEMATICAL BOLD DIGIT
            in 0x1D7D8..0x1D7E1 -> toUtf32() - 0x1D7D8 // MATHEMATICAL DOUBLE-STRUCK DIGIT
            in 0x1D7E2..0x1D7EB -> toUtf32() - 0x1D7E2 // MATHEMATICAL SANS-SERIF DIGIT
            in 0x1D7EC..0x1D7F5 -> toUtf32() - 0x1D7EC // MATHEMATICAL SANS-SERIF BOLD DIGIT
            in 0x1D7F6..0x1D7FF -> toUtf32() - 0x1D7F6 // MATHEMATICAL MONOSPACE DIGIT
            in 0x1E140..0x1E149 -> toUtf32() - 0x1E140 // NYIAKENG PUACHUE HMONG DIGIT
            in 0x1E2F0..0x1E2F9 -> toUtf32() - 0x1E2F0 // WANCHO DIGIT
            in 0x1E950..0x1E959 -> toUtf32() - 0x1E950 // ADLAM DIGIT
            in 0x1FBF0..0x1FBF9 -> toUtf32() - 0x1FBF0 // SEGMENTED DIGIT
            else -> -1
        }
}