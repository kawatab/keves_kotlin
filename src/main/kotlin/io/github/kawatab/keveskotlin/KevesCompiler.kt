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

class KevesCompiler(private val res: KevesResources) {
    private val r7rs = R7rs(res)
    private val listOfBinds = r7rs.listOfBinds


    fun transform(x: PtrObject): PtrObject {
        val removedDefine = transformDefine(x)
        return transformWithMacro(removedDefine)
    }

    private fun transformDefine(x: PtrObject): PtrObject {
        return if (x.isPair(res)) {
            val car = x.toPairOrNull().car(res)
            when {
                car.isPair(res) -> ScmPair.make(transformDefine(car), transformDefine(x.toPairOrNull().cdr(res)), res)
                    .toObject()
                car.isSymbol(res) -> {
                    when (car) {
                        ScmSymbol.get("begin", res).toObject() -> {
                            val (definition, body) = findDefinition(
                                sequence = x.toPairOrNull().cdr(res),
                                definition = PtrPairOrNull(0)
                            )
                            if (definition.isNull()) {
                                ScmPair.make(car, transformDefine(x = x.toPairOrNull().cdr(res)), res).toObject()
                            } else {
                                ScmPair.list(
                                    car,
                                    ScmPair.listStar(
                                        ScmSymbol.get("letrec*", res).toObject(),
                                        ScmPair.reverse(definition.toPairNonNull(), res).toObject(),
                                        transformDefine(x = body),
                                        res
                                    ).toObject(),
                                    res
                                ).toObject()
                            }
                        }
                        else -> ScmPair.make(car, transformDefine(x = x.toPairOrNull().cdr(res)), res).toObject()
                    }
                }
                else -> ScmPair.make(car, transform(x.toPairOrNull().cdr(res)), res).toObject()
            }
        } else {
            x
        }
    }

    private tailrec fun findDefinition(sequence: PtrObject, definition: PtrPairOrNull): Pair<PtrPairOrNull, PtrObject> {
        if (sequence.isPair(res)) {
            val obj = sequence.toPairOrNull().car(res)
            if (obj.isPair(res)) {
                val car = obj.toPair().car(res)
                if (car == ScmSymbol.get("define", res).toObject())
                    return findDefinition(
                        sequence = sequence.toPairOrNull().cdr(res),
                        definition = ScmPair.make(
                            findLambda(obj.toPair().cdr(res)).toObject(),
                            definition.toObject(),
                            res
                        )
                    )
            }
        }
        return definition to (findNextDefinition(sequence, PtrPairOrNull(0))
            .let { if (it.isNull()) sequence else it.toObject() })
    }

    private fun findLambda(definition: PtrObject): PtrPair {
        if (definition.isNotPair(res)) throw IllegalArgumentException("define is malformed")
        val car = definition.toPair().car(res)
        val cdr = definition.toPair().cdr(res)
        return when {
            car.isSymbol(res) -> definition.toPair()
            car.isPair(res) -> {
                val variable = car.toPair().car(res)
                val formals = car.toPair().cdr(res)
                if (variable.isNotNull() && variable.isNotSymbol(res)) throw IllegalArgumentException("found no identifier in definition")
                ScmPair.list(
                    variable,
                    ScmPair.listStar(ScmSymbol.get("lambda", res).toObject(), formals, cdr, res).toObject(),
                    res
                ).toPairNonNull()
            }
            else -> throw IllegalArgumentException("found no identifier in definition")
        }
    }

    private tailrec fun findNextDefinition(sequence: PtrObject, notDefinition: PtrPairOrNull): PtrPairOrNull {
        tailrec fun loop(rest: PtrPairOrNull, result: PtrPairOrNull): PtrPairOrNull =
            if (rest.isNull()) result
            else {
                loop(
                    rest = rest.cdr(res).let { if (it.isPair(res)) it.toPairOrNull() else PtrPairOrNull(0) },
                    result = ScmPair.make(rest.car(res), result.toObject(), res)
                )
            }

        // val valSequence = sequence.toVal(res)
        if (sequence.isPair(res)) {
            val obj = sequence.toPair().car(res)
            if (obj.isPair(res)) {
                val car = obj.toPair().car(res)
                if (car != ScmSymbol.get("define", res).toObject())
                    return findNextDefinition(
                        sequence = sequence.toPair().cdr(res),
                        notDefinition = ScmPair.make(sequence.toPair().car(res), notDefinition.toObject(), res)
                    )
            }
        }

        return if (sequence.isNotNull())
            loop(notDefinition, ScmPair.make(ScmSymbol.get("begin", res).toObject(), sequence, res))
        else PtrPairOrNull(0)
    }

    private fun transformWithMacro(x: PtrObject): PtrObject =
        when {
            x.isPair(res) -> {
                val car = x.toPair().car(res)
                when {
                    car.isPair(res) -> ScmPair.make(transformWithMacro(car), transformWithMacro(x.toPair().cdr(res)), res).toObject()
                    car.isSymbol(res) -> {
                        val obj: PtrObject = findBind(car.toSymbol())?.second ?: PtrObject(0)
                        if (obj.isMacro(res)) transformWithMacro(x = obj.toMacro().transform(x.toPair(), this, res))
                        else ScmPair.make(car, transformWithMacro(x = x.toPair().cdr(res)), res).toObject()
                    }
                    else -> ScmPair.make(car, transformWithMacro(x = x.toPair().cdr(res)), res).toObject()
                }
            }
            else -> {
                x
            }
        }

    /**
     * Compiler
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 110
     */
    fun compile(x: PtrObject, e: PtrPairOrNull, s: PtrPairOrNull, next: PtrInstruction): PtrInstruction =
        when {
            x.isSymbol(res) -> {
                val bind = findBind(x.toSymbol())
                val obj: PtrObject = bind?.let { (_, obj) ->
                    when {
                        obj.isSyntax(res) || obj.isProcedure(res) -> obj
                        else -> PtrObject(0)
                    }
                } ?: PtrObject(0)
                if (obj.isNotNull()) {
                    ScmInstruction.Constant.make(obj, next, res)
                } else {
                    compileRefer(
                        x.toSymbol(),
                        e,
                        if (setMemberQ(x.toSymbol(), s)) ScmInstruction.Indirect.make(next, res) else next
                    )
                }
            }
            x.isPair(res) -> {
                val bind = x.toPair().car(res).let {
                    if (it.isSymbol(res)) findBind(it.toSymbol()) else null
                }
                val obj: PtrObject = bind?.second ?: PtrObject(0)
                when {
                    obj.isSyntax(res) -> {
                        obj.toSyntax().compile(x.toPair(), e, s, next, this, res)
                    }
                    obj.isProcedure(res) && obj.toProcedure().hasSyntax(res) -> {
                        obj.toProcedure().getSyntax(res).toSyntaxNonNull().compile(x.toPair(), e, s, next, this, res)
                    }
                    else -> {
                        val ptrInstApply = ScmInstruction.Apply.make(0, res)
                        val instApply = ptrInstApply.toVal(res)
                        fun loop(args: PtrPairOrNull, c: PtrInstruction, n: Int): PtrInstruction =
                            if (args.isNull()) {
                                instApply.n = n
                                if (tailQ(next, res)) c
                                else ScmInstruction.Frame.make(next, c, res)
                            } else {
                                loop(
                                    args.cdr(res).also {
                                        if (it.isNeitherNullNorPair(res))
                                            throw IllegalArgumentException(
                                                KevesExceptions.badSyntax(ScmObject.getStringForWrite(x, res))
                                            )
                                    }.toPairOrNull(),
                                    compile(args.car(res), e, s, ScmInstruction.Argument.make(c, res)),
                                    n + 1
                                )
                            }

                        loop(
                            x.toPair().cdr(res).also {
                                if (it.isNeitherNullNorPair(res))
                                    throw IllegalArgumentException(KevesExceptions.badSyntax(x.toPair().toStringForWrite(res)))
                            }.toPairOrNull(),
                            compile(
                                x.toPair().car(res),
                                e,
                                s,
                                if (tailQ(next, res)) {
                                    ScmInstruction.Shift.make(
                                        ScmPair.length(x.toPair().cdr(res), res),
                                        next.asInstructionReturn(res).n,
                                        ptrInstApply.toInstruction(),
                                        res
                                    )
                                } else {
                                    ptrInstApply.toInstruction()
                                }
                            ),
                            0
                        )
                    }
                }
            }
            else -> ScmInstruction.Constant.make(x, next, res)
        }

    private fun findBind(symbol: PtrSymbol): Pair<PtrSymbol, PtrObject>? =
        listOfBinds.find { (id, _) -> symbol == id }?.let { it.first to it.second }

    /**
     * Looks for assignments to any of the set of variables [v]
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 101
     */
    fun findSets(x: PtrObject, v: PtrPairOrNull): PtrPairOrNull =
        when {
            x.isSymbol(res) ->
                PtrPairOrNull(0)
            x.isPair(res) -> {
                val bind = x.toPair().car(res).let { if (it.isSymbol(res)) findBind(it.toSymbol()) else null }
                val second = bind?.second ?: PtrObject(0)
                when {
                    second.isSyntax(res) ->
                        second.toSyntax().findSets(x.toPair(), v, this, res)
                    second.isProcedure(res) && second.toProcedure().hasSyntax(res) ->
                        second.toProcedure().getSyntax(res).toSyntaxNonNull().findSets(x.toPair(), v, this, res)
                    else -> {
                        fun next(x: PtrPairOrNull): PtrPairOrNull =
                            if (x.isNull()) PtrPairOrNull(0)
                            else {
                                val cdrX = try {
                                    x.cdr(res)
                                        .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'next' got non pair") }
                                        .toPairOrNull()
                                } catch (e: IllegalArgumentException) {
                                    throw IllegalArgumentException("'next' got improper list")
                                }
                                setUnion(findSets(x.car(res), v), next(cdrX))
                            }
                        next(
                            if (bind == null) x.toPairOrNull()
                            else x.toPair().cdr(res).let { if (it.isPair(res)) it.toPairOrNull() else PtrPairOrNull(0) }
                        )
                    }
                }
            }
            else -> PtrPairOrNull(0)
        }

    /**
     * Returns the set of ree variables of an expression [x], given an initial set of bound variables [b]
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 104
     */
    fun findFree(x: PtrObject, b: PtrPairOrNull): PtrPairOrNull =
        when {
            x.isSymbol(res) -> {
                when {
                    findBind(x.toSymbol()) != null -> PtrPairOrNull(0)
                    setMemberQ(x.toSymbol(), b) -> PtrPairOrNull(0)
                    else -> ScmPair.list(x, res)
                }
            }
            x.isPair(res) -> {
                val bind = x.toPair().car(res).let { if (it.isSymbol(res)) findBind(it.toSymbol()) else null }
                val second = bind?.second ?: PtrObject(0)
                when {
                    second.isSyntax(res) ->
                        second.toSyntax().findFree(x.toPair(), b, this, res)
                    second.isProcedure(res) && second.toProcedure().hasSyntax(res) ->
                        second.toProcedure().getSyntax(res).toSyntaxNonNull().findFree(x.toPair(), b, this, res)
                    else -> {
                        fun next(x: PtrPairOrNull): PtrPairOrNull =
                            if (x.isNull()) PtrPairOrNull(0)
                            else {
                                val cdrX = x.cdr(res).also {
                                    if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'next' got non pair")
                                }.toPairOrNull()
                                setUnion(findFree(x.car(res), b), next(cdrX))
                            }
                        next(
                            x = if (bind == null) x.toPairOrNull()
                            else x.toPairOrNull().cdr(res)
                                .let { if (it.isPair(res)) it.toPairOrNull() else PtrPairOrNull(0) }
                        )
                    }
                }
            }
            else -> PtrPairOrNull(0)
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    private fun compileRefer(x: PtrSymbol, e: PtrPairOrNull, next: PtrInstruction): PtrInstruction =
        try {
            compileLookup(
                x,
                e,
                { n: Int ->
                    ScmInstruction.ReferLocal.make(n, next, res)
                }, //REFER_LOCAL, ScmInt(n), next) },
                { n: Int ->
                    ScmInstruction.ReferFree.make(n, next, res)
                } // REFER_FREE, ScmInt(n), next) }
            )
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("found undefined variable: ${x.toVal(res).toStringForWrite(res)}")
        }

    /**
     * Lookup a reference in compile time
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    fun compileLookup(
        x: PtrSymbol,
        e: PtrPairOrNull,
        returnLocal: (Int) -> PtrInstruction,
        returnFree: (Int) -> PtrInstruction
    ): PtrInstruction {
        tailrec fun nextFree(free: PtrObject, n: Int): PtrInstruction =
            if (x.toObject() == ScmPair.car(free, res)) returnFree(n)
            else nextFree(free = ScmPair.cdr(free, res), n = n + 1)

        tailrec fun nextLocal(locals: PtrObject, n: Int): PtrInstruction =
            when {
                locals.isNull() -> nextFree(free = e.toVal(res)?.cdr ?: PtrObject(0), n = 0)
                x.toObject() == ScmPair.car(locals, res) -> returnLocal(n)
                else -> nextLocal(locals = ScmPair.cdr(locals, res), n = n + 1)
            }

        return nextLocal(locals = e.toVal(res)?.car ?: PtrObject(0), n = 0)
    }

    /**
     * Collects variables for inclusion in the closure
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    tailrec fun collectFree(vars: PtrPairOrNull, e: PtrPairOrNull, next: PtrInstruction): PtrInstruction =
        if (vars.isNull()) next
        else collectFree(
            vars = vars.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'collect-free' got none pair as vars") }
                .toPairOrNull(),
            e = e,
            next = compileRefer(
                vars.car(res)
                    .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'collect-free' got none symbol as identifier") }
                    .toSymbol(),
                e,
                ScmInstruction.Argument.make(next, res)
            )
        )

    /**
     * Makes boxes from a list of assigned variables [sets] and a list of arguments [vars]
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 102
     */
    fun makeBoxes(sets: PtrPairOrNull, vars: PtrObject, next: PtrInstruction): PtrInstruction {
        fun f(vars: PtrObject, n: Int): PtrInstruction =
            when {
                vars.isNull() -> next
                vars.isPair(res) -> {
                    val carVars = vars.toPair().car(res)
                        .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'make-box' got non symbol as vars") }
                        .toSymbol()
                    if (setMemberQ(carVars, sets)) ScmInstruction.Box.make(n, f(vars.toPair().cdr(res), n + 1), res)
                    else f(vars.toPair().cdr(res), n + 1)
                }
                vars.isSymbol(res) -> {
                    if (setMemberQ(vars.toSymbol(), sets)) ScmInstruction.BoxRest.make(n, next, res)
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
    tailrec fun setMemberQ(x: PtrSymbol, s: PtrPairOrNull): Boolean =
        when {
            s.isNull() -> false
            s.car(res) == x.toObject() -> true
            else -> {
                val cdrS = s.cdr(res)
                    .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-member?' got non pair") }
                    .toPairOrNull()
                setMemberQ(x = x, s = cdrS)
            }
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    private fun setCons(x: PtrSymbol, s: PtrPairOrNull): PtrPairOrNull =
        if (setMemberQ(x, s)) s else ScmPair.make(x.toObject(), s.toObject(), res)

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    tailrec fun setUnion(s1: PtrPairOrNull, s2: PtrPairOrNull): PtrPairOrNull =
        if (s1.isNull()) {
            s2
        } else {
            val carS1 = s1.car(res)
                .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'set-union' got illegal pair") }
                .toSymbol()
            val cdrS1 = s1.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-union' got non pair") }
                .toPairOrNull()
            setUnion(s1 = cdrS1, s2 = setCons(carS1, s2))
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    fun setMinus(s1: PtrPairOrNull, s2: PtrPairOrNull): PtrPairOrNull =
        if (s1.isNull()) {
            PtrPairOrNull(0)
        } else {
            val carS1 = s1.car(res)
                .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'set-minus' got non symbol as identifier") }
                .toSymbol()
            val cdrS1 = s1.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-minus' got non pair") }
                .toPairOrNull()
            if (setMemberQ(carS1, s2)) setMinus(s1 = cdrS1, s2 = s2)
            else ScmPair.make(carS1.toObject(), setMinus(s1 = cdrS1, s2 = s2).toObject(), res)
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    fun setIntersect(s1: PtrPairOrNull, s2: PtrPairOrNull): PtrPairOrNull =
        if (s1.isNull()) {
            PtrPairOrNull(0)
        } else {
            val carS1 = s1.car(res)
                .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'set-intersect' got non symbol as identifier") }
                .toSymbol()
            val cdrS1 = s1.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-intersect' got non pair") }
                .toPairOrNull()
            if (setMemberQ(carS1, s2)) ScmPair.make(carS1.toObject(), setIntersect(s1 = cdrS1, s2 = s2).toObject(), res)
            else setIntersect(s1 = cdrS1, s2 = s2)
        }

    /**
     * Looks at the next instruction to see if it is a return instruction
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 59
     */
    fun tailQ(next: PtrInstruction, res: KevesResources): Boolean = next.isInstructionReturn(res)

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

    fun splitBinds(binds: PtrPairOrNull): Pair<PtrPairOrNull, PtrPairOrNull> {
        tailrec fun loop(
            binds: PtrPairOrNull,
            variables: PtrPairOrNull,
            values: PtrPairOrNull
        ): Pair<PtrPairOrNull, PtrPairOrNull> =
            if (binds.isNull()) {
                variables to values
            } else {
                val pair = binds.car(res)
                    .also { if (it.isNotPair(res)) throw IllegalArgumentException("syntax error") }
                    .toPair()
                val variable = pair.car(res)
                    .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("syntax error") }
                val value = ScmPair.cadr(pair.toObject(), res)
                val cdr = binds.cdr(res)
                    .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("Syntax error") }
                    .toPairOrNull()
                loop(
                    cdr,
                    ScmPair.make(variable, variables.toObject(), res),
                    ScmPair.make(value, values.toObject(), res)
                )
            }
        return loop(binds, PtrPairOrNull(0), PtrPairOrNull(0)).let { (variables, values) ->
            (if (variables.isNotNull()) ScmPair.reverse(variables.toPairNonNull(), res) else PtrPairOrNull(0)) to
                    (if (values.isNotNull()) ScmPair.reverse(values.toPairNonNull(), res) else PtrPairOrNull(0))
        }
    }
}