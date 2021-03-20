/*
 * KevesVM.kt
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

package io.github.kawatab.keveskotlin

import io.github.kawatab.keveskotlin.objects.*

class KevesVM(val res: KevesResources) {
    val stack = KevesStack(res)

    // var acc: ScmObject? = null
    var acc = PtrObject(0)
    var x: PtrInstruction = res.constHalt.toInstruction()
    var fp: Int = 0
    var clsr: PtrClosure = ScmClosure.make("dummy lambda", res.constHalt.toInstruction(), 0, intArrayOf(), res)
    var sp: Int = 0

    fun evaluate(code: PtrInstruction): PtrObject {
        acc = PtrObject(0)
        x = code
        fp = 0
        clsr = ScmClosure.make("dummy lambda", res.constHalt.toInstruction(), 0, intArrayOf(), res)
        sp = 0
        return vm()
    }

    /**
     * Virtual machine
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 112
     */
    private fun vm(): PtrObject {
        while (x != res.constHalt.toInstruction()) {
            x.exec(this)
        }
        return if (acc.isNotNull()) acc else PtrObject(0)
    }

    /**
     * Builds a vector of the appropriate length, places the code for the body of the function
     * into the first vector slot and the free values found on the stack into the remaining slots
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 97
     */
    fun closure(body: PtrInstruction, m: Int, n: Int, s: Int): PtrClosure =
        ScmClosure.make("lambda", body, m, stack.makeArray(s, n), res)

    /**
     * Creates a continuation
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 86
     */
    fun continuation(s: Int): PtrClosure =
        closure(
            // TODO("Is this OK?")
            ScmInstruction.ReferFree.make(
                0,
                ScmInstruction.Nuate.make(stack.saveStack(s), ScmInstruction.Return.make(0, res), res),
                res
            ),
            1,
            0, // TODO("Is this OK?")
            0 // TODO("Is this OK?")
        )

    /**
     * Creates a continuation
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 111
     */
    fun shiftArgs(n: Int, m: Int, s: Int) {
        tailrec fun nextArg(i: Int) {
            if (i < 0) return
            stack.indexSetE(s, i + m, stack.index(s, i))
            return nextArg(i = (i - 1))
        }
        nextArg(i = n - 1)
        sp = s - m
    }

    /** Adds null at end of argument for lambda that accepts variable length of arguments */
    fun addNullAtEndOfArgs(sp: Int, n: Int) {
        stack.addNullAtEndOfArgs(sp, n)
    }

    /** Shrinks arguments for lambda that accepts variable length of arguments */
    fun shrinkArgs(sp: Int, n: Int, shift: Int) {
        stack.shrinkArgs(sp, n, shift)
    }

    // fun scmProcReturn(result: ScmObject?, n: Int) {
    fun scmProcReturn(result: PtrObject, n: Int) {
        val ret: PtrInstruction = stack.index(sp, n).toInstruction()
        val f: Int = stack.index(sp, n + 1).toInt().value
        val c = stack.index(sp, n + 2).toClosure()
        acc = result
        x = ret
        fp = f
        clsr = c
        sp -= n + 3
    }
}