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
        res.addProcedure(object : ScmProcedure("char?", null) {
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
        res.addProcedure(object : ScmProcedure("char=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            val first = vm.stack.index(sp, 0).asChar(res).toUtf32()
                            for (i in 1 until n) {
                                val obj = vm.stack.index(sp, i).asChar(res).toUtf32()
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
        res.addProcedure(object : ScmProcedure("char<?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toUtf32()
                                    if (last < obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toUtf32()
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
        res.addProcedure(object : ScmProcedure("char<=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toUtf32()
                                    if (last <= obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toUtf32()
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
        res.addProcedure(object : ScmProcedure("char>?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toUtf32()
                                    if (last > obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toUtf32()
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
        res.addProcedure(object : ScmProcedure("char>=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toUtf32()
                                    if (last >= obj) loop(index = index + 1, last = obj)
                                    else res.constFalse
                                } else {
                                    res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toUtf32()
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
        res.addProcedure(object : ScmProcedure("char-ci=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            val first = vm.stack.index(sp, 0).asChar(res).toLowerCase()
                            for (i in 1 until n) {
                                val obj = vm.stack.index(sp, i).asChar(res).toLowerCase()
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
        res.addProcedure(object : ScmProcedure("char-ci<?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toLowerCase()
                                    if (last < obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toLowerCase()
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
        res.addProcedure(object : ScmProcedure("char-ci<=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toLowerCase()
                                    if (last <= obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toLowerCase()
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
        res.addProcedure(object : ScmProcedure("char-ci>?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toLowerCase()
                                    if (last > obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toLowerCase()
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
        res.addProcedure(object : ScmProcedure("char-ci>=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else ->
                        try {
                            val sp = vm.sp
                            tailrec fun loop(index: Int, last: Int): PtrObject =
                                if (index < n) {
                                    val obj = vm.stack.index(sp, index).asChar(res).toLowerCase()
                                    if (last >= obj) loop(index = index + 1, last = obj)
                                    else vm.res.constFalse
                                } else {
                                    vm.res.constTrue
                                }

                            val first = vm.stack.index(sp, 0).asChar(res).toLowerCase()
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
        res.addProcedure(object : ScmProcedure("char-alphabetic?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isAlphabetic = try {
                            vm.stack.index(vm.sp, 0).asChar(res).isAlphabetic()
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
        res.addProcedure(object : ScmProcedure("char-numeric?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isNumeric = try {
                            vm.stack.index(vm.sp, 0).asChar(res).isNumeric()
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
        res.addProcedure(object : ScmProcedure("char-whitespace?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isWhitespace = try {
                            vm.stack.index(vm.sp, 0).asChar(res).isWhitespace()
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
        res.addProcedure(object : ScmProcedure("char-upper-case?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isUpperCase = try {
                            vm.stack.index(vm.sp, 0).asChar(res).isUpperCase()
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
        res.addProcedure(object : ScmProcedure("char-lower-case?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val isLowerCase = try {
                            vm.stack.index(vm.sp, 0).asChar(res).isLowerCase()
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
        res.addProcedure(object : ScmProcedure("digit-value", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = try {
                            vm.stack.index(vm.sp, 0).asChar(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val value = char.digitToInt()
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
        res.addProcedure(object : ScmProcedure("char->integer", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = try {
                            vm.stack.index(vm.sp, 0).asChar(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = ScmInt.make(char.toUtf32(), vm.res).toObject()
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
                    1 ->
                        try {
                            val value = vm.stack.index(vm.sp, 0).asInt(res).value
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
        res.addProcedure(object : ScmProcedure("char-upcase", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val char = try {
                            vm.stack.index(vm.sp, 0).asChar(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = ScmChar.make(char.toUpperCase(), vm.res).toObject()
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
                        val char = try {
                            vm.stack.index(vm.sp, 0).asChar(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = ScmChar.make(char.toLowerCase(), vm.res).toObject()
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
                        val char = try {
                            vm.stack.index(vm.sp, 0).asChar(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedChar(id)
                        }
                        val result = ScmChar.make(char.toFoldCase(), vm.res).toObject()
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }
}