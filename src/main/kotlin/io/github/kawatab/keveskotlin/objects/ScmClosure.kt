/*
 * ScmClosure.kt
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

package io.github.kawatab.keveskotlin.objects

import io.github.kawatab.keveskotlin.KevesVM
import java.lang.IllegalArgumentException

class ScmClosure(
    id: String,
    private val body: ScmPair?,
    private val numArg: Int,
    private val closure: ScmVector
) : ScmProcedure(id, null) {
    override fun toStringForWrite(): String = "#<procedure $id>"
    override fun toStringForDisplay(): String = toStringForWrite()
    override fun toString(): String = toStringForWrite()

    override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}

    override fun normalProc(n: Int, vm: KevesVM) {
        when {
            numArg == n -> {
                val sp = vm.sp
                vm.x = body
                vm.fp = sp
                vm.clsr = vm.acc as ScmClosure
            }
            numArg < 0 -> {
                val shift = numArg + n
                val sp = vm.sp
                when {
                    shift >= 0 -> {
                        vm.shrinkArgs(sp, n, shift)
                        vm.x = body
                        vm.fp = sp - shift
                        vm.clsr = vm.acc as ScmClosure
                        vm.sp = sp - shift
                    }
                    shift == -1 -> {
                        vm.addNullAtEndOfArgs(sp, n)
                        // vm.acc = acc
                        vm.x = body
                        vm.fp = sp + 1
                        vm.clsr = vm.acc as ScmClosure
                        vm.sp = sp + 1
                    }
                    else -> {
                        throw IllegalArgumentException("number of arguments is not much with lambda")
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("number of arguments is not much with lambda")
            }
        }
    }

    /**
     * Refers argument values of a display closure
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 98
     */
    fun indexClosure(n: Int): ScmObject? = closure.at(n)
}
