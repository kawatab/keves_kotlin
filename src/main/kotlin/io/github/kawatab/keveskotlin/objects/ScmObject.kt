/*
 * ScmObject.kt
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

abstract class ScmObject {
    abstract fun toStringForWrite(res: KevesResources): String
    abstract fun toStringForDisplay(res: KevesResources): String
    // abstract fun eqvQ(other: PtrObject, res: KevesResources): Boolean // = this === other.toVal(res)
    // abstract fun equalQ(other: PtrObject, res: KevesResources): Boolean // = this === other.toVal(res)

    open fun add(other: PtrObject, res: KevesResources): PtrObject = throw IllegalArgumentException("not number")
    open fun subtract(other: PtrObject, res: KevesResources): PtrObject = throw IllegalArgumentException("not number")
    open fun isLessThan(other: PtrObject, res: KevesResources): Boolean = throw IllegalArgumentException("not number")

    companion object {
        fun getStringForWrite(obj: PtrObject, res: KevesResources): String =
            when {
                obj.isNull() -> "()"
                obj.isBox() -> obj.toBox().toVal(res).toStringForWrite(res)
                obj.isByteVector() -> obj.toByteVector().toVal(res).toStringForWrite(res)
                obj.isChar() -> obj.toChar().toVal(res).toStringForWrite(res)
                obj.isClosure() -> obj.toClosure().toVal(res).toStringForWrite(res)
                obj.isConstant() -> obj.toConstant().toString()
                obj.isDouble() -> obj.toDouble().toVal(res).toStringForWrite(res)
                obj.isError() -> obj.toError().toVal(res).toStringForWrite(res)
                obj.isFloat() -> obj.toFloat().toVal(res).toStringForWrite(res)
                obj.isInstruction() -> obj.toInstruction().toVal(res).toStringForWrite(res)
                obj.isInt() -> obj.toInt().toString()
                obj.isMacro() -> obj.toMacro().toVal(res).toStringForWrite(res)
                obj.isPair() -> obj.toPair().toVal(res).toStringForWrite(res)
                obj.isProcedure() -> obj.toProcedure().toVal(res).toStringForWrite(res)
                obj.isString() -> obj.toString2().toVal(res).toStringForWrite(res)
                obj.isSymbol() -> obj.toSymbol().toVal(res).toStringForWrite(res)
                obj.isSyntax() -> obj.toSyntax().toVal(res).toStringForWrite(res)
                obj.isVector() -> obj.toVector().toVal(res).toStringForWrite(res)
                else -> throw RuntimeException("cannot write such object")
            }

        fun getStringForDisplay(obj: PtrObject, res: KevesResources): String =
            when {
                obj.isNull() -> "()"
                obj.isBox() -> obj.toBox().toVal(res).toStringForDisplay(res)
                obj.isByteVector() -> obj.toByteVector().toVal(res).toStringForDisplay(res)
                obj.isChar() -> obj.toChar().toVal(res).toStringForDisplay(res)
                obj.isClosure() -> obj.toClosure().toVal(res).toStringForDisplay(res)
                obj.isConstant() -> obj.toConstant().toString()
                obj.isDouble() -> obj.toDouble().toVal(res).toStringForDisplay(res)
                obj.isError() -> obj.toError().toVal(res).toStringForDisplay(res)
                obj.isFloat() -> obj.toFloat().toVal(res).toStringForDisplay(res)
                obj.isInstruction() -> obj.toInstruction().toVal(res).toStringForDisplay(res)
                obj.isInt() -> obj.toInt().toString()
                obj.isMacro() -> obj.toMacro().toVal(res).toStringForDisplay(res)
                obj.isPair() -> obj.toPair().toVal(res).toStringForDisplay(res)
                obj.isProcedure() -> obj.toProcedure().toVal(res).toStringForDisplay(res)
                obj.isString() -> obj.toString2().toVal(res).toStringForDisplay(res)
                obj.isSymbol() -> obj.toSymbol().toVal(res).toStringForDisplay(res)
                obj.isSyntax() -> obj.toSyntax().toVal(res).toStringForDisplay(res)
                obj.isVector() -> obj.toVector().toVal(res).toStringForDisplay(res)
                else -> throw RuntimeException("cannot display such object")
            }

        fun eqvQ(obj1: PtrObject, obj2: PtrObject, res: KevesResources): Boolean =
            when {
                obj1.isNull() -> obj2.isNull()
                obj1.isBox() -> obj1 == obj2
                obj1.isByteVector() -> obj1 == obj2
                obj1.isChar() -> obj1.toChar().toVal(res).eqvQ(obj2, res)
                obj1.isClosure() -> obj1 == obj2
                obj1.isConstant() -> obj1 == obj2
                obj1.isDouble() -> obj1.toDouble().toVal(res).eqvQ(obj2, res)
                obj1.isError() -> obj1 == obj2
                obj1.isFloat() -> obj1.toFloat().toVal(res).eqvQ(obj2, res)
                obj1.isInstruction() -> obj1 == obj2
                obj1.isInt() -> obj1 == obj2
                obj1.isMacro() -> obj1 == obj2
                obj1.isPair() -> obj1 == obj2
                obj1.isProcedure() -> obj1 == obj2
                obj1.isString() -> obj1 == obj2
                obj1.isSymbol() -> obj1 == obj2
                obj1.isSyntax() -> obj1 == obj2
                obj1.isVector() -> obj1 == obj2
                else -> throw RuntimeException("eqv? could not accept such object")
            }

        fun equalQ(obj1: PtrObject, obj2: PtrObject, res: KevesResources): Boolean =
            when {
                obj1.isNull() -> obj2.isNull()
                obj1.isBox() -> obj1.toBox().toVal(res).equalQ(obj2, res)
                obj1.isByteVector() -> obj1.toByteVector().toVal(res).equalQ(obj2, res)
                obj1.isChar() -> obj1.toChar().toVal(res).equalQ(obj2, res)
                obj1.isClosure() -> obj1 == obj2
                obj1.isConstant() -> obj1 == obj2
                obj1.isDouble() -> obj1.toDouble().toVal(res).equalQ(obj2, res)
                obj1.isError() -> obj1 == obj2
                obj1.isFloat() -> obj1.toFloat().toVal(res).equalQ(obj2, res)
                obj1.isInstruction() -> obj1 == obj2
                obj1.isInt() -> obj1 == obj2
                obj1.isMacro() -> obj1 == obj2
                obj1.isPair() -> obj1.toPair().toVal(res).equalQ(obj2, res)
                obj1.isProcedure() -> obj1 == obj2
                obj1.isString() -> obj1.toString2().toVal(res).equalQ(obj2, res)
                obj1.isSymbol() -> obj1 == obj2
                obj1.isSyntax() -> obj1 == obj2
                obj1.isVector() -> obj1.toVector().toVal(res).equalQ(obj2, res)
                else -> throw RuntimeException("eqv? could not accept such object")
            }

    }
}