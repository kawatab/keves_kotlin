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

class R7rsNumber(private val res: KevesResources) {
    /** procedure: number? */
    val procNumberQ by lazy {
        res.addProcedure(object : ScmProcedure("number?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result =
                            when {
                                obj.isInt(res) || obj.isFloat(res) || obj.isDouble(res) -> res.constTrue
                                else -> res.constFalse
                            }
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: add */
    val procAdd: PtrProcedure by lazy {
        res.addProcedure(object : ScmProcedure("+", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val sp = vm.sp
                when (n) {
                    0 -> {
                        vm.scmProcReturn(ScmInt.make(0, vm.res).toObject(), n)
                    }
                    1 -> {
                        val obj = vm.stack.index(sp, 0)
                        when {
                            obj.isInt(res) || obj.isFloat(res) || obj.isDouble(res) -> vm.scmProcReturn(obj, n)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    }
                    2 -> {
                        val obj1 =
                            vm.stack.index(sp, 0).also { if (it.isNull()) throw KevesExceptions.expectedNumber(id) }
                        val obj2 =
                            vm.stack.index(sp, 1).also { if (it.isNull()) throw KevesExceptions.expectedNumber(id) }
                        vm.scmProcReturn(
                            try {
                                obj1.toVal(res)!!.add(obj2, vm.res)
                            } catch (e: IllegalArgumentException) {
                                throw KevesExceptions.expectedNumber(id)
                            },
                            n
                        )
                    }
                    else -> {
                        tailrec fun doubleLoop(index: Int, sum: Double): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> doubleLoop(index + 1, sum + obj.asInt(res).value)
                                    obj.isFloat(res) -> doubleLoop(index + 1, sum + obj.asFloat(res).value)
                                    obj.isDouble(res) -> doubleLoop(index + 1, sum + obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmDouble.make(sum, vm.res).toObject()
                            }

                        tailrec fun floatLoop(index: Int, sum: Float): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> floatLoop(index + 1, sum + obj.asInt(res).value)
                                    obj.isFloat(res) -> floatLoop(index + 1, sum + obj.asFloat(res).value)
                                    obj.isDouble(res) -> doubleLoop(index + 1, sum + obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmFloat.make(sum, vm.res).toObject()
                            }

                        tailrec fun loop(index: Int, sum: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> loop(index + 1, sum + obj.asInt(res).value)
                                    obj.isFloat(res) -> floatLoop(index + 1, sum + obj.asFloat(res).value)
                                    obj.isDouble(res) -> doubleLoop(index + 1, sum + obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmInt.make(sum, vm.res).toObject()
                            }

                        val sum = loop(0, 0)
                        vm.scmProcReturn(sum, n)
                    }
                }
            }
        })
    }

    /** procedure: subtract */
    val procSubtract by lazy {
        res.addProcedure(object : ScmProcedure("-", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val difference: PtrObject = when (n) {
                    0 -> throw KevesExceptions.expected1OrMoreDatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        when {
                            obj.isInt(res) -> ScmInt.make(-obj.asInt(res).value, vm.res).toObject() // opposite
                            obj.isFloat(res) -> ScmFloat.make(-obj.asFloat(res).value, vm.res).toObject() // opposite
                            obj.isDouble(res) -> ScmDouble.make(-obj.asDouble(res).value, vm.res).toObject() // opposite
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    }
                    2 -> {
                        val sp = vm.sp
                        val obj1 =
                            vm.stack.index(sp, 0).also { if (it.isNull()) throw KevesExceptions.expectedNumber(id) }
                        val obj2 =
                            vm.stack.index(sp, 1).also { if (it.isNull()) throw KevesExceptions.expectedNumber(id) }
                        try {
                            obj1.toVal(res)!!.subtract(obj2, vm.res)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedNumber(id)
                        }
                    }
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, difference: Double): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> doubleLoop(index + 1, difference - obj.asInt(res).value)
                                    obj.isFloat(res) -> doubleLoop(index + 1, difference - obj.asFloat(res).value)
                                    obj.isDouble(res) -> doubleLoop(index + 1, difference - obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmDouble.make(difference, vm.res).toObject()
                            }

                        tailrec fun floatLoop(index: Int, difference: Float): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> floatLoop(index + 1, difference - obj.asInt(res).value)
                                    obj.isFloat(res) -> floatLoop(index + 1, difference - obj.asFloat(res).value)
                                    obj.isDouble(res) -> doubleLoop(index + 1, difference - obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmFloat.make(difference, vm.res).toObject()
                            }

                        tailrec fun intLoop(index: Int, difference: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> intLoop(index + 1, difference - obj.asInt(res).value)
                                    obj.isFloat(res) -> floatLoop(index + 1, difference - obj.asFloat(res).value)
                                    obj.isDouble(res) -> doubleLoop(index + 1, difference - obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmInt.make(difference, vm.res).toObject()
                            }

                        val first = vm.stack.index(sp, 0)
                        when {
                            first.isInt(res) -> intLoop(1, first.asInt(res).value)
                            first.isFloat(res) -> floatLoop(1, first.asFloat(res).value)
                            first.isDouble(res) -> doubleLoop(1, first.asDouble(res).value)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    }
                }

                vm.scmProcReturn(difference, n)
            }
        })
    }

    /** procedure: multiple */
    val procMultiple by lazy {
        res.addProcedure(object : ScmProcedure("*", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val i = vm.sp
                tailrec fun doubleLoop(index: Int, product: Double): PtrObject =
                    if (index < n) {
                        val obj = vm.stack.index(i, index)
                        doubleLoop(
                            index = index + 1,
                            product = when {
                                obj.isInt(res) -> product * obj.asInt(res).value
                                obj.isFloat(res) -> product * obj.asInt(res).value
                                obj.isDouble(res) -> product * obj.asInt(res).value
                                else -> throw KevesExceptions.expectedNumber(id)
                            }
                        )
                    } else {
                        ScmDouble.make(product, vm.res).toObject()
                    }

                tailrec fun floatLoop(index: Int, product: Float): PtrObject =
                    if (index < n) {
                        val obj = vm.stack.index(i, index)
                        when {
                            obj.isInt(res) -> floatLoop(index = index + 1, product = product * obj.asInt(res).value)
                            obj.isFloat(res) -> floatLoop(index = index + 1, product = product * obj.asFloat(res).value)
                            obj.isDouble(res) -> doubleLoop(index + 1, product * obj.asDouble(res).value)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    } else {
                        ScmFloat.make(product, vm.res).toObject()
                    }

                tailrec fun intLoop(index: Int, product: Int): PtrObject =
                    if (index < n) {
                        val obj = vm.stack.index(i, index)
                        when {
                            obj.isInt(res) -> intLoop(index = index + 1, product = product * obj.asInt(res).value)
                            obj.isFloat(res) -> floatLoop(index + 1, product * obj.asFloat(res).value)
                            obj.isDouble(res) -> doubleLoop(index + 1, product * obj.asDouble(res).value)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    } else {
                        ScmInt.make(product, vm.res).toObject()
                    }

                val product = intLoop(0, 1)
                vm.scmProcReturn(product, n)
            }
        })
    }

    /** procedure: divide */
    val procDivide by lazy {
        res.addProcedure(object : ScmProcedure("/", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val quotient: PtrObject = when (n) {
                    0 -> throw KevesExceptions.expected1OrMoreDatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        when {
                            obj.isInt(res) -> when (obj.asInt(res).value) { // reciprocal
                                0 -> throw KevesExceptions.expectedNonZero(id)
                                -1, 1 -> ScmInt.make(obj.asInt(res).value, res).toObject() // TODO("remove ScmInt.make")
                                else -> ScmDouble.make(1.0 / obj.asInt(res).value.toDouble(), vm.res).toObject()
                            }

                            obj.isDouble(res) -> ScmDouble.make(1.0 / obj.asDouble(res).value, vm.res)
                                .toObject() // reciprocal

                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    }
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, quotient: Double): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.asInt(res).value
                                    )
                                    obj.isFloat(res) -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.asFloat(res).value
                                    )
                                    obj.isDouble(res) -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.asDouble(res).value
                                    )
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmDouble.make(quotient, vm.res).toObject()
                            }

                        tailrec fun floatLoop(index: Int, quotient: Float): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> floatLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.asInt(res).value
                                    )
                                    obj.isFloat(res) -> floatLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.asFloat(res).value
                                    )
                                    obj.isDouble(res) -> doubleLoop(index + 1, quotient / obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmFloat.make(quotient, vm.res).toObject()
                            }

                        tailrec fun intLoop(index: Int, quotient: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) -> {
                                        if (obj.asInt(res).value == 0) {
                                            throw KevesExceptions.expectedNonZero(id)
                                        }
                                        val remainder = quotient % obj.asInt(res).value
                                        if (remainder == 0) {
                                            intLoop(index = index + 1, quotient = quotient / obj.asInt(res).value)
                                        } else {
                                            doubleLoop(index + 1, quotient.toDouble() / obj.asInt(res).value.toDouble())
                                        }
                                    }
                                    obj.isFloat(res) -> floatLoop(index + 1, quotient / obj.asFloat(res).value)
                                    obj.isDouble(res) -> doubleLoop(index + 1, quotient / obj.asDouble(res).value)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmInt.make(quotient, vm.res).toObject()
                            }

                        val first = vm.stack.index(sp, 0)
                        when {
                            first.isInt(res) -> intLoop(1, first.asInt(res).value)
                            first.isFloat(res) -> floatLoop(1, first.asFloat(res).value)
                            first.isDouble(res) -> doubleLoop(1, first.asDouble(res).value)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    }
                }
                vm.scmProcReturn(quotient, n)
            }
        })
    }

    /** procedure: = */
    val procEqual by lazy {
        res.addProcedure(object : ScmProcedure("=", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) ->
                                        if (last == obj.asInt(res).value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.asInt(res).value.toDouble())
                                        else res.constFalse
                                    obj.isDouble(res) ->
                                        if (last == obj.asDouble(res).value) doubleLoop(
                                            index = index + 1,
                                            last = obj.asDouble(res).value
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        tailrec fun intLoop(index: Int, last: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) ->
                                        if (last == obj.asInt(res).value) intLoop(index + 1, obj.asInt(res).value)
                                        else res.constFalse
                                    obj.isDouble(res) ->
                                        if (last.toDouble() == obj.asDouble(res).value) doubleLoop(
                                            index + 1,
                                            obj.asDouble(res).value
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        val first = vm.stack.index(sp, 0)
                        val result = when {
                            first.isInt(res) -> intLoop(1, first.asInt(res).value)
                            first.isDouble(res) -> doubleLoop(1, first.asDouble(res).value)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: '<' */
    val procLessThan by lazy {
        res.addProcedure(object : ScmProcedure("<", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj1 =
                            vm.stack.index(sp, 0).also { if (it.isNull()) throw KevesExceptions.expectedNumber(id) }
                        val obj2 =
                            vm.stack.index(sp, 1).also { if (it.isNull()) throw KevesExceptions.expectedNumber(id) }
                        val result = try {
                            if (obj1.toVal(res)!!.isLessThan(obj2, res)) res.constTrue else res.constFalse
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedNumber(id)
                        }
                        vm.scmProcReturn(result, n)
                    }
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) ->
                                        if (last < obj.asInt(res).value.toDouble()) doubleLoop(
                                            index = index + 1,
                                            last = obj.asInt(res).value.toDouble()
                                        )
                                        else res.constFalse
                                    obj.isDouble(res) ->
                                        if (last < obj.asDouble(res).value) doubleLoop(
                                            index = index + 1,
                                            last = obj.asDouble(res).value
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        tailrec fun intLoop(index: Int, last: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) ->
                                        if (last < obj.asInt(res).value) intLoop(
                                            index = index + 1,
                                            last = obj.asInt(res).value
                                        )
                                        else res.constFalse
                                    obj.isDouble(res) ->
                                        if (last.toDouble() < obj.asDouble(res).value) doubleLoop(
                                            index + 1,
                                            obj.asDouble(res).value
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        val first = vm.stack.index(sp, 0)
                        val result = when {
                            first.isInt(res) -> intLoop(1, first.asInt(res).value)
                            first.isDouble(res) -> doubleLoop(1, first.asDouble(res).value)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: '>' */
    val procGraterThan by lazy {
        res.addProcedure(object : ScmProcedure(">", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) ->
                                        if (last > obj.asInt(res).value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.asInt(res).value.toDouble())
                                        else res.constFalse
                                    obj.isDouble(res) ->
                                        if (last > obj.asDouble(res).value) doubleLoop(
                                            index = index + 1,
                                            last = obj.asDouble(res).value
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        tailrec fun intLoop(index: Int, last: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt(res) ->
                                        if (last > obj.asInt(res).value) intLoop(
                                            index = index + 1,
                                            last = obj.asInt(res).value
                                        )
                                        else res.constFalse
                                    obj.isDouble(res) ->
                                        if (last.toDouble() > obj.asDouble(res).value) doubleLoop(
                                            index + 1,
                                            obj.asDouble(res).value
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        val first = vm.stack.index(sp, 0)
                        val result = when {
                            first.isInt(res) -> intLoop(1, first.asInt(res).value)
                            first.isDouble(res) -> doubleLoop(1, first.asDouble(res).value)
                            else -> throw KevesExceptions.expectedNumber(id)
                        }

                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: zero? */
    val procZeroQ by lazy {
        res.addProcedure(object : ScmProcedure("zero?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = when {
                            obj.isInt(res) -> if (obj.asInt(res).value == 0) res.constTrue else res.constFalse
                            obj.isFloat(res) -> if (obj.asFloat(res).value == 0f) res.constTrue else res.constFalse
                            obj.isDouble(res) -> if (obj.asDouble(res).value == 0.0) res.constTrue else res.constFalse
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }
}
