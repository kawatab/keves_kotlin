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
import io.github.kawatab.keveskotlin.KevesExceptions
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
            ScmSymbol.get("*") to R7rsNumber.procMultiple,
            ScmSymbol.get("+") to R7rsNumber.procAdd,
            ScmSymbol.get("-") to R7rsNumber.procSubtract,
            ScmSymbol.get("/") to R7rsNumber.procDivide,
            ScmSymbol.get("<") to R7rsNumber.procLessThan,
            ScmSymbol.get("=") to R7rsNumber.procEqual,
            ScmSymbol.get(">") to R7rsNumber.procGraterThan,
            ScmSymbol.get("append") to R7rsList.procAppend,
            ScmSymbol.get("assoc") to R7rsList.procAssoc,
            ScmSymbol.get("assq") to R7rsList.procAssq,
            ScmSymbol.get("assv") to R7rsList.procAssv,
            ScmSymbol.get("cons") to R7rsList.procCons,
            ScmSymbol.get("caaaar") to R7rsList.procCaaaar,
            ScmSymbol.get("caaadr") to R7rsList.procCaaadr,
            ScmSymbol.get("caaar") to R7rsList.procCaaar,
            ScmSymbol.get("caadar") to R7rsList.procCaadar,
            ScmSymbol.get("caaddr") to R7rsList.procCaaddr,
            ScmSymbol.get("caadr") to R7rsList.procCaadr,
            ScmSymbol.get("caar") to R7rsList.procCaar,
            ScmSymbol.get("cadaar") to R7rsList.procCadaar,
            ScmSymbol.get("cadadr") to R7rsList.procCadadr,
            ScmSymbol.get("cadar") to R7rsList.procCadar,
            ScmSymbol.get("caddar") to R7rsList.procCaddar,
            ScmSymbol.get("cadddr") to R7rsList.procCadddr,
            ScmSymbol.get("caddr") to R7rsList.procCaddr,
            ScmSymbol.get("cadr") to R7rsList.procCadr,
            ScmSymbol.get("car") to R7rsList.procCar,
            ScmSymbol.get("cdaaar") to R7rsList.procCdaaar,
            ScmSymbol.get("cdaadr") to R7rsList.procCdaadr,
            ScmSymbol.get("cdaar") to R7rsList.procCdaar,
            ScmSymbol.get("cdadar") to R7rsList.procCdadar,
            ScmSymbol.get("cdaddr") to R7rsList.procCdaddr,
            ScmSymbol.get("cdadr") to R7rsList.procCdadr,
            ScmSymbol.get("cdar") to R7rsList.procCdar,
            ScmSymbol.get("cddaar") to R7rsList.procCddaar,
            ScmSymbol.get("cddadr") to R7rsList.procCddadr,
            ScmSymbol.get("cddar") to R7rsList.procCddar,
            ScmSymbol.get("cdddar") to R7rsList.procCdddar,
            ScmSymbol.get("cddddr") to R7rsList.procCddddr,
            ScmSymbol.get("cdddr") to R7rsList.procCdddr,
            ScmSymbol.get("cddr") to R7rsList.procCddr,
            ScmSymbol.get("cdr") to R7rsList.procCdr,
            ScmSymbol.get("char?") to R7rsChar.procCharQ,
            ScmSymbol.get("char->integer") to R7rsChar.procCharToInteger,
            ScmSymbol.get("char-ci<=?") to R7rsChar.procCharCILessThanEqualQ,
            ScmSymbol.get("char-ci<?") to R7rsChar.procCharCILessThanQ,
            ScmSymbol.get("char-ci=?") to R7rsChar.procCharCIEqualQ,
            ScmSymbol.get("char-ci>=?") to R7rsChar.procCharCIGraterThanEqualQ,
            ScmSymbol.get("char-ci>?") to R7rsChar.procCharCIGraterThanQ,
            ScmSymbol.get("char-alphabetic?") to R7rsChar.procCharAlphabeticQ,
            ScmSymbol.get("char-downcase") to R7rsChar.procCharDowncase,
            ScmSymbol.get("char-foldcase") to R7rsChar.procCharFoldcase,
            ScmSymbol.get("char-lower-case?") to R7rsChar.procCharLowerCaseQ,
            ScmSymbol.get("char-numeric?") to R7rsChar.procCharNumericQ,
            ScmSymbol.get("char-upper-case?") to R7rsChar.procCharUpperCaseQ,
            ScmSymbol.get("char-upcase") to R7rsChar.procCharUpcase,
            ScmSymbol.get("char-whitespace?") to R7rsChar.procCharWhitespaceQ,
            ScmSymbol.get("char<=?") to R7rsChar.procCharLessThanEqualQ,
            ScmSymbol.get("char<?") to R7rsChar.procCharLessThanQ,
            ScmSymbol.get("char=?") to R7rsChar.procCharEqualQ,
            ScmSymbol.get("char>=?") to R7rsChar.procCharGraterThanEqualQ,
            ScmSymbol.get("char>?") to R7rsChar.procCharGraterThanQ,
            ScmSymbol.get("digit-value") to R7rsChar.procDigitValue,
            ScmSymbol.get("eq?") to procEqQ,
            ScmSymbol.get("equal?") to procEqualQ,
            ScmSymbol.get("eqv?") to procEqvQ,
            ScmSymbol.get("integer->char") to R7rsChar.procIntegerToChar,
            ScmSymbol.get("length") to R7rsList.procLength,
            ScmSymbol.get("list") to R7rsList.procList,
            ScmSymbol.get("list-copy") to R7rsList.procListCopy,
            ScmSymbol.get("list-ref") to R7rsList.procListRef,
            ScmSymbol.get("list-set!") to R7rsList.procListSetE,
            ScmSymbol.get("list-tail") to R7rsList.procListTail,
            ScmSymbol.get("list?") to R7rsList.procListQ,
            ScmSymbol.get("make-list") to R7rsList.procMakeList,
            ScmSymbol.get("make-vector") to procMakeVector,
            ScmSymbol.get("member") to R7rsList.procMember,
            ScmSymbol.get("memq") to R7rsList.procMemq,
            ScmSymbol.get("memv") to R7rsList.procMemv,
            ScmSymbol.get("null?") to R7rsList.procNullQ,
            ScmSymbol.get("pair?") to R7rsList.procPairQ,
            ScmSymbol.get("reverse") to R7rsList.procReverse,
            ScmSymbol.get("set-car!") to R7rsList.procSetCarE,
            ScmSymbol.get("set-cdr!") to R7rsList.procSetCdrE,
            ScmSymbol.get("string->symbol") to R7rsSymbol.procStringToSymbol,
            ScmSymbol.get("string=?") to R7rsString.procStringEqualQ,
            ScmSymbol.get("string?") to R7rsString.procStringQ,
            ScmSymbol.get("symbol->string") to R7rsSymbol.procSymbolToString,
            ScmSymbol.get("symbol=?") to R7rsSymbol.procSymbolEqualQ,
            ScmSymbol.get("symbol?") to R7rsSymbol.procSymbolQ,
            ScmSymbol.get("zero?") to R7rsNumber.procZeroQ,
        )
    }

    val symbolBegin = ScmSymbol.get("begin")

    /** syntax: begin */
    private val syntaxBegin = object : ScmSyntax("begin") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair {
            tailrec fun loop(exps: ScmPair?, c: ScmPair?): ScmPair? =
                if (exps == null) {
                    c
                } else {
                    val expsCdr: ScmPair? = exps.cdr?.let {
                        it as? ScmPair
                            ?: throw IllegalArgumentException(KevesExceptions.badSyntax(x.toStringForWrite()))
                    }
                    loop(expsCdr, compiler.compile(ScmPair.car(exps), e, s, c))
                }

            return patternMatchBegin(x)?.let { expressions ->
                loop(ScmMutablePair.reverse(expressions), next)
            } ?: ScmPair.list(ScmInstruction.Constant(ScmConstant.UNDEF, next)) // CONSTANT, ScmConstant.UNDEF)
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
            it as? ScmPair ?: throw IllegalArgumentException(KevesExceptions.badSyntax(x.toStringForWrite()))
        }

    /** syntax: quote */
    private val syntaxQuote = object : ScmSyntax("quote") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair {
            val obj = patternMatchQuote(x)
            return ScmPair.list(ScmInstruction.Constant(obj, next)) // CONSTANT, obj, next)
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
            throw IllegalArgumentException(KevesExceptions.expected1DatumGot0("quote"))
        }.also {
            ScmPair.cddr(x)
                ?.let { throw IllegalArgumentException(KevesExceptions.expected1DatumGotMore("quote")) }
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
                    ScmInstruction.Close( // CLOSE,
                        /*ScmInt(*/ScmPair.length(free)/*)*/,
                        /*ScmInt(*/numArg/*)*/,
                        compiler.makeBoxes(
                            sets,
                            vars,
                            compiler.compile(
                                ScmPair(symbolBegin, body),
                                ScmPair(varsAsProperList, free),
                                compiler.setUnion(sets, compiler.setIntersect(s, free)),
                                ScmPair.list(ScmInstruction.Return(ScmPair.length(varsAsProperList))) // RETURN, ScmInt(ScmPair.length(varsAsProperList)))
                            )
                        ),
                        next
                    )
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
            throw IllegalArgumentException(KevesExceptions.expected2OrMoreDatumGotLess("lambda"))
        }?.let {
            it as? ScmPair ?: it as? ScmSymbol
            ?: throw KevesExceptions.badSyntax(x.toStringForWrite())
        }
        val body: ScmPair = try {
            ScmPair.cddr(x) // original is caddr instead of cddr
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2OrMoreDatumGotLess("lambda")
        } as? ScmPair ?: throw KevesExceptions.badSyntax(x.toStringForWrite())
        return vars to body
    }

    /** syntax: if */
    private val syntaxIf = object : ScmSyntax("if") {
        override fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair? {
            val (test, thn, els) = patternMatchIf(x)
            val thenC = compiler.compile(thn, e, s, next)
            val elseC = compiler.compile(els, e, s, next)
            return compiler.compile(test, e, s, ScmPair.list(ScmInstruction.Test(thenC, elseC))) // TEST, thenC, elseC))
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
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        val thn: ScmObject? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        val els: ScmObject? = try {
            ScmPair.cadddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        ScmPair.cddddr(x)?.let { throw KevesExceptions.expected3DatumGotMore("if") }
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
                    compiler.compile(
                        xx,
                        e,
                        s,
                        ScmPair.list(ScmInstruction.AssignLocal(n, next))
                    ) // ASSIGN_LOCAL, ScmInt(n), next))
                },
                { n: Int ->
                    compiler.compile(
                        xx,
                        e,
                        s,
                        ScmPair.list(ScmInstruction.AssignFree(n, next))
                    ) // ASSIGN_FREE, ScmInt(n), next))
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
            throw KevesExceptions.expected2DatumGotLess("set!")
        } ?: throw KevesExceptions.expectedSymbol("set!")
        val exp: ScmObject? = try {
            ScmPair.caddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("set!")
        }
        ScmPair.cdddr(x)?.let { throw KevesExceptions.expected2DatumGotMore("set!") }
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
            throw KevesExceptions.expected2DatumGotLess("let")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("let")
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("let")
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
            throw KevesExceptions.expected2DatumGotLess("let*")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("let*")
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let*")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("let*")
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
            throw KevesExceptions.expected2DatumGotLess("letrec")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("letrec")
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("letrec")
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
            throw KevesExceptions.expected2DatumGotLess("letrec*")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("letrec*")
        }

        val body: ScmPair? = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec*")
        }?.let {
            if (it is ScmPair) it
            else throw KevesExceptions.expectedSymbol("letrec*")
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
            throw KevesExceptions.expected2DatumGotLess("when")
        } as? ScmPair ?: throw KevesExceptions.expectedSymbol("when")

        val expressions: ScmPair = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("when")
        } as? ScmPair ?: throw KevesExceptions.expectedSymbol("when")

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
            throw KevesExceptions.expected2DatumGotLess("until")
        } as? ScmPair ?: throw KevesExceptions.expectedSymbol("until")

        val expressions: ScmPair = try {
            ScmPair.cddr(x)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("until")
        } as? ScmPair ?: throw KevesExceptions.expectedSymbol("until")

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
                    ScmInstruction.Conti(
                        ScmPair.list(
                            ScmInstruction.Argument( // ARGUMENT,
                                compiler.compile(
                                    xx,
                                    e,
                                    s,
                                    if (compiler.tailQ(next)) ScmPair.list(
                                        ScmInstruction.Shift( // SHIFT,
                                            1, // ScmInt(1),
                                            (ScmPair.car(next) as ScmInstruction.Return).n, // ScmPair.cadr(next),
                                            ScmPair.list(ScmInstruction.Apply(1)) // APPLY, ScmInt(1))
                                        )
                                    )
                                    else ScmPair.list(ScmInstruction.Apply(1)) // APPLY, ScmInt(1))
                                )
                            )
                        )
                    )
                )
                return if (compiler.tailQ(next)) c else ScmPair.list(ScmInstruction.Frame(next, c)) // FRAME, next, c)
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
            throw KevesExceptions.expected1DatumGot0("call/cc")
        }?.let {
            it as? ScmPair ?: throw KevesExceptions.badSyntax(x.toStringForWrite())
        }.also {
            ScmPair.cddr(x)
                ?.let { throw KevesExceptions.expected1DatumGotMore("call/cc") }
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
                        ScmInstruction.Shift( // SHIFT,
                            0, // ScmInt(0),
                            (ScmPair.car(next) as ScmInstruction.Return).n, // ScmPair.cadr(next),
                            ScmPair.list(procDisplay)
                        )
                    )
                    else ScmPair.list(procDisplay)
                )
                return if (compiler.tailQ(next)) c!! else ScmPair.list(ScmInstruction.Frame(next, c)) // FRAME, next, c)
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
            throw KevesExceptions.expected1DatumGot0("display")
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
}