/*
 * ScmInt.kt
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

import io.github.kawatab.keveskotlin.KevesResources
import io.github.kawatab.keveskotlin.PtrObject

class ScmInt private constructor(val value: Int) : ScmObject() {
    override fun toStringForWrite(res: KevesResources): String = toString()
    override fun toStringForDisplay(res: KevesResources): String = toString()
    override fun toString(): String = value.toString()
    override fun eqvQ(other: ScmObject?): Boolean = other is ScmInt && this.value == other.value
    override fun equalQ(other: ScmObject?, res: KevesResources): Boolean = eqvQ(other)


    override fun add(other: ScmObject, res: KevesResources): PtrObject =
        when (other) {
            is ScmInt -> make(this.value + other.value, res)
            is ScmFloat -> ScmFloat.make(this.value + other.value, res)
            is ScmDouble -> ScmDouble.make(this.value + other.value, res)
            else -> throw IllegalArgumentException("not number")
        }

    override fun subtract(other: ScmObject, res: KevesResources): PtrObject =
        when (other) {
            is ScmInt -> make(this.value - other.value, res)
            is ScmFloat -> ScmFloat.make(this.value - other.value, res)
            is ScmDouble -> ScmDouble.make(this.value - other.value, res)
            else -> throw IllegalArgumentException("not number")
        }

    override fun isLessThan(other: ScmObject): Boolean =
        when (other) {
            is ScmInt -> this.value < other.value
            is ScmFloat -> this.value < other.value
            is ScmDouble -> this.value < other.value
            else -> throw IllegalArgumentException("not number")
        }

    companion object {
        fun make(value: Int, res: KevesResources) = ScmInt(value).let { res.add(it) }
    }
}