/*
 * ScmByteVector.kt
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

class ScmByteVector private constructor(var array: ByteArray) : ScmObject() {
    val size get() = array.size
    fun set(i: Int, byte: Byte) {
        array[i] = byte
    }

    fun at(i: Int): Byte = array[i]

    override fun toStringForWrite(res: KevesResources): String = "#u8(${array.joinToString(" ")})"
    override fun toStringForDisplay(res: KevesResources): String = toStringForWrite(res)
    override fun toString(): String = "#u8(${array.joinToString(" ")})"

    fun equalQ(other: PtrObject, res: KevesResources): Boolean {
        when {
            this === other.toByteVector().toVal(res) -> return true
            other.isNotByteVector(res) -> return false
            else -> {
                val otherArray = other.toByteVector().getArray(res)
                if (this.array.size != otherArray.size) return false
                for (i in this.array.indices) {
                    if (this.array[i] != otherArray[i]) return false
                }
                return true
            }
        }
    }

    companion object {
        fun make(array: ByteArray, res: KevesResources) = res.addByteVector(ScmByteVector(array))
    }
}