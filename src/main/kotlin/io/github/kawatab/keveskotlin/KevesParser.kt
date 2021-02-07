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

class KevesParser(private val text: String) {
    val errorList = mutableListOf<ScmError>()

    fun parse(): ScmPair? {
        errorList.clear()
        return parseTopLevel()
    }

    private fun parseText(text: String): ScmObject {
        when (text) {
            "+nan.0" -> return ScmDouble(Double.NaN)
            "+inf.0" -> return ScmDouble(Double.POSITIVE_INFINITY)
            "-inf.0" -> return ScmDouble(Double.NEGATIVE_INFINITY)
        }
        text.toIntOrNull()?.let { return@parseText ScmInt(it) }
        text.replace('l', 'e', true).toDoubleOrNull()?.let { return@parseText ScmDouble(it) }
        text.replace('s', 'e', true).replace('f', 'e', true).toFloatOrNull()?.let { return@parseText ScmFloat(it) }
        return ScmSymbol.get(text)
    }

    private fun parseTopLevel(): ScmPair? {
        var i = 0
        val stack = ArrayDeque<ScmObject?>()
        while (i < text.length) {
            parseDatum(index = i, startChar = "")?.let { result ->
                if (errorList.isEmpty()) stack.addLast(result.first)
                i = result.second
            } ?: break
        }

        var result: ScmPair? = null
        while (stack.isNotEmpty()) {
            result = ScmPair(stack.removeLast(), result)
        }
        return result?.let { ScmPair(ScmSymbol.get("begin"), result) }
    }

    private fun parseDatum(index: Int, startChar: String): Pair<ScmObject?, Int>? {
        var previousPosition = index
        var i = index
        val stack = ArrayDeque<ScmObject?>()

        tokenizeLoop@ while (i < text.length) {
            when (text[i]) {
                ' ', '\n', '\r', '\t' -> {
                    if (previousPosition != i) {
                        val value = parseText(text.substring(previousPosition, i))
                        if (startChar.isEmpty()) return value to i + 1
                        stack.addLast(value)
                    }
                    i += 1
                    previousPosition = i
                }
                '(', '[' -> {
                    if (previousPosition != i) {
                        val value = parseText(text.substring(previousPosition, i))
                        if (startChar.isEmpty()) return value to i
                        stack.addLast(value)
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
                ')', ']' -> {
                    return if (startChar.isNotEmpty()) {
                        if (startChar[startChar.length - 1] == text[i]) {
                            var result =
                                if (previousPosition != i) ScmPair.list(parseText(text.substring(previousPosition, i)))
                                else null
                            while (stack.isNotEmpty()) {
                                val value = stack.removeLast()
                                result = ScmPair(value, result)
                            }
                            result to i + 1
                        } else {
                            errorList.add(
                                ScmError(
                                    "parser",
                                    "Expected ${startChar[startChar.length - 1]}, but got ${text[i]}\n" +
                                            "-----\n" +
                                            text.substring(previousPosition)
                                )
                            )
                            null
                        }
                    } else if (previousPosition != i) {
                        parseText(text.substring(previousPosition, i)) to i
                    } else {
                        val openPosition = text.lastIndexOf('\n') + 1
                        errorList.add(
                            ScmError(
                                "parser",
                                "not found the begin of parenthesis, but the end was found. \n-----\n${
                                    text.substring(openPosition)
                                }\n-----'"
                            )
                        )
                        null
                    }
                }
                ';' -> {
                    do {
                        i += 1
                        if (i >= text.length) return null to i
                        val c = text[i]
                    } while (c != '\n' && c != '\r')
                    previousPosition = i
                }
                '\'' -> {
                    val result = parseDatum(index = i + 1, startChar = "") ?: return null.also {
                        errorList.add(
                            ScmError(
                                "parser",
                                "quote is unclosed. \n-----\n${
                                    text.substring(i)
                                }\n-----'"
                            )
                        )
                    }
                    result.let { (value, index) ->
                        val datum = ScmPair(ScmSymbol.get("quote"), ScmPair(value, null))
                        if (startChar.isEmpty()) return datum to index
                        stack.addLast(datum)
                        i = index
                        previousPosition = i

                    }
                }
                '`' -> {
                    val result = parseDatum(index = i + 1, startChar = "") ?: return null.also {
                        errorList.add(
                            ScmError(
                                "parser",
                                "quasiquote is unclosed. \n-----\n${
                                    text.substring(i)
                                }\n-----'"
                            )
                        )
                    }
                    result.let { (value, index) ->
                        val datum = ScmPair(ScmSymbol.get("quasiquote"), ScmPair(value, null))
                        if (startChar.isEmpty()) return datum to index
                        stack.addLast(datum)
                        i = index
                        previousPosition = i
                    }
                }
                ',' -> {
                    if (i + 1 < text.length && text[i + 1] == '@') {
                        val result = parseDatum(index = i + 2, startChar = "") ?: return null.also {
                            errorList.add(
                                ScmError(
                                    "parser",
                                    "unquote-splicing is unclosed. \n-----\n${
                                        text.substring(i)
                                    }\n-----'"
                                )
                            )
                        }
                        result.let { (value, index) ->
                            val datum = ScmPair(ScmSymbol.get("unquote-splicing"), ScmPair(value, null))
                            if (startChar.isEmpty()) return datum to index
                            stack.addLast(datum)
                            i = index
                            previousPosition = i
                        }
                    } else {
                        val result = parseDatum(index = i + 1, startChar = "") ?: return null.also {
                            errorList.add(
                                ScmError(
                                    "parser",
                                    "unquote is unclosed. \n-----\n${
                                        text.substring(i)
                                    }\n-----'"
                                )
                            )
                        }
                        result.let { (value, index) ->
                            val datum = ScmPair(ScmSymbol.get("unquote"), ScmPair(value, null))
                            if (startChar.isEmpty()) return datum to index
                            stack.addLast(datum)
                            i = index
                            previousPosition = i
                        }
                    }
                }
                '.' -> {
                    if (previousPosition != i) {
                        i += 1
                        continue@tokenizeLoop
                    }
                    if (i >= text.length) return null.also {
                        errorList.add(
                            ScmError(
                                "parser",
                                "found no object after dot. \n-----\n${
                                    text.substring(previousPosition)
                                }\n-----'"
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
                '"' -> {
                    do {
                        i += 1
                        if (i >= text.length) return null.also {
                            errorList.add(
                                ScmError(
                                    "parser",
                                    "not found the end of quotation. \n-----\n${
                                        text.substring(previousPosition)
                                    }\n-----'"
                                )
                            )
                        }
                    } while (text[i] != '"')

                    val value = ScmString(text.substring(previousPosition + 1, i))
                    if (startChar.isEmpty()) return value to i + 1
                    stack.addLast(value)
                    i += 1
                    previousPosition = i
                }
                '|' -> {
                    var symbol = ""
                    do {
                        i += 1
                        if (i >= text.length) return null.also {
                            errorList.add(
                                ScmError(
                                    "parser",
                                    "not found the end of symbol with vertical lines. \n-----\n${
                                        text.substring(previousPosition)
                                    }\n-----'"
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
                                        ScmError(
                                            "parser",
                                            "malformed character \n-----\n${
                                                text.substring(previousPosition)
                                            }\n-----'"
                                        )
                                    )
                                }
                            }
                            '|' -> {
                                val value = ScmSymbol.get(symbol)
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
                '#' -> {
                    i += 1
                    if (i >= text.length) {
                        val char = ScmChar('#')
                        if (startChar.isEmpty()) return char to i
                        stack.addLast(char)
                        previousPosition = i
                        continue@tokenizeLoop
                    }

                    val booleanValue =
                        matchTrue(i)?.let { ScmConstant.TRUE to it } ?: matchFalse(i)?.let { ScmConstant.FALSE to it }
                    if (booleanValue != null) {
                        if (startChar.isEmpty()) return booleanValue
                        booleanValue.let { (value, next) ->
                            stack.addLast(value)
                            i = next
                        }
                        previousPosition = i
                        continue@tokenizeLoop
                    }

                    when (text[i]) {
                        '\\' -> {
                            if (i + 2 == text.length) {
                                val char = ScmChar(text[i + 1])
                                if (startChar.isEmpty()) return char to i + 1
                                stack.addLast(char)
                                previousPosition = i + 1
                                continue@tokenizeLoop
                            } else if (i + 2 < text.length) {
                                when (text[i + 2]) {
                                    ' ', '\t', '\r', '\n', ')' -> {
                                        val char = ScmChar(text[i + 1])
                                        if (startChar.isEmpty()) return char to i + 2
                                        stack.addLast(char)
                                        i += 2
                                        previousPosition = i
                                        continue@tokenizeLoop
                                    }
                                    else -> {
                                        errorList.add(
                                            ScmError(
                                                "parser",
                                                "malformed character. \n-----\n${
                                                    text.substring(i)
                                                }\n-----'"
                                            )
                                        )
                                        i = text.length
                                        previousPosition = i
                                        continue@tokenizeLoop
                                    }
                                }
                            } else {
                                errorList.add(
                                    ScmError(
                                        "parser",
                                        "constant vector is unclosed. \n-----\n${
                                            text.substring(i)
                                        }\n-----'"
                                    )
                                )
                                i = text.length
                                previousPosition = i
                                continue@tokenizeLoop
                            }
                        }

                        '(' -> {
                            val result = parseDatum(index = i + 1, startChar = ")") ?: return null.also {
                                errorList.add(
                                    ScmError(
                                        "parser",
                                        "constant vector is unclosed. \n-----\n${
                                            text.substring(i)
                                        }\n-----'"
                                    )
                                )
                            }
                            result.let { (value, index) ->
                                val datum = (value as? ScmPair).toVector()
                                if (startChar.isEmpty()) return datum to index
                                stack.addLast(datum)
                                i = index
                                previousPosition = i
                            }
                        }
                        '|' -> {
                            skipBlockComment(i + 1, 0)?.let { result ->
                                i = result
                                previousPosition = i
                            } ?: return null.also {
                                errorList.add(
                                    ScmError(
                                        "parser",
                                        "not found the end of comment block. \n-----\n${
                                            text.substring(i)
                                        }\n-----'"
                                    )
                                )
                            }
                        }
                        ';' -> {
                            findNextDatumEnd(i + 1, 0)?.let { end ->
                                i = end
                                previousPosition = i
                            } ?: return null.also {
                                errorList.add(
                                    ScmError(
                                        "parser",
                                        "datum comment is unclosed. \n-----\n${
                                            text.substring(i)
                                        }\n-----'"
                                    )
                                )
                            }
                        }
                        'u' -> {
                            val result = parseByteVector(i) ?: return null
                            if (startChar.isEmpty()) return result
                            result.let { (value, index) ->
                                stack.addLast(value)
                                i = index
                                previousPosition = i
                            }
                        }
                        else -> {
                            val char = ScmChar('#')
                            if (startChar.isEmpty()) return char to i
                            stack.addLast(char)
                            previousPosition = i
                        }
                    }
                }
                else -> {
                    i += 1
                }
            }
        }

        return if (startChar.isEmpty()) {
            if (previousPosition < text.length) {
                val value = parseText(text.substring(previousPosition, text.length))
                value to text.length
            } else {
                null
            }
        } else {
            null.also {
                // TODO("Not detect correct begin position, if there are any comments")
                val openPosition = text.lastIndexOf('(')
                errorList.add(
                    ScmError(
                        "parser",
                        "not found the end of parenthesis. The begin is\n-----\n${
                            text.substring(openPosition)
                        }\n-----'"
                    )
                )
            }
        }
    }

    private fun matchTrue(begin: Int): Int? {
        var i = begin
        if (i <= text.length) {
            when (text[i]) {
                't', 'T' -> {
                    i += 1
                    if (i >= text.length) return i // end
                    when (text[i]) { // delimiter
                        ' ', '\t', '\r', '\n', ';', '#', '(', ')', '[', ']', '"' -> {
                            return i
                        }
                        else -> {
                            if (i + 2 < text.length && text.substring(i, i + 3).toLowerCase() == "rue") {
                                return i + 3
                            } else if (i + 3 == text.length) {
                                return i + 3
                            } else {
                                when (text[i + 3]) {
                                    ' ', '\t', '\r', '\n', ';', '#', '(', ')', '[', ']', '"' -> return i + 3
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun matchFalse(begin: Int): Int? {
        var i = begin
        if (i <= text.length) {
            when (text[i]) {
                'f', 'F' -> {
                    i += 1
                    if (i >= text.length) return i // end
                    when (text[i]) { // delimiter
                        ' ', '\t', '\r', '\n', ';', '#', '(', ')', '[', ']', '"' -> {
                            return i
                        }
                        else -> {
                            @Suppress("SpellCheckingInspection")
                            if (i + 3 < text.length && text.substring(i, i + 4).toLowerCase() == "alse") {
                                return i + 4
                            } else if (i + 4 == text.length) {
                                return i + 4
                            } else {
                                when (text[i + 4]) {
                                    ' ', '\t', '\r', '\n', ';', '#', '(', ')', '[', ']', '"' -> return i + 4
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun terminateImproperList(
        stack: ArrayDeque<ScmObject?>,
        i: Int,
        startChar: String
    ): Pair<ScmObject?, Int>? {
        if (startChar.isNotEmpty()) {
            if (stack.isEmpty()) {
                errorList.add(
                    ScmError(
                        "parser",
                        "no object before dot. \n-----\n${text.substring(i)}\n-----'"
                    )
                )
                return null
            }

            val (last, next) =
                parseDatum(index = i + 1, startChar = startChar.substring(startChar.length - 1))
                    ?: return null.also {
                        errorList.add(
                            ScmError(
                                "parser",
                                "dot used illegally. \n-----\n${text.substring(i)}\n-----'"
                            )
                        )
                    }

            if ((last as? ScmPair)?.cdr != null) {
                errorList.add(
                    ScmError(
                        "parser",
                        "found more than one object after dot. \n-----\n${text.substring(i)}\n-----'"
                    )
                )
                return null
            }

            var result = (last as? ScmPair)?.car
            while (stack.isNotEmpty()) {
                val value = stack.removeLast()
                result = ScmPair(value, result)
            }
            return result to next
        } else {
            errorList.add(
                ScmError(
                    "parser",
                    "found dot at top level. \n-----\n${text.substring(i)}\n-----'"
                )
            )
            return null
        }
    }

    private fun parseByteVector(begin: Int): Pair<ScmByteVector, Int>? {
        var i = begin
        i += 1
        if (i + 3 < text.length && text.substring(i, i + 2) == "8(") {
            val truncatedBefore = text.substring(i + 2)
            i += 2
            val indexOfLast = truncatedBefore.indexOf(')')
            if (indexOfLast < 0) return null.also {
                errorList.add(
                    ScmError(
                        "parser",
                        "bytevector is unclosed. \n-----\n${
                            text.substring(i)
                        }\n-----'"
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
                            ScmError(
                                "parser",
                                "included non byte object. \n-----\n${
                                    text.substring(i)
                                }\n-----'"
                            )
                        )
                    }
                byteArray[idx] = value.toByte()
            }
            return ScmByteVector(byteArray) to i
        } else {
            return null.also {
                errorList.add(
                    ScmError(
                        "parser",
                        "unknown prefix. \n-----\n${
                            text.substring(begin - 1)
                        }\n-----'"
                    )
                )
            }
        }
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