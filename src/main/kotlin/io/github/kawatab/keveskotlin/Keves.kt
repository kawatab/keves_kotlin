/*
 * Keves.kt
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

class Keves {
    var res = KevesResources()

    fun getStringForDisplay(atom: ScmObject?): String = atom?.toStringForDisplay(res) ?: "()"
    fun getStringForWrite(atom: ScmObject?): String = atom?.toStringForWrite(res) ?: "()"

    fun load(text: String) {
        res = KevesResources()
        val parser = KevesParser(text, res)
        val result = parser.parse()
        parser.errorList.let { errorList ->
            if (errorList.isEmpty()) {
                println(ScmObject.getStringForWrite(result.toObject(), res))
            } else {
                displayError(parser.errorList, res)
            }
        }
    }

    fun parse(text: String): Pair<PtrObject, List<PtrError>> {
        res = KevesResources()
        val parser = KevesParser(text, res)
        val result: PtrObject = parser.parse().toObject()
        return result to parser.errorList
    }

    private fun displayError(errorList: List<PtrError>, res: KevesResources) {
        errorList.forEach { error -> println(error.toVal(res).toStringForDisplay(res)) }
    }

    fun compile(sExp: String) {
        res = KevesResources()
        val parser = KevesParser(sExp, res)
        val compiler = KevesCompiler(res)
        val code: PtrPairOrNull = parser.parse()
        println(
            ScmObject.getStringForDisplay(
                compiler.compile(
                    compiler.transform(code.toObject())
                        .also { println(ScmObject.getStringForDisplay(it, res)) },
                    PtrPairOrNull(0),
                    PtrPairOrNull(0),
                    res.constHalt.toInstruction(),
                ).toObject(),
                res
            )
        )
    }

    fun evaluate2(sExp: String): PtrObject {
        res = KevesResources()
        val parser = KevesParser(sExp, res)

        val compiler = try {
            KevesCompiler(res)
        } catch (e: IllegalArgumentException) {
            return ScmError.make("compiler", e.message ?: "", res).toObject()
        }
        val vm = KevesVM(res)
        return parser.parse().let { parsed ->
            if (parsed.isNull()) PtrObject(0)
            else {
                try {
                    compiler.compile(
                        compiler.transform(parsed.toObject()),
                        PtrPairOrNull(0),
                        PtrPairOrNull(0),
                        res.constHalt.toInstruction()
                    )
                } catch (e: IllegalArgumentException) {
                    println("compile error")
                    return ScmError.make("compiler", e.message ?: "", res).toObject()
                }.let { compiled ->
                    println("compiled: ${compiled.toVal(res).toStringForWrite(res)}")
                    try {
                        vm.evaluate(compiled).also { println("result: ${ScmObject.getStringForWrite(it, res)}") }
                    } catch (e: IllegalArgumentException) {
                        return ScmError.make("vm", e.message ?: "", res).toObject()
                    }
                }
            }
        }
    }

    fun evaluate(sExp: String) {
        res = KevesResources()
        val parser = KevesParser(sExp, res)
        val compiler = KevesCompiler(res)
        val vm = KevesVM(res)
        println(
            ScmObject.getStringForDisplay(
                parser.parse().let { parsed ->
                    if (parsed.isNull()) PtrObject(0)
                    else {
                        try {
                            compiler.compile(
                                compiler.transform(parsed.toObject()),
                                PtrPairOrNull(0),
                                PtrPairOrNull(0),
                                res.constHalt.toInstruction()
                            ).let { compiled ->
                                try {
                                    vm.evaluate(compiled)
                                } catch (e: IllegalArgumentException) {
                                    val error = ScmError.make("vm", e.message ?: "", res)
                                    return println(error.toVal(res).toStringForDisplay(res))
                                }
                            }
                        } catch (e: IllegalArgumentException) {
                            val error = ScmError.make("compiler", e.message ?: "", res)
                            return println(error.toVal(res).toStringForDisplay(res))
                        }
                    }
                },
                res
            )
        )
    }
}
