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
import io.github.kawatab.keveskotlin.PtrObject

// class ScmBox private constructor(var value: ScmObject?) : ScmObject() {
class ScmBox private constructor(var ptr: PtrObject) : ScmObject() {
    override fun toStringForWrite(res: KevesResources): String = "#<box ${getStringForWrite(res.get(ptr), res)}>"
    override fun toStringForDisplay(res: KevesResources): String = "#<box ${getStringForDisplay(res.get(ptr), res)}>"
    override fun toString(): String = "#<box $ptr>"
    override fun equalQ(other: ScmObject?, res: KevesResources): Boolean =
        if (this === other) true else (other is ScmBox && equalQ(other, ArrayDeque(), res))

    fun equalQ(other: ScmBox, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean {
        if (duplicated.indexOfFirst { (first, second) -> (this == first && other == second) || (this == second && other == first) } >= 0) return true
        duplicated.addLast(this to other)
        val ptrObj1 = this.ptr
        val ptrObj2 = other.ptr
        if (ptrObj1 == ptrObj2) return true
        if (!res.isScmObject(ptrObj1) || !res.isScmObject(ptrObj2)) return false
        val obj1 = ptrObj1.toVal(res)
        val obj2 = ptrObj2.toVal(res)
        return when (obj1) {
            null -> obj2 == null
            is ScmBox -> obj2 is ScmBox && obj1.equalQ(obj2, duplicated, res)
            is ScmPair -> obj2 is ScmPair && obj1.equalQ(obj2, duplicated, res)
            is ScmVector -> obj2 is ScmVector && obj1.equalQ(obj2, duplicated, res)
            else -> obj1.equalQ(obj2, res)
        }
    }

    companion object {
        // fun make(obj: ScmObject?, res: KevesResources) = ScmBox(obj).let { res.add(it) }
        fun make(obj: PtrObject, res: KevesResources) = ScmBox(obj).let { res.add(it) }

        /*
        fun unbox(obj: ScmObject?): ScmObject? =
            ((obj as? ScmBox) ?: throw RuntimeException("'unbox' got non box object")).value
         */
        fun unbox(obj: PtrObject, res: KevesResources): PtrObject {
            if (res.isScmObject(obj)) {
                val box = obj.toVal(res) as? ScmBox
                if (box != null) return box.ptr
            }
            throw RuntimeException("'unbox' got non box object")
        }
    }
}