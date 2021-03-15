/*
 * ScmPair.kt
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
import io.github.kawatab.keveskotlin.PtrPair

open class ScmPair protected constructor(car: PtrObject, cdr: PtrObject) : ScmObject() {
    var car: PtrObject = car
        protected set

    var cdr: PtrObject = cdr
        protected set

    override fun equalQ(other: PtrObject, res: KevesResources): Boolean =
        if (this === other.toVal(res)) true else (other.isPair(res) && equalQ(other.toPair(), ArrayDeque(), res))

    fun equalQ(other: PtrPair, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean {
        if (duplicated.indexOfFirst { (first, second) ->
                (this == first && other.toVal(res) == second) || (this == second && other.toVal(res) == first)
            } >= 0) return true
        duplicated.addLast(this to other.toVal(res))
        if (this.car == other.car(res)) return true
        val car1 = this.car
        val car2 = other.car(res)
        when {
            car1.isNull() -> return false
            car1.isBox(res) -> if (car2.isNotBox(res) || !car1.asBox(res)
                    .equalQ(car2.toBox(), duplicated, res)
            ) return false
            car1.isPair(res) -> if (car2.isNotPair(res) || !car1.asPair(res)
                    .equalQ(car2.toPair(), duplicated, res)
            ) return false
            car1.isVector(res) -> if (car2.isNotVector(res) || !car1.asVector(res)
                    .equalQ(car2.toVector(), duplicated, res)
            ) return false
            else -> if (!car1.toVal(res)!!.equalQ(car2, res)) return false
        }

        if (this.cdr == other.cdr(res)) return true
        val cdr1 = this.cdr
        val cdr2 = other.cdr(res)
        return when {
            car1.isNull() -> false
            car1.isBox(res) -> cdr2.isBox(res) && cdr1.asBox(res).equalQ(cdr2.toBox(), duplicated, res)
            car1.isPair(res) -> cdr2.isPair(res) && cdr1.asPair(res).equalQ(cdr2.toPair(), duplicated, res)
            car1.isVector(res) -> cdr2.isVector(res) && cdr1.asVector(res).equalQ(cdr2.toVector(), duplicated, res)
            else -> cdr1.toVal(res)!!.equalQ(cdr2, res)
        }
    }

    private fun KevesResources.searchCirculation(
        rest: ScmPair?,
        allPair: ArrayDeque<ScmPair>,
        duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>
    ) {
        if (rest != null) {
            val index = allPair.indexOf(rest)
            if (index < 0) {
                allPair.addLast(rest)
                val car = rest.car
                val cdr = rest.cdr
                searchCirculation(if (car.isPair(this)) car.asPair(this) else null, allPair, duplicatedPair)
                searchCirculation(if (cdr.isPair(this)) cdr.asPair(this) else null, allPair, duplicatedPair)
            } else if (duplicatedPair.indexOfFirst { (pair, _) -> pair == rest } < 0) {
                duplicatedPair.addLast(rest to false)
            }
        }
    }

    private fun writePair(
        pair: ScmPair,
        duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>,
        isCar: Boolean,
        res: KevesResources
    ): String =
        duplicatedPair.indexOfFirst { (duplicated, _) -> duplicated == pair }
            .let {
                when {
                    it < 0 -> if (isCar) "(${
                        pair.writeInner(
                            duplicatedPair,
                            res
                        )
                    })" else pair.writeInner(duplicatedPair, res)
                    duplicatedPair[it].second -> if (isCar) "#$it#" else ". #$it#"
                    else -> {
                        duplicatedPair[it] = duplicatedPair[it].first to true
                        "${if (isCar) "" else ". "}#$it=(${pair.writeInner(duplicatedPair, res)})"
                    }
                }
            }

    private fun displayPair(
        pair: ScmPair,
        duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>,
        isCar: Boolean,
        res: KevesResources
    ): String =
        duplicatedPair.indexOfFirst { (duplicated, _) -> duplicated == pair }
            .let {
                when {
                    it < 0 -> if (isCar) "(${pair.displayInner(duplicatedPair, res)})" else pair.displayInner(
                        duplicatedPair,
                        res
                    )
                    duplicatedPair[it].second -> if (isCar) "#$it#" else ". #$it#"
                    else -> {
                        duplicatedPair[it] = duplicatedPair[it].first to true
                        "${if (isCar) "" else ". "}#$it=(${pair.displayInner(duplicatedPair, res)})"
                    }
                }
            }


    override fun toStringForWrite(res: KevesResources): String {
        val allPair = ArrayDeque<ScmPair>()
        val duplicatedPair = ArrayDeque<Pair<ScmPair, Boolean>>()
        res.searchCirculation(this, allPair, duplicatedPair)
        val carStr =
            let {
                if (car.isPair(res)) writePair(car.asPair(res), duplicatedPair, true, res)
                else getStringForWrite(car.toVal(res), res)
            }
        return when {
            cdr.isNull() -> "($carStr)"
            cdr.isPair(res) -> {
                val prefix = duplicatedPair.indexOfFirst { (pair, _) -> pair == this }.let {
                    if (it < 0) {
                        ""
                    } else {
                        duplicatedPair[it] = duplicatedPair[it].first to true
                        "#$it="
                    }
                }
                val cdrStr = writePair(cdr.asPair(res), duplicatedPair, false, res)
                "$prefix($carStr $cdrStr)"
            }
            else -> "($carStr . ${cdr.toVal(res)!!.toStringForWrite(res)})"
        }
    }

    private fun writeInner(duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>, res: KevesResources): String {
        val carStr =
            if (car.isPair(res)) writePair(car.asPair(res), duplicatedPair, true, res)
            else getStringForWrite(car.toVal(res), res)
        return when {
            cdr.isNull() -> carStr
            cdr.isPair(res) -> {
                val cdrStr = writePair(cdr.asPair(res), duplicatedPair, false, res)
                "$carStr $cdrStr"
            }
            else -> "$carStr . ${cdr.toVal(res)!!.toStringForWrite(res)}"
        }
    }

    override fun toStringForDisplay(res: KevesResources): String {
        val allPair = ArrayDeque<ScmPair>()
        val duplicatedPair = ArrayDeque<Pair<ScmPair, Boolean>>()
        res.searchCirculation(this, allPair, duplicatedPair)
        val carStr =
            if (car.isPair(res)) displayPair(car.asPair(res), duplicatedPair, true, res)
            else getStringForDisplay(car.toVal(res), res)
        return when {
            cdr.isNull() -> "($carStr)"
            cdr.isPair(res) -> {
                val prefix = duplicatedPair.indexOfFirst { (pair, _) -> pair == this }.let {
                    if (it < 0) {
                        ""
                    } else {
                        duplicatedPair[it] = duplicatedPair[it].first to true
                        "#$it="
                    }
                }
                val cdrStr = displayPair(cdr.asPair(res), duplicatedPair, false, res)
                "$prefix($carStr $cdrStr)"
            }
            else -> "($carStr . ${cdr.toVal(res)!!.toStringForDisplay(res)})"
        }
    }

    private fun displayInner(duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>, res: KevesResources): String {
        val carStr =
            if (car.isPair(res)) displayPair(car.asPair(res), duplicatedPair, true, res)
            else getStringForDisplay(car.toVal(res), res)
        return when {
            cdr.isNull() -> carStr
            cdr.isPair(res) -> {
                val cdrStr = displayPair(cdr.asPair(res), duplicatedPair, false, res)
                "$carStr $cdrStr"
            }
            else -> "$carStr . ${cdr.toVal(res)!!.toStringForDisplay(res)}"
        }
    }

    companion object {
        fun make(car: PtrObject, cdr: PtrObject, res: KevesResources) = res.addPair(ScmPair(car, cdr))

        fun length(list: PtrObject, res: KevesResources): Int = res.length(list, 0, ArrayDeque())

        private tailrec fun KevesResources.length(
            rest: PtrObject,
            n: Int,
            tracedPair: ArrayDeque<ScmObject>
        ): Int = when {
            rest.isNull() -> n
            rest.isPair(this) -> {
                if (tracedPair.indexOf(rest.toVal(this)) > 0) throw IllegalArgumentException("cannot get the length of improper list")
                tracedPair.addLast(rest.toVal(this)!!)
                length(rest = rest.toPairOrNull().cdr(this), n = n + 1, tracedPair = tracedPair)
            }
            else -> throw IllegalArgumentException("cannot get the length of improper list")
        }

        fun isProperList(obj: PtrObject, res: KevesResources): Boolean =
            when {
                obj.isNull() -> true
                obj.isPair(res) -> isProperList(
                    obj.toPair(),
                    ArrayDeque<ScmPair>().apply { addLast(obj.asPair(res)) },
                    res
                )
                else -> false
            }

        private tailrec fun isProperList(pair: PtrPair, tracedPair: ArrayDeque<ScmPair>, res: KevesResources): Boolean {
            val cdr = pair.cdr(res)
            return when {
                cdr.isNull() -> true
                cdr.isPair(res) -> {
                    if (tracedPair.indexOf(cdr.asPair(res)) >= 0) {
                        false
                    } else {
                        tracedPair.addLast(cdr.asPair(res))
                        isProperList(cdr.toPair(), tracedPair, res)
                    }
                }
                else -> false
            }
        }

        fun toProperList(list: PtrObject, res: KevesResources): Pair<PtrPairOrNull, Int> =
            res.toProperList(list, PtrPairOrNull(0), 0)

        private tailrec fun KevesResources.toProperList(
            rest: PtrObject,
            result: PtrPairOrNull,
            n: Int
        ): Pair<PtrPairOrNull, Int> =
            when {
                rest.isNull() -> (if (result.isNotNull()) reverse(
                    result.toPairNonNull(),
                    this
                ) else PtrPairOrNull(0)) to n
                rest.isPair(this) -> toProperList(
                    rest = rest.toPair().cdr(this),
                    result = make(rest.toPair().car(this), result.toObject(), this),
                    n = n + 1
                )
                else -> reverse(make(rest, result.toObject(), this).toPairNonNull(), this) to -(n + 1)
            }

        fun reverse(list: PtrPair, res: KevesResources): PtrPairOrNull {
            return res.reverse(
                list.cdr(res),
                make(list.car(res), PtrObject(0), res),
                ArrayDeque<PtrObject>().apply { addLast(list.toObject()) })
        }

        private tailrec fun KevesResources.reverse(
            rest: PtrObject,
            result: PtrPairOrNull,
            tracedPair: ArrayDeque<PtrObject>
        ): PtrPairOrNull = when {
            rest.isNull() -> result
            rest.isPair(this) -> {
                if (tracedPair.indexOf(rest) >= 0) throw IllegalArgumentException("cannot reverse improper list")
                tracedPair.addLast(rest)
                reverse(
                    rest.toPair().cdr(this),
                    ScmMutablePair.make(rest.toPair().car(this), result.toObject(), this).toPair(),
                    tracedPair
                )
            }
            else -> throw IllegalArgumentException("cannot reverse improper list")
        }

        tailrec fun listTail(list: PtrPairOrNull, k: Int, res: KevesResources): PtrPairOrNull =
            if (k > 0) {
                if (list.isNull()) throw IllegalArgumentException("length not enough")
                else listTail(
                    list.cdr(res)
                        .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("not proper list") }
                        .toPairOrNull(),
                    k - 1,
                    res
                )
            } else {
                list
            }

        fun memq(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject = memq(obj, list, ArrayDeque(), res)

        private tailrec fun memq(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject = when {
            list.isNull() -> res.constFalse
            list.isPair(res) -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                if (list.toPair().car(res) == obj) list else memq(obj, list.toPair().cdr(res), tracedPair, res)
            }
            else -> throw IllegalArgumentException("not proper list")
        }

        fun memv(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject = memv(obj, list, ArrayDeque(), res)

        private tailrec fun memv(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject = when {
            list.isNull() -> res.constFalse
            list.isPair(res) -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                if (eqvQ(list.toPair().car(res), obj, res)) list else memv(obj, list.toPair().cdr(res), tracedPair, res)
            }
            else -> throw IllegalArgumentException("not proper list")
        }

        fun member(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject =
            member(obj, list, ArrayDeque(), res)

        private tailrec fun member(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject = when {
            list.isNull() -> res.constFalse
            list.isPair(res) -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                if (equalQ(list.toPair().car(res), obj, res)) list else member(
                    obj,
                    list.toPair().cdr(res),
                    tracedPair,
                    res
                )
            }
            else -> throw IllegalArgumentException("not proper list")
        }

        fun assq(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject = assq(obj, list, ArrayDeque(), res)

        private tailrec fun assq(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject = when {
            list.isNull() -> res.constFalse
            list.isPair(res) -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                val car = list.toPair().car(res)
                if (car.isNotPair(res)) throw IllegalArgumentException("not association list")
                if (car.toPair().car(res) == obj) car else assq(obj, list.toPair().cdr(res), tracedPair, res)
            }
            else -> throw IllegalArgumentException("not proper list")
        }

        fun assv(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject = assv(obj, list, ArrayDeque(), res)

        private tailrec fun assv(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject =
            when {
                list.isNull() -> res.constFalse
                list.isPair(res) -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    val car = list.toPair().car(res)
                    val valCar = car.toVal(res)
                    if (car.isNotPair(res)) throw IllegalArgumentException("not association list")
                    if (eqvQ(car.toPair().car(res), obj, res)) car else assv(
                        obj,
                        list.toPair().cdr(res),
                        tracedPair,
                        res
                    )
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        fun assoc(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject = assoc(obj, list, ArrayDeque(), res)

        private tailrec fun assoc(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject = when {
            list.isNull() -> res.constFalse
            list.isPair(res) -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                val car = list.toPair().car(res)
                when {
                    car.isNotPair(res) -> throw IllegalArgumentException("not association list")
                    equalQ(car.toPair().car(res), obj, res) -> car
                    else -> assoc(obj, list.toPair().cdr(res), tracedPair, res)
                }
            }
            else -> throw IllegalArgumentException("not proper list")
        }

        @Suppress("unused")
        fun car(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                obj.asPair(res).car
            } catch (e: TypeCastException) {
                throw IllegalArgumentException("'car' required pair, but got other")
            }

        @Suppress("unused")
        fun cdr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                obj.asPair(res).cdr
            } catch (e: TypeCastException) {
                throw IllegalArgumentException("'cdr' required pair, but got other")
            }

        @Suppress("unused")
        fun caar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(car(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cdr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(car(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cdr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddr' required pair, but got other")
            }

        @Suppress("unused")
        fun caaar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(caar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaar' required pair, but got other")
            }

        @Suppress("unused")
        fun caadr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cadr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cadar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cdar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadar' required pair, but got other")
            }

        @Suppress("unused")
        fun caddr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cddr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(caar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdadr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cadr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cddar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cdar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdddr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cddr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdddr' required pair, but got other")
            }

        @Suppress("unused")
        fun caaaar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(caaar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaaar' required pair, but got other")
            }

        @Suppress("unused")
        fun caaadr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(caadr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaadr' required pair, but got other")
            }

        @Suppress("unused")
        fun caadar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cadar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caadar' required pair, but got other")
            }

        @Suppress("unused")
        fun caaddr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(caddr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cadaar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cdaar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadadr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cdadr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadadr' required pair, but got other")
            }

        @Suppress("unused")
        fun caddar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cddar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadddr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                car(cdddr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaaar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(caaar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaadr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(caadr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdadar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cadar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdadar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaddr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(caddr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cddaar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cdaar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddadr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cdadr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdddar(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cddar(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddddr(obj: PtrObject, res: KevesResources): PtrObject =
            try {
                cdr(cdddr(obj, res), res)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddddr' required pair, but got other")
            }

        fun list(e: PtrObject, res: KevesResources) = make(e, PtrObject(0), res)
        fun list(e1: PtrObject, e2: PtrObject, res: KevesResources) = make(e1, PtrObject(list(e2, res).ptr), res)
        fun list(e1: PtrObject, e2: PtrObject, e3: PtrObject, res: KevesResources) =
            make(e1, list(e2, e3, res).toObject(), res)

        fun list(e1: PtrObject, e2: PtrObject, e3: PtrObject, e4: PtrObject, res: KevesResources) =
            make(e1, list(e2, e3, e4, res).toObject(), res)

        @Suppress("unused")
        fun list(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            res: KevesResources
        ) = make(e1, list(e2, e3, e4, e5, res).toObject(), res)

        @Suppress("unused")
        fun list(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            res: KevesResources
        ) = make(e1, list(e2, e3, e4, e5, e6, res).toObject(), res)

        @Suppress("unused")
        fun list(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            res: KevesResources
        ) = make(e1, list(e2, e3, e4, e5, e6, e7, res).toObject(), res)

        @Suppress("unused")
        fun list(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            e8: PtrObject,
            res: KevesResources
        ) = make(e1, list(e2, e3, e4, e5, e6, e7, e8, res).toObject(), res)

        @Suppress("unused")
        fun list(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            e8: PtrObject,
            e9: PtrObject,
            res: KevesResources
        ) = make(e1, list(e2, e3, e4, e5, e6, e7, e8, e9, res).toObject(), res)

        @Suppress("unused")
        fun list(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            e8: PtrObject,
            e9: PtrObject,
            e10: PtrObject,
            res: KevesResources
        ) = make(e1, list(e2, e3, e4, e5, e6, e7, e8, e9, e10, res).toObject(), res)

        @Suppress("unused")
        fun listStar(e: PtrObject) = e

        @Suppress("unused")
        fun listStar(e1: PtrObject, e2: PtrObject, res: KevesResources) = make(e1, e2, res)
        fun listStar(e1: PtrObject, e2: PtrObject, e3: PtrObject, res: KevesResources) =
            make(e1, listStar(e2, e3, res).toObject(), res)

        @Suppress("unused")
        fun listStar(e1: PtrObject, e2: PtrObject, e3: PtrObject, e4: PtrObject, res: KevesResources) =
            make(e1, listStar(e2, e3, e4, res).toObject(), res)

        @Suppress("unused")
        fun listStar(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            res: KevesResources
        ) = make(e1, listStar(e2, e3, e4, e5, res).toObject(), res)

        @Suppress("unused")
        fun listStar(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            res: KevesResources
        ) = make(e1, listStar(e2, e3, e4, e5, e6, res).toObject(), res)

        @Suppress("unused")
        fun listStar(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            res: KevesResources
        ) = make(e1, listStar(e2, e3, e4, e5, e6, e7, res).toObject(), res)

        @Suppress("unused")
        fun listStar(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            e8: PtrObject,
            res: KevesResources
        ) = make(e1, listStar(e2, e3, e4, e5, e6, e7, e8, res).toObject(), res)

        @Suppress("unused")
        fun listStar(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            e8: PtrObject,
            e9: PtrObject,
            res: KevesResources
        ) = make(e1, listStar(e2, e3, e4, e5, e6, e7, e8, e9, res).toObject(), res)

        @Suppress("unused")
        fun listStar(
            e1: PtrObject,
            e2: PtrObject,
            e3: PtrObject,
            e4: PtrObject,
            e5: PtrObject,
            e6: PtrObject,
            e7: PtrObject,
            e8: PtrObject,
            e9: PtrObject,
            e10: PtrObject,
            res: KevesResources
        ) = make(e1, listStar(e2, e3, e4, e5, e6, e7, e8, e9, e10, res).toObject(), res)
    }
}