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
                obj.isBox(res) -> obj.toBox().toVal(res).toStringForWrite(res)
                obj.isByteVector(res) -> obj.toByteVector().toVal(res).toStringForWrite(res)
                obj.isChar(res) -> obj.toChar().toVal(res).toStringForWrite(res)
                obj.isClosure(res) -> obj.toClosure().toVal(res).toStringForWrite(res)
                obj.isConstant(res) -> obj.toConstant().toVal(res).toStringForWrite(res)
                obj.isDouble(res) -> obj.toDouble().toVal(res).toStringForWrite(res)
                obj.isError(res) -> obj.toError().toVal(res).toStringForWrite(res)
                obj.isFloat(res) -> obj.toFloat().toVal(res).toStringForWrite(res)
                obj.isInstruction(res) -> obj.toInstruction().toVal(res).toStringForWrite(res)
                obj.isInt(res) -> obj.toInt().toVal(res).toStringForWrite(res)
                obj.isMacro(res) -> obj.toMacro().toVal(res).toStringForWrite(res)
                obj.isPair(res) -> obj.toPair().toVal(res).toStringForWrite(res)
                obj.isProcedure(res) -> obj.toProcedure().toVal(res).toStringForWrite(res)
                obj.isString(res) -> obj.toString2().toVal(res).toStringForWrite(res)
                obj.isSymbol(res) -> obj.toSymbol().toVal(res).toStringForWrite(res)
                obj.isSyntax(res) -> obj.toSyntax().toVal(res).toStringForWrite(res)
                obj.isVector(res) -> obj.toVector().toVal(res).toStringForWrite(res)
                else -> throw RuntimeException("cannot write such object")
            }

        fun getStringForDisplay(obj: PtrObject, res: KevesResources): String =
            when {
                obj.isNull() -> "()"
                obj.isBox(res) -> obj.toBox().toVal(res).toStringForDisplay(res)
                obj.isByteVector(res) -> obj.toByteVector().toVal(res).toStringForDisplay(res)
                obj.isChar(res) -> obj.toChar().toVal(res).toStringForDisplay(res)
                obj.isClosure(res) -> obj.toClosure().toVal(res).toStringForDisplay(res)
                obj.isConstant(res) -> obj.toConstant().toVal(res).toStringForDisplay(res)
                obj.isDouble(res) -> obj.toDouble().toVal(res).toStringForDisplay(res)
                obj.isError(res) -> obj.toError().toVal(res).toStringForDisplay(res)
                obj.isFloat(res) -> obj.toFloat().toVal(res).toStringForDisplay(res)
                obj.isInstruction(res) -> obj.toInstruction().toVal(res).toStringForDisplay(res)
                obj.isInt(res) -> obj.toInt().toVal(res).toStringForDisplay(res)
                obj.isMacro(res) -> obj.toMacro().toVal(res).toStringForDisplay(res)
                obj.isPair(res) -> obj.toPair().toVal(res).toStringForDisplay(res)
                obj.isProcedure(res) -> obj.toProcedure().toVal(res).toStringForDisplay(res)
                obj.isString(res) -> obj.toString2().toVal(res).toStringForDisplay(res)
                obj.isSymbol(res) -> obj.toSymbol().toVal(res).toStringForDisplay(res)
                obj.isSyntax(res) -> obj.toSyntax().toVal(res).toStringForDisplay(res)
                obj.isVector(res) -> obj.toVector().toVal(res).toStringForDisplay(res)
                else -> throw RuntimeException("cannot display such object")
            }

        fun eqvQ(obj1: PtrObject, obj2: PtrObject, res: KevesResources): Boolean =
            when {
                obj1.isNull() -> obj2.isNull()
                obj1.isBox(res) -> obj1 == obj2
                obj1.isByteVector(res) -> obj1 == obj2
                obj1.isChar(res) -> obj1.toChar().toVal(res).eqvQ(obj2, res)
                obj1.isClosure(res) -> obj1 == obj2
                obj1.isConstant(res) -> obj1 == obj2
                obj1.isDouble(res) -> obj1.toDouble().toVal(res).eqvQ(obj2, res)
                obj1.isError(res) -> obj1 == obj2
                obj1.isFloat(res) -> obj1.toFloat().toVal(res).eqvQ(obj2, res)
                obj1.isInstruction(res) -> obj1 == obj2
                obj1.isInt(res) -> obj1.toInt().toVal(res).eqvQ(obj2, res)
                obj1.isMacro(res) -> obj1 == obj2
                obj1.isPair(res) -> obj1 == obj2
                obj1.isProcedure(res) -> obj1 == obj2
                obj1.isString(res) -> obj1 == obj2
                obj1.isSymbol(res) -> obj1 == obj2
                obj1.isSyntax(res) -> obj1 == obj2
                obj1.isVector(res) -> obj1 == obj2
                else -> throw RuntimeException("eqv? could not accept such object")
            }

        fun equalQ(obj1: PtrObject, obj2: PtrObject, res: KevesResources): Boolean =
            when {
                obj1.isNull() -> obj2.isNull()
                obj1.isBox(res) -> obj1.toBox().toVal(res).equalQ(obj2, res)
                obj1.isByteVector(res) -> obj1.toByteVector().toVal(res).equalQ(obj2, res)
                obj1.isChar(res) -> obj1.toChar().toVal(res).equalQ(obj2, res)
                obj1.isClosure(res) -> obj1 == obj2
                obj1.isConstant(res) -> obj1 == obj2
                obj1.isDouble(res) -> obj1.toDouble().toVal(res).equalQ(obj2, res)
                obj1.isError(res) -> obj1 == obj2
                obj1.isFloat(res) -> obj1.toFloat().toVal(res).equalQ(obj2, res)
                obj1.isInstruction(res) -> obj1 == obj2
                obj1.isInt(res) -> obj1.toInt().toVal(res).equalQ(obj2, res)
                obj1.isMacro(res) -> obj1 == obj2
                obj1.isPair(res) -> obj1.toPair().toVal(res).equalQ(obj2, res)
                obj1.isProcedure(res) -> obj1 == obj2
                obj1.isString(res) -> obj1.toString2().toVal(res).equalQ(obj2, res)
                obj1.isSymbol(res) -> obj1 == obj2
                obj1.isSyntax(res) -> obj1 == obj2
                obj1.isVector(res) -> obj1.toVector().toVal(res).equalQ(obj2, res)
                else -> throw RuntimeException("eqv? could not accept such object")
            }

    }
}