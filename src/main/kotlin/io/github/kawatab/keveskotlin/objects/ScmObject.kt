/*
 * ScmObject.kt
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

abstract class ScmObject {
    abstract fun toStringForWrite(res: KevesResources): String
    abstract fun toStringForDisplay(res: KevesResources): String
    open fun eqvQ(other: ScmObject?): Boolean = this === other
    open fun equalQ(other: ScmObject?, res: KevesResources): Boolean = this === other

    open fun add(other: ScmObject, res: KevesResources): PtrObject = throw IllegalArgumentException("not number")
    open fun subtract(other: ScmObject, res: KevesResources): PtrObject = throw IllegalArgumentException("not number")
    open fun isLessThan(other: ScmObject): Boolean = throw IllegalArgumentException("not number")

    companion object {
        fun getStringForWrite(obj: ScmObject?, res: KevesResources): String = obj?.toStringForWrite(res) ?: "()"
        fun getStringForDisplay(obj: ScmObject?, res: KevesResources): String = obj?.toStringForDisplay(res) ?: "()"
        fun eqvQ(obj1: ScmObject?, obj2: ScmObject?): Boolean = obj1?.eqvQ(obj2) ?: (obj2 == null)
        fun equalQ(obj1: ScmObject?, obj2: ScmObject?, res: KevesResources): Boolean = obj1?.equalQ(obj2, res) ?: (obj2 == null)
    }
}