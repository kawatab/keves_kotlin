/*
 * KevesStack.kt
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

package io.github.kawatab.keveskotlin

import io.github.kawatab.keveskotlin.objects.ScmPair
import io.github.kawatab.keveskotlin.objects.ScmVector

class KevesStack(private val res: KevesResources) {
    private val array = IntArray(1000) { 0 }

    fun push(x: PtrObject, s: Int): Int {
        array[s] = x.ptr
        return s + 1
    }
    /*
    fun push(x: ScmObject?, s: Int): Int {
        array[s] = x
        return s + 1
    }
     */

    /**
     * Takes a stack pointer and an index, and return the object found at the specified
     * offset from the stack pointer
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 75
     */
    // fun index(n: Int, i: Int): ScmObject? = array[n - i - 1]
    fun index(n: Int, i: Int) = PtrObject(array[n - i - 1])

    /**
     * Takes a stack pointer, an index, and an object, and places the object at the specified
     * offset from the stack pointer
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 75
     */
    /*
    fun indexSetE(s: Int, i: Int, v: ScmObject?) {
        array[s - i - 1] = v
    }
     */
    fun indexSetE(s: Int, i: Int, pointer: PtrObject) {
        array[s - i - 1] = pointer.ptr
    }

    /**
     * Creates a Scheme vector to hold the stack
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 83
     */
    fun saveStack(s: Int): ScmVector =
        ScmVector.make(s, res).toVal(res).also { v ->
            tailrec fun copy(i: Int) {
                if (i == s) return
                v.set(i, PtrObject(array[i]))
                return copy(i = i + 1)
            }
            copy(0)
        }

    /**
     * Restores the stack from a Scheme vector
     * Cf. R. Kent Dybvig, "Three Implementation Models for Scheme", PhD thesis,
     * University of North Carolina at Chapel Hill, TR87-011, (1987), pp. 83
     */
    fun restoreStack(v: ScmVector): Int =
        v.size.also { s ->
            tailrec fun copy(i: Int) {
                if (i == s) return
                array[i] = v.at(i).ptr
                return copy(i = i + 1)
            }
            copy(0)
        }

    /** Creates an array from stack for closure */
    // fun makeArray(s: Int, n: Int): Array<ScmObject?> = array.copyOfRange(s - n, s)
    fun makeArray(s: Int, n: Int): IntArray = array.copyOfRange(s - n, s)

    /** Shrinks arguments for lambda that accepts variable length of arguments */
    fun shrinkArgs(sp: Int, n: Int, shift: Int) {
        val start = sp - n
        tailrec fun loop1(i: Int, result: PtrObject): PtrObject =
            if (i < 0) result
            else loop1(i - 1, ScmPair.make(PtrObject(array[start + shift - i]), result, res).toObject())
        array[start] = loop1(shift, PtrObject(0)).ptr
        for (i in start + 1 until sp - shift) {
            array[i] = array[i + shift]
        }
    }

    /** Pushes an argument for lambda that accepts variable length of arguments */
    fun addNullAtEndOfArgs(sp: Int, n: Int) {
        val start = sp - n
        for (i in sp downTo start) {
            array[i + 1] = array[i]
        }
        // array[start] = null
        array[start] = 0
    }
}