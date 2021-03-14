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
import io.github.kawatab.keveskotlin.PtrPair
import io.github.kawatab.keveskotlin.PtrPairNonNull

open class ScmPair protected constructor(car: PtrObject, cdr: PtrObject) : ScmObject() {
    var car: PtrObject = car
        protected set

    var cdr: PtrObject = cdr
        protected set

    override fun equalQ(other: ScmObject?, res: KevesResources): Boolean =
        if (this === other) true else (other is ScmPair && equalQ(other, ArrayDeque(), res))

    fun equalQ(other: ScmPair, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean {
        if (duplicated.indexOfFirst { (first, second) -> (this == first && other == second) || (this == second && other == first) } >= 0) return true
        duplicated.addLast(this to other)
        if (this.car == other.car) return true
        val car2 = other.car.toVal(res)
        when (val ptr1 = this.car.toVal(res)) {
            null -> return false
            is ScmBox -> if (car2 !is ScmBox || !ptr1.equalQ(car2, duplicated, res)) return false
            is ScmPair -> if (car2 !is ScmPair || !ptr1.equalQ(car2, duplicated, res)) return false
            is ScmVector -> if (car2 !is ScmVector || !ptr1.equalQ(car2, duplicated, res)) return false
            else -> if (!ptr1.equalQ(car2, res)) return false
        }

        if (this.cdr == other.cdr) return true
        val cdr2 = other.cdr.toVal(res)
        return when (val cdr1 = this.cdr.toVal(res)) {
            null -> false
            is ScmBox -> cdr2 is ScmBox && cdr1.equalQ(cdr2, duplicated, res)
            is ScmPair -> cdr2 is ScmPair && cdr1.equalQ(cdr2, duplicated, res)
            is ScmVector -> cdr2 is ScmVector && cdr1.equalQ(cdr2, duplicated, res)
            else -> cdr1.equalQ(cdr2, res)
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
                searchCirculation(car.toVal(this) as? ScmPair, allPair, duplicatedPair)
                searchCirculation(cdr.toVal(this) as? ScmPair, allPair, duplicatedPair)
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
            car.toVal(res).let { car ->
                if (car is ScmPair) writePair(car, duplicatedPair, true, res)
                else getStringForWrite(car, res)
            }
        return cdr.toVal(res).let { cdr ->
            when (cdr) {
                null -> "($carStr)"
                is ScmPair -> {
                    val prefix = duplicatedPair.indexOfFirst { (pair, _) -> pair == this }.let {
                        if (it < 0) {
                            ""
                        } else {
                            duplicatedPair[it] = duplicatedPair[it].first to true
                            "#$it="
                        }
                    }
                    val cdrStr = writePair(cdr, duplicatedPair, false, res)
                    "$prefix($carStr $cdrStr)"
                }
                else -> "($carStr . ${cdr.toStringForWrite(res)})"
            }
        }
    }

    private fun writeInner(duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>, res: KevesResources): String {
        val carStr =
            car.toVal(res).let { car ->
                if (car is ScmPair) writePair(car, duplicatedPair, true, res)
                else getStringForWrite(car, res)
            }
        return cdr.toVal(res).let { cdr ->
            when (cdr) {
                null -> carStr
                is ScmPair -> {
                    val cdrStr = writePair(cdr, duplicatedPair, false, res)
                    "$carStr $cdrStr"
                }
                else -> "$carStr . ${cdr.toStringForWrite(res)}"
            }
        }
    }

    override fun toStringForDisplay(res: KevesResources): String {
        val allPair = ArrayDeque<ScmPair>()
        val duplicatedPair = ArrayDeque<Pair<ScmPair, Boolean>>()
        res.searchCirculation(this, allPair, duplicatedPair)
        val carStr = car.toVal(res).let { car ->
            if (car is ScmPair) displayPair(car, duplicatedPair, true, res)
            else getStringForDisplay(car, res)
        }
        return cdr.toVal(res).let { cdr ->
            when (cdr) {
                null -> "($carStr)"
                is ScmPair -> {
                    val prefix = duplicatedPair.indexOfFirst { (pair, _) -> pair == this }.let {
                        if (it < 0) {
                            ""
                        } else {
                            duplicatedPair[it] = duplicatedPair[it].first to true
                            "#$it="
                        }
                    }
                    val cdrStr = displayPair(cdr, duplicatedPair, false, res)
                    "$prefix($carStr $cdrStr)"
                }
                else -> "($carStr . ${cdr.toStringForDisplay(res)})"
            }
        }
    }

    private fun displayInner(duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>, res: KevesResources): String {
        val carStr = car.toVal(res).let { car ->
            if (car is ScmPair) displayPair(car, duplicatedPair, true, res)
            else getStringForDisplay(car, res)
        }
        return cdr.toVal(res).let { cdr ->
            when (cdr) {
                null -> carStr
                is ScmPair -> {
                    val cdrStr = displayPair(cdr, duplicatedPair, false, res)
                    "$carStr $cdrStr"
                }
                else -> "$carStr . ${cdr.toStringForDisplay(res)}"
            }
        }
    }

    fun ref2and3(res: KevesResources): Pair<PtrObject, PtrObject> {
        val cdr = this.cdr.toVal(res) as? ScmPair ?: throw IllegalArgumentException("cdr was not pair")
        val cddr = cdr.cdr.toVal(res) as? ScmPair ?: throw IllegalArgumentException("cddr was not pair")
        val obj1 = cdr.car
        val obj2 = cddr.car
        return obj1 to obj2
    }

    companion object {
        fun make(car: PtrObject, cdr: PtrObject, res: KevesResources) = res.addPair(ScmPair(car, cdr))

        fun length(list: ScmObject?, res: KevesResources): Int = res.length(list, 0, ArrayDeque())

        private tailrec fun KevesResources.length(
            rest: ScmObject?,
            n: Int,
            tracedPair: ArrayDeque<ScmObject>
        ): Int = when (rest) {
            null -> n
            is ScmPair -> {
                if (tracedPair.indexOf(rest) > 0) throw IllegalArgumentException("cannot get the length of improper list")
                tracedPair.addLast(rest)
                length(rest = rest.cdr.toVal(this), n = n + 1, tracedPair = tracedPair)
            }
            else -> throw IllegalArgumentException("cannot get the length of improper list")
        }

        fun isPair(obj: ScmObject?): Boolean = obj is ScmPair

        fun isProperList(obj: ScmObject?, res: KevesResources): Boolean =
            when (obj) {
                null -> true
                is ScmPair -> isProperList(obj, ArrayDeque<ScmPair>().apply { addLast(obj) }, res)
                else -> false
            }

        private tailrec fun isProperList(pair: ScmPair, tracedPair: ArrayDeque<ScmPair>, res: KevesResources): Boolean =
            when (val cdr = pair.cdr.toVal(res)) {
                null -> true
                is ScmPair -> {
                    if (tracedPair.indexOf(cdr) >= 0) {
                        false
                    } else {
                        tracedPair.addLast(cdr)
                        isProperList(cdr, tracedPair, res)
                    }
                }
                else -> false
            }

        fun toProperList(list: PtrObject, res: KevesResources): Pair<PtrPair, Int> =
            res.toProperList(list, PtrPair(0), 0)

        private tailrec fun KevesResources.toProperList(
            rest: PtrObject,
            result: PtrPair,
            n: Int
        ): Pair<PtrPair, Int> =
            when (val valRest = rest.toVal(this)) {
                null -> (if (result.isNotNull()) reverse(result.toPairNonNull(), this) else PtrPair(0)) to n
                is ScmPair -> toProperList(
                    rest = valRest.cdr,
                    result = make(valRest.car, result.toObject(), this),
                    n = n + 1
                )
                else -> reverse(make(rest, result.toObject(), this).toPairNonNull(), this) to -(n + 1)
            }

        fun reverse(list: PtrPairNonNull, res: KevesResources): PtrPair {
            val valList = list.toVal(res)
            return res.reverse(
                valList.cdr,
                make(valList.car, PtrObject(0), res),
                ArrayDeque<PtrObject>().apply { addLast(list.toObject()) })
        }

        private tailrec fun KevesResources.reverse(
            rest: PtrObject,
            result: PtrPair,
            tracedPair: ArrayDeque<PtrObject>
        ): PtrPair = when (val valRest = rest.toVal(this)) {
            null -> result
            is ScmPair -> {
                if (tracedPair.indexOf(rest) >= 0) throw IllegalArgumentException("cannot reverse improper list")
                tracedPair.addLast(rest)
                reverse(
                    valRest.cdr,
                    ScmMutablePair.make(valRest.car, result.toObject(), this).toPair(),
                    tracedPair
                )
            }
            else -> throw IllegalArgumentException("cannot reverse improper list")
        }

        tailrec fun listTail(list: PtrPair, k: Int, res: KevesResources): PtrPair =
            if (k > 0) {
                if (list.isNull()) throw IllegalArgumentException("length not enough")
                else listTail(
                    list.cdr(res)
                        .also { if (it.isNeitherNullNorPair(res)) throw IllegalArgumentException("not proper list") }
                        .toPair(),
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
        ): PtrObject = when (val valObj = list.toVal(res)) {
            null -> res.constFalse
            is ScmPair -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                if (valObj.car == obj) list else memq(obj, valObj.cdr, tracedPair, res)
            }
            else -> throw IllegalArgumentException("not proper list")
        }

        fun memv(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject = memv(obj, list, ArrayDeque(), res)

        private tailrec fun memv(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject = when (val valList = list.toVal(res)) {
            null -> res.constFalse
            is ScmPair -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                if (eqvQ(valList.car.toVal(res), obj.toVal(res))) list else memv(obj, valList.cdr, tracedPair, res)
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
        ): PtrObject = when (val valList = list.toVal(res)) {
            null -> res.constFalse
            is ScmPair -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                if (equalQ(valList.car.toVal(res), obj.toVal(res), res)) list else member(
                    obj,
                    valList.cdr,
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
        ): PtrObject = when (val valList = list.toVal(res)) {
            null -> res.constFalse
            is ScmPair -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                val car = valList.car
                val valCar = car.toVal(res)
                if (valCar !is ScmPair) throw IllegalArgumentException("not association list")
                if (valCar.car == obj) car else assq(obj, valList.cdr, tracedPair, res)
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
            when (val valList = list.toVal(res)) {
                null -> res.constFalse
                is ScmPair -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    val car = valList.car
                    val valCar = car.toVal(res)
                    if (valCar !is ScmPair) throw IllegalArgumentException("not association list")
                    if (eqvQ(valCar.car.toVal(res), obj.toVal(res))) car else assv(obj, valList.cdr, tracedPair, res)
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        fun assoc(obj: PtrObject, list: PtrObject, res: KevesResources): PtrObject = assoc(obj, list, ArrayDeque(), res)

        private tailrec fun assoc(
            obj: PtrObject,
            list: PtrObject,
            tracedPair: ArrayDeque<PtrObject>,
            res: KevesResources
        ): PtrObject = when (val valList = list.toVal(res)) {
            null -> res.constFalse
            is ScmPair -> {
                if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                tracedPair.addLast(list)
                val car = valList.car
                val valCar = car.toVal(res)
                if (valCar !is ScmPair) throw IllegalArgumentException("not association list")
                if (equalQ(valCar.car.toVal(res), obj.toVal(res), res)) car else assoc(
                    obj,
                    valList.cdr,
                    tracedPair,
                    res
                )
            }
            else -> throw IllegalArgumentException("not proper list")
        }

        @Suppress("unused")
        fun car(pair: ScmObject?): PtrObject =
            (pair as? ScmPair ?: throw IllegalArgumentException("'car' required pair, but got other")).car

        @Suppress("unused")
        fun cdr(pair: ScmObject?): PtrObject =
            (pair as? ScmPair ?: throw IllegalArgumentException("'cdr' required pair, but got other")).cdr

        @Suppress("unused")
        fun caar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(car(pair).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cdr(pair).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(car(pair).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cdr(pair).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddr' required pair, but got other")
            }

        @Suppress("unused")
        fun caaar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(caar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaar' required pair, but got other")
            }

        @Suppress("unused")
        fun caadr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cadr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cadar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cdar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadar' required pair, but got other")
            }

        @Suppress("unused")
        fun caddr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cddr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(caar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdadr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cadr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cddar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cdar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdddr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cddr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdddr' required pair, but got other")
            }

        @Suppress("unused")
        fun caaaar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(caaar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaaar' required pair, but got other")
            }

        @Suppress("unused")
        fun caaadr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(caadr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaadr' required pair, but got other")
            }

        @Suppress("unused")
        fun caadar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cadar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caadar' required pair, but got other")
            }

        @Suppress("unused")
        fun caaddr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(caddr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cadaar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cdaar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadadr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cdadr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadadr' required pair, but got other")
            }

        @Suppress("unused")
        fun caddar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cddar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadddr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                car(cdddr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaaar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(caaar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaadr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(caadr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdadar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cadar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdadar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaddr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(caddr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cddaar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cdaar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddadr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cdadr(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdddar(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cddar(pair, res).toVal(res))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddddr(pair: ScmObject?, res: KevesResources): PtrObject =
            try {
                cdr(cdddr(pair, res).toVal(res))
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