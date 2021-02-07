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

class ScmVector(private val array: Array<ScmObject?>) : ScmObject() {
    constructor(size: Int) : this(Array<ScmObject?>(size) { ScmConstant.UNDEF })
    constructor(size: Int, fill: ScmObject?) : this(Array<ScmObject?>(size) { fill })

    override val type get() = ObjectType.VECTOR

    val size get() = array.size

    override fun toStringForWrite(): String = "#(${array.joinToString(" ") { getStringForWrite(it) }})"
    override fun toStringForDisplay(): String = "#(${array.joinToString(" ") { getStringForDisplay(it) }})"
    override fun toString(): String = toStringForWrite()

    override fun equalQ(other: ScmObject?): Boolean =
        if (this === other) true else (other is ScmVector && equalQ(other, ArrayDeque()))

    fun equalQ(other: ScmVector, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>): Boolean {
        if (duplicated.indexOfFirst { (first, second) -> (this == first && other == second) || (this == second && other == first) } >= 0) return true
        duplicated.addLast(this to other)
        if (this.array.size != other.array.size) return false
        for (i in this.array.indices) {
            val obj1 = this.array[i]
            val obj2 = other.array[i]
            if (obj1 === obj2) return true
            when (obj1) {
                null -> return false
                is ScmBox -> if (obj2 !is ScmBox || !obj1.equalQ(obj2, duplicated)) return false
                is ScmPair -> if (obj2 !is ScmPair || !obj1.equalQ(obj2, duplicated)) return false
                is ScmVector -> if (obj2 !is ScmVector || !obj1.equalQ(obj2, duplicated)) return false
                else -> if (!obj1.equalQ(obj2)) return false
            }
        }
        return true
    }

    fun at(i: Int): ScmObject? = array[i]

    fun set(i: Int, x: ScmObject?) {
        array[i] = x
    }

    companion object {
        fun ScmPair?.toVector(): ScmVector =
            ScmVector(ScmPair.length(this)).also { vector ->
                tailrec fun loop(rest: ScmPair?, i: Int) {
                    if (rest == null) return
                    vector.set(i, rest.car)
                    loop(rest.cdr as? ScmPair, i + 1)
                }
                loop(rest = this, i = 0)
            }
    }
}