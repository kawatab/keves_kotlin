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

class ScmBox(var value: ScmObject?) : ScmObject() {
    override val type get() = ObjectType.BOX

    override fun toStringForWrite(): String = "#<box ${getStringForWrite(value)}>"
    override fun toStringForDisplay(): String = "#<box ${getStringForDisplay(value)}>"
    override fun toString(): String = toStringForWrite()
    override fun equalQ(other: ScmObject?): Boolean =
        if (this === other) true else (other is ScmBox && equalQ(other, ArrayDeque()))

    fun equalQ(other: ScmBox, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>): Boolean {
        if (duplicated.indexOfFirst { (first, second) -> (this == first && other == second) || (this == second && other == first) } >= 0) return true
        duplicated.addLast(this to other)
        val obj1 = this.value
        val obj2 = other.value
        if (obj1 === obj2) return true
        return when (obj1) {
            null -> false
            is ScmBox -> obj2 is ScmBox && obj1.equalQ(obj2, duplicated)
            is ScmPair -> obj2 is ScmPair && obj1.equalQ(obj2, duplicated)
            is ScmVector -> obj2 is ScmVector && obj1.equalQ(obj2, duplicated)
            else -> obj1.equalQ(obj2)
        }
    }

    companion object {
        fun unbox(obj: ScmObject?): ScmObject? =
            ((obj as? ScmBox) ?: throw IllegalArgumentException("'unbox' got non box object")).value
    }
}