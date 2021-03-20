/*
 * ScmBox.kt
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
import io.github.kawatab.keveskotlin.PtrBox
import io.github.kawatab.keveskotlin.PtrObject

// class ScmBox private constructor(var value: ScmObject?) : ScmObject() {
class ScmBox private constructor(var value: PtrObject) : ScmObject() {
    override fun toStringForWrite(res: KevesResources): String = "#<box ${getStringForWrite(value, res)}>"
    override fun toStringForDisplay(res: KevesResources): String = "#<box ${getStringForDisplay(value, res)}>"
    override fun toString(): String = "#<box $value>"
    fun equalQ(other: PtrObject, res: KevesResources): Boolean =
        (other.isBox(res) && this === other.toBox().toVal(res)) ||
                (other.isBox(res) && equalQ(other.toBox(), ArrayDeque(), res))

    fun equalQ(other: PtrBox, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean {
        if (duplicated.indexOfFirst { (first, second) ->
                (this == first && other.toVal(res) == second) || (this == second && other.toVal(res) == first)
            } >= 0) return true
        duplicated.addLast(this to other.toVal(res))
        val obj1 = this.value
        val obj2 = other.toVal(res).value
        if (obj1 == obj2) return true
        if (!res.isScmObject(obj1) || !res.isScmObject(obj2)) return false
        return res.equalQ(obj1, obj2, duplicated)
    }

    companion object {
        fun make(obj: PtrObject, res: KevesResources) = ScmBox(obj).let { res.addBox(it) }
    }
}