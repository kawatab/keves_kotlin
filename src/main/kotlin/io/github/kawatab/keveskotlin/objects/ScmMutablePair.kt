/*
 * ScmMutablePair.kt
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

class ScmMutablePair(car: ScmObject?, cdr: ScmObject?) : ScmPair(car, cdr) {
    fun assignCar(x: ScmObject?) {
        car = x
    }

    fun assignCdr(x: ScmObject?) {
        cdr = x
    }

    companion object {
        fun makeList(k: Int): ScmMutablePair? = makeList(k, ScmConstant.UNDEF, null)
        fun makeList(k: Int, fill: ScmObject?): ScmMutablePair? = makeList(k, fill, null)

        private tailrec fun makeList(k: Int, fill: ScmObject?, result: ScmMutablePair?): ScmMutablePair? =
            if (k > 0) makeList(k - 1, fill, ScmMutablePair(fill, result))
            else result

        fun append(list1: ScmPair?, list2: ScmObject?): ScmObject? = appendHelper(list1?.let { reverse(it) }, list2)

        private tailrec fun appendHelper(reversedList: ScmPair?, result: ScmObject?): ScmObject? =
            if (reversedList == null) result
            else appendHelper(reversedList.cdr as? ScmPair, ScmMutablePair(reversedList.car, result))

        fun listCopy(list: ScmPair): ScmPair = ScmMutablePair(list.car, null).also { copy ->
            listCopy(list.cdr, copy, ArrayDeque())
        }

        private tailrec fun listCopy(rest: ScmObject?, last: ScmMutablePair, tracedPair: ArrayDeque<ScmPair>) {
            if (rest is ScmPair) {
                if (tracedPair.indexOf(rest) >= 0) throw IllegalArgumentException("cannot copy circulated list")
                tracedPair.addLast(rest)
                val next = ScmMutablePair(rest.car, null)
                last.assignCdr(next)
                listCopy(rest.cdr, next, tracedPair)
            } else {
                last.assignCdr(rest)
            }
        }

        fun reverse(list: ScmPair): ScmPair? =
            reverse(list.cdr, ScmMutablePair(list.car, null), ArrayDeque<ScmPair>().apply { addLast(list) })

        private tailrec fun reverse(rest: ScmObject?, result: ScmPair?, tracedPair: ArrayDeque<ScmPair>): ScmPair? =
            when (rest) {
                null -> result
                is ScmPair -> {
                    if (tracedPair.indexOf(rest) >= 0) throw IllegalArgumentException("cannot reverse improper list")
                    tracedPair.addLast(rest)
                    reverse(rest.cdr, ScmMutablePair(rest.car, result), tracedPair)
                }
                else -> throw IllegalArgumentException("cannot reverse improper list")
            }
    }
}