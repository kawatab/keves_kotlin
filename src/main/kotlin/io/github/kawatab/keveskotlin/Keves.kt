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
    companion object {
        fun getStringForDisplay(atom: ScmObject?): String = atom?.toStringForDisplay() ?: "()"
        fun getStringForWrite(atom: ScmObject?): String = atom?.toStringForWrite() ?: "()"
    }

    fun load(text: String) {
        val parser = KevesParser(text)
        val result = parser.parse()
        parser.errorList.let { errorList ->
            if (errorList.isEmpty()) {
                println(ScmObject.getStringForWrite(result))
            } else {
                displayError(parser.errorList)
            }
        }
    }

    fun parse(text: String): Pair<ScmObject?, List<ScmError>> {
        val parser = KevesParser(text)
        val result = parser.parse()
        return result to parser.errorList
    }

    private fun displayError(errorList: List<ScmError>) {
        errorList.forEach { error -> println(error.toStringForDisplay()) }
    }

    fun compile(sExp: String) {
        val parser = KevesParser(sExp)
        val compiler = KevesCompiler()
        val code: ScmPair? = parser.parse()
        println(
            ScmObject.getStringForDisplay(
                compiler.compile(
                    compiler.transform(code).also { println(ScmObject.getStringForDisplay(it)) },
                    null,
                    null,
                    ScmPair.list(ScmInstruction.HALT)
                )
            )
        )
    }

    /*
    fun evaluate(sExp: String) {
        val parser = io.github.withlet11.WL11Parser(sExp)
        val code: ScmPair? = parser.parse()
        when (val build = io.github.withlet11.WL11Compiler().compile(code)) {
            null -> directRun(null)
            is ScmPair -> directRun(build)
            else -> println(ScmObject.getStringForDisplay(build))
        }
    }
     */
    fun evaluate2(sExp: String): ScmObject? {
        val parser = KevesParser(sExp)

        val compiler = try {
            KevesCompiler()
        } catch (e: IllegalArgumentException) {
            return ScmError("compiler", e.message ?: "")
        }
        val vm = KevesVM()
        return parser.parse()?.let { parsed ->
            try {
                compiler.compile(compiler.transform(parsed), null, null, ScmPair.list(ScmInstruction.HALT))
            } catch (e: IllegalArgumentException) {
                return ScmError("compiler", e.message ?: "")
            }?.let { compiled ->
                try {
                    vm.evaluate(compiled)
                } catch (e: IllegalArgumentException) {
                    return ScmError("vm", e.message ?: "")
                }
            }
        }
    }

    fun evaluate(sExp: String) {
        val parser = KevesParser(sExp)
        val compiler = KevesCompiler()
        val vm = KevesVM()
        println(
            ScmObject.getStringForDisplay(
                parser.parse()?.let { parsed ->
                    try {
                        compiler.compile(compiler.transform(parsed), null, null, ScmPair.list(ScmInstruction.HALT))
                            ?.let { compiled ->
                                try {
                                    // vm.vm(null, compiled, 0, ScmClosure("dummy lambda", null, 0, ScmVector(0)), 0)
                                    vm.evaluate(compiled)
                                } catch (e: IllegalArgumentException) {
                                    val error = ScmError("vm", e.message ?: "")
                                    return println(error.toStringForDisplay())
                                }
                            }
                    } catch (e: IllegalArgumentException) {
                        val error = ScmError("compiler", e.message ?: "")
                        return println(error.toStringForDisplay())
                    }
                }
            )
        )
    }
}
