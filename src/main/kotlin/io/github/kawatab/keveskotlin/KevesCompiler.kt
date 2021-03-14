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
        val valX = x.toVal(res)
        return if (valX is ScmPair) {
            val car = valX.car
            when (car.toVal(res)) {
                is ScmPair -> ScmPair.make(transformDefine(car), transformDefine(valX.cdr), res).toObject()
                is ScmSymbol -> {
                    when (car) {
                        ScmSymbol.get("begin", res).toObject() -> {
                            val (definition, body) = findDefinition(sequence = valX.cdr, definition = PtrPair(0))
                            if (definition.isNull()) {
                                ScmPair.make(car, transformDefine(x = valX.cdr), res).toObject()
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
                        else -> ScmPair.make(car, transformDefine(x = valX.cdr), res).toObject()
                    }
                }
                else -> ScmPair.make(car, transform(valX.cdr), res).toObject()
            }
        } else {
            x
        }
    }

    private tailrec fun findDefinition(sequence: PtrObject, definition: PtrPair): Pair<PtrPair, PtrObject> {
        val valSequence = sequence.toVal(res)
        if (valSequence is ScmPair) {
            val obj = valSequence.car
            val valObj = obj.toVal(res)
            if (valObj is ScmPair) {
                val car = valObj.car
                if (car == ScmSymbol.get("define", res).toObject())
                    return findDefinition(
                        sequence = valSequence.cdr,
                        definition = ScmPair.make(findLambda(valObj.cdr).toObject(), definition.toObject(), res)
                    )
            }
        }
        return definition to (findNextDefinition(sequence, PtrPair(0))
            .let { if (it.isNull()) sequence else it.toObject() })
    }

    private fun findLambda(definition: PtrObject): PtrPairNonNull {
        val valDefinition = definition.toVal(res)
        if (valDefinition !is ScmPair) throw IllegalArgumentException("define is malformed")
        val car = valDefinition.car
        val cdr = valDefinition.cdr
        return when (val valCar = car.toVal(res)) {
            is ScmSymbol -> definition.toPairNonNull()
            is ScmPair -> {
                val variable = valCar.car
                val formals = valCar.cdr
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

    private tailrec fun findNextDefinition(sequence: PtrObject, notDefinition: PtrPair): PtrPair {
        tailrec fun loop(rest: PtrPair, result: PtrPair): PtrPair =
            if (rest.isNull()) result
            else {
                loop(
                    rest = rest.cdr(res).let { if (it.isPair(res)) it.toPair() else PtrPair(0) },
                    result = ScmPair.make(rest.car(res), result.toObject(), res)
                )
            }

        val valSequence = sequence.toVal(res)
        if (valSequence is ScmPair) {
            val obj = valSequence.car
            val valObj = obj.toVal(res)
            if (valObj is ScmPair) {
                val car = valObj.car
                if (car != ScmSymbol.get("define", res).toObject())
                    return findNextDefinition(
                        sequence = valSequence.cdr,
                        notDefinition = ScmPair.make(valSequence.car, notDefinition.toObject(), res)
                    )
            }
        }

        return valSequence?.let {
            loop(notDefinition, ScmPair.make(ScmSymbol.get("begin", res).toObject(), sequence, res))
        } ?: PtrPair(0)
    }

    private fun transformWithMacro(x: PtrObject): PtrObject =
        when (val valX = x.toVal(res)) {
            is ScmPair -> {
                val car = valX.car
                when (car.toVal(res)) {
                    is ScmPair -> ScmPair.make(transformWithMacro(car), transformWithMacro(valX.cdr), res).toObject()
                    is ScmSymbol -> {
                        val obj: ScmObject? = findBind(car.toSymbol())?.second?.toVal(res)
                        if (obj is ScmMacro) transformWithMacro(x = obj.transform(x.toPairNonNull(), this))
                        else ScmPair.make(car, transformWithMacro(x = valX.cdr), res).toObject()
                    }
                    else -> ScmPair.make(car, transformWithMacro(x = valX.cdr), res).toObject()
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
    fun compile(x: PtrObject, e: PtrPair, s: PtrPair, next: PtrInstruction): PtrInstruction =
        when (val valX = x.toVal(res)) {
            is ScmSymbol -> {
                val bind = findBind(x.toSymbol())
                val obj: PtrObject = bind?.let { (_, obj) ->
                    when (obj.toVal(res)) {
                        is ScmSyntax, is ScmProcedure -> obj
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
            is ScmPair -> {
                val bind = valX.car.let {
                    if (it.isSymbol(res)) findBind(it.toSymbol()) else null
                }
                val obj: ScmObject? = bind?.second?.toVal(res)
                when {
                    obj is ScmSyntax -> {
                        obj.compile(x.toPairNonNull(), e, s, next, this)
                    }
                    (obj as? ScmProcedure)?.syntax != null -> {
                        obj.syntax!!.compile(x.toPairNonNull(), e, s, next, this)
                    }
                    else -> {
                        val ptrInstApply = ScmInstruction.Apply.make(0, res)
                        val instApply = ptrInstApply.toVal(res) as ScmInstruction.Apply
                        fun loop(args: PtrPair, c: PtrInstruction, n: Int): PtrInstruction =
                            if (args.isNull()) {
                                instApply.n = n
                                if (tailQ(next, res)) c
                                else ScmInstruction.Frame.make(next, c, res)
                            } else {
                                loop(
                                    args.cdr(res).also {
                                        if (it.isNeitherNullNorPair(res))
                                            throw IllegalArgumentException(
                                                KevesExceptions.badSyntax(x.toVal(res)!!.toStringForWrite(res))
                                            )
                                    }.toPair(),
                                    compile(args.car(res), e, s, ScmInstruction.Argument.make(c, res)),
                                    n + 1
                                )
                            }

                        loop(
                            valX.cdr.also {
                                if (it.isNeitherNullNorPair(res))
                                    throw IllegalArgumentException(KevesExceptions.badSyntax(valX.toStringForWrite(res)))
                            }.toPair(),
                            compile(
                                valX.car,
                                e,
                                s,
                                if (tailQ(next, res)) {
                                    ScmInstruction.Shift.make( // SHIFT,
                                        ScmPair.length(valX.cdr.toVal(res) as? ScmPair, res),
                                        (next.toVal(res) as ScmInstruction.Return).n, // ScmPair.cadr(next),
                                        ptrInstApply,
                                        res
                                    )
                                } else {
                                    ptrInstApply
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
    fun findSets(x: PtrObject, v: PtrPair): PtrPair =
        when (val valX = x.toVal(res)) {
            is ScmSymbol ->
                PtrPair(0)
            is ScmPair -> {
                val bind = valX.car.let { if (it.isSymbol(res)) findBind(it.toSymbol()) else null }
                val second = bind?.second?.toVal(res)
                when {
                    second as? ScmSyntax != null ->
                        second.findSets(x.toPairNonNull(), v, this)
                    (second as? ScmProcedure)?.syntax != null ->
                        second.syntax!!.findSets(x.toPairNonNull(), v, this)
                    else -> {
                        fun next(x: PtrPair): PtrPair =
                            if (x.isNull()) PtrPair(0)
                            else {
                                val cdrX = try {
                                    x.cdr(res)
                                        .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'next' got non pair") }
                                        .toPair()
                                } catch (e: IllegalArgumentException) {
                                    throw IllegalArgumentException("'next' got improper list")
                                }
                                setUnion(findSets(x.car(res), v), next(cdrX))
                            }
                        next(
                            if (bind == null) x.toPair()
                            else valX.cdr.let { if (it.isPair(res)) it.toPair() else PtrPair(0) }
                        )
                    }
                }
            }
            else -> PtrPair(0)
        }

    /**
     * Returns the set of ree variables of an expression [x], given an initial set of bound variables [b]
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 104
     */
    fun findFree(x: PtrObject, b: PtrPair): PtrPair =
        when (val valX = x.toVal(res)) {
            is ScmSymbol -> {
                when {
                    findBind(x.toSymbol()) != null -> PtrPair(0)
                    setMemberQ(x.toSymbol(), b) -> PtrPair(0)
                    else -> ScmPair.list(x, res)
                }
            }
            is ScmPair -> {
                val bind = valX.car.let { if (it.isSymbol(res)) findBind(it.toSymbol()) else null }
                val second = bind?.second?.toVal(res)
                when {
                    second as? ScmSyntax != null ->
                        second.findFree(x.toPairNonNull(), b, this)
                    (second as? ScmProcedure)?.syntax != null ->
                        second.syntax!!.findFree(x.toPairNonNull(), b, this)
                    else -> {
                        fun next(x: PtrPair): PtrPair =
                            if (x.isNull()) PtrPair(0)
                            else {
                                val cdrX = x.cdr(res).also {
                                    if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'next' got non pair")
                                }.toPair()
                                setUnion(findFree(x.car(res), b), next(cdrX))
                            }
                        next(
                            x = if (bind == null) x.toPair()
                            else x.toPair().cdr(res).let { if (it.isPair(res)) it.toPair() else PtrPair(0) }
                        )
                    }
                }
            }
            else -> PtrPair(0)
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    private fun compileRefer(x: PtrSymbol, e: PtrPair, next: PtrInstruction): PtrInstruction =
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
            throw IllegalArgumentException("found undefined variable: ${x.toVal(res)?.toStringForWrite(res)}")
        }

    /**
     * Lookup a reference in compile time
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    fun compileLookup(
        x: PtrSymbol,
        e: PtrPair,
        returnLocal: (Int) -> PtrInstruction,
        returnFree: (Int) -> PtrInstruction
    ): PtrInstruction {
        tailrec fun nextFree(free: PtrObject, n: Int): PtrInstruction =
            if (x.toObject() == ScmPair.car(free.toVal(res))) returnFree(n)
            else nextFree(free = ScmPair.cdr(free.toVal(res)), n = n + 1)

        tailrec fun nextLocal(locals: PtrObject, n: Int): PtrInstruction =
            when {
                locals.isNull() -> nextFree(free = ScmPair.cdr(e.toVal(res)), n = 0)
                x.toObject() == ScmPair.car(locals.toVal(res)) -> returnLocal(n)
                else -> nextLocal(locals = ScmPair.cdr(locals.toVal(res)), n = n + 1)
            }

        return nextLocal(locals = ScmPair.car(e.toVal(res)), n = 0)
    }

    /**
     * Collects variables for inclusion in the closure
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 95
     */
    tailrec fun collectFree(vars: PtrPair, e: PtrPair, next: PtrInstruction): PtrInstruction =
        if (vars.isNull()) next
        else collectFree(
            vars = vars.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'collect-free' got none pair as vars") }
                .toPair(),
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
    fun makeBoxes(sets: PtrPair, vars: PtrObject, next: PtrInstruction): PtrInstruction {
        fun f(vars: PtrObject, n: Int): PtrInstruction =
            when (val valVars = vars.toVal(res)) {
                null -> next
                is ScmPair -> {
                    val carVars = valVars.car
                        .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'make-box' got non symbol as vars") }
                        .toSymbol()
                    if (setMemberQ(carVars, sets)) ScmInstruction.Box.make(n, f(valVars.cdr, n + 1), res)
                    else f(valVars.cdr, n + 1)
                }
                is ScmSymbol -> {
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
    tailrec fun setMemberQ(x: PtrSymbol, s: PtrPair): Boolean =
        when {
            s.isNull() -> false
            s.car(res) == x.toObject() -> true
            else -> {
                val cdrS = s.cdr(res)
                    .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-member?' got non pair") }
                    .toPair()
                setMemberQ(x = x, s = cdrS)
            }
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    private fun setCons(x: PtrSymbol, s: PtrPair): PtrPair =
        if (setMemberQ(x, s)) s else ScmPair.make(x.toObject(), s.toObject(), res)

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    tailrec fun setUnion(s1: PtrPair, s2: PtrPair): PtrPair =
        if (s1.isNull()) {
            s2
        } else {
            val carS1 = s1.car(res)
                .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'set-union' got illegal pair") }
                .toSymbol()
            val cdrS1 = s1.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-union' got non pair") }
                .toPair()
            setUnion(s1 = cdrS1, s2 = setCons(carS1, s2))
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    fun setMinus(s1: PtrPair, s2: PtrPair): PtrPair =
        if (s1.isNull()) {
            PtrPair(0)
        } else {
            val carS1 = s1.car(res)
                .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'set-minus' got non symbol as identifier") }
                .toSymbol()
            val cdrS1 = s1.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-minus' got non pair") }
                .toPair()
            if (setMemberQ(carS1, s2)) setMinus(s1 = cdrS1, s2 = s2)
            else ScmPair.make(carS1.toObject(), setMinus(s1 = cdrS1, s2 = s2).toObject(), res)
        }

    /**
     *
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 93
     */
    fun setIntersect(s1: PtrPair, s2: PtrPair): PtrPair =
        if (s1.isNull()) {
            PtrPair(0)
        } else {
            val carS1 = s1.car(res)
                .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("'set-intersect' got non symbol as identifier") }
                .toSymbol()
            val cdrS1 = s1.cdr(res)
                .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("'set-intersect' got non pair") }
                .toPair()
            if (setMemberQ(carS1, s2)) ScmPair.make(carS1.toObject(), setIntersect(s1 = cdrS1, s2 = s2).toObject(), res)
            else setIntersect(s1 = cdrS1, s2 = s2)
        }

    /**
     * Looks at the next instruction to see if it is a return instruction
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 59
     */
// fun tailQ(next: ScmPair?): Boolean = next != null && next.car is ScmInstruction.Return // === ScmInstruction.RETURN
    fun tailQ(next: PtrInstruction, res: KevesResources): Boolean = next.toVal(res) is ScmInstruction.Return

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

    fun splitBinds(binds: PtrPair): Pair<PtrPair, PtrPair> {
        tailrec fun loop(binds: PtrPair, variables: PtrPair, values: PtrPair): Pair<PtrPair, PtrPair> =
            if (binds.isNull()) {
                variables to values
            } else {
                val pair = binds.car(res)
                    .also { if (it.isNotPair(res)) throw IllegalArgumentException("syntax error") }
                    .toPairNonNull()
                val variable = pair.car(res)
                    .also { if (it.isNotSymbol(res)) throw IllegalArgumentException("syntax error") }
                val value = ScmPair.cadr(pair.toVal(res), res)
                val cdr = binds.cdr(res)
                    .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("Syntax error") }
                    .toPair()
                loop(
                    cdr,
                    ScmPair.make(variable, variables.toObject(), res),
                    ScmPair.make(value, values.toObject(), res)
                )
            }
        return loop(binds, PtrPair(0), PtrPair(0)).let { (variables, values) ->
            (if (variables.isNotNull()) ScmPair.reverse(variables.toPairNonNull(), res) else PtrPair(0)) to
                    (if (values.isNotNull()) ScmPair.reverse(values.toPairNonNull(), res) else PtrPair(0))
        }
    }
}