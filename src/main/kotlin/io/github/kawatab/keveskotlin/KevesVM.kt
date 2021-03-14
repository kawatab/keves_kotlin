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
    var x: ScmInstruction = res.constHalt.toInstruction().toVal(res)
    var fp: Int = 0
    var clsr: PtrClosure = ScmClosure.make("dummy lambda", res.constHalt.toInstruction(), 0, intArrayOf(), res)
    var sp: Int = 0

    fun evaluate(code: PtrInstruction): PtrObject {
        acc = PtrObject(0)
        x = code.toVal(res)
        fp = 0
        clsr = ScmClosure.make("dummy lambda", res.constHalt.toInstruction(), 0, intArrayOf(), res)
        sp = 0
        return vm()
    }

    fun accToProcedure(): ScmProcedure? = acc.toVal(res) as? ScmProcedure

    /**
     * Virtual machine
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 112
     */
    private fun vm(): PtrObject {
        while (x != res.constHalt.toInstruction().toVal(res)) {
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
    /*
    fun closure(body: ScmInstruction, m: Int, n: Int, s: Int): ScmClosure =
        ScmClosure.make("lambda", body, m, stack.makeArray(s, n), res).toVal(res) as ScmClosure
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
        val ret: ScmInstruction = stack.index(sp, n).toInstruction().toVal(res)
        val f: Int = (stack.index(sp, n + 1).toVal(res) as ScmInt).value
        val c = stack.index(sp, n + 2).toClosure()
        acc = result
        x = ret
        fp = f
        clsr = c
        sp -= n + 3
    }
    /*
    fun scmProcReturn(result: ScmObject?, n: Int, proc: ScmProcedure) {
        val ret: ScmInstruction = stack.index(sp, n) as ScmInstruction
        val f: Int = (stack.index(sp, n + 1) as? ScmInt)?.value
            ?: throw IllegalArgumentException("${proc.id} did wrong")
        val c: ScmClosure? = stack.index(sp, n + 2) as? ScmClosure
        acc = result
        x = ret
        fp = f
        clsr = c
        sp -= n + 3
    }
     */

    /*
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
        // val n: Int = (ScmPair.cadr(x) as ScmInt).value
        // val nx: ScmPair? = ScmPair.caddr(x) as? ScmPair
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
        // val n: Int = (ScmPair.cadr(x) as ScmInt).value
        // val nx: ScmPair? = ScmPair.caddr(x) as? ScmPair
        // return n to nx
    }

    private fun patternMatchIndirect(x: ScmPair?): ScmPair? =
        // ScmPair.cadr(x) as? ScmPair
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
        // val obj: ScmObject? = ScmPair.cadr(x)
        // val nx: ScmPair? = ScmPair.caddr(x) as? ScmPair
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

     */
}