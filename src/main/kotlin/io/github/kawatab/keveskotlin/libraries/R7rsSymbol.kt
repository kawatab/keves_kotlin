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

import io.github.kawatab.keveskotlin.KevesExceptions
import io.github.kawatab.keveskotlin.KevesVM
import io.github.kawatab.keveskotlin.objects.*

object R7rsSymbol {
    /** procedure: symbol? */
    val procSymbolQ: ScmProcedure by lazy {
        object : ScmProcedure("symbol?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procSymbolQ.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (obj is ScmSymbol) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procSymbolQ.id)
                }
            }
        }
    }

    /** procedure: symbol=? */
    val procSymbolEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("symbol=", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procSymbolEqualQ.id)
                    else -> {
                        val sp = vm.sp
                        val first = vm.stack.index(sp, 0) as? ScmSymbol
                            ?: throw KevesExceptions.expectedSymbol(procSymbolEqualQ.id)
                        for (i in 1 until n) {
                            val obj = vm.stack.index(sp, i) as? ScmSymbol
                                ?: throw KevesExceptions.expectedSymbol(procSymbolEqualQ.id)
                            if (first !== obj) return vm.scmProcReturn(ScmConstant.FALSE, n, this)
                        }
                        vm.scmProcReturn(ScmConstant.TRUE, n, this)
                    }
                }
            }
        }
    }

    /** procedure: symbol->string */
    val procSymbolToString: ScmProcedure by lazy {
        object : ScmProcedure("symbol->string", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procSymbolToString.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmSymbol
                            ?: throw KevesExceptions.expectedSymbol(procSymbolToString.id)

                        val result = ScmString(obj.rawString)
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procSymbolToString.id)
                }
            }
        }
    }

    /** procedure: string->symbol */
    val procStringToSymbol: ScmProcedure by lazy {
        object : ScmProcedure("string->symbol", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procStringToSymbol.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmString
                            ?: throw KevesExceptions.expectedSymbol(procStringToSymbol.id)

                        val result = ScmSymbol.get(obj.toStringForDisplay())
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procStringToSymbol.id)
                }
            }
        }
    }
}