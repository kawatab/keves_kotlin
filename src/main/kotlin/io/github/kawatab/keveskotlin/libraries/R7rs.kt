/*
 * R7rs.kt
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

import io.github.kawatab.keveskotlin.KevesCompiler
import io.github.kawatab.keveskotlin.KevesVM
import io.github.kawatab.keveskotlin.objects.*

class R7rs {
    val listOfBinds by lazy {
        listOf<Pair<ScmSymbol, ScmObject?>>(
            ScmSymbol.get("begin") to syntaxBegin,
            ScmSymbol.get("quote") to syntaxQuote,
            ScmSymbol.get("lambda") to syntaxLambda,
            ScmSymbol.get("if") to syntaxIf,
            ScmSymbol.get("set!") to syntaxSetE,
            ScmSymbol.get("let") to macroLet,
            ScmSymbol.get("let*") to macroLetStar,
            ScmSymbol.get("letrec") to macroLetrec,
            ScmSymbol.get("letrec*") to macroLetrecStar,
            ScmSymbol.get("cond") to macroCond,
            ScmSymbol.get("when") to macroWhen,
            ScmSymbol.get("until") to macroUntil,
            ScmSymbol.get("call/cc") to procCallWithCC,
            ScmSymbol.get("call-with-current-continuation") to procCallWithCC,
            ScmSymbol.get("display") to procDisplay,
            ScmSymbol.get("+") to procAdd,
            ScmSymbol.get("-") to procSubtract,
            ScmSymbol.get("*") to procMultiple,
            ScmSymbol.get("/") to procDivide,
            ScmSymbol.get("=") to procEqual,
            ScmSymbol.get("<") to procLessThan,
            ScmSymbol.get(">") to procGraterThan,
            ScmSymbol.get("append") to procAppend,
            ScmSymbol.get("cons") to procCons,
            ScmSymbol.get("caaaar") to procCaaaar,
            ScmSymbol.get("caaadr") to procCaaadr,
            ScmSymbol.get("caaar") to procCaaar,
            ScmSymbol.get("caadar") to procCaadar,
            ScmSymbol.get("caaddr") to procCaaddr,
            ScmSymbol.get("caadr") to procCaadr,
            ScmSymbol.get("caar") to procCaar,
            ScmSymbol.get("cadaar") to procCadaar,
            ScmSymbol.get("cadadr") to procCadadr,
            ScmSymbol.get("cadar") to procCadar,
            ScmSymbol.get("caddar") to procCaddar,
            ScmSymbol.get("cadddr") to procCadddr,
            ScmSymbol.get("caddr") to procCaddr,
            ScmSymbol.get("cadr") to procCadr,
            ScmSymbol.get("car") to procCar,
            ScmSymbol.get("cdaaar") to procCdaaar,
            ScmSymbol.get("cdaadr") to procCdaadr,
            ScmSymbol.get("cdaar") to procCdaar,
            ScmSymbol.get("cdadar") to procCdadar,
            ScmSymbol.get("cdaddr") to procCdaddr,
            ScmSymbol.get("cdadr") to procCdadr,
            ScmSymbol.get("cdar") to procCdar,
            ScmSymbol.get("cddaar") to procCddaar,
            ScmSymbol.get("cddadr") to procCddadr,
            ScmSymbol.get("cddar") to procCddar,
            ScmSymbol.get("cdddar") to procCdddar,
            ScmSymbol.get("cddddr") to procCddddr,
            ScmSymbol.get("cdddr") to procCdddr,
            ScmSymbol.get("cddr") to procCddr,
            ScmSymbol.get("cdr") to procCdr,
            ScmSymbol.get("eq?") to procEqQ,
            ScmSymbol.get("equal?") to procEqualQ,
            ScmSymbol.get("eqv?") to procEqvQ,
            ScmSymbol.get("length") to procLength,
            ScmSymbol.get("list") to procList,
            ScmSymbol.get("list?") to procListQ,
            ScmSymbol.get("make-list") to procMakeList,
            ScmSymbol.get("make-vector") to procMakeVector,
            ScmSymbol.get("null?") to procNullQ,
            ScmSymbol.get("pair?") to procPairQ,
            ScmSymbol.get("reverse") to procReverse,
            ScmSymbol.get("set-car!") to procSetCarE,
            ScmSymbol.get("set-cdr!") to procSetCdrE,
            ScmSymbol.get("zero?") to procZeroQ,
        )
    }

    /** syntax: begin */
    private val syntaxBegin = object : ScmSyntax("begin") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair {
            tailrec fun loop(exps: ScmPair?, c: ScmPair?): ScmPair? =
                if (exps == null) {
                    c
                } else {
                    val expsCdr: ScmPair? = exps.cdr?.let {
                        it as? ScmPair
                            ?: throw IllegalArgumentException(KevesCompiler.badSyntax.format(x.toStringForWrite()))
                    }
                    loop(expsCdr, compiler.compile(ScmPair.car(exps), e, s, c))
                }

            return patternMatchBegin(x)?.let { expressions ->
                loop(ScmPair.reverse(expressions), next)
            } ?: ScmPair.list(ScmInstruction.CONSTANT, ScmConstant.UNDEF)
        }

        override fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val exps = patternMatchBegin(x)
            return compiler.findSets(exps, v)
        }

        override fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val exps = patternMatchBegin(x)
            return compiler.findFree(exps, b)
        }
    }

    private fun patternMatchBegin(x: ScmPair): ScmPair? =
        ScmPair.cdr(x)?.let {
            it as? ScmPair ?: throw IllegalArgumentException(KevesCompiler.badSyntax.format(x.toStringForWrite()))
        }

    /** syntax: quote */
    private val syntaxQuote = object : ScmSyntax("quote") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair {
            val obj = patternMatchQuote(x)
            return ScmPair.list(ScmInstruction.CONSTANT, obj, next)
        }

        override fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair? {
            patternMatchQuote(x)
            return null
        }

        override fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair? {
            patternMatchQuote(x)
            return null
        }
    }

    private fun patternMatchQuote(x: ScmPair): ScmObject? =
        try {
            ScmPair.cadr(x) // obj
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected1DatumButGotNothing.format("quote"))
        }.also {
            ScmPair.cddr(x)
                ?.let { throw IllegalArgumentException(KevesCompiler.expected1DatumButGotMore.format("quote")) }
        }

    /** syntax: lambda */
    private val syntaxLambda = object : ScmSyntax("lambda") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (vars, body) = patternMatchLambda(x)
            val (varsAsProperList, numArg) = ScmPair.toProperList(vars)
            val free = compiler.findFree(body, varsAsProperList)
            val sets = compiler.findSets(body, varsAsProperList)
            return compiler.collectFree(
                free,
                e,
                ScmPair.list(
                    ScmInstruction.CLOSE,
                    ScmInt(ScmPair.length(free)),
                    ScmInt(numArg),
                    compiler.makeBoxes(
                        sets,
                        vars,
                        compiler.compile(
                            ScmPair(KevesCompiler.symbolBegin, body),
                            ScmPair(varsAsProperList, free),
                            compiler.setUnion(sets, compiler.setIntersect(s, free)),
                            ScmPair.list(ScmInstruction.RETURN, ScmInt(ScmPair.length(varsAsProperList)))
                        )
                    ),
                    next
                )
            )
        }

        override fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (vars, body) = patternMatchLambda(x)
            return compiler.findSets(body, compiler.setMinus(v, ScmPair.toProperList(vars).first))
        }

        override fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (vars, body) = patternMatchLambda(x)
            return compiler.findFree(body, compiler.setUnion(ScmPair.toProperList(vars).first, b))
        }
    }

    private fun patternMatchLambda(x: ScmPair): Pair<ScmObject?, ScmPair> {
        val vars: ScmObject? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2OrMoreDatumButGotLess.format("lambda"))
        }?.let {
            it as? ScmPair ?: it as? ScmSymbol
            ?: throw IllegalArgumentException(KevesCompiler.badSyntax.format(x.toStringForWrite()))
        }
        val body: ScmPair = try {
            ScmPair.cddr(x) // original is caddr instead of cddr
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2OrMoreDatumButGotLess.format("lambda"))
        } as? ScmPair ?: throw IllegalArgumentException(KevesCompiler.badSyntax.format(x.toStringForWrite()))
        return vars to body
    }

    /** syntax: if */
    private val syntaxIf = object : ScmSyntax("if") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (test, thn, els) = patternMatchIf(x)
            val thenC = compiler.compile(thn, e, s, next)
            val elseC = compiler.compile(els, e, s, next)
            return compiler.compile(test, e, s, ScmPair.list(ScmInstruction.TEST, thenC, elseC))
        }

        override fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (test, thn, els) = patternMatchIf(x)
            return compiler.setUnion(
                compiler.findSets(test, v),
                compiler.setUnion(compiler.findSets(thn, v), compiler.findSets(els, v))
            )
        }

        override fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (test, thn, els) = patternMatchIf(x)
            return compiler.setUnion(
                compiler.findFree(test, b),
                compiler.setUnion(compiler.findFree(thn, b), compiler.findFree(els, b))
            )
        }
    }

    private fun patternMatchIf(x: ScmPair): Triple<ScmObject?, ScmObject?, ScmObject?> {
        val test: ScmObject? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected3DatumButGotLess.format("if"))
        }
        val thn: ScmObject? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected3DatumButGotLess.format("if"))
        }
        val els: ScmObject? = try {
            ScmPair.cadddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected3DatumButGotLess.format("if"))
        }
        ScmPair.cddddr(x)?.let { throw IllegalArgumentException(KevesCompiler.expected3DatumButGotMore.format("if")) }
        return Triple(test, thn, els)
    }

    /** syntax: set! */
    private val syntaxSetE = object : ScmSyntax("set!") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (variable, xx) = patternMatchSetE(x)
            return compiler.compileLookup(
                variable,
                e,
                { n: Int ->
                    compiler.compile(xx, e, s, ScmPair.list(ScmInstruction.ASSIGN_LOCAL, ScmInt(n), next))
                },
                { n: Int ->
                    compiler.compile(xx, e, s, ScmPair.list(ScmInstruction.ASSIGN_FREE, ScmInt(n), next))
                }
            )
        }

        override fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (variable, xx) = patternMatchSetE(x)
            return compiler.setUnion(
                if (compiler.setMemberQ(variable, v)) ScmPair.list(variable) else null,
                compiler.findSets(xx, v)
            )
        }

        override fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (variable, exp) = patternMatchSetE(x)
            return compiler.setUnion(
                if (compiler.setMemberQ(variable, b)) null else ScmPair.list(variable),
                compiler.findFree(exp, b)
            )
        }
    }

    private fun patternMatchSetE(x: ScmPair): Pair<ScmSymbol, ScmObject?> {
        val variable: ScmSymbol = try {
            ScmPair.cadr(x) as? ScmSymbol
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("set!"))
        } ?: throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("set!"))
        val exp: ScmObject? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("set!"))
        }
        ScmPair.cdddr(x)?.let { throw IllegalArgumentException(KevesCompiler.expected2DatumButGotMore.format("set!")) }
        return variable to exp
    }

    /** macro: let */
    private val macroLet = object : ScmMacro("let") {
        override fun transform(x: ScmPair, compiler: KevesCompiler): ScmObject? {
            val (bindings, body) = patternMatchLet(x)
            return compiler.transform(
                if (bindings == null) {
                    ScmPair(ScmSymbol.get("begin"), body)
                } else {
                    val (variables, values) = compiler.splitBinds(bindings)
                    ScmPair(ScmPair(ScmSymbol.get("lambda"), ScmPair(variables, body)), values)
                }
            )
        }
    }

    private fun patternMatchLet(x: ScmPair): Pair<ScmPair?, ScmPair?> {
        val bindings: ScmPair? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("let"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("let"))
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("let"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("let"))
        }

        return bindings to body
    }

    /** macro: let* */
    private val macroLetStar = object : ScmMacro("let*") {
        override fun transform(x: ScmPair, compiler: KevesCompiler): ScmObject? {
            val (bindings, body) = patternMatchLetStar(x)
            return compiler.transform(
                if (bindings == null) {
                    ScmPair(ScmSymbol.get("begin"), body)
                } else {
                    val first = bindings.car
                    val rest = bindings.cdr
                    ScmPair(
                        ScmSymbol.get("let"),
                        ScmPair.list(
                            ScmPair.list(first),
                            ScmPair(ScmSymbol.get("let*"), ScmPair(rest, body))
                        )
                    )
                }
            )
        }
    }

    private fun patternMatchLetStar(x: ScmPair): Pair<ScmPair?, ScmPair?> {
        val bindings: ScmPair? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("let*"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("let*"))
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("let*"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("let*"))
        }

        return bindings to body
    }

    /** macro: letrec */
    private val macroLetrec = object : ScmMacro("letrec") {
        override fun transform(x: ScmPair, compiler: KevesCompiler): ScmObject? {
            tailrec fun loop(
                bindings: ScmPair?,
                outerBindings: ScmPair?,
                innerBindings: ScmPair?,
                innerBody: ScmPair?
            ): Triple<ScmPair?, ScmPair?, ScmPair?> =
                if (bindings == null) Triple(outerBindings, innerBindings, innerBody)
                else {
                    val temp = ScmSymbol.generate()
                    val variable = (bindings.car as? ScmPair)?.car
                    val exp = (bindings.car as? ScmPair)?.cdr
                    loop(
                        bindings = bindings.cdr as? ScmPair,
                        outerBindings = ScmPair(ScmPair.list(variable, ScmConstant.UNDEF), outerBindings),
                        innerBindings = ScmPair(ScmPair(temp, exp), innerBindings),
                        innerBody = ScmPair(ScmPair.list(ScmSymbol.get("set!"), variable, temp), innerBody)
                    )
                }

            val (bindings, body) = patternMatchLetrec(x)
            return compiler.transform(
                if (bindings == null) {
                    ScmPair(ScmSymbol.get("begin"), body)
                } else {
                    val (outerBindings, innerBindings, innerBody) = loop(ScmPair.reverse(bindings), null, null, body)
                    ScmPair.list(
                        ScmSymbol.get("let"), outerBindings,
                        ScmPair.listStar(ScmSymbol.get("let"), innerBindings, innerBody)
                    )
                }
            )
        }
    }

    private fun patternMatchLetrec(x: ScmPair): Pair<ScmPair?, ScmPair?> {
        val bindings: ScmPair? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("letrec"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("letrec"))
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("letrec"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("letrec"))
        }

        return bindings to body
    }


    /** macro: letrec* */
    private val macroLetrecStar = object : ScmMacro("letrec*") {
        override fun transform(x: ScmPair, compiler: KevesCompiler): ScmObject? {
            tailrec fun loop(
                bindings: ScmPair?,
                variables: ScmPair?,
                assignments: ScmPair?,
                initValues: ScmPair?
            ): Triple<ScmPair?, ScmPair?, ScmPair?> =
                if (bindings == null) Triple(variables, assignments, initValues)
                else loop(
                    bindings = bindings.cdr as? ScmPair,
                    variables = ScmPair((bindings.car as? ScmPair)?.car, variables),
                    assignments = ScmPair(ScmPair(ScmSymbol.get("set!"), bindings.car), assignments),
                    initValues = ScmPair(ScmConstant.UNDEF, initValues)
                )

            val (bindings, body) = patternMatchLetrecStar(x)
            return compiler.transform(
                if (bindings == null) {
                    ScmPair(ScmSymbol.get("begin"), body)
                } else {
                    val (variables, assignments, initValues) = loop(ScmPair.reverse(bindings), null, body, null)
                    ScmPair(ScmPair(ScmSymbol.get("lambda"), ScmPair(variables, assignments)), initValues)
                }
            )
        }
    }

    private fun patternMatchLetrecStar(x: ScmPair): Pair<ScmPair?, ScmPair?> {
        val bindings: ScmPair? = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("letrec*"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("letrec*"))
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("letrec*"))
        }?.let {
            if (it is ScmPair) it
            else throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("letrec*"))
        }

        return bindings to body
    }

    /** macro: cond */
    private val macroCond = object : ScmMacro("cond") {
        override fun transform(x: ScmPair, compiler: KevesCompiler): ScmObject? {
            tailrec fun loop(rest: ScmPair?, result: ScmObject?): ScmObject? =
                if (rest == null) {
                    result
                } else {
                    val obj = rest.car
                    loop(
                        rest = rest.cdr as? ScmPair,
                        result = ScmPair.list(ScmSymbol.get("if"), ScmPair.car(obj), ScmPair.cdr(obj), result)
                    )
                }

            val clause = (x.cdr as? ScmPair) ?: throw IllegalArgumentException("'cond' had no clause")
            val converted = addBeginToExpressions(rest = clause, result = null)
            val first = ScmPair.car(converted)
            val rest = ScmPair.cdr(converted)
            val firstTest = ScmPair.car(first)
            val firstExpression = ScmPair.cdr(first)
            val result =
                if (firstTest === ScmSymbol.get("else")) firstExpression
                else ScmPair.list(ScmSymbol.get("if"), firstTest, firstExpression, ScmConstant.UNDEF)

            val xx = loop(rest = rest as? ScmPair, result = result)
            return compiler.transform(xx)
        }
    }

    private tailrec fun addBeginToExpressions(rest: ScmPair?, result: ScmPair?): ScmPair? =
        if (rest == null) {
            result
        } else {
            val car = (rest.car as? ScmPair)
                ?: throw IllegalArgumentException("clause must be list but got other in 'cond'")
            val test = car.car
            val expressions = (car.cdr as? ScmPair)
                ?: throw IllegalArgumentException("expression must be list but got other in 'cond'")
            addBeginToExpressions(
                rest.cdr as ScmPair?,
                ScmPair(
                    ScmPair(
                        test,
                        if (ScmPair.length(expressions) > 1) ScmPair(ScmSymbol.get("begin"), expressions)
                        else expressions.car
                    ),
                    result
                )
            )
        }

    /** macro: when */
    private val macroWhen = object : ScmMacro("when") {
        override fun transform(x: ScmPair, compiler: KevesCompiler): ScmObject? {
            val (test, expressions) = patternMatchWhen(x)
            val xx =
                if (ScmPair.length(expressions) > 1) {
                    ScmPair.list(
                        ScmSymbol.get("if"),
                        test,
                        ScmPair(ScmSymbol.get("begin"), expressions),
                        ScmConstant.UNDEF
                    )
                } else {
                    ScmPair.list(ScmSymbol.get("if"), test, expressions.car, ScmConstant.UNDEF)
                }
            return compiler.transform(xx)
        }
    }

    private fun patternMatchWhen(x: ScmPair): Pair<ScmPair, ScmPair> {
        val test: ScmPair = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("when"))
        } as? ScmPair ?: throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("when"))

        val expressions: ScmPair = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("when"))
        } as? ScmPair ?: throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("when"))

        return test to expressions
    }

    /** macro: until */
    private val macroUntil = object : ScmMacro("until") {
        override fun transform(x: ScmPair, compiler: KevesCompiler): ScmObject? {
            val (test, expressions) = patternMatchUntil(x)
            val xx =
                if (ScmPair.length(expressions) > 1) {
                    ScmPair.list(
                        ScmSymbol.get("if"),
                        test,
                        ScmConstant.UNDEF,
                        ScmPair(ScmSymbol.get("begin"), expressions)
                    )
                } else {
                    ScmPair.list(ScmSymbol.get("if"), test, ScmConstant.UNDEF, expressions.car)
                }
            return compiler.transform(xx)
        }
    }

    private fun patternMatchUntil(x: ScmPair): Pair<ScmPair, ScmPair> {
        val test: ScmPair = try {
            ScmPair.cadr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("until"))
        } as? ScmPair ?: throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("until"))

        val expressions: ScmPair = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected2DatumButGotLess.format("until"))
        } as? ScmPair ?: throw IllegalArgumentException(KevesCompiler.expectedSymbol.format("until"))

        return test to expressions
    }

    /** procedure: call/cc, call-with-current-continuation */
    private val procCallWithCC = object : ScmProcedure(
        "call/cc",
        object : ScmSyntax(id = "call/cc") {
            override fun compile(
                x: ScmPair,
                e: ScmPair?,
                s: ScmPair?,
                next: ScmPair?,
                compiler: KevesCompiler
            ): ScmPair {
                val xx = patternMatchCallCC(x)
                val c = ScmPair.list(
                    ScmInstruction.CONTI,
                    ScmPair.list(
                        ScmInstruction.ARGUMENT,
                        compiler.compile(
                            xx,
                            e,
                            s,
                            if (compiler.tailQ(next)) ScmPair.list(
                                ScmInstruction.SHIFT,
                                ScmInt(1),
                                ScmPair.cadr(next),
                                ScmPair.list(ScmInstruction.APPLY, ScmInt(1))
                            )
                            else ScmPair.list(ScmInstruction.APPLY, ScmInt(1))
                        )
                    )
                )
                return if (compiler.tailQ(next)) c else ScmPair.list(ScmInstruction.FRAME, next, c)
            }

            override fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair? {
                val exp = patternMatchCallCC(x)
                return compiler.findSets(exp, v)
            }

            override fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair? {
                val exp = patternMatchCallCC(x)
                return compiler.findFree(exp, b)
            }
        }) {
        override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
        override fun normalProc(n: Int, vm: KevesVM) {}
    }

    private fun patternMatchCallCC(x: ScmPair): ScmPair? =
        try {
            ScmPair.cadr(x) // exp
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected1DatumButGotNothing.format("call/cc"))
        }?.let {
            it as? ScmPair ?: throw IllegalArgumentException(KevesCompiler.badSyntax.format(x.toStringForWrite()))
        }.also {
            ScmPair.cddr(x)
                ?.let { throw IllegalArgumentException(KevesCompiler.expected1DatumButGotMore.format("call/cc")) }
        }

    /** procedure: display */
    private val procDisplay: ScmProcedure by lazy {
        object : ScmProcedure("display", object : ScmSyntax("display") {
            override fun compile(
                x: ScmPair,
                e: ScmPair?,
                s: ScmPair?,
                next: ScmPair?,
                compiler: KevesCompiler
            ): ScmPair {
                val xx = patternMatchDisplay(x)
                val c = compiler.compile(
                    xx,
                    e,
                    s,
                    if (compiler.tailQ(next)) ScmPair.list(
                        ScmInstruction.SHIFT,
                        ScmInt(1),
                        ScmPair.cadr(next),
                        ScmPair.list(procDisplay)
                    )
                    else ScmPair.list(procDisplay)
                )
                return if (compiler.tailQ(next)) c!! else ScmPair.list(ScmInstruction.FRAME, next, c)
            }

            override fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair? {
                val exp = patternMatchCallCC(x)
                return compiler.findSets(exp, v)
            }

            override fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair? {
                val exp = patternMatchCallCC(x)
                return compiler.findFree(exp, b)
            }
        }) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {
                print(getStringForDisplay(acc))
                val ret: ScmPair? = vm.stack.index(sp, 0) as? ScmPair
                val f: Int =
                    (vm.stack.index(sp, 1) as? ScmInt)?.value
                        ?: throw IllegalArgumentException("${procDisplay.id} did wrong")
                val c: ScmClosure? = vm.stack.index(sp, 2) as? ScmClosure
                vm.acc = ScmConstant.UNDEF
                vm.x = ret
                vm.fp = f
                vm.clsr = c
                vm.sp = sp - 3
                return
            }

            override fun normalProc(n: Int, vm: KevesVM) {}
        }
    }

    private fun patternMatchDisplay(x: ScmPair): ScmObject? =
        try {
            ScmPair.cadr(x) // exp
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesCompiler.expected1DatumButGotNothing.format("display"))
        }

    /** procedure: plus */
    private val procAdd: ScmProcedure by lazy {
        object : ScmProcedure("+", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val sp = vm.sp
                tailrec fun doubleLoop(index: Int, sum: Double): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(sp, index)) {
                            is ScmInt -> doubleLoop(index + 1, sum + obj.value)
                            is ScmDouble -> doubleLoop(index + 1, sum + obj.value)
                            else -> throw IllegalArgumentException("${procAdd.id} expected number object, but got other")
                        }
                    } else {
                        ScmDouble(sum)
                    }

                tailrec fun loop(index: Int, sum: Int): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(sp, index)) {
                            is ScmInt -> loop(index + 1, sum + obj.value)
                            is ScmDouble -> doubleLoop(index + 1, sum + obj.value)
                            else -> throw IllegalArgumentException("${procAdd.id} expected number object, but got other")
                        }
                    } else {
                        ScmInt(sum)
                    }

                val sum = loop(0, 0)
                vm.scmProcReturn(sum, n, this)
            }
        }
    }

    /** procedure: minus */
    private val procSubtract: ScmProcedure by lazy {
        object : ScmProcedure("-", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val difference = when (n) {
                    0 -> throw IllegalArgumentException("${procSubtract.id} expected one or more object, but got nothing")

                    1 -> {
                        when (val obj = vm.stack.index(vm.sp, 0)) {
                            is ScmInt -> ScmInt(-obj.value) // opposite
                            is ScmDouble -> ScmDouble(-obj.value) // opposite
                            else -> throw IllegalArgumentException("${procSubtract.id} expected number object, but got other")
                        }
                    }
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, difference: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> doubleLoop(index + 1, difference - obj.value)
                                    is ScmDouble -> doubleLoop(index + 1, difference - obj.value)
                                    else -> throw IllegalArgumentException("${procSubtract.id} expected number object, but got other")
                                }
                            } else {
                                ScmDouble(difference)
                            }

                        tailrec fun intLoop(index: Int, difference: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> intLoop(index + 1, difference - obj.value)
                                    is ScmDouble -> doubleLoop(index + 1, difference - obj.value)
                                    else -> throw IllegalArgumentException("${procSubtract.id} expected number object, but got other")
                                }
                            } else {
                                ScmInt(difference)
                            }

                        when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw IllegalArgumentException("${procSubtract.id} expected number object, but got other")
                        }
                    }
                }

                vm.scmProcReturn(difference, n, this)
            }
        }
    }

    /** procedure: multiple */
    private val procMultiple: ScmProcedure by lazy {
        object : ScmProcedure("*", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val i = vm.sp
                tailrec fun doubleLoop(index: Int, product: Double): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(i, index)) {
                            is ScmInt -> doubleLoop(index = index + 1, product = product * obj.value)
                            is ScmDouble -> doubleLoop(index = index + 1, product = product * obj.value)
                            else -> throw IllegalArgumentException("${procMultiple.id} expected number object, but got other")
                        }
                    } else {
                        ScmDouble(product)
                    }

                tailrec fun intLoop(index: Int, product: Int): ScmObject =
                    if (index < n) {
                        when (val obj = vm.stack.index(i, index)) {
                            is ScmInt -> intLoop(index = index + 1, product = product * obj.value)
                            is ScmDouble -> doubleLoop(index + 1, product * obj.value)
                            else -> throw IllegalArgumentException("${procMultiple.id} expected number object, but got other")
                        }
                    } else {
                        ScmInt(product)
                    }

                val product = intLoop(0, 1)
                vm.scmProcReturn(product, n, this)
            }
        }
    }

    /** procedure: divide */
    private val procDivide: ScmProcedure by lazy {
        object : ScmProcedure("/", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val quotient = when (n) {
                    0 -> throw IllegalArgumentException("${procDivide.id} expected one or more object, but got nothing")

                    1 -> {
                        when (val obj = vm.stack.index(vm.sp, 0)) {
                            is ScmInt -> when (obj.value) { // reciprocal
                                0 -> throw IllegalArgumentException("${procDivide.id} expected non zero number, but got 0")
                                -1, 1 -> obj
                                else -> ScmDouble(1.0 / obj.value.toDouble())
                            }

                            is ScmDouble -> ScmDouble(1.0 / obj.value) // reciprocal

                            else -> throw IllegalArgumentException("${procDivide.id} expected number object, but got other")
                        }
                    }
                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, quotient: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> doubleLoop(
                                        index = index + 1,
                                        quotient = quotient / obj.value.toDouble()
                                    )
                                    is ScmDouble -> doubleLoop(index = index + 1, quotient = quotient / obj.value)
                                    else -> throw IllegalArgumentException("${procDivide.id} expected number object, but got other")
                                }
                            } else {
                                ScmDouble(quotient)
                            }

                        tailrec fun intLoop(index: Int, quotient: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt -> {
                                        if (obj.value == 0) {
                                            throw IllegalArgumentException("${procDivide.id} expected non zero number, but got 0")
                                        }
                                        val remainder = quotient % obj.value
                                        if (remainder == 0) {
                                            intLoop(index = index + 1, quotient = quotient / obj.value)
                                        } else {
                                            doubleLoop(index + 1, quotient.toDouble() / obj.value.toDouble())
                                        }
                                    }
                                    is ScmDouble -> doubleLoop(index + 1, quotient.toDouble() / obj.value)
                                    else -> throw IllegalArgumentException("${procDivide.id} expected number object, but got other")
                                }
                            } else {
                                ScmInt(quotient)
                            }

                        when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw IllegalArgumentException("${procDivide.id} expected number object, but got other")
                        }
                    }
                }
                vm.scmProcReturn(quotient, n, this)
            }
        }
    }

    /** procedure: = */
    private val procEqual: ScmProcedure by lazy {
        object : ScmProcedure("=", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procEqual.id} expected two or more object, but got nothing")
                    1 -> throw IllegalArgumentException("${procEqual.id} expected two or more object, but got one")

                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last == obj.value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.value.toDouble())
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last == obj.value) doubleLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw IllegalArgumentException("${procEqual.id} expected number object, but got other")
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        tailrec fun intLoop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last == obj.value) intLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last.toDouble() == obj.value) doubleLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw IllegalArgumentException("${procEqual.id} expected number object, but got other")
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        val result = when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw IllegalArgumentException("${procEqual.id} expected number object, but got other")
                        }

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: '<' */
    private val procLessThan: ScmProcedure by lazy {
        object : ScmProcedure("<", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procLessThan.id} expected two or more object, but got nothing")
                    1 -> throw IllegalArgumentException("${procLessThan.id} expected two or more object, but got one")

                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last < obj.value.toDouble()) doubleLoop(
                                            index = index + 1,
                                            last = obj.value.toDouble()
                                        )
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last < obj.value) doubleLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw IllegalArgumentException("${procLessThan.id} expected number object, but got other")
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        tailrec fun intLoop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last < obj.value) intLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last.toDouble() < obj.value) doubleLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw IllegalArgumentException("${procLessThan.id} expected number object, but got other")
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        val result = when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw IllegalArgumentException("${procLessThan.id} expected number object, but got other")
                        }

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: '>' */
    private val procGraterThan: ScmProcedure by lazy {
        object : ScmProcedure(">", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procGraterThan.id} expected two or more object, but got nothing")
                    1 -> throw IllegalArgumentException("${procGraterThan.id} expected two or more object, but got one")

                    else -> {
                        val sp = vm.sp
                        tailrec fun doubleLoop(index: Int, last: Double): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last > obj.value.toDouble())
                                            doubleLoop(index = index + 1, last = obj.value.toDouble())
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last > obj.value) doubleLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw IllegalArgumentException("${procGraterThan.id} expected number object, but got other")
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        tailrec fun intLoop(index: Int, last: Int): ScmObject =
                            if (index < n) {
                                when (val obj = vm.stack.index(sp, index)) {
                                    is ScmInt ->
                                        if (last > obj.value) intLoop(index = index + 1, last = obj.value)
                                        else ScmConstant.FALSE
                                    is ScmDouble ->
                                        if (last.toDouble() > obj.value) doubleLoop(index + 1, obj.value)
                                        else ScmConstant.FALSE
                                    else -> throw IllegalArgumentException("${procGraterThan.id} expected number object, but got other")
                                }
                            } else {
                                ScmConstant.TRUE
                            }

                        val result = when (val first = vm.stack.index(sp, 0)) {
                            is ScmInt -> intLoop(1, first.value)
                            is ScmDouble -> doubleLoop(1, first.value)
                            else -> throw IllegalArgumentException("${procGraterThan.id} expected number object, but got other")
                        }

                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: pair? */
    private val procPairQ: ScmProcedure by lazy {
        object : ScmProcedure("pair?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procPairQ.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (ScmPair.isPair(obj)) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procPairQ.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cons */
    private val procCons: ScmProcedure by lazy {
        object : ScmProcedure("cons", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCons.id} expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("${procCons.id} expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 0)
                        val obj2 = vm.stack.index(sp, 1)
                        val result = ScmMutablePair(obj1, obj2)
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCons.id} expected 2 object, but got more")
                }
            }
        }
    }

    /** procedure: car */
    private val procCar: ScmProcedure by lazy {
        object : ScmProcedure("car", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCar.id} expected one object, but got nothing")
                    1 -> {
                        val pair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCar.id} expected pair but got other")
                        vm.scmProcReturn(pair.car, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdr */
    private val procCdr: ScmProcedure by lazy {
        object : ScmProcedure("cdr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdr.id} expected one object, but got nothing")
                    1 -> {
                        val pair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdr.id} expected pair but got other")
                        vm.scmProcReturn(pair.cdr, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: set-car! */
    private val procSetCarE: ScmProcedure by lazy {
        object : ScmProcedure("set-car!", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procSetCarE.id} expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("${procSetCarE.id} expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val value = vm.stack.index(sp, 1)
                        val pair = vm.stack.index(sp, 0) as? ScmMutablePair
                            ?: throw IllegalArgumentException("${procSetCarE.id} expected mutable pair but got other")
                        pair.assignCar(value)
                        vm.scmProcReturn(ScmConstant.UNDEF, n, this)
                    }
                    else -> throw IllegalArgumentException("${procSetCarE.id} expected 2 object, but got more")
                }
            }
        }
    }

    /** procedure: set-cdr! */
    private val procSetCdrE: ScmProcedure by lazy {
        object : ScmProcedure("set-cdr!", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procSetCdrE.id} expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("${procSetCdrE.id} expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val value = vm.stack.index(sp, 1)
                        val pair = vm.stack.index(sp, 0) as? ScmMutablePair
                            ?: throw IllegalArgumentException("${procSetCdrE.id} expected mutable pair but got other")
                        pair.assignCdr(value)
                        vm.scmProcReturn(ScmConstant.UNDEF, n, this)
                    }
                    else -> throw IllegalArgumentException("${procSetCdrE.id} expected 2 object, but got more")
                }
            }
        }
    }

    /** procedure: caar */
    private val procCaar: ScmProcedure by lazy {
        object : ScmProcedure("caar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaar.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaar.id} expected pair but got other")
                        val result = try {
                            ScmPair.caar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaar.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cadr */
    private val procCadr: ScmProcedure by lazy {
        object : ScmProcedure("cadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdar */
    private val procCdar: ScmProcedure by lazy {
        object : ScmProcedure("cdar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cddr */
    private val procCddr: ScmProcedure by lazy {
        object : ScmProcedure("cddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCddr.id} expected one object, but got nothing")
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCddr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCddr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCddr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caaar */
    private val procCaaar: ScmProcedure by lazy {
        object : ScmProcedure("caaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaaar.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaaar.id} expected pair but got other")
                        val result = try {
                            ScmPair.caaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaaar.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaaar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caadr */
    private val procCaadr: ScmProcedure by lazy {
        object : ScmProcedure("caadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.caadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cadar */
    private val procCadar: ScmProcedure by lazy {
        object : ScmProcedure("cadar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cadar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caddr */
    private val procCaddr: ScmProcedure by lazy {
        object : ScmProcedure("caddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaddr.id} expected one object, but got nothing")
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaddr.id} expected pair but got other")
                        val result = try {
                            ScmPair.caddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaddr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaddr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdaar */
    private val procCdaar: ScmProcedure by lazy {
        object : ScmProcedure("cdaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdaar.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdaar.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdaar.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdaar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdadr */
    private val procCdadr: ScmProcedure by lazy {
        object : ScmProcedure("cdadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cddar */
    private val procCddar: ScmProcedure by lazy {
        object : ScmProcedure("cddar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cddar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdddr */
    private val procCdddr: ScmProcedure by lazy {
        object : ScmProcedure("cdddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdddr.id} expected one object, but got nothing")
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdddr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdddr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdddr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caaaar */
    private val procCaaaar: ScmProcedure by lazy {
        object : ScmProcedure("caaaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaaaar.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaaaar.id} expected pair but got other")
                        val result = try {
                            ScmPair.caaaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaaaar.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaaaar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caaadr */
    private val procCaaadr: ScmProcedure by lazy {
        object : ScmProcedure("caaadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaaadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaaadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.caaadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaaadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaaadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caadar */
    private val procCaadar: ScmProcedure by lazy {
        object : ScmProcedure("caadar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaaadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaaadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.caadar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaaadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaaadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caaddr */
    private val procCaaddr: ScmProcedure by lazy {
        object : ScmProcedure("caaddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCaaddr.id} expected one object, but got nothing")
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCaaddr.id} expected pair but got other")
                        val result = try {
                            ScmPair.caaddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCaaddr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCaaddr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cadaar */
    private val procCadaar: ScmProcedure by lazy {
        object : ScmProcedure("cadaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCadaar.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCadaar.id} expected pair but got other")
                        val result = try {
                            ScmPair.cadaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCadaar.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCadaar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cadadr */
    private val procCadadr: ScmProcedure by lazy {
        object : ScmProcedure("cadadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCadadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCadadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cadadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCadadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCadadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: caddar */
    private val procCaddar: ScmProcedure by lazy {
        object : ScmProcedure("caddar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCadadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCadadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.caddar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCadadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCadadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cadddr */
    private val procCadddr: ScmProcedure by lazy {
        object : ScmProcedure("cadddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCadddr.id} expected one object, but got nothing")
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCadddr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cadddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCadddr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCadddr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdaaar */
    private val procCdaaar: ScmProcedure by lazy {
        object : ScmProcedure("cdaaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdaaar.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdaaar.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdaaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdaaar.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdaaar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdaadr */
    private val procCdaadr: ScmProcedure by lazy {
        object : ScmProcedure("cdaadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdaadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdaadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdaadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdaadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdaadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdadar */
    private val procCdadar: ScmProcedure by lazy {
        object : ScmProcedure("cdadar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdaadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdaadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdadar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdaadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdaadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdaddr */
    private val procCdaddr: ScmProcedure by lazy {
        object : ScmProcedure("cdaddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCdaddr.id} expected one object, but got nothing")
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCdaddr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdaddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCdaddr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCdaddr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cddaar */
    private val procCddaar: ScmProcedure by lazy {
        object : ScmProcedure("cddaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCddaar.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCddaar.id} expected pair but got other")
                        val result = try {
                            ScmPair.cddaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCddaar.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCddaar.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cddadr */
    private val procCddadr: ScmProcedure by lazy {
        object : ScmProcedure("cddadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCddadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCddadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cddadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCddadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCddadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cdddar */
    private val procCdddar: ScmProcedure by lazy {
        object : ScmProcedure("cdddar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCddadr.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCddadr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cdddar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCddadr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCddadr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: cddddr */
    private val procCddddr: ScmProcedure by lazy {
        object : ScmProcedure("cddddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procCddddr.id} expected one object, but got nothing")
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw IllegalArgumentException("${procCddddr.id} expected pair but got other")
                        val result = try {
                            ScmPair.cddddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procCddddr.id} failed")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procCddddr.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: null? */
    private val procNullQ: ScmProcedure by lazy {
        object : ScmProcedure("null?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procNullQ.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (obj == null) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procNullQ.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: list? */
    private val procListQ: ScmProcedure by lazy {
        object : ScmProcedure("list?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procListQ.id} expected one object, but got nothing")
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (ScmPair.isProperList(obj)) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procListQ.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: make-list */
    private val procMakeList: ScmProcedure by lazy {
        object : ScmProcedure("make-list", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val result = when (n) {
                    0 -> throw IllegalArgumentException("${procMakeList.id} expected two or more object, but got nothing")
                    1 -> {
                        val k = (vm.stack.index(vm.sp, 0) as? ScmInt)?.value
                            ?: throw IllegalArgumentException("${procMakeList.id} expected int but got other")
                        if (k < 0) throw IllegalArgumentException("${procMakeList.id} doesn't accept negative number")
                        ScmMutablePair.makeList(k)
                    }
                    2 -> {
                        val sp = vm.sp
                        val k = (vm.stack.index(sp, 0) as? ScmInt)?.value
                            ?: throw IllegalArgumentException("${procMakeList.id} expected int but got other")
                        if (k < 0) throw IllegalArgumentException("${procMakeList.id} doesn't accept negative number")
                        val fill = vm.stack.index(sp, 1)
                        ScmMutablePair.makeList(k, fill)
                    }
                    else -> throw IllegalArgumentException("${procMakeList.id} expected two or more object, but got more")
                }
                vm.scmProcReturn(result, n, this)
            }
        }
    }

    /** procedure: list */
    private val procList: ScmProcedure by lazy {
        object : ScmProcedure("list", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val sp = vm.sp
                tailrec fun loop(index: Int, result: ScmPair?): ScmPair? =
                    if (index < 0) {
                        result
                    } else {
                        val obj = vm.stack.index(sp, index)
                        loop(index - 1, ScmMutablePair(obj, result))
                    }

                val list = loop(index = n - 1, result = null)
                vm.scmProcReturn(list, n, this)
            }
        }
    }

    /** procedure: length */
    private val procLength: ScmProcedure by lazy {
        object : ScmProcedure("length", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procLength.id} expected one object, but got nothing")
                    1 -> {
                        val list = vm.stack.index(vm.sp, 0)
                        val length = try {
                            ScmInt(ScmPair.length(list))
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procLength.id} expected a proper list, but got nothing")
                        }
                        vm.scmProcReturn(length, n, this)
                    }
                    else -> throw IllegalArgumentException("${procLength.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: reverse */
    private val procReverse: ScmProcedure by lazy {
        object : ScmProcedure("reverse", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procReverse.id} expected one object, but got nothing")
                    1 -> {
                        val list = vm.stack.index(vm.sp, 0)?.let {
                            it as? ScmPair
                                ?: throw IllegalArgumentException("${procReverse.id} expected a list, but got other")
                        }
                        val reversed = try {
                            ScmPair.reverse(list)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("${procReverse.id} expected a proper list, but got other")
                        }
                        vm.scmProcReturn(reversed, n, this)
                    }
                    else -> throw IllegalArgumentException("${procReverse.id} expected one object, but got more")
                }
            }
        }
    }

    /** procedure: append */
    private val procAppend: ScmProcedure by lazy {
        object : ScmProcedure("append", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procAppend.id} expected one or more object, but got nothing")
                    1 -> throw IllegalArgumentException("${procAppend.id} expected one or more object, but got one")
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, result: ScmObject?): ScmObject? {
                            return if (index < 0) {
                                result
                            } else {
                                val list = vm.stack.index(sp, index)?.let {
                                    it as? ScmPair
                                        ?: throw IllegalArgumentException("${procAppend.id} expected proper list, but got other")
                                }
                                loop(index - 1, ScmPair.append(list, result))
                            }
                        }

                        val last: ScmObject? = vm.stack.index(sp, n - 1)
                        val result = loop(n - 2, last)
                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: make-vector */
    private val procMakeVector: ScmProcedure by lazy {
        object : ScmProcedure("make-vector", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val result = when (n) {
                    0 -> throw IllegalArgumentException("${procMakeVector.id} expected two or more object, but got nothing")
                    1 -> {
                        val k = (vm.stack.index(vm.sp, 0) as? ScmInt)?.value
                            ?: throw IllegalArgumentException("${procMakeVector.id} expected int but got other")
                        if (k < 0) throw IllegalArgumentException("${procMakeVector.id} doesn't accept negative number")
                        ScmVector(k)
                    }
                    2 -> {
                        val sp = vm.sp
                        val k = (vm.stack.index(sp, 0) as? ScmInt)?.value
                            ?: throw IllegalArgumentException("${procMakeVector.id} expected int but got other")
                        if (k < 0) throw IllegalArgumentException("${procMakeVector.id} doesn't accept negative number")
                        val fill = vm.stack.index(sp, 1)
                        ScmVector(k, fill)
                    }
                    else -> throw IllegalArgumentException("${procMakeVector.id} expected two or more object, but got more")
                }
                vm.scmProcReturn(result, n, this)
            }
        }
    }

    /** procedure: eq? */
    private val procEqQ: ScmProcedure by lazy {
        object : ScmProcedure("eq?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procEqQ.id} expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("${procEqQ.id} expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 1)
                        val obj2 = vm.stack.index(sp, 0)
                        val result = if (obj1 === obj2) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procEqQ.id} expected 2 object, but got more")
                }
            }
        }
    }

    /** procedure: eqv? */
    private val procEqvQ: ScmProcedure by lazy {
        object : ScmProcedure("eqv?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procEqvQ.id} expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("${procEqvQ.id} expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 1)
                        val obj2 = vm.stack.index(sp, 0)
                        val result = if (eqvQ(obj1, obj2)) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procEqvQ.id} expected 2 object, but got more")
                }
            }
        }
    }

    /** procedure: equal? */
    private val procEqualQ: ScmProcedure by lazy {
        object : ScmProcedure("equal?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procEqualQ.id} expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("${procEqualQ.id} expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 1)
                        val obj2 = vm.stack.index(sp, 0)
                        val result = if (equalQ(obj1, obj2)) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procEqualQ.id} expected 2 object, but got more")
                }
            }
        }
    }

    /** procedure: zero? */
    private val procZeroQ: ScmProcedure by lazy {
        object : ScmProcedure("zero?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("${procZeroQ.id} expected one object, but got nothing")
                    1 -> {
                        val result = when (val obj = vm.stack.index(vm.sp, 0)) {
                            is ScmInt -> if (obj.value == 0) ScmConstant.TRUE else ScmConstant.FALSE
                            is ScmFloat -> if (obj.value == 0f) ScmConstant.TRUE else ScmConstant.FALSE
                            is ScmDouble -> if (obj.value == 0.0) ScmConstant.TRUE else ScmConstant.FALSE
                            else -> throw IllegalArgumentException("${procZeroQ.id} expected number but got other")
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw IllegalArgumentException("${procZeroQ.id} expected one object, but got more")
                }
            }
        }
    }
}