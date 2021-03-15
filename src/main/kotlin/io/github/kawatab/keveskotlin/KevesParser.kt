/*
 * KevesParser.kt
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

@file:Suppress("SpellCheckingInspection")

package io.github.kawatab.keveskotlin

import io.github.kawatab.keveskotlin.objects.*
import io.github.kawatab.keveskotlin.objects.ScmVector.Companion.toVector
import kotlin.collections.ArrayDeque

class KevesParser(private val text: String, private val res: KevesResources) {
    val errorList = mutableListOf<PtrError>()

    // delimiter
    private val regexWhiteSpaceAtHead = """^\s.*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexVerticalLine = """\|.*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexLeftParenthesis = """[(\[].*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexRightParenthesis = """[)\]].*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexDoubleQuotation = """".*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexComment = ";.*".toRegex(RegexOption.DOT_MATCHES_ALL)

    // token
    private val regexVector = """#\(.*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexByteVector = """#u8\(.*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexQuote = "'.*".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexQuasiQuote = "`.*".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexUnquote = ",.*".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexDotAtHead = """\..*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexBlockComment = """#\|.*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexDatumComment = "#;.*".toRegex(RegexOption.DOT_MATCHES_ALL)
    private val regexTrue1 =
        """#t([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexTrue2 =
        """#true([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexFalse1 =
        """#f([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexFalse2 =
        """#false([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))


    private val regexChar =
        ("""#\\(.|[\\x""" +
                Char.MIN_HIGH_SURROGATE.toInt().toString(16) +
                """-\\x""" +
                Char.MAX_HIGH_SURROGATE.toInt().toString(16) +
                """][\\x""" +
                Char.MIN_LOW_SURROGATE.toInt().toString(16) +
                """-\\x""" +
                Char.MAX_LOW_SURROGATE.toInt().toString(16) +
                """])([\s";()\[\]|].*|)""").toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexAlarm =
        """#\\alarm([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexBackspace =
        """#\\backspace([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexDelete =
        """#\\delete([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexEscape =
        """#\\escape([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexNewline =
        """#\\newline([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexNull =
        """#\\null([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexReturn =
        """#\\return([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexSpace =
        """#\\space([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexTab =
        """#\\tab([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexHexScaleValue =
        """#\\x[0-9a-f]+([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val regexCrossHatch =
        """#([\s";()\[\]|].*|)""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    fun parse(): PtrPairOrNull {
        errorList.clear()
        return parseTopLevel()
    }

    private fun parseText(text: String): PtrObjectNonNull {
        when (text) {
            "+nan.0" -> return res.constNaN.toNonNull()
            "+inf.0" -> return res.constPositiveInfinity.toNonNull()
            "-inf.0" -> return res.constNegativeInfinity.toNonNull()
        }
        text.toIntOrNull()?.let { return@parseText ScmInt.make(it, res).toObjectNonNull() }
        text.replace('l', 'e', true).toDoubleOrNull()
            ?.let { return@parseText ScmDouble.make(it, res).toObjectNonNull() }
        text.replace('s', 'e', true).replace('f', 'e', true).toFloatOrNull()
            ?.let { return@parseText ScmFloat.make(it, res).toObjectNonNull() }
        return ScmSymbol.get(text, res).toObject().let {
            if (it.isNotNull()) it.toNonNull() else throw RuntimeException("parseText got null as symbol")
        }
    }

    private fun parseTopLevel(): PtrPairOrNull {
        var i = 0
        val stack = ArrayDeque<PtrObject>()
        while (i < text.length) {
            parseDatum(index = i, startChar = "")?.let { result ->
                if (errorList.isEmpty()) stack.addLast(result.first)
                i = result.second
            } ?: break
        }

        var result = PtrPairOrNull(0)
        while (stack.isNotEmpty()) {
            result = ScmPair.make(stack.removeLast(), result.toObject(), res)
        }
        return result.let {
            if (it.isNull()) it
            else ScmPair.make(ScmSymbol.get("begin", res).toObject(), result.toObject(), res)
        }
    }

    private fun parseDatum(index: Int, startChar: String): Pair<PtrObject, Int>? {
        var previousPosition = index
        var i = index
        val stack = ArrayDeque<PtrObject>()

        tokenizeLoop@ while (i < text.length) {
            val currentText = text.substring(i)
            when {
                regexWhiteSpaceAtHead matches currentText -> { // white spaces
                    if (previousPosition != i) {
                        val value = parseText(text.substring(previousPosition, i))
                        if (startChar.isEmpty()) return value.toObject() to i + 1
                        stack.addLast(value.toObject())
                    }
                    i += 1
                    previousPosition = i
                }
                regexLeftParenthesis matches currentText -> {
                    if (previousPosition != i) {
                        val value = parseText(text.substring(previousPosition, i))
                        if (startChar.isEmpty()) return value.toObject() to i
                        stack.addLast(value.toObject())
                        previousPosition = i
                    }
                    val result = parseDatum(index = i + 1, startChar = startChar + if (text[i] == '(') ')' else ']')
                    if (startChar.isEmpty()) {
                        return result
                    } else {
                        result?.let { (value, index) ->
                            stack.addLast(value)
                            previousPosition = index
                            i = previousPosition
                        } ?: return null
                    }
                }
                regexRightParenthesis matches currentText -> {
                    return if (startChar.isNotEmpty()) {
                        if (startChar[startChar.length - 1] == text[i]) {
                            var result: PtrObject =
                                if (previousPosition != i)
                                    ScmPair.list(parseText(text.substring(previousPosition, i)).toObject(), res)
                                        .toObject()
                                else PtrObject(0)
                            while (stack.isNotEmpty()) {
                                val value = stack.removeLast()
                                result = ScmPair.make(value, result, res).toObject()
                            }
                            result to i + 1
                        } else {
                            errorList.add(
                                ScmError.make(
                                    "parser",
                                    "Expected ${startChar[startChar.length - 1]}, but got ${text[i]}\n" +
                                            "-----\n" +
                                            text.substring(previousPosition),
                                    res
                                )
                            )
                            null
                        }
                    } else if (previousPosition != i) {
                        parseText(text.substring(previousPosition, i)).toObject() to i
                    } else {
                        val openPosition = text.lastIndexOf('\n') + 1
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "not found the begin of parenthesis, but the end was found. \n-----\n${
                                    text.substring(openPosition)
                                }\n-----'",
                                res
                            )
                        )
                        null
                    }
                }
                regexComment matches currentText -> { // ; ...
                    do {
                        i += 1
                        if (i >= text.length) return PtrObject(0) to i
                        val c = text[i]
                    } while (c != '\n' && c != '\r')
                    previousPosition = i
                }
                regexQuote matches currentText -> {
                    val result = parseDatum(index = i + 1, startChar = "") ?: return null.also {
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "quote is unclosed. \n-----\n${
                                    currentText
                                }\n-----'",
                                res
                            )
                        )
                    }
                    result.let { (value, index) ->
                        val datum: PtrObject = ScmPair.make(
                            ScmSymbol.get("quote", res).toObject(),
                            ScmPair.make(value, PtrObject(0), res).toObject(),
                            res
                        ).toObject()
                        if (startChar.isEmpty()) return datum to index
                        stack.addLast(datum)
                        i = index
                        previousPosition = i

                    }
                }
                regexQuasiQuote matches currentText -> {
                    val result = parseDatum(index = i + 1, startChar = "") ?: return null.also {
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "quasiquote is unclosed. \n-----\n${
                                    currentText
                                }\n-----'",
                                res
                            )
                        )
                    }
                    result.let { (value, index) ->
                        val datum: PtrObject = ScmPair.make(
                            ScmSymbol.get("quasiquote", res).toObject(),
                            ScmPair.make(value, PtrObject(0), res).toObject(),
                            res
                        ).toObject()
                        if (startChar.isEmpty()) return datum to index
                        stack.addLast(datum)
                        i = index
                        previousPosition = i
                    }
                }
                regexUnquote matches currentText -> {
                    if (i + 1 < text.length && text[i + 1] == '@') {
                        val result = parseDatum(index = i + 2, startChar = "") ?: return null.also {
                            errorList.add(
                                ScmError.make(
                                    "parser",
                                    "unquote-splicing is unclosed. \n-----\n${
                                        currentText
                                    }\n-----'",
                                    res
                                )
                            )
                        }
                        result.let { (value, index) ->
                            val datum: PtrObject = ScmPair.make(
                                ScmSymbol.get("unquote-splicing", res).toObject(),
                                ScmPair.make(value, PtrObject(0), res).toObject(),
                                res
                            ).toObject()
                            if (startChar.isEmpty()) return datum to index
                            stack.addLast(datum)
                            i = index
                            previousPosition = i
                        }
                    } else {
                        val result = parseDatum(index = i + 1, startChar = "") ?: return null.also {
                            errorList.add(
                                ScmError.make(
                                    "parser",
                                    "unquote is unclosed. \n-----\n${
                                        currentText
                                    }\n-----'",
                                    res
                                )
                            )
                        }
                        result.let { (value, index) ->
                            val datum: PtrObject = ScmPair.make(
                                ScmSymbol.get("unquote", res).toObject(),
                                ScmPair.make(value, PtrObject(0), res).toObject(),
                                res
                            ).toObject()
                            if (startChar.isEmpty()) return datum to index
                            stack.addLast(datum)
                            i = index
                            previousPosition = i
                        }
                    }
                }
                regexDotAtHead matches currentText -> {
                    if (previousPosition != i) {
                        i += 1
                        continue@tokenizeLoop
                    }
                    if (i >= text.length) return null.also {
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "found no object after dot. \n-----\n${
                                    text.substring(previousPosition)
                                }\n-----'",
                                res
                            )
                        )
                    }
                    when (text[i + 1]) {
                        ' ', '\t', '\r', '\n', '(', '\"' -> {
                            return terminateImproperList(stack, i, startChar)
                        }
                        else -> {
                            previousPosition = i
                            i += 1
                        }
                    }
                }
                regexDoubleQuotation matches currentText -> {
                    do {
                        i += 1
                        if (i >= text.length) return null.also {
                            errorList.add(
                                ScmError.make(
                                    "parser",
                                    "not found the end of quotation. \n-----\n${
                                        text.substring(previousPosition)
                                    }\n-----'",
                                    res
                                )
                            )
                        }
                    } while (text[i] != '"')

                    val value = ScmString.make(text.substring(previousPosition + 1, i), res).toObject()
                    if (startChar.isEmpty()) return value to i + 1
                    stack.addLast(value)
                    i += 1
                    previousPosition = i
                }
                regexVerticalLine matches currentText -> {
                    var symbol = ""
                    do {
                        i += 1
                        if (i >= text.length) return null.also {
                            errorList.add(
                                ScmError.make(
                                    "parser",
                                    "not found the end of symbol with vertical lines. \n-----\n${
                                        text.substring(previousPosition)
                                    }\n-----'",
                                    res
                                )
                            )
                        }
                        when (val c = text[i]) {
                            '\\' -> {
                                getEscapeCode(i)?.let { (c, index) ->
                                    symbol += c
                                    i = index
                                } ?: return null.also {
                                    errorList.add(
                                        ScmError.make(
                                            "parser",
                                            "malformed character \n-----\n${
                                                text.substring(previousPosition)
                                            }\n-----'",
                                            res
                                        )
                                    )
                                }
                            }
                            '|' -> {
                                val value: PtrObject = ScmSymbol.get(symbol, res).toObject()
                                if (startChar.isEmpty()) return value to i + 1
                                stack.addLast(value)
                                i += 1
                                previousPosition = i
                                break
                            }
                            else -> symbol += c
                        }
                    } while (true)
                }
                regexBlockComment matches currentText -> { // #| ... |#
                    skipBlockComment(i + 2, 0)?.let { result ->
                        i = result
                        previousPosition = i
                    } ?: return null.also {
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "not found the end of comment block. \n-----\n${
                                    currentText
                                }\n-----'",
                                res
                            )
                        )
                    }
                }
                regexDatumComment matches currentText -> { // #;...
                    findNextDatumEnd(i + 2, 0)?.let { end ->
                        i = end
                        previousPosition = i
                    } ?: return null.also {
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "datum comment is unclosed. \n-----\n${
                                    currentText
                                }\n-----'",
                                res
                            )
                        )
                    }
                }
                regexVector matches currentText -> {
                    val result = parseDatum(index = i + 2, startChar = ")") ?: return null.also {
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "constant vector is unclosed. \n-----\n${
                                    currentText
                                }\n-----'",
                                res
                            )
                        )
                    }
                    result.let { (value, index) ->
                        val datum = value.toPairOrNull().toVector(res).toObject()
                        if (startChar.isEmpty()) return datum to index
                        stack.addLast(datum)
                        i = index
                        previousPosition = i
                    }
                }
                regexByteVector matches currentText -> {
                    val result = parseByteVector(i) ?: return null
                    if (startChar.isEmpty()) return result
                    result.let { (value, index) ->
                        stack.addLast(value)
                        i = index
                        previousPosition = i
                    }
                }
                regexTrue1 matches currentText -> { // #t
                    if (startChar.isEmpty()) return res.constTrue to i + 2
                    stack.addLast(res.constTrue)
                    i += 2
                    previousPosition = i
                }
                regexTrue2 matches currentText -> { // #true
                    if (startChar.isEmpty()) return res.constTrue to i + 5
                    stack.addLast(res.constTrue)
                    i += 5
                    previousPosition = i
                }
                regexFalse1 matches currentText -> { // #f
                    if (startChar.isEmpty()) return res.constFalse to i + 2
                    stack.addLast(res.constFalse)
                    i += 2
                    previousPosition = i
                }
                regexFalse2 matches currentText -> { // #false
                    if (startChar.isEmpty()) return res.constFalse to i + 6
                    stack.addLast(res.constFalse)
                    i += 6
                    previousPosition = i
                }
                regexChar matches currentText -> { // #\ <any character>
                    val char1 = currentText[2]
                    val (result, length) = if (char1.isSurrogate()) {
                        val char2 = currentText[3]
                        ScmChar.make(char1, char2, res).toObject() to 4
                    } else {
                        ScmChar.make(currentText[2], res).toObject() to 3
                    }
                    if (startChar.isEmpty()) return result to i + length
                    stack.addLast(result)
                    i += length
                    previousPosition = i
                }
                regexAlarm matches currentText -> { // #\alarm
                    val result = ScmChar.make('\u0007', res).toObject()
                    if (startChar.isEmpty()) return result to i + 7
                    stack.addLast(result)
                    i += 7
                    previousPosition = i
                }
                regexBackspace matches currentText -> { // #\backspace
                    val result = ScmChar.make('\u0008', res).toObject()
                    if (startChar.isEmpty()) return result to i + 11
                    stack.addLast(result)
                    i += 11
                    previousPosition = i
                }
                regexDelete matches currentText -> { // #\delete
                    val result = ScmChar.make('\u007F', res).toObject()
                    if (startChar.isEmpty()) return result to i + 8
                    stack.addLast(result)
                    i += 8
                    previousPosition = i
                }
                regexEscape matches currentText -> { // #\escape
                    val result = ScmChar.make('\u001B', res).toObject()
                    if (startChar.isEmpty()) return result to i + 8
                    stack.addLast(result)
                    i += 8
                    previousPosition = i
                }
                regexNewline matches currentText -> { // #\newline
                    val result = ScmChar.make('\u000A', res).toObject()
                    if (startChar.isEmpty()) return result to i + 9
                    stack.addLast(result)
                    i += 9
                    previousPosition = i
                }
                regexNull matches currentText -> { // #\null
                    val result = ScmChar.make('\u0000', res).toObject()
                    if (startChar.isEmpty()) return result to i + 6
                    stack.addLast(result)
                    i += 6
                    previousPosition = i
                }
                regexReturn matches currentText -> { // #\return
                    val result = ScmChar.make('\u000D', res).toObject()
                    if (startChar.isEmpty()) return result to i + 8
                    stack.addLast(result)
                    i += 8
                    previousPosition = i
                }
                regexSpace matches currentText -> { // #\space
                    val result = ScmChar.make(' ', res).toObject()
                    if (startChar.isEmpty()) return result to i + 7
                    stack.addLast(result)
                    i += 7
                    previousPosition = i
                }
                regexTab matches currentText -> { // #\tab
                    val result = ScmChar.make('\u0009', res).toObject()
                    if (startChar.isEmpty()) return result to i + 5
                    stack.addLast(result)
                    i += 5
                    previousPosition = i
                }
                regexHexScaleValue matches currentText -> { // #\x <hex scale value>
                    val hexValue =
                        "[0-9a-f]+".toRegex(setOf(RegexOption.IGNORE_CASE)).find(currentText.substring(3))?.value
                            ?: return null.also {
                                errorList.add(
                                    ScmError.make(
                                        "parser",
                                        "Hex scale value of character is wrong. \n-----\n${
                                            currentText
                                        }\n-----'",
                                        res
                                    )
                                )
                            }

                    val value = hexValue.toInt(16)
                    val result = ScmChar.make(value, res).toObject()
                    if (startChar.isEmpty()) return result to i + 3 + hexValue.length
                    stack.addLast(result)
                    i += 3 + hexValue.length
                    previousPosition = i
                }
                regexCrossHatch matches currentText -> { // #
                    val result = ScmChar.make('#', res).toObject()
                    if (startChar.isEmpty()) return result to i + 1
                    stack.addLast(result)
                    i += 1
                    previousPosition = i
                }
                else -> {
                    i += 1
                }
            }
        }

        return if (startChar.isEmpty()) {
            if (previousPosition < text.length) {
                val value: PtrObject = parseText(text.substring(previousPosition, text.length)).toObject()
                value to text.length
            } else {
                null
            }
        } else {
            null.also {
                // TODO("Not detect correct begin position, if there are any comments")
                val openPosition = text.lastIndexOf('(')
                errorList.add(
                    ScmError.make(
                        "parser",
                        "not found the end of parenthesis. The begin is\n-----\n${
                            text.substring(openPosition)
                        }\n-----'",
                        res
                    )
                )
            }
        }
    }

    private fun terminateImproperList(
        stack: ArrayDeque<PtrObject>,
        i: Int,
        startChar: String
    ): Pair<PtrObject, Int>? {
        if (startChar.isNotEmpty()) {
            if (stack.isEmpty()) {
                errorList.add(
                    ScmError.make(
                        "parser",
                        "no object before dot. \n-----\n${text.substring(i)}\n-----'",
                        res
                    )
                )
                return null
            }

            val (last, next) =
                parseDatum(index = i + 1, startChar = startChar.substring(startChar.length - 1))
                    ?: return null.also {
                        errorList.add(
                            ScmError.make(
                                "parser",
                                "dot used illegally. \n-----\n${text.substring(i)}\n-----'",
                                res
                            )
                        )
                    }

            if (last.isPair(res) && last.asPair(res).cdr.isNotNull()) {
                errorList.add(
                    ScmError.make(
                        "parser",
                        "found more than one object after dot. \n-----\n${text.substring(i)}\n-----'",
                        res
                    )
                )
                return null
            }

            var result: PtrObject = if (last.isPair(res)) last.asPair(res).car else PtrObject(0)
            while (stack.isNotEmpty()) {
                val value = stack.removeLast()
                result = ScmPair.make(value, result, res).toObject()
            }
            return result to next
        } else {
            errorList.add(
                ScmError.make(
                    "parser",
                    "found dot at top level. \n-----\n${text.substring(i)}\n-----'",
                    res
                )
            )
            return null
        }
    }

    private fun parseByteVector(begin: Int): Pair<PtrObject, Int>? {
        var i = begin + 4
        val truncatedBefore = text.substring(i)
        val indexOfLast = truncatedBefore.indexOf(')')
        if (indexOfLast < 0) return null.also {
            errorList.add(
                ScmError.make(
                    "parser",
                    "bytevector is unclosed. \n-----\n${
                        text.substring(i)
                    }\n-----'",
                    res
                )
            )
        }
        i += indexOfLast + 1
        val partOfByte = truncatedBefore.substring(0, indexOfLast)
        val textArray = partOfByte.split(' ', '\t', '\r', '\n').filter { it.isNotBlank() }
        val byteArray = ByteArray(textArray.size)
        for (idx in textArray.indices) {
            val value = textArray[idx].toIntOrNull()?.let { if (it in 0..255) it else null }
                ?: return null.also {
                    errorList.add(
                        ScmError.make(
                            "parser",
                            "included non byte object. \n-----\n${
                                text.substring(i)
                            }\n-----'",
                            res
                        )
                    )
                }
            byteArray[idx] = value.toByte()
        }
        return ScmByteVector.make(byteArray, res).toObject() to i
    }

    private fun skipBlockComment(begin: Int, level: Int): Int? {
        var i = begin
        while (i + 1 <= text.length) {
            when (text.substring(i, i + 2)) {
                "|#" -> {
                    return i + 2
                }
                "#|" -> {
                    i = skipBlockComment(i + 2, level + 1) ?: return null
                }
                else -> {
                    i += 1
                }
            }
        }
        return null
    }

    private fun findNextDatumEnd(begin: Int, level: Int): Int? {
        var i = begin
        while (text[i].isWhitespace()) {
            if (i >= text.length) return text.length
            else i += 1
        }

        return if (i + 1 < text.length && text.substring(i, i + 2) == "#;") {
            findNextDatumEnd(i + 2, level + 1)
        } else {
            parseDatum(index = i, startChar = "")?.let { (_, index) -> index }
        }
    }

    private fun getEscapeCode(begin: Int): Pair<Char, Int>? {
        var i = begin + 1
        if (i < text.length) {
            when (text[i]) {
                'n' -> return '\n' to i
                'r' -> return '\r' to i
                't' -> return '\t' to i
                'x' -> {
                    i += 1
                    while (i < text.length) {
                        if (text[i] == ';') {
                            val num = text.substring(begin + 2, i)
                            num.toIntOrNull(16)?.let {
                                return it.toChar() to i
                            } ?: return null
                        }
                        i += 1
                    }
                }
            }
        }
        return null
    }
}