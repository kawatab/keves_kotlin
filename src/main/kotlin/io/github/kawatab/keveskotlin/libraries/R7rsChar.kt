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
import io.github.kawatab.keveskotlin.KevesResources
import io.github.kawatab.keveskotlin.KevesVM
import io.github.kawatab.keveskotlin.PtrObject
import io.github.kawatab.keveskotlin.objects.*

class R7rsChar(private val res: KevesResources) {
    /** procedure: char? */
    val procCharQ by lazy {
        res.addProcedure(object : ScmProcedure("char?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val ptr = vm.stack.index(vm.sp, 0)
                        val result = if (res.get(ptr) is ScmChar) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char=? */
    val procCharEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedChar(id)
                        for (i in 1 until n) {
                            val obj = (res.get(vm.stack.index(sp, i)) as? ScmChar)?.toUtf32()
                                ?: throw KevesExceptions.expectedChar(id)
                            if (first != obj) return vm.scmProcReturn(res.constFalse, n)
                        }
                        vm.scmProcReturn(res.constTrue, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char<?' */
    val procCharLessThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char<?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last < obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char<=?' */
    val procCharLessThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char<=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last <= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char>?' */
    val procCharGraterThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char>?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last > obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char>=?' */
    val procCharGraterThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char>=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toUtf32()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last >= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toUtf32()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: char-ci=? */
    val procCharCIEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedChar(id)
                        for (i in 1 until n) {
                            val obj = (res.get(vm.stack.index(sp, i)) as? ScmChar)?.toLowerCase()
                                ?: throw KevesExceptions.expectedChar(id)
                            if (first != obj) return vm.scmProcReturn(res.constFalse, n)
                        }
                        vm.scmProcReturn(res.constTrue, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char-ci<?' */
    val procCharCILessThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci<?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last < obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char-ci<=?' */
    val procCharCILessThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci<=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last <= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char-ci>?' */
    val procCharCIGraterThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci>?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last > obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: 'char-ci>=?' */
    val procCharCIGraterThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci>=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                val obj = (res.get(vm.stack.index(sp, index)) as? ScmChar)?.toLowerCase()
                                    ?: throw KevesExceptions.expectedNumber(id)
                                if (last >= obj) loop(index = index + 1, last = obj)
                                else ScmConstant.FALSE
                            } else {
                                ScmConstant.TRUE
                            }

                        val first = (res.get(vm.stack.index(sp, 0)) as? ScmChar)?.toLowerCase()
                            ?: throw KevesExceptions.expectedNumber(id)
                        val result = res.add(loop(1, first))

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: char-alphabetic? */
    val procCharAlphabeticQ by lazy {
        res.addProcedure(object : ScmProcedure("char-alphabetic?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isAlphabetic = (res.get(vm.stack.index(vm.sp, 0)) as? ScmChar)?.isAlphabetic()
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = if (isAlphabetic) res.constTrue else res.constFalse //ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-numeric? */
    val procCharNumericQ by lazy {
        res.addProcedure(object : ScmProcedure("char-numeric?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isNumeric = (res.get(vm.stack.index(vm.sp, 0)) as? ScmChar)?.isNumeric()
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = if (isNumeric) res.constTrue else res.constFalse //ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-whitespace? */
    val procCharWhitespaceQ by lazy {
        res.addProcedure(object : ScmProcedure("char-whitespace?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isWhitespace = (res.get(vm.stack.index(vm.sp, 0)) as? ScmChar)?.isWhitespace()
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = if (isWhitespace) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-upper-case? */
    val procCharUpperCaseQ by lazy {
        res.addProcedure(object : ScmProcedure("char-upper-case?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isUpperCase = (res.get(vm.stack.index(vm.sp, 0)) as? ScmChar)?.isUpperCase()
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = if (isUpperCase) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-lower-case? */
    val procCharLowerCaseQ by lazy {
        res.addProcedure(object : ScmProcedure("char-lower-case?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isLowerCase = (res.get(vm.stack.index(vm.sp, 0)) as? ScmChar)?.isLowerCase()
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = if (isLowerCase) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: digit-value */
    val procDigitValue by lazy {
        res.addProcedure(object : ScmProcedure("digit-value", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = (res.get(vm.stack.index(vm.sp, 0)) as? ScmChar)
                            ?: throw KevesExceptions.expectedChar(id)
                        val value = char.digitToInt()
                        val result = if (value >= 0) ScmInt.make(value, vm.res) else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char->integer */
    val procCharToInteger by lazy {
        res.addProcedure(object : ScmProcedure("char->integer", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = (res.get(vm.stack.index(vm.sp, 0)) as? ScmChar)
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = ScmInt.make(char.toUtf32(), vm.res)
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: integer->char */
    val procIntegerToChar by lazy {
        res.addProcedure(object : ScmProcedure("integer->char", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val value = (res.get(vm.stack.index(vm.sp, 0)) as? ScmInt)?.value
                            ?: throw KevesExceptions.expectedInt(id)
                        val result = ScmChar.make(value, vm.res)
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-upcase */
    val procCharUpcase by lazy {
        res.addProcedure(object : ScmProcedure("char-upcase", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = res.get(vm.stack.index(vm.sp, 0)) as? ScmChar
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = ScmChar.make(char.toUpperCase(), vm.res)
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-downcase */
    val procCharDowncase by lazy {
        res.addProcedure(object : ScmProcedure("char-downcase", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = res.get(vm.stack.index(vm.sp, 0)) as? ScmChar
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = ScmChar.make(char.toLowerCase(), vm.res)
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-foldcase */
    val procCharFoldcase by lazy {
        res.addProcedure(object : ScmProcedure("char-foldcase", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = res.get(vm.stack.index(vm.sp, 0)) as? ScmChar
                            ?: throw KevesExceptions.expectedChar(id)
                        val result = ScmChar.make(char.toFoldCase(), vm.res)
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }
}