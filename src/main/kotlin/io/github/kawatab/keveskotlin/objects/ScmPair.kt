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

open class ScmPair(car: ScmObject?, cdr: ScmObject?) : ScmObject() {
    var car: ScmObject? = car
        protected set

    var cdr: ScmObject? = cdr
        protected set

    override fun equalQ(other: ScmObject?): Boolean =
        if (this === other) true else (other is ScmPair && equalQ(other, ArrayDeque()))

    fun equalQ(other: ScmPair, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>): Boolean {
        if (duplicated.indexOfFirst { (first, second) -> (this == first && other == second) || (this == second && other == first) } >= 0) return true
        duplicated.addLast(this to other)
        val car1 = this.car
        val car2 = other.car
        if (car1 === car2) return true
        when (car1) {
            null -> return false
            is ScmBox -> if (car2 !is ScmBox || !car1.equalQ(car2, duplicated)) return false
            is ScmPair -> if (car2 !is ScmPair || !car1.equalQ(car2, duplicated)) return false
            is ScmVector -> if (car2 !is ScmVector || !car1.equalQ(car2, duplicated)) return false
            else -> if (!car1.equalQ(car2)) return false
        }

        val cdr1 = this.cdr
        val cdr2 = other.cdr
        if (cdr1 === cdr2) return true
        return when (cdr1) {
            null -> false
            is ScmBox -> cdr2 is ScmBox && cdr1.equalQ(cdr2, duplicated)
            is ScmPair -> cdr2 is ScmPair && cdr1.equalQ(cdr2, duplicated)
            is ScmVector -> cdr2 is ScmVector && cdr1.equalQ(cdr2, duplicated)
            else -> cdr1.equalQ(cdr2)
        }
    }

    private fun searchCirculation(
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
                searchCirculation(car as? ScmPair, allPair, duplicatedPair)
                searchCirculation(cdr as? ScmPair, allPair, duplicatedPair)
            } else if (duplicatedPair.indexOfFirst { (pair, _) -> pair == rest } < 0) {
                duplicatedPair.addLast(rest to false)
            }
        }
    }

    private fun writePair(pair: ScmPair, duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>, isCar: Boolean): String =
        duplicatedPair.indexOfFirst { (duplicated, _) -> duplicated == pair }
            .let {
                when {
                    it < 0 -> if (isCar) "(${pair.writeInner(duplicatedPair)})" else pair.writeInner(duplicatedPair)
                    duplicatedPair[it].second -> if (isCar) "#$it#" else ". #$it#"
                    else -> {
                        duplicatedPair[it] = duplicatedPair[it].first to true
                        "${if (isCar) "" else ". "}#$it=(${pair.writeInner(duplicatedPair)})"
                    }
                }
            }

    private fun displayPair(pair: ScmPair, duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>, isCar: Boolean): String =
        duplicatedPair.indexOfFirst { (duplicated, _) -> duplicated == pair }
            .let {
                when {
                    it < 0 -> if (isCar) "(${pair.displayInner(duplicatedPair)})" else pair.displayInner(duplicatedPair)
                    duplicatedPair[it].second -> if (isCar) "#$it#" else ". #$it#"
                    else -> {
                        duplicatedPair[it] = duplicatedPair[it].first to true
                        "${if (isCar) "" else ". "}#$it=(${pair.displayInner(duplicatedPair)})"
                    }
                }
            }


    override fun toStringForWrite(): String {
        val allPair = ArrayDeque<ScmPair>()
        val duplicatedPair = ArrayDeque<Pair<ScmPair, Boolean>>()
        searchCirculation(this, allPair, duplicatedPair)
        val carStr =
            if (car is ScmPair) writePair(car as ScmPair, duplicatedPair, true)
            else getStringForWrite(car)
        return when (cdr) {
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
                val cdrStr = writePair(cdr as ScmPair, duplicatedPair, false)
                "$prefix($carStr $cdrStr)"
            }
            else -> "($carStr . ${cdr!!.toStringForWrite()})"
        }
    }

    private fun writeInner(duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>): String {
        val carStr =
            if (car is ScmPair) writePair((car as ScmPair), duplicatedPair, true)
            else getStringForWrite(car)
        return when (cdr) {
            null -> carStr
            is ScmPair -> {
                val cdrStr = writePair((cdr as ScmPair), duplicatedPair, false)
                "$carStr $cdrStr"
            }
            else -> "$carStr . ${cdr!!.toStringForWrite()}"
        }
    }

    override fun toStringForDisplay(): String {
        val allPair = ArrayDeque<ScmPair>()
        val duplicatedPair = ArrayDeque<Pair<ScmPair, Boolean>>()
        searchCirculation(this, allPair, duplicatedPair)
        val carStr =
            if (car is ScmPair) displayPair((car as ScmPair), duplicatedPair, true)
            else getStringForDisplay(car)
        return when (cdr) {
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
                val cdrStr = displayPair((cdr as ScmPair), duplicatedPair, false)
                "$prefix($carStr $cdrStr)"
            }
            else -> "($carStr . ${cdr!!.toStringForDisplay()})"
        }
    }

    private fun displayInner(duplicatedPair: ArrayDeque<Pair<ScmPair, Boolean>>): String {
        val carStr =
            if (car is ScmPair) displayPair((car as ScmPair), duplicatedPair, true)
            else getStringForDisplay(car)
        return when (cdr) {
            null -> carStr
            is ScmPair -> {
                val cdrStr = displayPair((cdr as ScmPair), duplicatedPair, false)
                "$carStr $cdrStr"
            }
            else -> "$carStr . ${cdr!!.toStringForDisplay()}"
        }
    }

    companion object {
        fun length(list: ScmObject?): Int = length(list, 0, ArrayDeque())

        private tailrec fun length(rest: ScmObject?, n: Int, tracedPair: ArrayDeque<ScmPair>): Int =
            when (rest) {
                null -> n
                is ScmPair -> {
                    if (tracedPair.indexOf(rest) > 0) throw IllegalArgumentException("cannot get the length of improper list")
                    tracedPair.addLast(rest)
                    length(rest = rest.cdr, n = n + 1, tracedPair = tracedPair)
                }
                else -> throw IllegalArgumentException("cannot get the length of improper list")
            }

        fun isPair(obj: ScmObject?): Boolean = obj is ScmPair

        fun isProperList(obj: ScmObject?): Boolean =
            when (obj) {
                null -> true
                is ScmPair -> isProperList(obj, ArrayDeque<ScmPair>().apply { addLast(obj) })
                else -> false
            }

        private tailrec fun isProperList(pair: ScmPair, tracedPair: ArrayDeque<ScmPair>): Boolean =
            when (val cdr = pair.cdr) {
                null -> true
                is ScmPair -> {
                    if (tracedPair.indexOf(cdr) >= 0) {
                        false
                    } else {
                        tracedPair.addLast(cdr)
                        isProperList(cdr, tracedPair)
                    }
                }
                else -> false
            }

        fun toProperList(list: ScmObject?): Pair<ScmPair?, Int> = toProperList(list, null, 0)

        private tailrec fun toProperList(rest: ScmObject?, result: ScmPair?, n: Int): Pair<ScmPair?, Int> =
            when (rest) {
                null -> result?.let { reverse(it) } to n
                is ScmPair -> toProperList(rest = rest.cdr, result = ScmPair(rest.car, result), n = n + 1)
                else -> reverse(ScmPair(rest, result)) to -(n + 1)
            }

        fun reverse(list: ScmPair): ScmPair? =
            reverse(list.cdr, ScmPair(list.car, null), ArrayDeque<ScmPair>().apply { addLast(list) })

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

        tailrec fun listTail(list: ScmPair?, k: Int): ScmPair? =
            if (k > 0) {
                if (list == null) throw IllegalArgumentException("length not enough")
                else listTail(
                    list.cdr?.let { it as? ScmPair ?: throw IllegalArgumentException("not proper list") },
                    k - 1
                )
            } else {
                list
            }

        fun memq(obj: ScmObject?, list: ScmPair?): ScmObject = memq(obj, list, ArrayDeque())

        private tailrec fun memq(obj: ScmObject?, list: ScmObject?, tracedPair: ArrayDeque<ScmPair>): ScmObject =
            when (list) {
                null -> ScmConstant.FALSE
                is ScmPair -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    if (list.car === obj) list else memq(obj, list.cdr, tracedPair)
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        fun memv(obj: ScmObject?, list: ScmPair?): ScmObject = memv(obj, list, ArrayDeque())

        private tailrec fun memv(obj: ScmObject?, list: ScmObject?, tracedPair: ArrayDeque<ScmPair>): ScmObject =
            when (list) {
                null -> ScmConstant.FALSE
                is ScmPair -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    if (eqvQ(list.car, obj)) list else memv(obj, list.cdr, tracedPair)
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        fun member(obj: ScmObject?, list: ScmPair?): ScmObject = member(obj, list, ArrayDeque())

        private tailrec fun member(obj: ScmObject?, list: ScmObject?, tracedPair: ArrayDeque<ScmPair>): ScmObject =
            when (list) {
                null -> ScmConstant.FALSE
                is ScmPair -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    if (equalQ(list.car, obj)) list else member(obj, list.cdr, tracedPair)
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        fun assq(obj: ScmObject?, list: ScmPair?): ScmObject = assq(obj, list, ArrayDeque())

        private tailrec fun assq(obj: ScmObject?, list: ScmObject?, tracedPair: ArrayDeque<ScmPair>): ScmObject =
            when (list) {
                null -> ScmConstant.FALSE
                is ScmPair -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    val car = list.car
                    if (car !is ScmPair) throw IllegalArgumentException("not association list")
                    if (car.car === obj) car else assq(obj, list.cdr, tracedPair)
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        fun assv(obj: ScmObject?, list: ScmPair?): ScmObject = assv(obj, list, ArrayDeque())

        private tailrec fun assv(obj: ScmObject?, list: ScmObject?, tracedPair: ArrayDeque<ScmPair>): ScmObject =
            when (list) {
                null -> ScmConstant.FALSE
                is ScmPair -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    val car = list.car
                    if (car !is ScmPair) throw IllegalArgumentException("not association list")
                    if (eqvQ(car.car, obj)) car else assv(obj, list.cdr, tracedPair)
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        fun assoc(obj: ScmObject?, list: ScmPair?): ScmObject = assoc(obj, list, ArrayDeque())

        private tailrec fun assoc(obj: ScmObject?, list: ScmObject?, tracedPair: ArrayDeque<ScmPair>): ScmObject =
            when (list) {
                null -> ScmConstant.FALSE
                is ScmPair -> {
                    if (tracedPair.indexOf(list) >= 0) throw IllegalArgumentException("not proper list")
                    tracedPair.addLast(list)
                    val car = list.car
                    if (car !is ScmPair) throw IllegalArgumentException("not association list")
                    if (equalQ(car.car, obj)) car else assoc(obj, list.cdr, tracedPair)
                }
                else -> throw IllegalArgumentException("not proper list")
            }

        @Suppress("unused")
        fun car(pair: ScmObject?): ScmObject? =
            (pair as? ScmPair ?: throw IllegalArgumentException("'car' required pair, but got other")).car

        @Suppress("unused")
        fun cdr(pair: ScmObject?): ScmObject? =
            (pair as? ScmPair ?: throw IllegalArgumentException("'cdr' required pair, but got other")).cdr

        @Suppress("unused")
        fun caar(pair: ScmObject?): ScmObject? =
            try {
                car(car(pair))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadr(pair: ScmObject?): ScmObject? =
            try {
                car(cdr(pair))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdar(pair: ScmObject?): ScmObject? =
            try {
                cdr(car(pair))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddr(pair: ScmObject?): ScmObject? =
            try {
                cdr(cdr(pair))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddr' required pair, but got other")
            }

        @Suppress("unused")
        fun caaar(pair: ScmObject?): ScmObject? =
            try {
                car(car(car(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaar' required pair, but got other")
            }

        @Suppress("unused")
        fun caadr(pair: ScmObject?): ScmObject? =
            try {
                car(car(cdr(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cadar(pair: ScmObject?): ScmObject? =
            try {
                car(cdr(car(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadar' required pair, but got other")
            }

        @Suppress("unused")
        fun caddr(pair: ScmObject?): ScmObject? =
            try {
                car(cdr(cdr(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaar(pair: ScmObject?): ScmObject? =
            try {
                cdr(car(car(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdadr(pair: ScmObject?): ScmObject? =
            try {
                cdr(car(cdr(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cddar(pair: ScmObject?): ScmObject? =
            try {
                cdr(cdr(car(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdddr(pair: ScmObject?): ScmObject? =
            try {
                cdr(cdr(cdr(pair)))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdddr' required pair, but got other")
            }

        @Suppress("unused")
        fun caaaar(pair: ScmObject?): ScmObject? =
            try {
                car(car(car(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaaar' required pair, but got other")
            }

        @Suppress("unused")
        fun caaadr(pair: ScmObject?): ScmObject? =
            try {
                car(car(car(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaadr' required pair, but got other")
            }

        @Suppress("unused")
        fun caadar(pair: ScmObject?): ScmObject? =
            try {
                car(car(cdr(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caadar' required pair, but got other")
            }

        @Suppress("unused")
        fun caaddr(pair: ScmObject?): ScmObject? =
            try {
                car(car(cdr(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caaddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cadaar(pair: ScmObject?): ScmObject? =
            try {
                car(cdr(car(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadadr(pair: ScmObject?): ScmObject? =
            try {
                car(cdr(car(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadadr' required pair, but got other")
            }

        @Suppress("unused")
        fun caddar(pair: ScmObject?): ScmObject? =
            try {
                car(cdr(cdr(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'caddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cadddr(pair: ScmObject?): ScmObject? =
            try {
                car(cdr(cdr(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cadddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaaar(pair: ScmObject?): ScmObject? =
            try {
                cdr(car(car(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaadr(pair: ScmObject?): ScmObject? =
            try {
                cdr(car(car(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdadar(pair: ScmObject?): ScmObject? =
            try {
                cdr(car(cdr(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdadar' required pair, but got other")
            }

        @Suppress("unused")
        fun cdaddr(pair: ScmObject?): ScmObject? =
            try {
                cdr(car(cdr(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdaddr' required pair, but got other")
            }

        @Suppress("unused")
        fun cddaar(pair: ScmObject?): ScmObject? =
            try {
                cdr(cdr(car(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddaar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddadr(pair: ScmObject?): ScmObject? =
            try {
                cdr(cdr(car(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddadr' required pair, but got other")
            }

        @Suppress("unused")
        fun cdddar(pair: ScmObject?): ScmObject? =
            try {
                cdr(cdr(cdr(car(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cdddar' required pair, but got other")
            }

        @Suppress("unused")
        fun cddddr(pair: ScmObject?): ScmObject? =
            try {
                cdr(cdr(cdr(cdr(pair))))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("'cddddr' required pair, but got other")
            }

        fun list(e: ScmObject?) = ScmPair(e, null)
        fun list(e1: ScmObject?, e2: ScmObject?) = ScmPair(e1, list(e2))
        fun list(e1: ScmObject?, e2: ScmObject?, e3: ScmObject?) = ScmPair(e1, list(e2, e3))
        fun list(e1: ScmObject?, e2: ScmObject?, e3: ScmObject?, e4: ScmObject?) = ScmPair(e1, list(e2, e3, e4))

        @Suppress("unused")
        fun list(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?
        ) = ScmPair(e1, list(e2, e3, e4, e5))

        @Suppress("unused")
        fun list(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?
        ) = ScmPair(e1, list(e2, e3, e4, e5, e6))

        @Suppress("unused")
        fun list(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?
        ) = ScmPair(e1, list(e2, e3, e4, e5, e6, e7))

        @Suppress("unused")
        fun list(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?,
            e8: ScmObject?
        ) = ScmPair(e1, list(e2, e3, e4, e5, e6, e7, e8))

        @Suppress("unused")
        fun list(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?,
            e8: ScmObject?,
            e9: ScmObject?
        ) = ScmPair(e1, list(e2, e3, e4, e5, e6, e7, e8, e9))

        @Suppress("unused")
        fun list(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?,
            e8: ScmObject?,
            e9: ScmObject?,
            e10: ScmObject?
        ) = ScmPair(e1, list(e2, e3, e4, e5, e6, e7, e8, e9, e10))

        @Suppress("unused")
        fun listStar(e: ScmObject?) = e

        @Suppress("unused")
        fun listStar(e1: ScmObject?, e2: ScmObject?) = ScmPair(e1, e2)
        fun listStar(e1: ScmObject?, e2: ScmObject?, e3: ScmObject?) = ScmPair(e1, listStar(e2, e3))

        @Suppress("unused")
        fun listStar(e1: ScmObject?, e2: ScmObject?, e3: ScmObject?, e4: ScmObject?) = ScmPair(e1, listStar(e2, e3, e4))

        @Suppress("unused")
        fun listStar(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?
        ) = ScmPair(e1, listStar(e2, e3, e4, e5))

        @Suppress("unused")
        fun listStar(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?
        ) = ScmPair(e1, listStar(e2, e3, e4, e5, e6))

        @Suppress("unused")
        fun listStar(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?
        ) = ScmPair(e1, listStar(e2, e3, e4, e5, e6, e7))

        @Suppress("unused")
        fun listStar(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?,
            e8: ScmObject?
        ) = ScmPair(e1, listStar(e2, e3, e4, e5, e6, e7, e8))

        @Suppress("unused")
        fun listStar(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?,
            e8: ScmObject?,
            e9: ScmObject?
        ) = ScmPair(e1, listStar(e2, e3, e4, e5, e6, e7, e8, e9))

        @Suppress("unused")
        fun listStar(
            e1: ScmObject?,
            e2: ScmObject?,
            e3: ScmObject?,
            e4: ScmObject?,
            e5: ScmObject?,
            e6: ScmObject?,
            e7: ScmObject?,
            e8: ScmObject?,
            e9: ScmObject?,
            e10: ScmObject?
        ) = ScmPair(e1, listStar(e2, e3, e4, e5, e6, e7, e8, e9, e10))
    }
}