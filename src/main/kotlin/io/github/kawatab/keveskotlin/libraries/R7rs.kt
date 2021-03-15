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
            ScmSymbol.get("begin", res) to syntaxBegin,
            ScmSymbol.get("quote", res) to syntaxQuote,
            ScmSymbol.get("lambda", res) to syntaxLambda,
            ScmSymbol.get("if", res) to syntaxIf,
            ScmSymbol.get("set!", res) to syntaxSetE,
            ScmSymbol.get("let", res) to macroLet,
            ScmSymbol.get("let*", res) to macroLetStar,
            ScmSymbol.get("letrec", res) to macroLetrec,
            ScmSymbol.get("letrec*", res) to macroLetrecStar,
            ScmSymbol.get("cond", res) to macroCond,
            ScmSymbol.get("when", res) to macroWhen,
            ScmSymbol.get("until", res) to macroUntil,
            ScmSymbol.get("call/cc", res) to procCallWithCC,
            ScmSymbol.get("call-with-current-continuation", res) to procCallWithCC,
            ScmSymbol.get("display", res) to procDisplay,
            ScmSymbol.get("*", res) to r7rsNumber.procMultiple,
            ScmSymbol.get("+", res) to r7rsNumber.procAdd,
            ScmSymbol.get("-", res) to r7rsNumber.procSubtract,
            ScmSymbol.get("/", res) to r7rsNumber.procDivide,
            ScmSymbol.get("<", res) to r7rsNumber.procLessThan,
            ScmSymbol.get("=", res) to r7rsNumber.procEqual,
            ScmSymbol.get(">", res) to r7rsNumber.procGraterThan,
            ScmSymbol.get("append", res) to r7rsList.procAppend,
            ScmSymbol.get("assoc", res) to r7rsList.procAssoc,
            ScmSymbol.get("assq", res) to r7rsList.procAssq,
            ScmSymbol.get("assv", res) to r7rsList.procAssv,
            ScmSymbol.get("cons", res) to r7rsList.procCons,
            ScmSymbol.get("caaaar", res) to r7rsList.procCaaaar,
            ScmSymbol.get("caaadr", res) to r7rsList.procCaaadr,
            ScmSymbol.get("caaar", res) to r7rsList.procCaaar,
            ScmSymbol.get("caadar", res) to r7rsList.procCaadar,
            ScmSymbol.get("caaddr", res) to r7rsList.procCaaddr,
            ScmSymbol.get("caadr", res) to r7rsList.procCaadr,
            ScmSymbol.get("caar", res) to r7rsList.procCaar,
            ScmSymbol.get("cadaar", res) to r7rsList.procCadaar,
            ScmSymbol.get("cadadr", res) to r7rsList.procCadadr,
            ScmSymbol.get("cadar", res) to r7rsList.procCadar,
            ScmSymbol.get("caddar", res) to r7rsList.procCaddar,
            ScmSymbol.get("cadddr", res) to r7rsList.procCadddr,
            ScmSymbol.get("caddr", res) to r7rsList.procCaddr,
            ScmSymbol.get("cadr", res) to r7rsList.procCadr,
            ScmSymbol.get("car", res) to r7rsList.procCar,
            ScmSymbol.get("cdaaar", res) to r7rsList.procCdaaar,
            ScmSymbol.get("cdaadr", res) to r7rsList.procCdaadr,
            ScmSymbol.get("cdaar", res) to r7rsList.procCdaar,
            ScmSymbol.get("cdadar", res) to r7rsList.procCdadar,
            ScmSymbol.get("cdaddr", res) to r7rsList.procCdaddr,
            ScmSymbol.get("cdadr", res) to r7rsList.procCdadr,
            ScmSymbol.get("cdar", res) to r7rsList.procCdar,
            ScmSymbol.get("cddaar", res) to r7rsList.procCddaar,
            ScmSymbol.get("cddadr", res) to r7rsList.procCddadr,
            ScmSymbol.get("cddar", res) to r7rsList.procCddar,
            ScmSymbol.get("cdddar", res) to r7rsList.procCdddar,
            ScmSymbol.get("cddddr", res) to r7rsList.procCddddr,
            ScmSymbol.get("cdddr", res) to r7rsList.procCdddr,
            ScmSymbol.get("cddr", res) to r7rsList.procCddr,
            ScmSymbol.get("cdr", res) to r7rsList.procCdr,
            ScmSymbol.get("char?", res) to r7rsChar.procCharQ,
            ScmSymbol.get("char->integer", res) to r7rsChar.procCharToInteger,
            ScmSymbol.get("char-ci<=?", res) to r7rsChar.procCharCILessThanEqualQ,
            ScmSymbol.get("char-ci<?", res) to r7rsChar.procCharCILessThanQ,
            ScmSymbol.get("char-ci=?", res) to r7rsChar.procCharCIEqualQ,
            ScmSymbol.get("char-ci>=?", res) to r7rsChar.procCharCIGraterThanEqualQ,
            ScmSymbol.get("char-ci>?", res) to r7rsChar.procCharCIGraterThanQ,
            ScmSymbol.get("char-alphabetic?", res) to r7rsChar.procCharAlphabeticQ,
            ScmSymbol.get("char-downcase", res) to r7rsChar.procCharDowncase,
            ScmSymbol.get("char-foldcase", res) to r7rsChar.procCharFoldcase,
            ScmSymbol.get("char-lower-case?", res) to r7rsChar.procCharLowerCaseQ,
            ScmSymbol.get("char-numeric?", res) to r7rsChar.procCharNumericQ,
            ScmSymbol.get("char-upper-case?", res) to r7rsChar.procCharUpperCaseQ,
            ScmSymbol.get("char-upcase", res) to r7rsChar.procCharUpcase,
            ScmSymbol.get("char-whitespace?", res) to r7rsChar.procCharWhitespaceQ,
            ScmSymbol.get("char<=?", res) to r7rsChar.procCharLessThanEqualQ,
            ScmSymbol.get("char<?", res) to r7rsChar.procCharLessThanQ,
            ScmSymbol.get("char=?", res) to r7rsChar.procCharEqualQ,
            ScmSymbol.get("char>=?", res) to r7rsChar.procCharGraterThanEqualQ,
            ScmSymbol.get("char>?", res) to r7rsChar.procCharGraterThanQ,
            ScmSymbol.get("digit-value", res) to r7rsChar.procDigitValue,
            ScmSymbol.get("eq?", res) to procEqQ,
            ScmSymbol.get("equal?", res) to procEqualQ,
            ScmSymbol.get("eqv?", res) to procEqvQ,
            ScmSymbol.get("integer->char", res) to r7rsChar.procIntegerToChar,
            ScmSymbol.get("length", res) to r7rsList.procLength,
            ScmSymbol.get("list", res) to r7rsList.procList,
            ScmSymbol.get("list-copy", res) to r7rsList.procListCopy,
            ScmSymbol.get("list-ref", res) to r7rsList.procListRef,
            ScmSymbol.get("list-set!", res) to r7rsList.procListSetE,
            ScmSymbol.get("list-tail", res) to r7rsList.procListTail,
            ScmSymbol.get("list?", res) to r7rsList.procListQ,
            ScmSymbol.get("make-list", res) to r7rsList.procMakeList,
            ScmSymbol.get("make-vector", res) to procMakeVector,
            ScmSymbol.get("member", res) to r7rsList.procMember,
            ScmSymbol.get("memq", res) to r7rsList.procMemq,
            ScmSymbol.get("memv", res) to r7rsList.procMemv,
            ScmSymbol.get("null?", res) to r7rsList.procNullQ,
            ScmSymbol.get("pair?", res) to r7rsList.procPairQ,
            ScmSymbol.get("reverse", res) to r7rsList.procReverse,
            ScmSymbol.get("set-car!", res) to r7rsList.procSetCarE,
            ScmSymbol.get("set-cdr!", res) to r7rsList.procSetCdrE,
            ScmSymbol.get("string->symbol", res) to r7rsSymbol.procStringToSymbol,
            ScmSymbol.get("string=?", res) to r7rsString.procStringEqualQ,
            ScmSymbol.get("string?", res) to r7rsString.procStringQ,
            ScmSymbol.get("symbol->string", res) to r7rsSymbol.procSymbolToString,
            ScmSymbol.get("symbol=?", res) to r7rsSymbol.procSymbolEqualQ,
            ScmSymbol.get("symbol?", res) to r7rsSymbol.procSymbolQ,
            ScmSymbol.get("zero?", res) to r7rsNumber.procZeroQ,
        )
    }

    val symbolBegin = ScmSymbol.get("begin", res)

    /** syntax: begin */
    private val syntaxBegin = res.addSyntax(object : ScmSyntax("begin") {
        override fun compile(
            x: PtrPairNonNull,
            e: PtrPair,
            s: PtrPair,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            tailrec fun loop(exps: PtrPair, c: PtrInstruction): PtrInstruction =
                if (exps.isNull()) {
                    c
                } else {
                    val expsCdr = exps.cdr(res).also {
                        if (it.isNotNull() && it.toVal(res) !is ScmPair)
                            throw IllegalArgumentException(
                                KevesExceptions.badSyntax(x.toVal(res).toStringForWrite(res))
                            )
                    }.toPair()
                    loop(expsCdr, compiler.compile(exps.car(res), e, s, c))
                }

            return patternMatchBegin(x.toVal(res)).let { expressions ->
                if (expressions.isNull()) ScmInstruction.Constant.make(res.constUndef, next, res)
                else loop(ScmMutablePair.reverse(expressions, res).toPair(), next)
            }
            // } ?: res.get(ScmInstruction.Constant.make(ScmConstant.UNDEF, next, res)) as ScmInstruction
        }

        override fun findSets(x: PtrPairNonNull, v: PtrPair, compiler: KevesCompiler): PtrPair {
            val exps = patternMatchBegin(x.toVal(res))
            return compiler.findSets(exps.toObject(), v)
        }

        override fun findFree(x: PtrPairNonNull, b: PtrPair, compiler: KevesCompiler): PtrPair {
            val exps = patternMatchBegin(x.toVal(res))
            return compiler.findFree(exps.toObject(), b)
        }
    })

    private fun patternMatchBegin(x: ScmPair): PtrPair =
        x.cdr.also {
            if (it.isNeitherNullNorPair(res))
                throw IllegalArgumentException(KevesExceptions.badSyntax(x.toStringForWrite(res)))
        }.toPair()

    /** syntax: quote */
    private val syntaxQuote = res.addSyntax(object : ScmSyntax("quote") {
        override fun compile(
            x: PtrPairNonNull,
            e: PtrPair,
            s: PtrPair,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val obj = patternMatchQuote(x.toVal(res))
            return ScmInstruction.Constant.make(obj, next, res)
        }

        override fun findSets(x: PtrPairNonNull, v: PtrPair, compiler: KevesCompiler): PtrPair {
            patternMatchQuote(x.toVal(res))
            return PtrPair(0)
        }

        override fun findFree(x: PtrPairNonNull, b: PtrPair, compiler: KevesCompiler): PtrPair {
            patternMatchQuote(x.toVal(res))
            return PtrPair(0)
        }
    })

    private fun patternMatchQuote(x: ScmPair): PtrObject =
        try {
            ScmPair.cadr(x, res)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesExceptions.expected1DatumGot0("quote"))
        }.also {
            if (ScmPair.cddr(x, res).isNotNull())
                throw IllegalArgumentException(KevesExceptions.expected1DatumGotMore("quote"))
        }

    /** syntax: lambda */
    private val syntaxLambda = res.addSyntax(object : ScmSyntax("lambda") {
        override fun compile(
            x: PtrPairNonNull,
            e: PtrPair,
            s: PtrPair,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val (vars, body) = patternMatchLambda(x.toVal(res))
            val (varsAsProperList, numArg) = ScmPair.toProperList(vars, res)
            val free = compiler.findFree(body.toObject(), varsAsProperList)
            val sets = compiler.findSets(body.toObject(), varsAsProperList)
            return compiler.collectFree(
                free,
                e,
                ScmInstruction.Close.make( // CLOSE,
                    ScmPair.length(free.toVal(res), res),
                    numArg,
                    compiler.makeBoxes(
                        sets,
                        vars,
                        compiler.compile(
                            ScmPair.make(symbolBegin.toObject(), body.toObject(), res).toObject(),
                            ScmPair.make(varsAsProperList.toObject(), free.toObject(), res),
                            compiler.setUnion(sets, compiler.setIntersect(s, free)),
                            ScmInstruction.Return.make(
                                ScmPair.length(varsAsProperList.toVal(res), res),
                                res
                            )
                        )
                    ),
                    next,
                    res
                )
            )
        }

        override fun findSets(x: PtrPairNonNull, v: PtrPair, compiler: KevesCompiler): PtrPair {
            val (vars, body) = patternMatchLambda(x.toVal(res))
            return compiler.findSets(body.toObject(), compiler.setMinus(v, ScmPair.toProperList(vars, res).first))
        }

        override fun findFree(x: PtrPairNonNull, b: PtrPair, compiler: KevesCompiler): PtrPair {
            val (vars, body) = patternMatchLambda(x.toVal(res))
            return compiler.findFree(body.toObject(), compiler.setUnion(ScmPair.toProperList(vars, res).first, b))
        }
    })

    private fun patternMatchLambda(x: ScmPair): Pair<PtrObject, PtrPairNonNull> {
        val vars: PtrObject = try {
            ScmPair.cadr(x, res).also {
                if (it.isNeitherNullNorPair(res) && it.isNotSymbol(res)) throw KevesExceptions.badSyntax(x.toStringForWrite(res))
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(KevesExceptions.expected2OrMoreDatumGotLess("lambda"))
        }
        val body: PtrPairNonNull = try {
            ScmPair.cddr(x, res).also { // original is caddr instead of cddr
                if (it.isNotPair(res)) throw KevesExceptions.badSyntax(x.toStringForWrite(res))
            }.toPairNonNull()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2OrMoreDatumGotLess("lambda")
        }
        return vars to body
    }

    /** syntax: if */
    private val syntaxIf = res.addSyntax(object : ScmSyntax("if") {
        override fun compile(
            x: PtrPairNonNull,
            e: PtrPair,
            s: PtrPair,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val (test, thn, els) = patternMatchIf(x.toVal(res))
            val thenC = compiler.compile(thn, e, s, next)
            val elseC = compiler.compile(els, e, s, next)
            return compiler.compile(
                test,
                e,
                s,
                ScmInstruction.Test.make(thenC, elseC, res)
            ) // TEST, thenC, elseC))
        }

        override fun findSets(x: PtrPairNonNull, v: PtrPair, compiler: KevesCompiler): PtrPair {
            val (test, thn, els) = patternMatchIf(x.toVal(res))
            return compiler.setUnion(
                compiler.findSets(test, v),
                compiler.setUnion(compiler.findSets(thn, v), compiler.findSets(els, v))
            )
        }

        override fun findFree(x: PtrPairNonNull, b: PtrPair, compiler: KevesCompiler): PtrPair {
            val (test, thn, els) = patternMatchIf(x.toVal(res))
            return compiler.setUnion(
                compiler.findFree(test, b),
                compiler.setUnion(compiler.findFree(thn, b), compiler.findFree(els, b))
            )
        }
    })

    private fun patternMatchIf(x: ScmPair): Triple<PtrObject, PtrObject, PtrObject> {
        val test: PtrObject = try {
            ScmPair.cadr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        val thn: PtrObject = try {
            ScmPair.caddr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        val els: PtrObject = try {
            ScmPair.cadddr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected3DatumGotLess("if")
        }
        if (ScmPair.cddddr(x, res).isNotNull()) throw KevesExceptions.expected3DatumGotMore("if")
        return Triple(test, thn, els)
    }

    /** syntax: set! */
    private val syntaxSetE = res.addSyntax(object : ScmSyntax("set!") {
        override fun compile(
            x: PtrPairNonNull,
            e: PtrPair,
            s: PtrPair,
            next: PtrInstruction,
            compiler: KevesCompiler
        ): PtrInstruction {
            val (variable, xx) = patternMatchSetE(x.toVal(res))
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

        override fun findSets(x: PtrPairNonNull, v: PtrPair, compiler: KevesCompiler): PtrPair {
            val (variable, xx) = patternMatchSetE(x.toVal(res))
            return compiler.setUnion(
                if (compiler.setMemberQ(variable, v)) ScmPair.list(variable.toObject(), res) else PtrPair(0),
                compiler.findSets(xx, v)
            )
        }

        override fun findFree(x: PtrPairNonNull, b: PtrPair, compiler: KevesCompiler): PtrPair {
            val (variable, exp) = patternMatchSetE(x.toVal(res))
            return compiler.setUnion(
                if (compiler.setMemberQ(variable, b)) PtrPair(0) else ScmPair.list(variable.toObject(), res),
                compiler.findFree(exp, b)
            )
        }
    })

    private fun patternMatchSetE(x: ScmPair): Pair<PtrSymbol, PtrObject> {
        val variable: PtrSymbol = try {
            ScmPair.cadr(x, res)
                .also { if (it.isNotSymbol(res)) throw KevesExceptions.expectedSymbol("set!") }
                .toSymbol()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("set!")
        }
        val exp: PtrObject = try {
            ScmPair.caddr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("set!")
        }
        if (ScmPair.cdddr(x, res).isNotNull()) throw KevesExceptions.expected2DatumGotMore("set!")
        return variable to exp
    }

    /** macro: let */
    private val macroLet = res.addMacro(object : ScmMacro("let") {
        override fun transform(x: PtrPairNonNull, compiler: KevesCompiler): PtrObject {
            val (bindings, body) = patternMatchLet(x.toVal(res))
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

    private fun patternMatchLet(x: ScmPair): Pair<PtrPair, PtrPair> {
        val bindings: PtrPair = try {
            ScmPair.cadr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let")
        }.toPair()

        val body: PtrPair = try {
            ScmPair.cddr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let")
        }.toPair()

        return bindings to body
    }

    /** macro: let* */
    private val macroLetStar = res.addMacro(object : ScmMacro("let*") {
        override fun transform(x: PtrPairNonNull, compiler: KevesCompiler): PtrObject {
            val (bindings, body) = patternMatchLetStar(x.toVal(res))
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

    private fun patternMatchLetStar(x: ScmPair): Pair<PtrPair, PtrPair> {
        val bindings: PtrPair = try {
            ScmPair.cadr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let*")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let*")
        }.toPair()

        val body: PtrPair = try {
            ScmPair.cddr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("let*")
        }.also {
            if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("let*")
        }.toPair()

        return bindings to body
    }

    /** macro: letrec */
    private val macroLetrec = res.addMacro(object : ScmMacro("letrec") {
        override fun transform(x: PtrPairNonNull, compiler: KevesCompiler): PtrObject {
            tailrec fun loop(
                bindings: PtrPair,
                outerBindings: PtrPair,
                innerBindings: PtrPair,
                innerBody: PtrPair
            ): Triple<PtrPair, PtrPair, PtrPair> =
                if (bindings.isNull()) Triple(outerBindings, innerBindings, innerBody)
                else {
                    val temp = ScmSymbol.generate(res)
                    val variable = bindings.car(res).toPair().car(res)
                    val exp = bindings.car(res).toPair().cdr(res)
                    loop(
                        bindings = bindings.cdr(res).toPair(),
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

            val (bindings, body) = patternMatchLetrec(x.toVal(res))
            return compiler.transform(
                if (bindings.isNull()) {
                    ScmPair.make(ScmSymbol.get("begin", res).toObject(), body.toObject(), res).toObject()
                } else {
                    val (outerBindings, innerBindings, innerBody) = loop(
                        ScmPair.reverse(bindings.toPairNonNull(), res),
                        PtrPair(0),
                        PtrPair(0),
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

    private fun patternMatchLetrec(x: ScmPair): Pair<PtrPair, PtrPair> {
        val bindings: PtrPair = try {
            ScmPair.cadr(x, res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec") }
                .toPair()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec")
        }

        val body: PtrPair = try {
            ScmPair.cddr(x, res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec") }
                .toPair()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec")
        }

        return bindings to body
    }


    /** macro: letrec* */
    private val macroLetrecStar = res.addMacro(object : ScmMacro("letrec*") {
        override fun transform(x: PtrPairNonNull, compiler: KevesCompiler): PtrObject {
            tailrec fun loop(
                bindings: PtrPair,
                variables: PtrPair,
                assignments: PtrPair,
                initValues: PtrPair
            ): Triple<PtrPair, PtrPair, PtrPair> =
                if (bindings.isNull()) Triple(variables, assignments, initValues)
                else loop(
                    bindings = bindings.cdr(res).toPair(),
                    variables = ScmPair.make(bindings.car(res).toPair().car(res), variables.toObject(), res),
                    assignments = ScmPair.make(
                        ScmPair.make(ScmSymbol.get("set!", res).toObject(), bindings.car(res), res).toObject(),
                        assignments.toObject(),
                        res
                    ),
                    initValues = ScmPair.make(res.constUndef, initValues.toObject(), res)
                )

            val (bindings, body) = patternMatchLetrecStar(x.toVal(res))
            return compiler.transform(
                if (bindings.isNull()) {
                    ScmPair.make(ScmSymbol.get("begin", res).toObject(), body.toObject(), res).toObject()
                } else {
                    val (variables, assignments, initValues) =
                        loop(ScmPair.reverse(bindings.toPairNonNull(), res), PtrPair(0), body, PtrPair(0))
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

    private fun patternMatchLetrecStar(x: ScmPair): Pair<PtrPair, PtrPair> {
        val bindings: PtrPair = try {
            ScmPair.cadr(x, res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec*") }
                .toPair()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec*")
        }

        val body: PtrPair = try {
            ScmPair.cddr(x, res)
                .also { if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedSymbol("letrec*") }
                .toPair()
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("letrec*")
        }

        return bindings to body
    }

    /** macro: cond */
    private val macroCond = res.addMacro(object : ScmMacro("cond") {
        override fun transform(x: PtrPairNonNull, compiler: KevesCompiler): PtrObject {
            tailrec fun loop(rest: PtrPair, result: PtrObject): PtrObject =
                if (rest.isNull()) {
                    result
                } else {
                    val obj = rest.car(res)
                    loop(
                        rest = rest.cdr(res).toPair(),
                        result = ScmPair.list(
                            ScmSymbol.get("if", res).toObject(),
                            obj.toPair().toVal(res)!!.car,
                            obj.toPair().toVal(res)!!.cdr,
                            result,
                            res
                        ).toObject()
                    )
                }

            val clause = x.cdr(res).let {
                if (it.toVal(res) is ScmPair) it.toPair() else throw IllegalArgumentException("'cond' had no clause")
            }
            val converted = addBeginToExpressions(rest = clause, result = PtrPair(0))
            val first = ScmPair.car(converted.toVal(res))
            val rest = ScmPair.cdr(converted.toVal(res))
            val firstTest = ScmPair.car(first.toVal(res))
            val firstExpression = ScmPair.cdr(first.toVal(res))
            val result =
                if (firstTest.toSymbol() == ScmSymbol.get("else", res)) firstExpression
                else ScmPair.list(
                    ScmSymbol.get("if", res).toObject(),
                    firstTest,
                    firstExpression,
                    res.constUndef,
                    res
                ).toObject()

            val xx = loop(rest = rest.toPair(), result = result)
            return compiler.transform(xx)
        }
    })

    private tailrec fun addBeginToExpressions(rest: PtrPair, result: PtrPair): PtrPair =
        if (rest.isNull()) {
            result
        } else {
            val car = rest.car(res).also {
                if (it.isNotPair(res)) throw IllegalArgumentException("clause must be list but got other in 'cond'")
            }.toPair()
            val test = car.car(res)
            val expressions = car.cdr(res).also {
                if (it.isNotPair(res)) throw IllegalArgumentException("expression must be list but got other in 'cond'")
            }.toPair()
            addBeginToExpressions(
                rest.cdr(res).toPair(),
                ScmPair.make(
                    ScmPair.make(
                        test,
                        if (ScmPair.length(expressions.toVal(res), res) > 1)
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
        override fun transform(x: PtrPairNonNull, compiler: KevesCompiler): PtrObject {
            val (test, expressions) = patternMatchWhen(x.toVal(res))
            val xx =
                if (ScmPair.length(expressions.toVal(res), res) > 1) {
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

    private fun patternMatchWhen(x: ScmPair): Pair<PtrPairNonNull, PtrPairNonNull> {
        val test: PtrPairNonNull = try {
            ScmPair.cadr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("when")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("when") }.toPairNonNull()

        val expressions: PtrPairNonNull = try {
            ScmPair.cddr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("when")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("when") }.toPairNonNull()

        return test to expressions
    }

    /** macro: until */
    private val macroUntil = res.addMacro(object : ScmMacro("until") {
        override fun transform(x: PtrPairNonNull, compiler: KevesCompiler): PtrObject {
            val (test, expressions) = patternMatchUntil(x.toVal(res))
            val xx =
                if (ScmPair.length(expressions.toVal(res), res) > 1) {
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

    private fun patternMatchUntil(x: ScmPair): Pair<PtrPairNonNull, PtrPairNonNull> {
        val test: PtrPairNonNull = try {
            ScmPair.cadr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("until")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("until") }.toPairNonNull()

        val expressions: PtrPairNonNull = try {
            ScmPair.cddr(x, res)
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected2DatumGotLess("until")
        }.also { if (it.isNotPair(res)) throw KevesExceptions.expectedSymbol("until") }.toPairNonNull()

        return test to expressions
    }

    /** procedure: call/cc, call-with-current-continuation */
    private val procCallWithCC = res.addProcedure(object : ScmProcedure(
        "call/cc",
        res.addSyntax(object : ScmSyntax(id = "call/cc") {
            override fun compile(
                x: PtrPairNonNull,
                e: PtrPair,
                s: PtrPair,
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
                                    (next.toVal(res) as ScmInstruction.Return).n,
                                    // (ScmPair.car(next, res).toVal(res) as ScmInstruction.Return).n,
                                    ScmInstruction.Apply.make(1, res),
                                    res
                                )
                            else ScmInstruction.Apply.make(1, res)
                        ),
                        res
                    ),
                    res
                )
                return if (compiler.tailQ(next, res)) c
                else ScmInstruction.Frame.make(next, c, res)
            }

            override fun findSets(x: PtrPairNonNull, v: PtrPair, compiler: KevesCompiler): PtrPair {
                val exp = patternMatchCallCC(x)
                return compiler.findSets(exp.toObject(), v)
            }

            override fun findFree(x: PtrPairNonNull, b: PtrPair, compiler: KevesCompiler): PtrPair {
                val exp = patternMatchCallCC(x)
                return compiler.findFree(exp.toObject(), b)
            }
        }).toVal(res) as ScmSyntax
    ) {
        override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
        override fun normalProc(n: Int, vm: KevesVM) {}
    })

    private fun patternMatchCallCC(x: PtrPairNonNull): PtrPair =
        try {
            ScmPair.cadr(x.toVal(res), res) // exp
        } catch (e: IllegalArgumentException) {
            throw KevesExceptions.expected1DatumGot0("call/cc")
        }.also {
            if (it.isNotPair(res)) throw KevesExceptions.badSyntax(x.toVal(res).toStringForWrite(res))
            if (ScmPair.cddr(x.toVal(res), res).isNotNull()) throw KevesExceptions.expected1DatumGotMore("call/cc")
        }.toPair()

    /** procedure: display */
    private val procDisplay: PtrObject by lazy {
        res.addProcedure(object : ScmProcedure(
            "display",
            res.addSyntax(object : ScmSyntax("display") {
                override fun compile(
                    x: PtrPairNonNull,
                    e: PtrPair,
                    s: PtrPair,
                    next: PtrInstruction,
                    compiler: KevesCompiler
                ): PtrInstruction {
                    val xx = patternMatchDisplay(x.toVal(res))
                    val c = compiler.compile(
                        xx,
                        e,
                        s,
                        if (compiler.tailQ(next, res))
                            ScmInstruction.Shift.make( // SHIFT,
                                0, // ScmInt(0),
                                (next.toVal(res) as ScmInstruction.Return).n,
                                // (ScmPair.car(next) as ScmInstruction.Return).n,
                                ScmInstruction.ApplyDirect.make(
                                    procDisplay.toVal(res) as ScmProcedure,
                                    res
                                ),
                                res
                            )
                        else ScmInstruction.ApplyDirect.make(
                            procDisplay.toVal(res) as ScmProcedure,
                            res
                        )
                    )
                    return if (compiler.tailQ(next, res)) c
                    else ScmInstruction.Frame.make(next, c, res)
                }

                override fun findSets(x: PtrPairNonNull, v: PtrPair, compiler: KevesCompiler): PtrPair {
                    val exp = patternMatchCallCC(x)
                    return compiler.findSets(exp.toObject(), v)
                }

                override fun findFree(x: PtrPairNonNull, b: PtrPair, compiler: KevesCompiler): PtrPair {
                    val exp = patternMatchCallCC(x)
                    return compiler.findFree(exp.toObject(), b)
                }
            }).toVal(res) as ScmSyntax
        ) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {
                print(getStringForDisplay(acc.toVal(res), res))
                val ret: ScmInstruction = vm.stack.index(sp, 0).toVal(res) as ScmInstruction
                val f: Int =
                    (vm.stack.index(sp, 1).toVal(res) as? ScmInt)?.value
                        ?: throw IllegalArgumentException("$id did wrong")
                val c: PtrClosure = vm.stack.index(sp, 2).toClosure()
                vm.acc = res.constUndef // ScmConstant.UNDEF
                vm.x = ret
                vm.fp = f
                vm.clsr = c
                vm.sp = sp - 3
                return
            }

            override fun normalProc(n: Int, vm: KevesVM) {}
        })
    }

    private fun patternMatchDisplay(x: ScmPair): PtrObject =
        try {
            ScmPair.cadr(x, res) // exp
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
                        val k = (vm.stack.index(vm.sp, 0).toVal(res) as? ScmInt)?.value
                            ?: throw IllegalArgumentException("$id expected int but got other")
                        if (k < 0) throw IllegalArgumentException("$id doesn't accept negative number")
                        ScmVector.make(k, vm.res)
                    }
                    2 -> {
                        val sp = vm.sp
                        val k = (vm.stack.index(sp, 0).toVal(res) as? ScmInt)?.value
                            ?: throw IllegalArgumentException("$id expected int but got other")
                        if (k < 0) throw IllegalArgumentException("$id doesn't accept negative number")
                        val fill = vm.stack.index(sp, 1)
                        ScmVector.make(k, fill, vm.res)
                    }
                    else -> throw IllegalArgumentException("$id expected two or more object, but got more")
                }
                vm.scmProcReturn(result, n)
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
                        val ptr1 = vm.stack.index(sp, 1)
                        val ptr2 = vm.stack.index(sp, 0)
                        val result = if (eqvQ(
                                ptr1.toVal(res),
                                ptr2.toVal(res)
                            )
                        ) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
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
                        val ptr1 = vm.stack.index(sp, 1)
                        val ptr2 = vm.stack.index(sp, 0)
                        val result = if (equalQ(
                                ptr1.toVal(res),
                                ptr2.toVal(res),
                                res
                            )
                        ) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw IllegalArgumentException("$id expected 2 object, but got more")
                }
            }
        })
    }
}