/*
 * R7rsChar.kt
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

package io.github.kawatab.keveskotlin.libraries

import io.github.kawatab.keveskotlin.KevesExceptions
import io.github.kawatab.keveskotlin.KevesVM
import io.github.kawatab.keveskotlin.objects.*

object R7rsChar {
    /** procedure: char? */
    val procCharQ: ScmProcedure by lazy {
        object : ScmProcedure("char?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharQ.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (obj is ScmChar) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharQ.id)
                }
            }
        }
    }

    /** procedure: char=? */
    val procCharEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("char=?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharEqualQ.id)
                    else -> {
                        val sp = vm.sp
                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedChar(procCharEqualQ.id)
                        for (i in 1 until n) {
                            val obj = (vm.stack.index(sp, i) as? ScmChar)?.toUtf32()
                                ?: throw KevesExceptions.expectedChar(procCharEqualQ.id)
                            if (first != obj) return vm.scmProcReturn(ScmConstant.FALSE, n, this)
                        }
                        vm.scmProcReturn(ScmConstant.TRUE, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char<?' */
    val procCharLessThanQ: ScmProcedure by lazy {
        object : ScmProcedure("char<?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharLessThanQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(procCharLessThanQ.id)
                                if (last < obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(procCharLessThanQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char<=?' */
    val procCharLessThanEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("char<=?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharLessThanEqualQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(procCharLessThanEqualQ.id)
                                if (last <= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(procCharLessThanEqualQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char>?' */
    val procCharGraterThanQ: ScmProcedure by lazy {
        object : ScmProcedure("char>?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharGraterThanQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(procCharGraterThanQ.id)
                                if (last > obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(procCharGraterThanQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char>=?' */
    val procCharGraterThanEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("char>=?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharGraterThanEqualQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(procCharGraterThanEqualQ.id)
                                if (last >= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(procCharGraterThanEqualQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: char-ci=? */
    val procCharCIEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("char-ci=?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharCIEqualQ.id)
                    else -> {
                        val sp = vm.sp
                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedChar(procCharCIEqualQ.id)
                        for (i in 1 until n) {
                            val obj = (vm.stack.index(sp, i) as? ScmChar)?.toLowerCase()
                                ?: throw KevesExceptions.expectedChar(procCharCIEqualQ.id)
                            if (first != obj) return vm.scmProcReturn(ScmConstant.FALSE, n, this)
                        }
                        vm.scmProcReturn(ScmConstant.TRUE, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char-ci<?' */
    val procCharCILessThanQ: ScmProcedure by lazy {
        object : ScmProcedure("char-ci<?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharCILessThanQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(procCharCILessThanQ.id)
                                if (last < obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(procCharCILessThanQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char-ci<=?' */
    val procCharCILessThanEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("char-ci<=?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharCILessThanEqualQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(procCharCILessThanEqualQ.id)
                                if (last <= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(procCharCILessThanEqualQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char-ci>?' */
    val procCharCIGraterThanQ: ScmProcedure by lazy {
        object : ScmProcedure("char-ci>?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharCIGraterThanQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(procCharCIGraterThanQ.id)
                                if (last > obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(procCharCIGraterThanQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: 'char-ci>=?' */
    val procCharCIGraterThanEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("char-ci>=?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procCharCIGraterThanEqualQ.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (vm.stack.index(sp, index) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(procCharCIGraterThanEqualQ.id)
                                if (last >= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (vm.stack.index(sp, 0) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(procCharCIGraterThanEqualQ.id)
                        val result = loop(1, first)

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: char-alphabetic? */
    val procCharAlphabeticQ: ScmProcedure by lazy {
        object : ScmProcedure("char-alphabetic?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharAlphabeticQ.id)
                    1 -> {
                        val isAlphabetic = (vm.stack.index(vm.sp, 0) as? ScmChar)?.isAlphabetic()
                            ?: throw KevesExceptions.expectedChar(procCharAlphabeticQ.id)
                        val result = if (isAlphabetic) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharAlphabeticQ.id)
                }
            }
        }
    }

    /** procedure: char-numeric? */
    val procCharNumericQ: ScmProcedure by lazy {
        object : ScmProcedure("char-numeric?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharNumericQ.id)
                    1 -> {
                        val isNumeric = (vm.stack.index(vm.sp, 0) as? ScmChar)?.isNumeric()
                            ?: throw KevesExceptions.expectedChar(procCharNumericQ.id)
                        val result = if (isNumeric) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharNumericQ.id)
                }
            }
        }
    }

    /** procedure: char-whitespace? */
    val procCharWhitespaceQ: ScmProcedure by lazy {
        object : ScmProcedure("char-whitespace?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharWhitespaceQ.id)
                    1 -> {
                        val isWhitespace = (vm.stack.index(vm.sp, 0) as? ScmChar)?.isWhitespace()
                            ?: throw KevesExceptions.expectedChar(procCharWhitespaceQ.id)
                        val result = if (isWhitespace) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharWhitespaceQ.id)
                }
            }
        }
    }

    /** procedure: char-upper-case? */
    val procCharUpperCaseQ: ScmProcedure by lazy {
        object : ScmProcedure("char-upper-case?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharUpperCaseQ.id)
                    1 -> {
                        val isUpperCase = (vm.stack.index(vm.sp, 0) as? ScmChar)?.isUpperCase()
                            ?: throw KevesExceptions.expectedChar(procCharLowerCaseQ.id)
                        val result = if (isUpperCase) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharUpperCaseQ.id)
                }
            }
        }
    }

    /** procedure: char-lower-case? */
    val procCharLowerCaseQ: ScmProcedure by lazy {
        object : ScmProcedure("char-lower-case?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharLowerCaseQ.id)
                    1 -> {
                        val isLowerCase = (vm.stack.index(vm.sp, 0) as? ScmChar)?.isLowerCase()
                            ?: throw KevesExceptions.expectedChar(procCharLowerCaseQ.id)
                        val result = if (isLowerCase) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharLowerCaseQ.id)
                }
            }
        }
    }

    /** procedure: digit-value */
    val procDigitValue: ScmProcedure by lazy {
        object : ScmProcedure("digit-value", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procDigitValue.id)
                    1 -> {
                        val char = (vm.stack.index(vm.sp, 0) as? ScmChar)
                            ?: throw KevesExceptions.expectedChar(procDigitValue.id)
                        val value = char.digitToInt()
                        val result = if (value >= 0) ScmInt(value) else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procDigitValue.id)
                }
            }
        }
    }

    /** procedure: char->integer */
    val procCharToInteger: ScmProcedure by lazy {
        object : ScmProcedure("char->integer", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharToInteger.id)
                    1 -> {
                        val char = (vm.stack.index(vm.sp, 0) as? ScmChar)
                            ?: throw KevesExceptions.expectedChar(procCharToInteger.id)
                        val result = ScmInt(char.toUtf32())
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharToInteger.id)
                }
            }
        }
    }

    /** procedure: integer->char */
    val procIntegerToChar: ScmProcedure by lazy {
        object : ScmProcedure("integer->char", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procIntegerToChar.id)
                    1 -> {
                        val value = (vm.stack.index(vm.sp, 0) as? ScmInt)?.value
                            ?: throw KevesExceptions.expectedInt(procIntegerToChar.id)
                        val result = ScmChar(value)
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procIntegerToChar.id)
                }
            }
        }
    }

    /** procedure: char-upcase */
    val procCharUpcase: ScmProcedure by lazy {
        object : ScmProcedure("char-upcase", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharUpcase.id)
                    1 -> {
                        val char = (vm.stack.index(vm.sp, 0) as? ScmChar)
                            ?: throw KevesExceptions.expectedChar(procCharUpcase.id)
                        val result = ScmChar(char.toUpperCase())
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharUpcase.id)
                }
            }
        }
    }

    /** procedure: char-downcase */
    val procCharDowncase: ScmProcedure by lazy {
        object : ScmProcedure("char-downcase", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharDowncase.id)
                    1 -> {
                        val char = (vm.stack.index(vm.sp, 0) as? ScmChar)
                            ?: throw KevesExceptions.expectedChar(procCharDowncase.id)
                        val result = ScmChar(char.toLowerCase())
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharDowncase.id)
                }
            }
        }
    }

    /** procedure: char-foldcase */
    val procCharFoldcase: ScmProcedure by lazy {
        object : ScmProcedure("char-foldcase", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procCharFoldcase.id)
                    1 -> {
                        val char = (vm.stack.index(vm.sp, 0) as? ScmChar)
                            ?: throw KevesExceptions.expectedChar(procCharFoldcase.id)
                        val result = ScmChar(char.toFoldCase())
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCharFoldcase.id)
                }
            }
        }
    }
}