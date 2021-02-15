/*
 * KevesCompiler.kt
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

import io.github.kawatab.keveskotlin.libraries.R7rs
import io.github.kawatab.keveskotlin.objects.*

class KevesCompiler {
    private val r7rs = R7rs()
    private val listOfBinds = r7rs.listOfBinds


    fun transform(x: ScmObject?): ScmObject? {
        val removedDefine = transformDefine(x)
        return transformWithMacro(removedDefine)
    }

    private fun transformDefine(x: ScmObject?): ScmObject? =
        if (x is ScmPair) {
            when (val car = x.car) {
                is ScmPair -> ScmPair(transformDefine(x = car), transformDefine(x = x.cdr))
                is ScmSymbol -> {
                    when (car) {
                        ScmSymbol.get("begin") -> {
                            val (definition, body) = findDefinition(sequence = x.cdr, definition = null)
                            if (definition == null) {
                                ScmPair(car, transformDefine(x = x.cdr))
                            } else {
                                ScmPair.list(
                                    car,
                                    ScmPair.listStar(
                                        ScmSymbol.get("letrec*"),
                                        ScmPair.reverse(definition),
                                        transformDefine(x = body)
                                    )
                                )
                            }
                        }
                        else -> ScmPair(car, transformDefine(x = x.cdr))
                    }
                }
                else -> ScmPair(car, transform(x.cdr))
            }
        } else {
            x
        }

    private tailrec fun findDefinition(sequence: ScmObject?, definition: ScmPair?): Pair<ScmPair?, ScmObject?> {
        if (sequence is ScmPair) {
            val obj = sequence.car
            if (obj is ScmPair) {
                val car = obj.car
                if (car === ScmSymbol.get("define"))
                    return findDefinition(
                        sequence = sequence.cdr,
                        definition = ScmPair(findLambda(obj.cdr), definition)
                    )
            }
        }
        return definition to (findNextDefinition(sequence, null) ?: sequence)
    }

    private fun findLambda(definition: ScmObject?): ScmPair {
        if (definition !is ScmPair) throw IllegalArgumentException("define is malformed")
        val car = definition.car
        val cdr = definition.cdr
        return when (car) {
            is ScmSymbol -> definition
            is ScmPair -> {
                val variable = car.car
                val formals = car.cdr
                if (variable != null && variable !is ScmSymbol) throw IllegalArgumentException("found no identifier in definition")
                ScmPair.list(variable, ScmPair.listStar(ScmSymbol.get("lambda"), formals, cdr))
            }
            else -> throw IllegalArgumentException("found no identifier in definition")
        }
    }

    private tailrec fun findNextDefinition(sequence: ScmObject?, notDefinition: ScmPair?): ScmPair? {
        tailrec fun loop(rest: ScmPair?, result: ScmPair?): ScmPair? =
            if (rest == null) result
            else loop(rest = rest.cdr as? ScmPair, result = ScmPair(rest.car, result))

        if (sequence is ScmPair) {
            val obj = sequence.car
            if (obj is ScmPair) {
                val car = obj.car
                if (car !== ScmSymbol.get("define"))
                    return findNextDefinition(
                        sequence = sequence.cdr,
                        notDefinition = ScmPair(sequence.car, notDefinition)
                    )
            }
        }

        return sequence?.let { loop(notDefinition, ScmPair(ScmSymbol.get("begin"), sequence)) }
    }

    private fun transformWithMacro(x: ScmObject?): ScmObject? =
        if (x is ScmPair) {
            when (val car = x.car) {
                is ScmPair -> ScmPair(transformWithMacro(car), transformWithMacro(x = x.cdr))
                is ScmSymbol -> {
                    val obj = findBind(car)?.second
                    if (obj is ScmMacro) transformWithMacro(x = obj.transform(x, this))
                    else ScmPair(car, transformWithMacro(x = x.cdr))
                }
                else -> ScmPair(car, transformWithMacro(x = x.cdr))
            }
        } else {
            x
        }

    /**
     * Compiler
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 110
     */
    fun compile(x: ScmObject?, e: ScmPair?, s: ScmPair?, next: ScmPair?): ScmPair? =
        when (x) {
            is ScmSymbol -> {
                val bind = findBind(x)
                val obj: ScmObject? = bind?.let { (_, obj) -> obj as? ScmSyntax ?: obj as? ScmProcedure }
                if (obj != null) {
                    ScmPair.list(ScmInstruction.Constant(obj, next)) // CONSTANT, obj, next)
                } else {
                    compileRefer(
                        x,
                        e,
                        if (setMemberQ(x, s)) ScmPair.list(ScmInstruction.Indirect(next)/*INDIRECT, next*/) else next
                    )
                }
            }
            is ScmPair -> {
                val bind = (x.car as? ScmSymbol)?.let { symbol: ScmSymbol -> findBind(symbol) }
                val obj = bind?.second
                when {
                    obj is ScmSyntax -> {
                        obj.compile(x, e, s, next, this)
                    }
                    (obj as? ScmProcedure)?.syntax != null -> {
                        obj.syntax!!.compile(x, e, s, next, this)
                    }
                    else -> {
                        val instApply = ScmMutablePair(ScmInstruction.Apply(0), null) // APPLY, ScmPair.list(ScmInt(0)))
                        fun loop(args: ScmPair?, c: ScmPair?, n: Int): ScmPair? =
                            if (args == null) {
                                instApply.assignCar(ScmInstruction.Apply(n))
                                if (tailQ(next)) c
                                else ScmPair.list(ScmInstruction.Frame(next, c)) // FRAME, next, c)
                            } else {
                                loop(
                                    args.cdr?.let {
                                        it as? ScmPair
                                            ?: throw IllegalArgumentException(KevesExceptions.badSyntax(x.toStringForWrite()))
                                    },
                                    compile(args.car, e, s, ScmPair.list(ScmInstruction.Argument(c))), // ARGUMENT, c)),
                                    n + 1
                                )
                            }

                        loop(
                            x.cdr?.let {
                                it as? ScmPair
                                    ?: throw IllegalArgumentException(KevesExceptions.badSyntax(x.toStringForWrite()))
                            },
                            compile(
                                x.car,
                                e,
                                s,
                                if (tailQ(next)) {
                                    ScmPair.list(
                                        ScmInstruction.Shift( // SHIFT,
                                            ScmPair.length(x.cdr as? ScmPair),
                                            (ScmPair.car(next) as ScmInstruction.Return).n, // ScmPair.cadr(next),
                                            instApply
                                        )
                                    )
                                } else {
                                    instApply
                                }
                            ),
                            0
                        )
                    }
                }
            }
            else -> {
                ScmPair.list(ScmInstruction.Constant(x, next)) // CONSTANT, x, next)
            }
        }

    private fun findBind(symbol: ScmSymbol): Pair<ScmSymbol, ScmObject?>? =
        listOfBinds.find { (id, _) -> symbol === id }

    /**
     * Looks for assignments to any of the set of variables [v]
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 101
     */
    fun findSets(x: ScmObject?, v: ScmPair?): ScmPair? =
        when (x) {
            is ScmSymbol ->
                null
            is ScmPair -> {
                val bind = (x.car as? ScmSymbol)?.let { symbol: ScmSymbol -> findBind(symbol) }
                when {
                    bind?.second as? ScmSyntax != null ->
                        (bind.second as ScmSyntax).findSets(x, v, this)
                    (bind?.second as? ScmProcedure)?.syntax != null ->
                        (bind.second as ScmProcedure).syntax!!.findSets(x, v, this)
                    else -> {
                        fun next(x: ScmPair?): ScmPair? =
                            if (x == null) null
                            else {
                                val cdrX = try {
                                    ScmPair.cdr(x)
                                } catch (e: IllegalArgumentException) {
                                    throw IllegalArgumentException("'next' got improper list")
                                }?.let { it as? ScmPair ?: throw IllegalArgumentException("'next' got non pair") }
                                setUnion(findSets(ScmPair.car(x), v), next(cdrX))
                            }
                        next(x = if (bind == null) x else (x.cdr as? ScmPair))
                    }
                }
            }
            else ->
                null
        }

    /**
     * Returns the set of ree variables of an expression [x], given an initial set of bound variables [b]
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 104
     */
    fun findFree(x: ScmObject?, b: ScmPair?): ScmPair? =
        when (x) {
            is ScmSymbol -> {
                when {
                    findBind(x) != null -> null
                    setMemberQ(x, b) -> null
                    else -> ScmPair.list(x)
                }
            }
            is ScmPair -> {
                val bind = (x.car as? ScmSymbol)?.let { symbol: ScmSymbol -> findBind(symbol) }
                when {
                    bind?.second as? ScmSyntax != null ->
                        (bind.second as ScmSyntax).findFree(x, b, this)
                    (bind?.second as? ScmProcedure)?.syntax != null ->
                        (bind.second as ScmProcedure).syntax!!.findFree(x, b, this)
                    else -> {
                        fun next(x: ScmPair?): ScmPair? =
                            if (x == null) null
                            else {
                                val cdrX: ScmPair? = x.cdr?.let {
                                    it as? ScmPair ?: throw IllegalArgumentException("'next' got non pair")
                                }
                                setUnion(findFree(x.car, b), next(cdrX))
                            }
                        next(x = if (bind == null) x else (x.cdr as? ScmPair))
                    }
                }
            }
            else ->
                null
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    private fun compileRefer(x: ScmSymbol, e: ScmPair?, next: ScmPair?): ScmPair? =
        try {
            compileLookup(
                x,
                e,
                { n: Int -> ScmPair.list(ScmInstruction.ReferLocal(n, next)) }, //REFER_LOCAL, ScmInt(n), next) },
                { n: Int -> ScmPair.list(ScmInstruction.ReferFree(n, next)) } // REFER_FREE, ScmInt(n), next) }
            )
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("found undefined variable: ${x.toStringForWrite()}")
        }

    /**
     * Lookup a reference in compile time
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    fun compileLookup(
        x: ScmSymbol,
        e: ScmPair?,
        returnLocal: (Int) -> ScmPair?,
        returnFree: (Int) -> ScmPair?
    ): ScmPair? {
        tailrec fun nextFree(free: ScmObject?, n: Int): ScmPair? =
            if (x === ScmPair.car(free)) returnFree(n)
            else nextFree(free = ScmPair.cdr(free), n = n + 1)

        tailrec fun nextLocal(locals: ScmObject?, n: Int): ScmPair? =
            when {
                locals == null -> nextFree(free = ScmPair.cdr(e), n = 0)
                x === ScmPair.car(locals) -> returnLocal(n)
                else -> nextLocal(locals = ScmPair.cdr(locals), n = n + 1)
            }

        return nextLocal(locals = ScmPair.car(e), n = 0)
    }

    /**
     * Collects variables for inclusion in the closure
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    tailrec fun collectFree(vars: ScmPair?, e: ScmPair?, next: ScmPair?): ScmPair? =
        if (vars == null) next
        else collectFree(
            vars = vars.cdr?.let {
                it as? ScmPair ?: throw IllegalArgumentException("'collect-free' got none pair as vars")
            },
            e = e,
            next = compileRefer(
                vars.car as? ScmSymbol
                    ?: throw IllegalArgumentException("'collect-free' got none symbol as identifier"),
                e,
                ScmPair.list(ScmInstruction.Argument(next)) // ARGUMENT, next)
            )
        )

    /**
     * Makes boxes from a list of assigned variables [sets] and a list of arguments [vars]
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 102
     */
    fun makeBoxes(sets: ScmPair?, vars: ScmObject?, next: ScmPair?): ScmPair? {
        fun f(vars: ScmObject?, n: Int): ScmPair? =
            when (vars) {
                null -> next
                is ScmPair -> {
                    val carVars: ScmSymbol = vars.car as? ScmSymbol
                        ?: throw IllegalArgumentException("'make-box' got non symbol as vars")
                    if (setMemberQ(carVars, sets)) ScmPair.list(
                        ScmInstruction.Box(/*BOX, ScmInt(*/n/*)*/,
                            f(vars.cdr, n + 1)
                        )
                    )
                    else f(vars.cdr, n + 1)
                }
                is ScmSymbol -> {
                    if (setMemberQ(vars, sets)) ScmPair.list(
                        ScmInstruction.BoxRest(
                            n,
                            next
                        )
                    ) //BOX_REST , ScmInt(n), next)
                    else next
                }
                else ->
                    throw IllegalArgumentException("'make-box' got non symbol as vars")
            }
        return f(vars = vars, n = 0)
    }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    tailrec fun setMemberQ(x: ScmSymbol, s: ScmPair?): Boolean =
        when {
            s == null -> false

            s.car === x -> true

            else -> {
                val cdrS: ScmPair? = s.cdr?.let {
                    it as? ScmPair ?: throw IllegalArgumentException("'set-member?' got non pair")
                }
                setMemberQ(x = x, s = cdrS)
            }
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    private fun setCons(x: ScmSymbol, s: ScmPair?): ScmPair? = if (setMemberQ(x, s)) s else ScmPair(x, s)

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    tailrec fun setUnion(s1: ScmPair?, s2: ScmPair?): ScmPair? =
        if (s1 == null) {
            s2
        } else {
            val carS1: ScmSymbol =
                s1.car as? ScmSymbol ?: throw IllegalArgumentException("'set-union' got illegal pair")
            val cdrS1: ScmPair? = s1.cdr?.let {
                it as? ScmPair ?: throw IllegalArgumentException("'set-union' got non pair")
            }
            setUnion(s1 = cdrS1, s2 = setCons(carS1, s2))
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    fun setMinus(s1: ScmPair?, s2: ScmPair?): ScmPair? =
        if (s1 == null) {
            null
        } else {
            val carS1: ScmSymbol = s1.car as? ScmSymbol
                ?: throw IllegalArgumentException("'set-minus' got non symbol as identifier")
            val cdrS1: ScmPair? = s1.cdr?.let {
                it as? ScmPair ?: throw IllegalArgumentException("'set-minus' got non pair")
            }
            if (setMemberQ(carS1, s2)) setMinus(s1 = cdrS1, s2 = s2)
            else ScmPair(carS1, setMinus(s1 = cdrS1, s2 = s2))
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    fun setIntersect(s1: ScmPair?, s2: ScmPair?): ScmPair? =
        if (s1 == null) {
            null
        } else {
            val carS1: ScmSymbol = s1.car as? ScmSymbol
                ?: throw IllegalArgumentException("'set-intersect' got non symbol as identifier")
            val cdrS1: ScmPair? = s1.cdr?.let {
                it as? ScmPair ?: throw IllegalArgumentException("'set-intersect' got non pair")
            }
            if (setMemberQ(carS1, s2)) ScmPair(carS1, setIntersect(s1 = cdrS1, s2 = s2))
            else setIntersect(s1 = cdrS1, s2 = s2)
        }

    /**
     * Looks at the next instruction to see if it is a return instruction
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 59
     */
    fun tailQ(next: ScmPair?): Boolean = next != null && next.car is ScmInstruction.Return // === ScmInstruction.RETURN

/*
companion object {
    val symbolBegin = ScmSymbol.get("begin")

    // const val badSyntax = "bad syntax in '%s'"
    // const val expectedSymbol = "'%s' expected a symbol, but got other"
    // const val expected1DatumButGotNothing = "'%s' expected 1 datum, but got nothing"
    // const val expected1DatumButGotMore = "'%s' expected 1 datum, but got more"
    // const val expected2DatumButGotLess = "'%s' expected 2 datum, but got less"
    // const val expected2DatumButGotMore = "'%s' expected 2 datum, but got more"
    // const val expected2OrMoreDatumButGotLess = "'%s' expected 2 or more datum, but got less"
    // const val expected3DatumButGotLess = "'%s' expected 3 datum, but got less"
    // const val expected3DatumButGotMore = "'%s' expected 3 datum, but got more"
}
 */

    fun splitBinds(binds: ScmPair?): Pair<ScmPair?, ScmPair?> {
        tailrec fun loop(binds: ScmPair?, variables: ScmPair?, values: ScmPair?): Pair<ScmPair?, ScmPair?> =
            if (binds == null) {
                variables to values
            } else {
                val pair = binds.car as? ScmPair ?: throw IllegalArgumentException("syntax error")
                val variable: ScmSymbol = pair.car as? ScmSymbol ?: throw IllegalArgumentException("syntax error")
                val value: ScmObject? = ScmPair.cadr(pair)
                val cdr: ScmPair? =
                    binds.cdr?.let { (it as? ScmPair) ?: throw IllegalArgumentException("Syntax error") }
                loop(cdr, ScmPair(variable, variables), ScmPair(value, values))
            }
        return loop(binds, null, null).let { (variables, values) ->
            variables?.let { ScmPair.reverse(it) } to values?.let { ScmPair.reverse(it) }
        }
    }
}