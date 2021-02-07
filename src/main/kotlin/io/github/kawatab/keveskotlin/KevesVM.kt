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

class KevesVM {
    val stack = KevesStack()
    var acc: ScmObject? = null
    var x: ScmPair? = null
    var fp: Int = 0
    var clsr: ScmClosure? = null
    var sp: Int = 0

    fun evaluate(code: ScmPair?): ScmObject? {
        acc = null
        x = code
        fp = 0
        clsr = ScmClosure("dummy lambda", null, 0, ScmVector(0))
        sp = 0
        return vm()
    }

    /**
     * Virtual machine
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 112
     */
    private fun vm(): ScmObject? {
        while (true) {
            if (x != null) {
                when (x?.car) {
                    ScmInstruction.HALT -> {
                        return acc
                    }

                    ScmInstruction.REFER_LOCAL -> {
                        val (n, nx) = patternMatchReferLocal(x)
                        acc = stack.index(fp, n)
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.REFER_FREE -> {
                        val (n, nx) = patternMatchReferFree(x)
                        acc = clsr?.indexClosure(n)
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.INDIRECT -> {
                        val nx = patternMatchIndirect(x)
                        val unboxA = try {
                            ScmBox.unbox(acc)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("<INDIRECT> required box, but got other")
                        }
                        acc = unboxA
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.CONSTANT -> {
                        val (obj, nx) = patternMatchConstant(x)
                        acc = obj
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.CLOSE -> {
                        val (counts, body, nx) = patternMatchClose(x)
                        val (n, numArg) = counts
                        acc = closure(body, numArg, n, sp)
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        sp = (sp - n)
                        continue
                    }

                    ScmInstruction.BOX -> {
                        val (n, nx) = patternMatchBox(x)
                        stack.indexSetE(sp, n, ScmBox(stack.index(sp, n)))
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.BOX_REST -> {
                        val (n, nx) = patternMatchBox(x)
                        stack.indexSetE(sp, n, ScmBox(stack.index(sp, n)))
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.TEST -> {
                        val (thn, els) = patternMatchTest(x)
                        // acc = acc
                        x = if (acc != ScmConstant.FALSE) thn else els
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.ASSIGN_LOCAL -> {
                        val (n, nx) = patternMatchAssignLocal(x)
                        val box: ScmBox = try {
                            stack.index(fp, n) as? ScmBox
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("<ASSIGN_LOCAL> expected box but got other")
                        } ?: throw IllegalArgumentException("<ASSIGN_LOCAL> expected box but got other")
                        box.value = acc
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.ASSIGN_FREE -> {
                        val (n, nx) = patternMatchAssignFree(x)
                        val box: ScmBox = try {
                            clsr?.indexClosure(n) as? ScmBox
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("<ASSIGN_FREE> expected box but got other")
                        } ?: throw IllegalArgumentException("<ASSIGN_FREE> expected box but got other")
                        box.value = acc
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.CONTI -> {
                        val nx = patternMatchConti(x)
                        acc = continuation(sp)
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        // sp = sp
                        continue
                    }

                    ScmInstruction.NUATE -> {
                        val (s, nx) = patternMatchNuate(x)
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        sp = stack.restoreStack(s)
                        continue
                    }

                    ScmInstruction.FRAME -> {
                        val (ret, nx) = patternMatchFrame(x)
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        sp = stack.push(ret, stack.push(ScmInt(fp), stack.push(clsr, sp)))
                        continue
                    }

                    ScmInstruction.ARGUMENT -> {
                        val nx = patternMatchArgument(x)
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        sp = stack.push(acc, sp)
                        continue
                    }

                    ScmInstruction.SHIFT -> {
                        val (n, m, nx) = patternMatchShift(x)
                        // acc = acc
                        x = nx
                        // fp = fp
                        // clsr = clsr
                        sp = shiftArgs(n, m, sp)
                        continue
                    }

                    ScmInstruction.APPLY -> {
                        val n = patternMatchApply(x)
                        if (acc is ScmProcedure) {
                            (acc as ScmProcedure).normalProc(n, this)
                            continue
                        } else throw IllegalArgumentException("<APPLY> got non procedure")
                    }

                    ScmInstruction.RETURN -> {
                        val n = patternMatchReturn(x)
                        val sp1 = sp - n
                        val s0: ScmPair? = stack.index(sp1, 0)?.let {
                            it as? ScmPair
                                ?: throw IllegalArgumentException("SP pointed by <RETURN> did not include pair")
                        }
                        val s1: Int = (stack.index(sp1, 1) as? ScmInt)?.value
                            ?: throw IllegalArgumentException("SP pointed by <RETURN> did not include Int")
                        val s2: ScmClosure = stack.index(sp1, 2) as? ScmClosure
                            ?: throw IllegalArgumentException("SP pointed by <RETURN> did not include vector")
                        // acc = acc
                        x = s0
                        fp = s1
                        clsr = s2
                        sp = sp1 - 3
                        continue
                    }

                    is ScmProcedure -> {
                        val procedure: ScmProcedure = x?.car as ScmProcedure
                        procedure.directProc(acc, sp, this)
                        continue
                    }
                    else -> {
                        println("not instruction: ${ScmObject.getStringForDisplay(x?.car)}")
                        throw IllegalArgumentException("No instruction")
                    }
                }
            } else {
                return ScmConstant.UNDEF
            }
        }
    }

    /**
     * Builds a vector of the appropriate length, places the code for the body of the function
     * into the first vector slot and the free values found on the stack into the remaining slots
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 97
     */
    private fun closure(body: ScmPair?, m: Int, n: Int, s: Int): ScmClosure =
        ScmClosure("lambda", body, m, stack.makeScmVector(s, n))

    /**
     * Creates a continuation
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 86
     */
    private fun continuation(s: Int): ScmClosure =
        closure(
            ScmPair.list(
                ScmInstruction.REFER, ScmInt(0), ScmInt(0), // TODO("Is this OK?")
                ScmPair.list(ScmInstruction.NUATE, stack.saveStack(s), ScmPair.list(ScmInstruction.RETURN, ScmInt(0)))
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
    private fun shiftArgs(n: Int, m: Int, s: Int): Int {
        tailrec fun nextArg(i: Int) {
            if (i < 0) return
            stack.indexSetE(s, i + m, stack.index(s, i))
            return nextArg((i - 1))
        }
        nextArg(i = n - 1)
        return s - m
    }

    /** Adds null at end of argument for lambda that accepts variable length of arguments */
    fun addNullAtEndOfArgs(sp: Int, n: Int) {
        stack.addNullAtEndOfArgs(sp, n)
    }

    /** Shrinks arguments for lambda that accepts variable length of arguments */
    fun shrinkArgs(sp: Int, n: Int, shift: Int) {
        stack.shrinkArgs(sp, n, shift)
    }

    fun scmProcReturn(result: ScmObject?, n: Int, proc: ScmProcedure) {
        val ret: ScmPair? = stack.index(sp, n) as? ScmPair
        val f: Int = (stack.index(sp, n + 1) as? ScmInt)?.value
            ?: throw IllegalArgumentException("${proc.id} did wrong")
        val c: ScmClosure? = stack.index(sp, n + 2) as? ScmClosure
        acc = result
        x = ret
        fp = f
        clsr = c
        sp = sp - n - 3
    }

    private fun patternMatchReferLocal(x: ScmPair?): Pair<Int, ScmPair?> {
        val n: Int = (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<REFER_LOCAL> got nothing as n")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<REFER_LOCAL> got non number value as index")
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<REFER_LOCAL> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<REFER_LOCAL> got non pair as x") }
        return n to nx
    }

    private fun patternMatchReferFree(x: ScmPair?): Pair<Int, ScmPair?> {
        val n: Int = (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<REFER_LOCAL> got nothing as n")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<REFER_LOCAL> got non number value as index")
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<REFER_LOCAL> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<REFER_LOCAL> got non pair as x") }
        return n to nx
    }

    private fun patternMatchIndirect(x: ScmPair?): ScmPair? =
        try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<INDIRECT> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<INDIRECT> got improper list") }

    private fun patternMatchConstant(x: ScmPair?): Pair<ScmObject?, ScmPair?> {
        val obj: ScmObject? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<CONSTANT> got nothing as obj")
        }
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<CONSTANT> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<REFER_LOCAL> got non pair as x") }
        return obj to nx
    }

    private fun patternMatchClose(x: ScmPair?): Triple<Pair<Int, Int>, ScmPair?, ScmPair?> {
        val m: Int = (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<CLOSE> got nothing as m")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<CLOSE> got non number value as index")
        val n: Int = (try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<CLOSE> got nothing as n")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<CLOSE> got non number value as index")
        val body: ScmPair? = try {
            ScmPair.cadddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<CLOSE> got nothing as body")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<CLOSE> got non pair as body") }
        val nx: ScmPair? = try {
            ScmPair.car(ScmPair.cddddr(x))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<CLOSE> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<CLOSE> got non pair as x") }
        return Triple(m to n, body, nx)
    }

    private fun patternMatchBox(x: ScmPair?): Pair<Int, ScmPair?> {
        val n: Int = (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<BOX> got nothing as n")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<BOX> got non number value as index")
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<BOX> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<BOX> got non pair as x") }
        return n to nx
    }

    private fun patternMatchTest(x: ScmPair?): Pair<ScmPair?, ScmPair?> {
        val thn: ScmPair? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<TEST> got nothing as then")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<TEST> got non number value as then") }
        val els: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<TEST> got nothing as else")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<TEST> got non pair as else") }
        return thn to els
    }

    private fun patternMatchAssignLocal(x: ScmPair?): Pair<Int, ScmPair?> {
        val n: Int = (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<ASSIGN_LOCAL> got nothing as n")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<ASSIGN_LOCAL> got non number value as index")
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<ASSIGN_LOCAL> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<ASSIGN_LOCAL> got non pair as x") }
        return n to nx
    }

    private fun patternMatchAssignFree(x: ScmPair?): Pair<Int, ScmPair?> {
        val n: Int = (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<ASSIGN_FREE> got nothing as n")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<ASSIGN_FREE> got non number value as index")
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<ASSIGN_FREE> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<ASSIGN_FREE> got non pair as x") }
        return n to nx
    }

    @Suppress("SpellCheckingInspection")
    private fun patternMatchConti(x: ScmPair?): ScmPair? =
        try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<CONTI> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<CONTI> got improper list") }

    @Suppress("SpellCheckingInspection")
    private fun patternMatchNuate(x: ScmPair?): Pair<ScmVector, ScmPair?> {
        val stack: ScmVector = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<NUATE> got nothing as stack")
        } as? ScmVector ?: throw IllegalArgumentException("<NUATE> did not got stack")
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<NUATE> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<NUATE> got non pair as x") }
        return stack to nx
    }

    private fun patternMatchFrame(x: ScmPair?): Pair<ScmPair?, ScmPair?> {
        val ret: ScmPair? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<FRAME> got nothing as ret")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<FRAME> got non pair as ret") }
        val nx: ScmPair? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<FRAME> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<FRAME> got non pair as x") }
        return ret to nx
    }

    private fun patternMatchArgument(x: ScmPair?): ScmPair? =
        try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<ARGUMENT> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<ARGUMENT> got improper list") }

    private fun patternMatchShift(x: ScmPair?): Triple<Int, Int, ScmPair?> {
        val n: Int = (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<SHIFT> got nothing as n")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<SHIFT> got non number value as index")
        val m: Int = (try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<SHIFT> got nothing as m")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<SHIFT> got non number value as index")
        val nx: ScmPair? = try {
            ScmPair.cadddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<SHIFT> got nothing as x")
        }?.let { it as? ScmPair ?: throw IllegalArgumentException("<SHIFT> got non pair as x") }
        return Triple(n, m, nx)
    }

    private fun patternMatchApply(x: ScmPair?): Int =
        (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<APPLY> got nothing as x")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<APPLY> got improper list")

    private fun patternMatchReturn(x: ScmPair?): Int =
        (try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("<RETURN> got nothing as x")
        } as? ScmInt)?.value ?: throw IllegalArgumentException("<RETURN> got improper list")
}