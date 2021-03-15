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
import io.github.kawatab.keveskotlin.PtrPairOrNull
import io.github.kawatab.keveskotlin.PtrVector

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

    override fun equalQ(other: PtrObject, res: KevesResources): Boolean =
        if (this === other.toVal(res)) true else (other.isVector(res) && equalQ(other.toVector(), ArrayDeque(), res))

    fun equalQ(other: PtrVector, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean {
        if (duplicated.indexOfFirst { (first, second) -> (this == first && other.toVal(res) == second) || (this == second && other.toVal(res) == first) } >= 0) return true
        duplicated.addLast(this to other.toVal(res))
        if (this.array.size != other.toVal(res).array.size) return false
        for (i in this.array.indices) {
            val obj1 = this.at(i)
            val obj2 = other.toVal(res).at(i)
            if (obj1 == obj2) return true
            if (!res.isScmObject(obj1) || !res.isScmObject(obj2)) return false
            when {
                obj1.isNull() -> obj2.isNull()
                obj1.isBox(res) -> if (obj2.isBox(res) || !obj1.asBox(res).equalQ(obj2.toBox(), duplicated, res)) return false
                obj1.isPair(res) -> if (obj2.isPair(res) || !obj1.asPair(res).equalQ(obj2.toPair(), duplicated, res)) return false
                obj1.isVector(res) -> if (obj2.isVector(res) || !obj1.asVector(res).equalQ(obj2.toVector(), duplicated, res)) return false
                else -> if (!obj1.toVal(res)!!.equalQ(obj2, res)) return false
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

        fun PtrPairOrNull.toVector(res: KevesResources) =
            make(ScmPair.length(this.toObject(), res), res).also { vector ->
                tailrec fun loop(rest: ScmPair?, i: Int) {
                    if (rest == null) return
                    vector.toVal(res).set(i, rest.car)
                    loop(rest.cdr.asPairOrNull(res), i + 1)
                }
                loop(rest = this.toVal(res), i = 0)
            }
    }
}