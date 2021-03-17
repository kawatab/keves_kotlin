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

import io.github.kawatab.keveskotlin.*
import io.github.kawatab.keveskotlin.objects.*

class R7rsChar(private val res: KevesResources) {
    /** procedure: char? */
    val procCharQ: PtrProcedure by lazy {
        res.addProcedure(object : ScmProcedure("char?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val ptr = vm.stack.index(vm.sp, 0)
                        val result = if (ptr.isChar(res)) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char=? */
    val procCharEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char=?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            val first = vm.stack.index(sp, 0).toChar().toUtf32(res)
                            for (i in 1 until n) {
                                val obj = vm.stack.index(sp, i).toChar().toUtf32(res)
                                if (first != obj) return vm.scmProcReturn(res.constFalse, n)
                            }
                            vm.scmProcReturn(res.constTrue, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char<?' */
    val procCharLessThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char<?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toUtf32(res)
                                    if (last < obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toUtf32(res)
                            val result = loop(1, first)

                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char<=?' */
    val procCharLessThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char<=?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toUtf32(res)
                                    if (last <= obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toUtf32(res)
                            val result = loop(1, first)
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char>?' */
    val procCharGraterThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char>?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toUtf32(res)
                                    if (last > obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toUtf32(res)
                            val result = loop(1, first)
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char>=?' */
    val procCharGraterThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char>=?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toUtf32(res)
                                    if (last >= obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toUtf32(res)
                            val result = loop(1, first)
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: char-ci=? */
    val procCharCIEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci=?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            val first = vm.stack.index(sp, 0).toChar().toLowerCase(res)
                            for (i in 1 until n) {
                                val obj = vm.stack.index(sp, i).toChar().toLowerCase(res)
                                if (first != obj) return vm.scmProcReturn(res.constFalse, n)
                            }
                            vm.scmProcReturn(res.constTrue, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char-ci<?' */
    val procCharCILessThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci<?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toLowerCase(res)
                                    if (last < obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toLowerCase(res)
                            val result = loop(1, first)
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char-ci<=?' */
    val procCharCILessThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci<=?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toLowerCase(res)
                                    if (last <= obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toLowerCase(res)
                            val result = loop(1, first)
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char-ci>?' */
    val procCharCIGraterThanQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci>?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toLowerCase(res)
                                    if (last > obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toLowerCase(res)
                            val result = loop(1, first)
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: 'char-ci>=?' */
    val procCharCIGraterThanEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("char-ci>=?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).toChar().toLowerCase(res)
                                    if (last >= obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).toChar().toLowerCase(res)
                            val result = loop(1, first)
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                }
            }
        })
    }

    /** procedure: char-alphabetic? */
    val procCharAlphabeticQ by lazy {
        res.addProcedure(object : ScmProcedure("char-alphabetic?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isAlphabetic = try {
                            vm.stack.index(vm.sp, 0).toChar().isAlphabetic(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = if (isAlphabetic) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-numeric? */
    val procCharNumericQ by lazy {
        res.addProcedure(object : ScmProcedure("char-numeric?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isNumeric = try {
                            vm.stack.index(vm.sp, 0).toChar().isNumeric(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = if (isNumeric) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-whitespace? */
    val procCharWhitespaceQ by lazy {
        res.addProcedure(object : ScmProcedure("char-whitespace?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isWhitespace = try {
                            vm.stack.index(vm.sp, 0).toChar().isWhitespace(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = if (isWhitespace) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-upper-case? */
    val procCharUpperCaseQ by lazy {
        res.addProcedure(object : ScmProcedure("char-upper-case?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isUpperCase = try {
                            vm.stack.index(vm.sp, 0).toChar().isUpperCase(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = if (isUpperCase) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-lower-case? */
    val procCharLowerCaseQ by lazy {
        res.addProcedure(object : ScmProcedure("char-lower-case?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isLowerCase = try {
                            vm.stack.index(vm.sp, 0).toChar().isLowerCase(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = if (isLowerCase) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: digit-value */
    val procDigitValue by lazy {
        res.addProcedure(object : ScmProcedure("digit-value", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = vm.stack.index(vm.sp, 0)
                            .also { if (it.isNotChar(res)) throw KevesExceptions.expectedChar(id) }
                            .toChar()
                        val value = char.digitToInt(res)
                        val result = if (value >= 0) ScmInt.make(value, vm.res).toObject() else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char->integer */
    val procCharToInteger by lazy {
        res.addProcedure(object : ScmProcedure("char->integer", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = vm.stack.index(vm.sp, 0)
                            .also { if (it.isNotChar(res)) throw KevesExceptions.expectedChar(id) }
                            .toChar()
                        val result = ScmInt.make(char.toUtf32(res), res).toObject()
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: integer->char */
    val procIntegerToChar by lazy {
        res.addProcedure(object : ScmProcedure("integer->char", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 ->
                        try {
                            val value = vm.stack.index(vm.sp, 0).toInt().value(res)
                            val result = ScmChar.make(value, vm.res).toObject()
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedInt(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-upcase */
    val procCharUpcase by lazy {
        res.addProcedure(object : ScmProcedure("char-upcase", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = vm.stack.index(vm.sp, 0)
                            .also { if (it.isNotChar(res)) throw KevesExceptions.expectedChar(id) }
                            .toChar()
                        val result = ScmChar.make(char.toUpperCase(res), res).toObject()
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-downcase */
    val procCharDowncase by lazy {
        res.addProcedure(object : ScmProcedure("char-downcase", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = vm.stack.index(vm.sp, 0)
                            .also { if (it.isNotChar(res)) throw KevesExceptions.expectedChar(id) }
                            .toChar()
                        val result = ScmChar.make(char.toLowerCase(res), res).toObject()
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: char-foldcase */
    val procCharFoldcase by lazy {
        res.addProcedure(object : ScmProcedure("char-foldcase", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = vm.stack.index(vm.sp, 0)
                            .also { if (it.isNotChar(res)) throw KevesExceptions.expectedChar(id) }
                            .toChar()
                        val result = ScmChar.make(char.toFoldCase(res), vm.res).toObject()
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }
}