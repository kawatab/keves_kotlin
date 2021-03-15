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

import io.github.kawatab.keveskotlin.*
import io.github.kawatab.keveskotlin.objects.*

class R7rs(private val res: KevesResources) {
    private val r7rsChar = R7rsChar(res)
    private val r7rsList = R7rsList(res)
    private val r7rsNumber = R7rsNumber(res)
    private val r7rsString = R7rsString(res)
    private val r7rsSymbol = R7rsSymbol(res)

    val listOfBinds by lazy {
        listOf<Pair<PtrSymbol, PtrObject>>(
            ScmSymbol.get("begin", res) to syntaxBegin.toObject(),
            ScmSymbol.get("quote", res) to syntaxQuote.toObject(),
            ScmSymbol.get("lambda", res) to syntaxLambda.toObject(),
            ScmSymbol.get("if", res) to syntaxIf.toObject(),
            ScmSymbol.get("set!", res) to syntaxSetE.toObject(),
            ScmSymbol.get("let", res) to macroLet,
            ScmSymbol.get("let*", res) to macroLetStar,
            ScmSymbol.get("letrec", res) to macroLetrec,
            ScmSymbol.get("letrec*", res) to macroLetrecStar,
            ScmSymbol.get("cond", res) to macroCond,
            ScmSymbol.get("when", res) to macroWhen,
            ScmSymbol.get("until", res) to macroUntil,
            ScmSymbol.get("call/cc", res) to procCallWithCC.toObject(),
            ScmSymbol.get("call-with-current-continuation", res) to procCallWithCC.toObject(),
            ScmSymbol.get("display", res) to procDisplay.toObject(),
            ScmSymbol.get("*", res) to r7rsNumber.procMultiple.toObject(),
            ScmSymbol.get("+", res) to r7rsNumber.procAdd.toObject(),
            ScmSymbol.get("-", res) to r7rsNumber.procSubtract.toObject(),
            ScmSymbol.get("/", res) to r7rsNumber.procDivide.toObject(),
            ScmSymbol.get("<", res) to r7rsNumber.procLessThan.toObject(),
            ScmSymbol.get("=", res) to r7rsNumber.procEqual.toObject(),
            ScmSymbol.get(">", res) to r7rsNumber.procGraterThan.toObject(),
            ScmSymbol.get("append", res) to r7rsList.procAppend.toObject(),
            ScmSymbol.get("assoc", res) to r7rsList.procAssoc.toObject(),
            ScmSymbol.get("assq", res) to r7rsList.procAssq.toObject(),
            ScmSymbol.get("assv", res) to r7rsList.procAssv.toObject(),
            ScmSymbol.get("cons", res) to r7rsList.procCons.toObject(),
            ScmSymbol.get("caaaar", res) to r7rsList.procCaaaar.toObject(),
            ScmSymbol.get("caaadr", res) to r7rsList.procCaaadr.toObject(),
            ScmSymbol.get("caaar", res) to r7rsList.procCaaar.toObject(),
            ScmSymbol.get("caadar", res) to r7rsList.procCaadar.toObject(),
            ScmSymbol.get("caaddr", res) to r7rsList.procCaaddr.toObject(),
            ScmSymbol.get("caadr", res) to r7rsList.procCaadr.toObject(),
            ScmSymbol.get("caar", res) to r7rsList.procCaar.toObject(),
            ScmSymbol.get("cadaar", res) to r7rsList.procCadaar.toObject(),
            ScmSymbol.get("cadadr", res) to r7rsList.procCadadr.toObject(),
            ScmSymbol.get("cadar", res) to r7rsList.procCadar.toObject(),
            ScmSymbol.get("caddar", res) to r7rsList.procCaddar.toObject(),
            ScmSymbol.get("cadddr", res) to r7rsList.procCadddr.toObject(),
            ScmSymbol.get("caddr", res) to r7rsList.procCaddr.toObject(),
            ScmSymbol.get("cadr", res) to r7rsList.procCadr.toObject(),
            ScmSymbol.get("car", res) to r7rsList.procCar.toObject(),
            ScmSymbol.get("cdaaar", res) to r7rsList.procCdaaar.toObject(),
            ScmSymbol.get("cdaadr", res) to r7rsList.procCdaadr.toObject(),
            ScmSymbol.get("cdaar", res) to r7rsList.procCdaar.toObject(),
            ScmSymbol.get("cdadar", res) to r7rsList.procCdadar.toObject(),
            ScmSymbol.get("cdaddr", res) to r7rsList.procCdaddr.toObject(),
            ScmSymbol.get("cdadr", res) to r7rsList.procCdadr.toObject(),
            ScmSymbol.get("cdar", res) to r7rsList.procCdar.toObject(),
            ScmSymbol.get("cddaar", res) to r7rsList.procCddaar.toObject(),
            ScmSymbol.get("cddadr", res) to r7rsList.procCddadr.toObject(),
            ScmSymbol.get("cddar", res) to r7rsList.procCddar.toObject(),
            ScmSymbol.get("cdddar", res) to r7rsList.procCdddar.toObject(),
            ScmSymbol.get("cddddr", res) to r7rsList.procCddddr.toObject(),
            ScmSymbol.get("cdddr", res) to r7rsList.procCdddr.toObject(),
            ScmSymbol.get("cddr", res) to r7rsList.procCddr.toObject(),
            ScmSymbol.get("cdr", res) to r7rsList.procCdr.toObject(),
            ScmSymbol.get("char?", res) to r7rsChar.procCharQ.toObject(),
            ScmSymbol.get("char->integer", res) to r7rsChar.procCharToInteger.toObject(),
            ScmSymbol.get("char-ci<=?", res) to r7rsChar.procCharCILessThanEqualQ.toObject(),
            ScmSymbol.get("char-ci<?", res) to r7rsChar.procCharCILessThanQ.toObject(),
            ScmSymbol.get("char-ci=?", res) to r7rsChar.procCharCIEqualQ.toObject(),
            ScmSymbol.get("char-ci>=?", res) to r7rsChar.procCharCIGraterThanEqualQ.toObject(),
            ScmSymbol.get("char-ci>?", res) to r7rsChar.procCharCIGraterThanQ.toObject(),
            ScmSymbol.get("char-alphabetic?", res) to r7rsChar.procCharAlphabeticQ.toObject(),
            ScmSymbol.get("char-downcase", res) to r7rsChar.procCharDowncase.toObject(),
            ScmSymbol.get("char-foldcase", res) to r7rsChar.procCharFoldcase.toObject(),
            ScmSymbol.get("char-lower-case?", res) to r7rsChar.procCharLowerCaseQ.toObject(),
            ScmSymbol.get("char-numeric?", res) to r7rsChar.procCharNumericQ.toObject(),
            ScmSymbol.get("char-upper-case?", res) to r7rsChar.procCharUpperCaseQ.toObject(),
            ScmSymbol.get("char-upcase", res) to r7rsChar.procCharUpcase.toObject(),
            ScmSymbol.get("char-whitespace?", res) to r7rsChar.procCharWhitespaceQ.toObject(),
            ScmSymbol.get("char<=?", res) to r7rsChar.procCharLessThanEqualQ.toObject(),
            ScmSymbol.get("char<?", res) to r7rsChar.procCharLessThanQ.toObject(),
            ScmSymbol.get("char=?", res) to r7rsChar.procCharEqualQ.toObject(),
            ScmSymbol.get("char>=?", res) to r7rsChar.procCharGraterThanEqualQ.toObject(),
            ScmSymbol.get("char>?", res) to r7rsChar.procCharGraterThanQ.toObject(),
            ScmSymbol.get("digit-value", res) to r7rsChar.procDigitValue.toObject(),
            ScmSymbol.get("eq?", res) to procEqQ.toObject(),
            ScmSymbol.get("equal?", res) to procEqualQ.toObject(),
            ScmSymbol.get("eqv?", res) to procEqvQ.toObject(),
            ScmSymbol.get("integer->char", res) to r7rsChar.procIntegerToChar.toObject(),
            ScmSymbol.get("length", res) to r7rsList.procLength.toObject(),
            ScmSymbol.get("list", res) to r7rsList.procList.toObject(),
            ScmSymbol.get("list-copy", res) to r7rsList.procListCopy.toObject(),
            ScmSymbol.get("list-ref", res) to r7rsList.procListRef.toObject(),
            ScmSymbol.get("list-set!", res) to r7rsList.procListSetE.toObject(),
            ScmSymbol.get("list-tail", res) to r7rsList.procListTail.toObject(),
            ScmSymbol.get("list?", res) to r7rsList.procListQ.toObject(),
            ScmSymbol.get("make-list", res) to r7rsList.procMakeList.toObject(),
            ScmSymbol.get("make-vector", res) to procMakeVector.toObject(),
            ScmSymbol.get("member", res) to r7rsList.procMember.toObject(),
            ScmSymbol.get("memq", res) to r7rsList.procMemq.toObject(),
            ScmSymbol.get("memv", res) to r7rsList.procMemv.toObject(),
            ScmSymbol.get("null?", res) to r7rsList.procNullQ.toObject(),
            ScmSymbol.get("pair?", res) to r7rsList.procPairQ.toObject(),
            ScmSymbol.get("reverse", res) to r7rsList.procReverse.toObject(),
            ScmSymbol.get("set-car!", res) to r7rsList.procSetCarE.toObject(),
            ScmSymbol.get("set-cdr!", res) to r7rsList.procSetCdrE.toObject(),
            ScmSymbol.get("string->symbol", res) to r7rsSymbol.procStringToSymbol.toObject(),
            ScmSymbol.get("string=?", res) to r7rsString.procStringEqualQ.toObject(),
            ScmSymbol.get("string?", res) to r7rsString.procStringQ.toObject(),
            ScmSymbol.get("symbol->string", res) to r7rsSymbol.procSymbolToString.toObject(),
            ScmSymbol.get("symbol=?", res) to r7rsSymbol.procSymbolEqualQ.toObject(),
            ScmSymbol.get("symbol?", res) to r7rsSymbol.procSymbolQ.toObject(),
            ScmSymbol.get("zero?", res) to r7rsNumber.procZeroQ.toObject(),
        )
    }

    val symbolBegin = ScmSymbol.get("begin", res)

    /** syntax: begin */
    private val syntaxBegin = res.addSyntax(object : ScmSyntax("begin") {
        override fun compile(
            x: PtrPair,
            e: PtrPairOrNull,
            s: PtrPairOrNull,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            tailrec fun loop(exps: PtrPairOrNull, c: PtrInstruction): PtrInstruction =
                if (exps.isNull()) {
                    c
                } else {
                    val expsCdr = exps.cdr(res).also {
                        if (it.isNotNull() && it.isNotPair(res))
                            throw IllegalArgumentException(
                                KevesExceptions.badSyntax(x.toVal(res).toStringForWrite(res))
                            )
                    }.toPairOrNull()
                    loop(expsCdr, compiler.compile(exps.car(res), e, s, c))
                }

            return patternMatchBegin(x.toVal(res)).let { expressions ->
                if (expressions.isNull()) ScmInstruction.Constant.make(res.constUndef, next, res)
                else loop(ScmMutablePair.reverse(expressions, res).toPair(), next)
            }
        }

        override fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val exps = patternMatchBegin(x.toVal(res))
            return compiler.findSets(exps.toObject(), v)
        }

        override fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val exps = patternMatchBegin(x.toVal(res))
            return compiler.findFree(exps.toObject(), b)
        }
    })

    private fun patternMatchBegin(x: ScmPair): PtrPairOrNull =
        x.cdr.also {
            if (it.isNeitherNullNorPair(res))
                throw IllegalArgumentException(KevesExceptions.badSyntax(x.toStringForWrite(res)))
        }.toPairOrNull()

    /** syntax: quote */
    private val syntaxQuote = res.addSyntax(object : ScmSyntax("quote") {
        override fun compile(
            x: PtrPair,
            e: PtrPairOrNull,
            s: PtrPairOrNull,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val obj = patternMatchQuote(x)
            return ScmInstruction.Constant.make(obj, next, res)
        }

        override fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            patternMatchQuote(x)
            return PtrPairOrNull(0)
        }

        override fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            patternMatchQuote(x)
            return PtrPairOrNull(0)
        }
    })

    private fun patternMatchQuote(x: PtrPair): PtrObject =
        try {
            ScmPair.cadr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesExceptions.expected1DatumGot0("quote"))
        }.also {
            if (ScmPair.cddr(x.toObject(), res).isNotNull())
                throw IllegalArgumentException(KevesExceptions.expected1DatumGotMore("quote"))
        }

    /** syntax: lambda */
    private val syntaxLambda = res.addSyntax(object : ScmSyntax("lambda") {
        override fun compile(
            x: PtrPair,
            e: PtrPairOrNull,
            s: PtrPairOrNull,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val (vars, body) = patternMatchLambda(x)
            val (varsAsProperList, numArg) = ScmPair.toProperList(vars, res)
            val free = compiler.findFree(body.toObject(), varsAsProperList)
            val sets = compiler.findSets(body.toObject(), varsAsProperList)
            return compiler.collectFree(
                free,
                e,
                ScmInstruction.Close.make( // CLOSE,
                    ScmPair.length(free.toObject(), res),
                    numArg,
                    compiler.makeBoxes(
                        sets,
                        vars,
                        compiler.compile(
                            ScmPair.make(symbolBegin.toObject(), body.toObject(), res).toObject(),
                            ScmPair.make(varsAsProperList.toObject(), free.toObject(), res),
                            compiler.setUnion(sets, compiler.setIntersect(s, free)),
                            ScmInstruction.Return.make(
                                ScmPair.length(varsAsProperList.toObject(), res),
                                res
                            )
                        )
                    ),
                    next,
                    res
                )
            )
        }

        override fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val (vars, body) = patternMatchLambda(x)
            return compiler.findSets(body.toObject(), compiler.setMinus(v, ScmPair.toProperList(vars, res).first))
        }

        override fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val (vars, body) = patternMatchLambda(x)
            return compiler.findFree(body.toObject(), compiler.setUnion(ScmPair.toProperList(vars, res).first, b))
        }
    })

    private fun patternMatchLambda(x: PtrPair): Pair<PtrObject, PtrPair> {
        val vars: PtrObject = try {
            ScmPair.cadr(x.toObject(), res).also {
                if (it.isNeitherNullNorPair(res) && it.isNotSymbol(res)) throw KevesExceptions.badSyntax(
                    x.toVal(res).toStringForWrite(
                        res
                    )
                )
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesExceptions.expected2OrMoreDatumGotLess("lambda"))
        }
        val body: PtrPair = try {
            ScmPair.cddr(x.toObject(), res).also { // original is caddr instead of cddr
                if (it.isNotPair(res)) throw KevesExceptions.badSyntax(x.toVal(res).toStringForWrite(res))
            }.toPair()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2OrMoreDatumGotLess("lambda")
        }
        return vars to body
    }

    /** syntax: if */
    private val syntaxIf = res.addSyntax(object : ScmSyntax("if") {
        override fun compile(
            x: PtrPair,
            e: PtrPairOrNull,
            s: PtrPairOrNull,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val (test, thn, els) = patternMatchIf(x)
            val thenC = compiler.compile(thn, e, s, next)
            val elseC = compiler.compile(els, e, s, next)
            return compiler.compile(
                test,
                e,
                s,
                ScmInstruction.Test.make(thenC, elseC, res)
            ) // TEST, thenC, elseC))
        }

        override fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val (test, thn, els) = patternMatchIf(x)
            return compiler.setUnion(
                compiler.findSets(test, v),
                compiler.setUnion(compiler.findSets(thn, v), compiler.findSets(els, v))
            )
        }

        override fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val (test, thn, els) = patternMatchIf(x)
            return compiler.setUnion(
                compiler.findFree(test, b),
                compiler.setUnion(compiler.findFree(thn, b), compiler.findFree(els, b))
            )
        }
    })

    private fun patternMatchIf(x: PtrPair): Triple<PtrObject, PtrObject, PtrObject> {
        val test: PtrObject = try {
            ScmPair.cadr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        val thn: PtrObject = try {
            ScmPair.caddr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        val els: PtrObject = try {
            ScmPair.cadddr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        if (ScmPair.cddddr(x.toObject(), res).isNotNull()) throw KevesExceptions.expected3DatumGotMore("if")
        return Triple(test, thn, els)
    }

    /** syntax: set! */
    private val syntaxSetE = res.addSyntax(object : ScmSyntax("set!") {
        override fun compile(
            x: PtrPair,
            e: PtrPairOrNull,
            s: PtrPairOrNull,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val (variable, xx) = patternMatchSetE(x)
            return compiler.compileLookup(
                variable,
                e,
                { n: Int ->
                    compiler.compile(
                        xx,
                        e,
                        s,
                        ScmInstruction.AssignLocal.make(n, next, res)
                    ) // ASSIGN_LOCAL, ScmInt(n), next))
                },
                { n: Int ->
                    compiler.compile(
                        xx,
                        e,
                        s,
                        ScmInstruction.AssignFree.make(n, next, res)
                    ) // ASSIGN_FREE, ScmInt(n), next))
                }
            )
        }

        override fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val (variable, xx) = patternMatchSetE(x)
            return compiler.setUnion(
                if (compiler.setMemberQ(variable, v)) ScmPair.list(variable.toObject(), res) else PtrPairOrNull(0),
                compiler.findSets(xx, v)
            )
        }

        override fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
            val (variable, exp) = patternMatchSetE(x)
            return compiler.setUnion(
                if (compiler.setMemberQ(variable, b)) PtrPairOrNull(0) else ScmPair.list(variable.toObject(), res),
                compiler.findFree(exp, b)
            )
        }
    })

    private fun patternMatchSetE(x: PtrPair): Pair<PtrSymbol, PtrObject> {
        val variable: PtrSymbol = try {
            ScmPair.cadr(x.toObject(), res)
                .also { if (it.isNotSymbol(res)) throw KevesExceptions.expectedSymbol("set!") }
                .toSymbol()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("set!")
        }
        val exp: PtrObject = try {
            ScmPair.caddr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("set!")
        }
        if (ScmPair.cdddr(x.toObject(), res).isNotNull()) throw KevesExceptions.expected2DatumGotMore("set!")
        return variable to exp
    }

    /** macro: let */
    private val macroLet = res.addMacro(object : ScmMacro("let") {
        override fun transform(x: PtrPair, compiler: KevesCompiler): PtrObject {
            val (bindings, body) = patternMatchLet(x)
            return compiler.transform(
                if (bindings.isNull()) {
                    ScmPair.make(ScmSymbol.get("begin", res).toObject(), body.toObject(), res).toObject()
                } else {
                    val (variables, values) = compiler.splitBinds(bindings)
                    ScmPair.make(
                        ScmPair.make(
                            ScmSymbol.get("lambda", res).toObject(),
                            ScmPair.make(variables.toObject(), body.toObject(), res).toObject(),
                            res
                        ).toObject(),
                        values.toObject(),
                        res
                    ).toObject()
                }
            )
        }
    })

    private fun patternMatchLet(x: PtrPair): Pair<PtrPairOrNull, PtrPairOrNull> {
        val bindings: PtrPairOrNull = try {
            ScmPair.cadr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let")
        }.toPairOrNull()

        val body: PtrPairOrNull = try {
            ScmPair.cddr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let")
        }.toPairOrNull()

        return bindings to body
    }

    /** macro: let* */
    private val macroLetStar = res.addMacro(object : ScmMacro("let*") {
        override fun transform(x: PtrPair, compiler: KevesCompiler): PtrObject {
            val (bindings, body) = patternMatchLetStar(x)
            return compiler.transform(
                if (bindings.isNull()) {
                    ScmPair.make(ScmSymbol.get("begin", res).toObject(), body.toObject(), res).toObject()
                } else {
                    val first = bindings.car(res)
                    val rest = bindings.cdr(res)
                    ScmPair.make(
                        ScmSymbol.get("let", res).toObject(),
                        ScmPair.list(
                            ScmPair.list(first, res).toObject(),
                            ScmPair.make(
                                ScmSymbol.get("let*", res).toObject(),
                                ScmPair.make(rest, body.toObject(), res).toObject(),
                                res
                            ).toObject(),
                            res
                        ).toObject(),
                        res
                    ).toObject()
                }
            )
        }
    })

    private fun patternMatchLetStar(x: PtrPair): Pair<PtrPairOrNull, PtrPairOrNull> {
        val bindings: PtrPairOrNull = try {
            ScmPair.cadr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let*")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let*")
        }.toPairOrNull()

        val body: PtrPairOrNull = try {
            ScmPair.cddr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let*")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let*")
        }.toPairOrNull()

        return bindings to body
    }

    /** macro: letrec */
    private val macroLetrec = res.addMacro(object : ScmMacro("letrec") {
        override fun transform(x: PtrPair, compiler: KevesCompiler): PtrObject {
            tailrec fun loop(
                bindings: PtrPairOrNull,
                outerBindings: PtrPairOrNull,
                innerBindings: PtrPairOrNull,
                innerBody: PtrPairOrNull
            ): Triple<PtrPairOrNull, PtrPairOrNull, PtrPairOrNull> =
                if (bindings.isNull()) Triple(outerBindings, innerBindings, innerBody)
                else {
                    val temp = ScmSymbol.generate(res)
                    val variable = bindings.car(res).toPairOrNull().car(res)
                    val exp = bindings.car(res).toPairOrNull().cdr(res)
                    loop(
                        bindings = bindings.cdr(res).toPairOrNull(),
                        outerBindings = ScmPair.make(
                            ScmPair.list(variable, res.constUndef, res).toObject(),
                            outerBindings.toObject(),
                            res
                        ),
                        innerBindings = ScmPair.make(
                            ScmPair.make(temp.toObject(), exp, res).toObject(),
                            innerBindings.toObject(),
                            res
                        ),
                        innerBody = ScmPair.make(
                            ScmPair.list(
                                ScmSymbol.get("set!", res).toObject(),
                                variable,
                                temp.toObject(),
                                res
                            ).toObject(),
                            innerBody.toObject(),
                            res
                        )
                    )
                }

            val (bindings, body) = patternMatchLetrec(x)
            return compiler.transform(
                if (bindings.isNull()) {
                    ScmPair.make(ScmSymbol.get("begin", res).toObject(), body.toObject(), res).toObject()
                } else {
                    val (outerBindings, innerBindings, innerBody) = loop(
                        ScmPair.reverse(bindings.toPairNonNull(), res),
                        PtrPairOrNull(0),
                        PtrPairOrNull(0),
                        body
                    )
                    ScmPair.list(
                        ScmSymbol.get("let", res).toObject(), outerBindings.toObject(),
                        ScmPair.listStar(
                            ScmSymbol.get("let", res).toObject(),
                            innerBindings.toObject(),
                            innerBody.toObject(),
                            res
                        ).toObject(),
                        res
                    ).toObject()
                }
            )
        }
    })

    private fun patternMatchLetrec(x: PtrPair): Pair<PtrPairOrNull, PtrPairOrNull> {
        val bindings: PtrPairOrNull = try {
            ScmPair.cadr(x.toObject(), res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec") }
                .toPairOrNull()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec")
        }

        val body: PtrPairOrNull = try {
            ScmPair.cddr(x.toObject(), res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec") }
                .toPairOrNull()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec")
        }

        return bindings to body
    }


    /** macro: letrec* */
    private val macroLetrecStar = res.addMacro(object : ScmMacro("letrec*") {
        override fun transform(x: PtrPair, compiler: KevesCompiler): PtrObject {
            tailrec fun loop(
                bindings: PtrPairOrNull,
                variables: PtrPairOrNull,
                assignments: PtrPairOrNull,
                initValues: PtrPairOrNull
            ): Triple<PtrPairOrNull, PtrPairOrNull, PtrPairOrNull> =
                if (bindings.isNull()) Triple(variables, assignments, initValues)
                else loop(
                    bindings = bindings.cdr(res).toPairOrNull(),
                    variables = ScmPair.make(bindings.car(res).toPairOrNull().car(res), variables.toObject(), res),
                    assignments = ScmPair.make(
                        ScmPair.make(ScmSymbol.get("set!", res).toObject(), bindings.car(res), res).toObject(),
                        assignments.toObject(),
                        res
                    ),
                    initValues = ScmPair.make(res.constUndef, initValues.toObject(), res)
                )

            val (bindings, body) = patternMatchLetrecStar(x)
            return compiler.transform(
                if (bindings.isNull()) {
                    ScmPair.make(ScmSymbol.get("begin", res).toObject(), body.toObject(), res).toObject()
                } else {
                    val (variables, assignments, initValues) =
                        loop(ScmPair.reverse(bindings.toPairNonNull(), res), PtrPairOrNull(0), body, PtrPairOrNull(0))
                    ScmPair.make(
                        ScmPair.make(
                            ScmSymbol.get("lambda", res).toObject(),
                            ScmPair.make(variables.toObject(), assignments.toObject(), res).toObject(),
                            res
                        ).toObject(),
                        initValues.toObject(),
                        res
                    ).toObject()
                }
            )
        }
    })

    private fun patternMatchLetrecStar(x: PtrPair): Pair<PtrPairOrNull, PtrPairOrNull> {
        val bindings: PtrPairOrNull = try {
            ScmPair.cadr(x.toObject(), res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec*") }
                .toPairOrNull()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec*")
        }

        val body: PtrPairOrNull = try {
            ScmPair.cddr(x.toObject(), res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec*") }
                .toPairOrNull()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec*")
        }

        return bindings to body
    }

    /** macro: cond */
    private val macroCond = res.addMacro(object : ScmMacro("cond") {
        override fun transform(x: PtrPair, compiler: KevesCompiler): PtrObject {
            tailrec fun loop(rest: PtrPairOrNull, result: PtrObject): PtrObject =
                if (rest.isNull()) {
                    result
                } else {
                    val obj = rest.car(res)
                    loop(
                        rest = rest.cdr(res).toPairOrNull(),
                        result = ScmPair.list(
                            ScmSymbol.get("if", res).toObject(),
                            obj.toPairOrNull().toVal(res)!!.car,
                            obj.toPairOrNull().toVal(res)!!.cdr,
                            result,
                            res
                        ).toObject()
                    )
                }

            val clause = x.cdr(res).let {
                if (it.isPair(res)) it.toPairOrNull() else throw IllegalArgumentException("'cond' had no clause")
            }
            val converted = addBeginToExpressions(rest = clause, result = PtrPairOrNull(0))
            val first = converted.toVal(res)?.car ?: throw IllegalArgumentException("'cond' was malformed")
            val rest = converted.toVal(res)?.cdr ?: throw IllegalArgumentException("'cond' was malformed")
            val firstTest = ScmPair.car(first, res)
            val firstExpression = ScmPair.cdr(first, res)
            val result =
                if (firstTest.toSymbol() == ScmSymbol.get("else", res)) firstExpression
                else ScmPair.list(
                    ScmSymbol.get("if", res).toObject(),
                    firstTest,
                    firstExpression,
                    res.constUndef,
                    res
                ).toObject()

            val xx = loop(rest = rest.toPairOrNull(), result = result)
            return compiler.transform(xx)
        }
    })

    private tailrec fun addBeginToExpressions(rest: PtrPairOrNull, result: PtrPairOrNull): PtrPairOrNull =
        if (rest.isNull()) {
            result
        } else {
            val car = rest.car(res).also {
                if (it.isNotPair(res)) throw IllegalArgumentException("clause must be list but got other in 'cond'")
            }.toPairOrNull()
            val test = car.car(res)
            val expressions = car.cdr(res).also {
                if (it.isNotPair(res)) throw IllegalArgumentException("expression must be list but got other in 'cond'")
            }.toPairOrNull()
            addBeginToExpressions(
                rest.cdr(res).toPairOrNull(),
                ScmPair.make(
                    ScmPair.make(
                        test,
                        if (ScmPair.length(expressions.toObject(), res) > 1)
                            ScmPair.make(ScmSymbol.get("begin", res).toObject(), expressions.toObject(), res).toObject()
                        else expressions.car(res),
                        res
                    ).toObject(),
                    result.toObject(),
                    res
                )
            )
        }

    /** macro: when */
    private val macroWhen = res.addMacro(object : ScmMacro("when") {
        override fun transform(x: PtrPair, compiler: KevesCompiler): PtrObject {
            val (test, expressions) = patternMatchWhen(x)
            val xx =
                if (ScmPair.length(expressions.toObject(), res) > 1) {
                    ScmPair.list(
                        ScmSymbol.get("if", res).toObject(),
                        test.toObject(),
                        ScmPair.make(ScmSymbol.get("begin", res).toObject(), expressions.toObject(), res).toObject(),
                        res.constUndef,
                        res
                    )
                } else {
                    ScmPair.list(
                        ScmSymbol.get("if", res).toObject(),
                        test.toObject(),
                        expressions.car(res),
                        res.constUndef,
                        res
                    )
                }
            return compiler.transform(xx.toObject())
        }
    })

    private fun patternMatchWhen(x: PtrPair): Pair<PtrPair, PtrPair> {
        val test: PtrPair = try {
            ScmPair.cadr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("when")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("when") }.toPair()

        val expressions: PtrPair = try {
            ScmPair.cddr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("when")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("when") }.toPair()

        return test to expressions
    }

    /** macro: until */
    private val macroUntil = res.addMacro(object : ScmMacro("until") {
        override fun transform(x: PtrPair, compiler: KevesCompiler): PtrObject {
            val (test, expressions) = patternMatchUntil(x)
            val xx =
                if (ScmPair.length(expressions.toObject(), res) > 1) {
                    ScmPair.list(
                        ScmSymbol.get("if", res).toObject(),
                        test.toObject(),
                        res.constUndef,
                        ScmPair.make(ScmSymbol.get("begin", res).toObject(), expressions.toObject(), res).toObject(),
                        res
                    )
                } else {
                    ScmPair.list(
                        ScmSymbol.get("if", res).toObject(),
                        test.toObject(),
                        res.constUndef,
                        expressions.car(res),
                        res
                    )
                }
            return compiler.transform(xx.toObject())
        }
    })

    private fun patternMatchUntil(x: PtrPair): Pair<PtrPair, PtrPair> {
        val test: PtrPair = try {
            ScmPair.cadr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("until")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("until") }.toPair()

        val expressions: PtrPair = try {
            ScmPair.cddr(x.toObject(), res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("until")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("until") }.toPair()

        return test to expressions
    }

    /** procedure: call/cc, call-with-current-continuation */
    private val procCallWithCC = res.addProcedure(object : ScmProcedure(
        "call/cc",
        res.addSyntax(object : ScmSyntax(id = "call/cc") {
            override fun compile(
                x: PtrPair,
                e: PtrPairOrNull,
                s: PtrPairOrNull,
                next: PtrInstruction,
                compiler: KevesCompiler
            ): PtrInstruction {
                val xx = patternMatchCallCC(x)
                val c = ScmInstruction.Conti.make(
                    ScmInstruction.Argument.make(
                        compiler.compile(
                            xx.toObject(),
                            e,
                            s,
                            if (compiler.tailQ(next, res))
                                ScmInstruction.Shift.make(
                                    1,
                                    next.asInstructionReturn(res).n,
                                    ScmInstruction.Apply.make(1, res).toInstruction(),
                                    res
                                )
                            else ScmInstruction.Apply.make(1, res).toInstruction()
                        ),
                        res
                    ),
                    res
                )
                return if (compiler.tailQ(next, res)) c
                else ScmInstruction.Frame.make(next, c, res)
            }

            override fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
                val exp = patternMatchCallCC(x)
                return compiler.findSets(exp.toObject(), v)
            }

            override fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
                val exp = patternMatchCallCC(x)
                return compiler.findFree(exp.toObject(), b)
            }
        }).toVal(res)
    ) {
        override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
        override fun normalProc(n: Int, vm: KevesVM) {}
    })

    private fun patternMatchCallCC(x: PtrPair): PtrPairOrNull =
        try {
            ScmPair.cadr(x.toObject(), res) // exp
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected1DatumGot0("call/cc")
        }.also {
            if (it.isNotPair(res)) throw KevesExceptions.badSyntax(x.toVal(res).toStringForWrite(res))
            if (ScmPair.cddr(x.toObject(), res).isNotNull()) throw KevesExceptions.expected1DatumGotMore("call/cc")
        }.toPairOrNull()

    /** procedure: display */
    private val procDisplay: PtrProcedure by lazy {
        res.addProcedure(object : ScmProcedure(
            "display",
            res.addSyntax(object : ScmSyntax("display") {
                override fun compile(
                    x: PtrPair,
                    e: PtrPairOrNull,
                    s: PtrPairOrNull,
                    next: PtrInstruction,
                    compiler: KevesCompiler
                ): PtrInstruction {
                    val xx = patternMatchDisplay(x)
                    val c = compiler.compile(
                        xx,
                        e,
                        s,
                        if (compiler.tailQ(next, res))
                            ScmInstruction.Shift.make( // SHIFT,
                                0, // ScmInt(0),
                                next.asInstructionReturn(res).n,
                                ScmInstruction.ApplyDirect.make(
                                    procDisplay,
                                    res
                                ),
                                res
                            )
                        else ScmInstruction.ApplyDirect.make(procDisplay, res)
                    )
                    return if (compiler.tailQ(next, res)) c
                    else ScmInstruction.Frame.make(next, c, res)
                }

                override fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
                    val exp = patternMatchCallCC(x)
                    return compiler.findSets(exp.toObject(), v)
                }

                override fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler): PtrPairOrNull {
                    val exp = patternMatchCallCC(x)
                    return compiler.findFree(exp.toObject(), b)
                }
            }).toVal(res)
        ) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {
                print(getStringForDisplay(acc.toVal(res), res))
                val ret: ScmInstruction = vm.stack.index(sp, 0).asInstruction(res)
                val f: Int = try {
                    vm.stack.index(sp, 1).asInt(res).value
                } catch (e: TypeCastException) {
                    throw IllegalArgumentException("$id did wrong")
                }
                val c: PtrClosure = vm.stack.index(sp, 2).toClosure()
                vm.acc = res.constUndef
                vm.x = ret
                vm.fp = f
                vm.clsr = c
                vm.sp = sp - 3
                return
            }

            override fun normalProc(n: Int, vm: KevesVM) {}
        })
    }

    private fun patternMatchDisplay(x: PtrPair): PtrObject =
        try {
            ScmPair.cadr(x.toObject(), res) // exp
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected1DatumGot0("display")
        }

    /** procedure: make-vector */
    private val procMakeVector by lazy {
        res.addProcedure(object : ScmProcedure("make-vector", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val result = when (n) {
                    0 -> throw IllegalArgumentException("$id expected two or more object, but got nothing")
                    1 -> {
                        val k = try {
                            vm.stack.index(vm.sp, 0).asInt(res).value
                        } catch (e: TypeCastException) {
                            throw IllegalArgumentException("$id expected int but got other")
                        }
                        if (k < 0) throw IllegalArgumentException("$id doesn't accept negative number")
                        ScmVector.make(k, vm.res)
                    }
                    2 -> {
                        val sp = vm.sp
                        val k = try {
                            vm.stack.index(sp, 0).asInt(res).value
                        } catch (e: TypeCastException) {
                            throw IllegalArgumentException("$id expected int but got other")
                        }
                        if (k < 0) throw IllegalArgumentException("$id doesn't accept negative number")
                        val fill = vm.stack.index(sp, 1)
                        ScmVector.make(k, fill, vm.res)
                    }
                    else -> throw IllegalArgumentException("$id expected two or more object, but got more")
                }
                vm.scmProcReturn(result.toObject(), n)
            }
        })
    }

    /** procedure: eq? */
    private val procEqQ by lazy {
        res.addProcedure(object : ScmProcedure("eq?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("$id expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("$id expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val ptr1 = vm.stack.index(sp, 1)
                        val ptr2 = vm.stack.index(sp, 0)
                        val result =
                            if (ptr1 == ptr2) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw IllegalArgumentException("$id expected 2 object, but got more")
                }
            }
        })
    }

    /** procedure: eqv? */
    private val procEqvQ by lazy {
        res.addProcedure(object : ScmProcedure("eqv?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("$id expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("$id expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 1)
                        val obj2 = vm.stack.index(sp, 0)
                        val result = if (eqvQ(obj1, obj2, res)) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw IllegalArgumentException("$id expected 2 object, but got more")
                }
            }
        })
    }

    /** procedure: equal? */
    private val procEqualQ by lazy {
        res.addProcedure(object : ScmProcedure("equal?", null) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw IllegalArgumentException("$id expected 2 object, but got nothing")
                    1 -> throw IllegalArgumentException("$id expected 2 object, but got 1")
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 1)
                        val obj2 = vm.stack.index(sp, 0)
                        val result = if (equalQ(obj1, obj2, res)) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw IllegalArgumentException("$id expected 2 object, but got more")
                }
            }
        })
    }
}