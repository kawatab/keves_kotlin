/*
 * R7rsString.kt
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

class R7rsString(private val res: KevesResources) {
    /** procedure: string? */
    val procStringQ: PtrProcedure by lazy {
        res.addProcedure(object : ScmProcedure("string?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0).toVal(res)
                        val result = if (obj is ScmString) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: string=? */
    val procStringEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("string=?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        try {
                            val sp = vm.sp
                            val first = vm.stack.index(sp, 0).asString(res)
                            for (i in 1 until n) {
                                val obj = vm.stack.index(sp, i).asString(res)
                                if (!first.equalQ(obj, res)) return vm.scmProcReturn(res.constFalse, n)
                            }
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedString(id)
                        }
                        vm.scmProcReturn(res.constTrue, n)
                    }
                }
            }
        })
    }
}