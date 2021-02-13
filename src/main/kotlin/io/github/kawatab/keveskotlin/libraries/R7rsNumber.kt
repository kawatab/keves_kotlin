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

object R7rsNumber {
    /** procedure: number? */
    val procNumberQ: ScmProcedure by lazy {
        object : ScmProcedure("number?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGotMore(procNumberQ.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result =
                            if (obj is ScmInt || obj is ScmFloat || obj is ScmDouble) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procNumberQ.id)
                }
            }
        }
    }

    /** procedure: plus */
    val procAdd: ScmProcedure by lazy {
        object : ScmProcedure("+", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val sp = vm.sp
                tailrec fun doubleLoop(index: Int, sum: Double): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(sp, index)) {
                            is ScmInt -> doubleLoop(index + 1, sum + obj.value)
                            is ScmDouble -> doubleLoop(index + 1, sum + obj.value)
                            else -> throw KevesExceptions.expectedNumber(procAdd.id)
                        }
                    } else {
                        ScmDouble(sum)
                    }

                tailrec fun loop(index: Int, sum: Int): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(sp, index)) {
                            is ScmInt -> loop(index + 1, sum + obj.value)
                            is ScmDouble -> doubleLoop(index + 1, sum + obj.value)
                            else -> throw KevesExceptions.expectedNumber(procAdd.id)
                        }
                    } else {
                        ScmInt(sum)
                    }

                val sum = loop(0, 0)
                vm.scmProcReturn(sum, n, this)
            }
        }
    }

    /** procedure: minus */
    val procSubtract: ScmProcedure by lazy {
        object : ScmProcedure("-", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val difference = when (n) {
                    0 -> throw KevesExceptions.expected1OrMoreDatumGot0(procSubtract.id)
                    1 -> {
                        when (val obj = vm.stack.index(vm.sp, 0)) {
                            is ScmInt -> ScmInt(-obj.value) // opposite
                            is ScmDouble -> ScmDouble(-obj.value) // opposite
                            else -> throw KevesExceptions.expectedNumber(procSubtract.id)
                        }
                    }
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, difference: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> doubleLoop(index + 1, difference - obj.value)
                                    is ScmDouble -> doubleLoop(index + 1, difference - obj.value)
                                    else -> throw KevesExceptions.expectedNumber(procSubtract.id)
                                }
                            } else {
                                ScmDouble(difference)
                            }

                        tailrec fun intLoop(index: Int, difference: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> intLoop(index + 1, difference - obj.value)
                                    is ScmDouble -> doubleLoop(index + 1, difference - obj.value)
                                    else -> throw KevesExceptions.expectedNumber(procSubtract.id)
                                }
                            } else {
                                ScmInt(difference)
                            }

                        when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw KevesExceptions.expectedNumber(procSubtract.id)
                        }
                    }
                }

                vm.scmProcReturn(difference, n, this)
            }
        }
    }

    /** procedure: multiple */
    val procMultiple: ScmProcedure by lazy {
        object : ScmProcedure("*", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val i = vm.sp
                tailrec fun doubleLoop(index: Int, product: Double): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(i, index)) {
                            is ScmInt -> doubleLoop(index = index + 1, product = product * obj.value)
                            is ScmDouble -> doubleLoop(index = index + 1, product = product * obj.value)
                            else -> throw KevesExceptions.expectedNumber(procMultiple.id)
                        }
                    } else {
                        ScmDouble(product)
                    }

                tailrec fun intLoop(index: Int, product: Int): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(i, index)) {
                            is ScmInt -> intLoop(index = index + 1, product = product * obj.value)
                            is ScmDouble -> doubleLoop(index + 1, product * obj.value)
                            else -> throw KevesExceptions.expectedNumber(procMultiple.id)
                        }
                    } else {
                        ScmInt(product)
                    }

                val product = intLoop(0, 1)
                vm.scmProcReturn(product, n, this)
            }
        }
    }

    /** procedure: divide */
    val procDivide: ScmProcedure by lazy {
        object : ScmProcedure("/", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val quotient = when (n) {
                    0 -> throw KevesExceptions.expected1OrMoreDatumGot0(procDivide.id)
                    1 -> {
                        when (val obj = vm.stack.index(vm.sp, 0)) {
                            is ScmInt -> when (obj.value) { // reciprocal
                                0 -> throw KevesExceptions.expectedNonZero(procDivide.id)
                                -1, 1 -> obj
                                else -> ScmDouble(1.0 / obj.value.toDouble())
                            }

                            is ScmDouble -> ScmDouble(1.0 / obj.value) // reciprocal

                            else -> throw KevesExceptions.expectedNumber(procDivide.id)
                        }
                    }
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, quotient: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.value.toDouble()
                                    )
                                    is ScmDouble -> doubleLoop(index = index + 1, quotient = quotient / obj.value)
                                    else -> throw KevesExceptions.expectedNumber(procDivide.id)
                                }
                            } else {
                                ScmDouble(quotient)
                            }

                        tailrec fun intLoop(index: Int, quotient: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> {
                                        if (obj.value == 0) {
                                            throw KevesExceptions.expectedNonZero(procDivide.id)
                                        }
                                        val remainder = quotient % obj.value
                                        if (remainder == 0) {
                                            intLoop(index = index + 1, quotient = quotient / obj.value)
                                        } else {
                                            doubleLoop(index + 1, quotient.toDouble() / obj.value.toDouble())
                                        }
                                    }
                                    is ScmDouble -> doubleLoop(index + 1, quotient.toDouble() / obj.value)
                                    else -> throw KevesExceptions.expectedNumber(procDivide.id)
                                }
                            } else {
                                ScmInt(quotient)
                            }

                        when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw KevesExceptions.expectedNumber(procDivide.id)
                        }
                    }
                }
                vm.scmProcReturn(quotient, n, this)
            }
        }
    }

    /** procedure: = */
    val procEqual: ScmProcedure by lazy {
        object : ScmProcedure("=", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procEqual.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last == obj.value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.value.toDouble())
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last == obj.value) doubleLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw KevesExceptions.expectedNumber(procEqual.id)
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        tailrec fun intLoop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last == obj.value) intLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last.toDouble() == obj.value) doubleLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw KevesExceptions.expectedNumber(procEqual.id)
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        val result = when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw KevesExceptions.expectedNumber(procEqual.id)
                        }

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: '<' */
    val procLessThan: ScmProcedure by lazy {
        object : ScmProcedure("<", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procLessThan.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last < obj.value.toDouble()) doubleLoop(
                                            index = index + 1,
                                            last = obj.value.toDouble()
                                        )
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last < obj.value) doubleLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw KevesExceptions.expectedNumber(procLessThan.id)
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        tailrec fun intLoop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last < obj.value) intLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last.toDouble() < obj.value) doubleLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw KevesExceptions.expectedNumber(procLessThan.id)
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        val result = when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw KevesExceptions.expectedNumber(procLessThan.id)
                        }

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: '>' */
    val procGraterThan: ScmProcedure by lazy {
        object : ScmProcedure(">", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procGraterThan.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last > obj.value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.value.toDouble())
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last > obj.value) doubleLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw KevesExceptions.expectedNumber(procGraterThan.id)
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        tailrec fun intLoop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last > obj.value) intLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last.toDouble() > obj.value) doubleLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw KevesExceptions.expectedNumber(procGraterThan.id)
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        val result = when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw KevesExceptions.expectedNumber(procGraterThan.id)
                        }

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: zero? */
    val procZeroQ: ScmProcedure by lazy {
        object : ScmProcedure("zero?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procZeroQ.id)
                    1 -> {
                        val result = when (val obj = vm.stack.index(vm.sp, 0)) {
                            is ScmInt -> if (obj.value == 0) ScmConstant.TRUE else ScmConstant.FALSE
                            is ScmFloat -> if (obj.value == 0f) ScmConstant.TRUE else ScmConstant.FALSE
                            is ScmDouble -> if (obj.value == 0.0) ScmConstant.TRUE else ScmConstant.FALSE
                            else -> throw KevesExceptions.expectedNumber(procZeroQ.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procZeroQ.id)
                }
            }
        }
    }
}
