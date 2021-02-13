/*
 * ScmSyntax.kt
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

package io.github.kawatab.keveskotlin.objects

import io.github.kawatab.keveskotlin.KevesCompiler

abstract class ScmSyntax(private val id: String) : ScmObject() {
    override fun toStringForWrite(): String = "#<syntax $id>"
    override fun toStringForDisplay(): String = toStringForWrite()
    override fun toString(): String = toStringForWrite()

    abstract fun compile(x: ScmPair, e: ScmPair?, s: ScmPair?, next: ScmPair?, compiler: KevesCompiler): ScmPair?
    abstract fun findSets(x: ScmPair, v: ScmPair?, compiler: KevesCompiler): ScmPair?
    abstract fun findFree(x: ScmPair, b: ScmPair?, compiler: KevesCompiler): ScmPair?
}