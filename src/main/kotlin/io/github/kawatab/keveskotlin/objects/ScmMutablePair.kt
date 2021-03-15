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

import io.github.kawatab.keveskotlin.KevesResources
import io.github.kawatab.keveskotlin.PtrMutablePair
import io.github.kawatab.keveskotlin.PtrObject
import io.github.kawatab.keveskotlin.PtrPairOrNull

class ScmMutablePair private constructor(car: PtrObject, cdr: PtrObject) : ScmPair(car, cdr) {
    fun assignCar(x: PtrObject) {
        car = x
    }

    fun assignCdr(x: PtrObject) {
        cdr = x
    }

    companion object {
        fun make(car: PtrObject, cdr: PtrObject, res: KevesResources) =
            ScmMutablePair(car, cdr).let { res.addMutablePair(it) }

        fun makeList(k: Int, res: KevesResources): PtrMutablePair = res.makeList(k, res.constUndef, PtrMutablePair(0))
        fun makeList(k: Int, fill: PtrObject, res: KevesResources): PtrMutablePair =
            res.makeList(k, fill, PtrMutablePair(0))

        private tailrec fun KevesResources.makeList(
            k: Int,
            fill: PtrObject,
            result: PtrMutablePair
        ): PtrMutablePair =
            if (k > 0) makeList(k - 1, fill, make(fill, PtrObject(result.ptr), this))
            else result

        fun append(list1: PtrPairOrNull, list2: PtrObject, res: KevesResources): PtrObject =
            if (list1.isNull()) list2 else res.appendHelper(reverse(list1.toPairNonNull(), res), list2)

        private tailrec fun KevesResources.appendHelper(
            reversedList: PtrPairOrNull,
            result: PtrObject
        ): PtrObject {
            val valReversedList = getPairOrNull(reversedList)
            return if (valReversedList == null) PtrObject(result.ptr)
            else appendHelper(
                PtrPairOrNull(valReversedList.cdr.ptr),
                PtrObject(make(valReversedList.car, result, this).ptr)
            )
        }

        fun listCopy(list: PtrPairOrNull, res: KevesResources): PtrPairOrNull =
            PtrPairOrNull(make(res.getPairOrNull(list)!!.car, PtrObject(0), res).also { copy ->
                res.listCopy(res.getPairOrNull(list)!!.cdr, copy, ArrayDeque())
            }.ptr)

        private tailrec fun KevesResources.listCopy(
            rest: PtrObject,
            last: PtrMutablePair,
            tracedPair: ArrayDeque<PtrObject>
        ) {
            if (rest.isPair(this)) {
                if (tracedPair.indexOf(rest) >= 0) throw IllegalArgumentException("cannot copy circulated list")
                tracedPair.addLast(rest)
                val next = make(rest.toPairOrNull().car(this), PtrObject(0), this)
                getMutablePair(last).assignCdr(PtrObject(next.ptr))
                listCopy(rest.toPairOrNull().cdr(this), PtrMutablePair(next.ptr), tracedPair)
            } else {
                getMutablePair(last).assignCdr(rest)
            }
        }

        fun reverse(list: PtrPairOrNull, res: KevesResources): PtrMutablePair {
            val valList = res.getPairOrNull(list)
            return res.reverse(
                valList!!.cdr,
                make(valList.car, PtrObject(0), res),
                ArrayDeque<PtrObject>().apply { addLast(PtrObject(list.ptr)) })
        }

        private tailrec fun KevesResources.reverse(
            rest: PtrObject,
            result: PtrMutablePair,
            tracedPair: ArrayDeque<PtrObject>
        ): PtrMutablePair =
            when {
                rest.isNull() -> result
                rest.isPair(this) -> {
                    if (tracedPair.indexOf(rest) >= 0) throw IllegalArgumentException("cannot reverse improper list")
                    tracedPair.addLast(rest)
                    reverse(
                        rest.toPairOrNull().cdr(this),
                        make(rest.toPairOrNull().car(this), result.toObject(), this),
                        tracedPair
                    )
                }
                else -> throw IllegalArgumentException("cannot reverse improper list")
            }
    }
}