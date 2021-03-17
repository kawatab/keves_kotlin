/*
 * R7rsSymbol.kt
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
import java.lang.IllegalArgumentException

class R7rsSymbol(private val res: KevesResources) {
    /** procedure: symbol? */
    val procSymbolQ: PtrProcedure by lazy {
        res.addProcedure(object : ScmProcedure("symbol?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (obj.isSymbol(res)) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: symbol=? */
    val procSymbolEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("symbol=", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        try {
                            val sp = vm.sp
                            val first = vm.stack.index(sp, 0)
                            if (first.isNotSymbol(res)) throw KevesExceptions.expectedSymbol(id)
                            for (i in 1 until n) {
                                val obj = vm.stack.index(sp, i)
                                if (obj.isNotSymbol(res)) throw KevesExceptions.expectedSymbol(id)
                                if (first != obj) return vm.scmProcReturn(res.constFalse, n)
                            }
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedSymbol(id)
                        }
                        vm.scmProcReturn(res.constTrue, n)
                    }
                }
            }
        })
    }

    /** procedure: symbol->string */
    val procSymbolToString by lazy {
        res.addProcedure(object : ScmProcedure("symbol->string", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            if (obj.isNotSymbol(res)) throw KevesExceptions.expectedSymbol(id)
                            val result = ScmString.make(obj.toSymbol().getRawString(res), vm.res).toObject()
                            vm.scmProcReturn(result, n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedSymbol(id)
                        }
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: string->symbol */
    val procStringToSymbol by lazy {
        res.addProcedure(object : ScmProcedure("string->symbol", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val obj = try {
                            vm.stack.index(vm.sp, 0).toString2()
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedSymbol(id)
                        }

                        val result = ScmSymbol.get(obj.toStringForDisplay(res), vm.res).toObject()
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }
}