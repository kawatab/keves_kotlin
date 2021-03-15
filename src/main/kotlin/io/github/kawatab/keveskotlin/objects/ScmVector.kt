/*
 * ScmVector.kt
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

/*
class ScmVector private constructor(private val array: Array<ScmObject?>) : ScmObject() {
    constructor(size: Int) : this(Array<ScmObject?>(size) { ScmConstant.UNDEF })
    constructor(size: Int, fill: ScmObject?) : this(Array<ScmObject?>(size) { fill })
 */
class ScmVector private constructor(private val array: IntArray) : ScmObject() {
    constructor(size: Int, res: KevesResources) : this(IntArray(size) { res.constUndef.ptr })
    constructor(size: Int, fill: PtrObject) : this(IntArray(size) { fill.ptr })

    val size get() = array.size

    override fun toStringForWrite(res: KevesResources): String =
        "#(${array.joinToString(" ") { getStringForWrite(PtrObject(it).toVal(res), res) }})"

    override fun toStringForDisplay(res: KevesResources): String =
        "#(${array.joinToString(" ") { getStringForDisplay(PtrObject(it).toVal(res), res) }})"

    override fun toString(): String = "#()"

    override fun equalQ(other: ScmObject?, res: KevesResources): Boolean =
        if (this === other) true else (other is ScmVector && equalQ(other, ArrayDeque(), res))

    fun equalQ(other: ScmVector, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean {
        if (duplicated.indexOfFirst { (first, second) -> (this == first && other == second) || (this == second && other == first) } >= 0) return true
        duplicated.addLast(this to other)
        if (this.array.size != other.array.size) return false
        for (i in this.array.indices) {
            val ptr1 = this.at(i)
            val ptr2 = other.at(i)
            if (ptr1 == ptr2) return true
            if (!res.isScmObject(ptr1) || !res.isScmObject(ptr2)) return false
            val obj1 = ptr1.toVal(res)
            val obj2 = ptr2.toVal(res)
            when (obj1) {
                null -> obj1 == obj2
                is ScmBox -> if (obj2 !is ScmBox || !obj1.equalQ(obj2, duplicated, res)) return false
                is ScmPair -> if (obj2 !is ScmPair || !obj1.equalQ(obj2, duplicated, res)) return false
                is ScmVector -> if (obj2 !is ScmVector || !obj1.equalQ(obj2, duplicated, res)) return false
                else -> if (!obj1.equalQ(obj2, res)) return false
            }
        }
        return true
    }

    fun at(i: Int): PtrObject = PtrObject(array[i])

    fun set(i: Int, pointer: PtrObject) {
        array[i] = pointer.ptr
    }

    companion object {
        fun make(array: IntArray, res: KevesResources) = ScmVector(array).let { res.addVector(it) }
        fun make(size: Int, res: KevesResources) = ScmVector(size, res).let { res.addVector(it) }

        fun make(size: Int, fill: PtrObject, res: KevesResources) = ScmVector(size, fill).let { res.addVector(it) }

        fun ScmPair?.toVector(res: KevesResources) =
            make(ScmPair.length(this, res), res).also { vector ->
                tailrec fun loop(rest: ScmPair?, i: Int) {
                    if (rest == null) return
                    vector.toVal(res).set(i, rest.car)
                    loop(rest.cdr.asPairOrNull(res), i + 1)
                }
                loop(rest = this, i = 0)
            }
    }
}