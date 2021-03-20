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
        res.addProcedure(object : ScmProcedure("number?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result =
                            when {
                                obj.isInt() || obj.isFloat() || obj.isDouble() -> res.constTrue
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
        res.addProcedure(object : ScmProcedure("+", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val sp = vm.sp
                when (n) {
                    0 -> {
                        vm.scmProcReturn(KevesResources.makeInt(0), n)
                    }
                    1 -> {
                        val obj = vm.stack.index(sp, 0)
                        when {
                            obj.isInt() || obj.isFloat() || obj.isDouble() -> vm.scmProcReturn(obj, n)
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
                                when {
                                    obj1.isInt() ->
                                        when {
                                            obj2.isInt() -> KevesResources.makeInt(obj1.toInt().value + obj2.toInt().value)
                                            obj2.isFloat() -> obj2.toFloat().add(obj1, vm.res)
                                            obj2.isDouble() -> obj2.toDouble().add(obj1, vm.res)
                                            else -> throw KevesExceptions.expectedNumber(id)
                                        }
                                    obj1.isFloat() -> obj1.toFloat().add(obj2, vm.res)
                                    obj1.isDouble() -> obj1.toDouble().add(obj2, vm.res)
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
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
                                    obj.isInt() -> doubleLoop(index + 1, sum + obj.toInt().value)
                                    obj.isFloat() -> doubleLoop(index + 1, sum + obj.toFloat().getValue(res))
                                    obj.isDouble() -> doubleLoop(index + 1, sum + obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmDouble.make(sum, vm.res).toObject()
                            }

                        tailrec fun floatLoop(index: Int, sum: Float): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt() -> floatLoop(index + 1, sum + obj.toInt().value)
                                    obj.isFloat() -> floatLoop(index + 1, sum + obj.toFloat().getValue(res))
                                    obj.isDouble() -> doubleLoop(index + 1, sum + obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmFloat.make(sum, vm.res).toObject()
                            }

                        tailrec fun loop(index: Int, sum: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt() -> loop(index + 1, sum + obj.toInt().value)
                                    obj.isFloat() -> floatLoop(index + 1, sum + obj.toFloat().getValue(res))
                                    obj.isDouble() -> doubleLoop(index + 1, sum + obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                KevesResources.makeInt(sum)
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
        res.addProcedure(object : ScmProcedure("-", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val difference: PtrObject = when (n) {
                    0 -> throw KevesExceptions.expected1OrMoreDatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        when {
                            obj.isInt() -> KevesResources.makeInt(-obj.toInt().value) // opposite
                            obj.isFloat() -> ScmFloat.make(-obj.toFloat().getValue(res), vm.res).toObject() // opposite
                            obj.isDouble() -> ScmDouble.make(-obj.toDouble().getValue(res), vm.res)
                                .toObject() // opposite
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
                            when {
                                obj1.isInt() ->
                                    when {
                                        obj2.isInt() -> KevesResources.makeInt(obj1.toInt().value - obj2.toInt().value)
                                        obj2.isFloat() -> ScmFloat.make(obj1.toInt().value.toFloat() - obj2.toFloat().getValue(res), res).toObject()
                                        obj2.isDouble() -> ScmDouble.make(obj1.toInt().value.toDouble() - obj2.toDouble().getValue(res), res).toObject()
                                        else -> throw KevesExceptions.expectedNumber(id)
                                    }
                                obj1.isFloat() -> obj1.toFloat().toVal(res).subtract(obj2, vm.res)
                                obj1.isDouble() -> obj1.toDouble().toVal(res).subtract(obj2, vm.res)
                                else -> throw KevesExceptions.expectedNumber(id)
                            }
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
                                    obj.isInt() -> doubleLoop(index + 1, difference - obj.toInt().value)
                                    obj.isFloat() -> doubleLoop(index + 1, difference - obj.toFloat().getValue(res))
                                    obj.isDouble() -> doubleLoop(index + 1, difference - obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmDouble.make(difference, vm.res).toObject()
                            }

                        tailrec fun floatLoop(index: Int, difference: Float): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt() -> floatLoop(index + 1, difference - obj.toInt().value)
                                    obj.isFloat() -> floatLoop(index + 1, difference - obj.toFloat().getValue(res))
                                    obj.isDouble() -> doubleLoop(index + 1, difference - obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmFloat.make(difference, vm.res).toObject()
                            }

                        tailrec fun intLoop(index: Int, difference: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt() -> intLoop(index + 1, difference - obj.toInt().value)
                                    obj.isFloat() -> floatLoop(index + 1, difference - obj.toFloat().getValue(res))
                                    obj.isDouble() -> doubleLoop(index + 1, difference - obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                KevesResources.makeInt(difference)
                            }

                        val first = vm.stack.index(sp, 0)
                        when {
                            first.isInt() -> intLoop(1, first.toInt().value)
                            first.isFloat() -> floatLoop(1, first.toFloat().getValue(res))
                            first.isDouble() -> doubleLoop(1, first.toDouble().getValue(res))
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
        res.addProcedure(object : ScmProcedure("*", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val i = vm.sp
                tailrec fun doubleLoop(index: Int, product: Double): PtrObject =
                    if (index < n) {
                        val obj = vm.stack.index(i, index)
                        doubleLoop(
                            index = index + 1,
                            product = when {
                                obj.isInt() -> product * obj.toInt().value
                                obj.isFloat() -> product * obj.toFloat().getValue(res)
                                obj.isDouble() -> product * obj.toDouble().getValue(res)
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
                            obj.isInt() -> floatLoop(index = index + 1, product = product * obj.toInt().value)
                            obj.isFloat() -> floatLoop(
                                index = index + 1,
                                product = product * obj.toFloat().getValue(res)
                            )
                            obj.isDouble() -> doubleLoop(index + 1, product * obj.toDouble().getValue(res))
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    } else {
                        ScmFloat.make(product, vm.res).toObject()
                    }

                tailrec fun intLoop(index: Int, product: Int): PtrObject =
                    if (index < n) {
                        val obj = vm.stack.index(i, index)
                        when {
                            obj.isInt() -> intLoop(index = index + 1, product = product * obj.toInt().value)
                            obj.isFloat() -> floatLoop(index + 1, product * obj.toFloat().getValue(res))
                            obj.isDouble() -> doubleLoop(index + 1, product * obj.toDouble().getValue(res))
                            else -> throw KevesExceptions.expectedNumber(id)
                        }
                    } else {
                        KevesResources.makeInt(product)
                    }

                val product = intLoop(0, 1)
                vm.scmProcReturn(product, n)
            }
        })
    }

    /** procedure: divide */
    val procDivide by lazy {
        res.addProcedure(object : ScmProcedure("/", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val quotient: PtrObject = when (n) {
                    0 -> throw KevesExceptions.expected1OrMoreDatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        when {
                            obj.isInt() -> when (obj.toInt().value) { // reciprocal
                                0 -> throw KevesExceptions.expectedNonZero(id)
                                -1, 1 -> KevesResources.makeInt(obj.toInt().value)
                                else -> ScmDouble.make(1.0 / obj.toInt().value.toDouble(), vm.res).toObject()
                            }

                            obj.isDouble() -> ScmDouble.make(1.0 / obj.toDouble().getValue(res), vm.res)
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
                                    obj.isInt() -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.toInt().value
                                    )
                                    obj.isFloat() -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.toFloat().getValue(res)
                                    )
                                    obj.isDouble() -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.toDouble().getValue(res)
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
                                    obj.isInt() -> floatLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.toInt().value
                                    )
                                    obj.isFloat() -> floatLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.toFloat().getValue(res)
                                    )
                                    obj.isDouble() -> doubleLoop(index + 1, quotient / obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                ScmFloat.make(quotient, vm.res).toObject()
                            }

                        tailrec fun intLoop(index: Int, quotient: Int): PtrObject =
                            if (index < n) {
                                val obj = vm.stack.index(sp, index)
                                when {
                                    obj.isInt() -> {
                                        if (obj.toInt().value == 0) {
                                            throw KevesExceptions.expectedNonZero(id)
                                        }
                                        val remainder = quotient % obj.toInt().value
                                        if (remainder == 0) {
                                            intLoop(index = index + 1, quotient = quotient / obj.toInt().value)
                                        } else {
                                            doubleLoop(
                                                index + 1,
                                                quotient.toDouble() / obj.toInt().value.toDouble()
                                            )
                                        }
                                    }
                                    obj.isFloat() -> floatLoop(index + 1, quotient / obj.toFloat().getValue(res))
                                    obj.isDouble() -> doubleLoop(index + 1, quotient / obj.toDouble().getValue(res))
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                KevesResources.makeInt(quotient)
                            }

                        val first = vm.stack.index(sp, 0)
                        when {
                            first.isInt() -> intLoop(1, first.toInt().value)
                            first.isFloat() -> floatLoop(1, first.toFloat().getValue(res))
                            first.isDouble() -> doubleLoop(1, first.toDouble().getValue(res))
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
        res.addProcedure(object : ScmProcedure("=", PtrSyntaxOrNull(0)) {
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
                                    obj.isInt() ->
                                        if (last == obj.toInt().value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.toInt().value.toDouble())
                                        else res.constFalse
                                    obj.isDouble() ->
                                        if (last == obj.toDouble().getValue(res)) doubleLoop(
                                            index = index + 1,
                                            last = obj.toDouble().getValue(res)
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
                                    obj.isInt() ->
                                        if (last == obj.toInt().value) intLoop(index + 1, obj.toInt().value)
                                        else res.constFalse
                                    obj.isDouble() ->
                                        if (last.toDouble() == obj.toDouble().getValue(res)) doubleLoop(
                                            index + 1,
                                            obj.toDouble().getValue(res)
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        val first = vm.stack.index(sp, 0)
                        val result = when {
                            first.isInt() -> intLoop(1, first.toInt().value)
                            first.isDouble() -> doubleLoop(1, first.toDouble().getValue(res))
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
        res.addProcedure(object : ScmProcedure("<", PtrSyntaxOrNull(0)) {
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
                            when {
                                obj1.isInt() ->
                                    when {
                                        obj2.isInt() -> if (obj1.toInt().value < obj2.toInt().value) res.constTrue else res.constFalse
                                        obj2.isFloat() -> if (obj1.toInt().value < obj2.toFloat().getValue(res)) res.constTrue else res.constFalse
                                        obj2.isDouble() -> if (obj1.toInt().value < obj2.toDouble().getValue(res)) res.constTrue else res.constFalse
                                        else -> throw KevesExceptions.expectedNumber(id)
                                    }
                                obj1.isFloat() ->
                                    if (obj1.toFloat().toVal(res).isLessThan(obj2, res)) res.constTrue
                                    else res.constFalse
                                obj1.isDouble() ->
                                    if (obj1.toDouble().toVal(res).isLessThan(obj2, res)) res.constTrue
                                    else res.constFalse
                                else -> throw KevesExceptions.expectedNumber(id)

                            }
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
                                    obj.isInt() ->
                                        if (last < obj.toInt().value.toDouble()) doubleLoop(
                                            index = index + 1,
                                            last = obj.toInt().value.toDouble()
                                        )
                                        else res.constFalse
                                    obj.isDouble() ->
                                        if (last < obj.toDouble().getValue(res)) doubleLoop(
                                            index = index + 1,
                                            last = obj.toDouble().getValue(res)
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
                                    obj.isInt() ->
                                        if (last < obj.toInt().value) intLoop(
                                            index = index + 1,
                                            last = obj.toInt().value
                                        )
                                        else res.constFalse
                                    obj.isDouble() ->
                                        if (last.toDouble() < obj.toDouble().getValue(res)) doubleLoop(
                                            index + 1,
                                            obj.toDouble().getValue(res)
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        val first = vm.stack.index(sp, 0)
                        val result = when {
                            first.isInt() -> intLoop(1, first.toInt().value)
                            first.isDouble() -> doubleLoop(1, first.toDouble().getValue(res))
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
        res.addProcedure(object : ScmProcedure(">", PtrSyntaxOrNull(0)) {
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
                                    obj.isInt() ->
                                        if (last > obj.toInt().value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.toInt().value.toDouble())
                                        else res.constFalse
                                    obj.isDouble() ->
                                        if (last > obj.toDouble().getValue(res)) doubleLoop(
                                            index = index + 1,
                                            last = obj.toDouble().getValue(res)
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
                                    obj.isInt() ->
                                        if (last > obj.toInt().value) intLoop(
                                            index = index + 1,
                                            last = obj.toInt().value
                                        )
                                        else res.constFalse
                                    obj.isDouble() ->
                                        if (last.toDouble() > obj.toDouble().getValue(res)) doubleLoop(
                                            index + 1,
                                            obj.toDouble().getValue(res)
                                        )
                                        else res.constFalse
                                    else -> throw KevesExceptions.expectedNumber(id)
                                }
                            } else {
                                res.constTrue
                            }

                        val first = vm.stack.index(sp, 0)
                        val result = when {
                            first.isInt() -> intLoop(1, first.toInt().value)
                            first.isDouble() -> doubleLoop(1, first.toDouble().getValue(res))
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
        res.addProcedure(object : ScmProcedure("zero?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = when {
                            obj.isInt() -> if (obj.toInt().value == 0) res.constTrue else res.constFalse
                            obj.isFloat() -> if (obj.toFloat().getValue(res) == 0f) res.constTrue else res.constFalse
                            obj.isDouble() -> if (obj.toDouble().getValue(res) == 0.0) res.constTrue else res.constFalse
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
