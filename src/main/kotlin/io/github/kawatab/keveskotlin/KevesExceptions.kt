/*
 * KevesExceptions.kt
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

object KevesExceptions {
    // Illegal Syntax
    fun badSyntax(who: String) = IllegalArgumentException(
        "bad syntax in '%s'".format(who)
    )

    // General error
    fun failed(who: String) = IllegalArgumentException(
        "'%s' failed".format(who)
    )

    // Invalid character
    fun expectedChar(who: String) = IllegalArgumentException(
        "'%s' expected a character, but got other".format(who)
    )

    // Invalid number
    fun expectedNumber(who: String) = IllegalArgumentException(
        "'%s' expected an number, but got other".format(who)
    )

    fun expectedInt(who: String) = IllegalArgumentException(
        "'%s' expected an integer, but got other".format(who)
    )

    fun expectedPositiveNumberGotNegative(who: String) = IllegalArgumentException(
        "'%s' expected a positive number but negative".format(who)
    )

    fun expectedNonZero(who: String) = IllegalArgumentException(
        "'%s' expected a non-zero number but zero".format(who)
    )

    // Invalid pair
    fun expectedPair(who: String) = IllegalArgumentException(
        "'%s' expected a pair, but got other".format(who)
    )

    fun expectedMutablePair(who: String) = IllegalArgumentException(
        "'%s' expected a mutable pair, but got other".format(who)
    )

    // Invalid list
    fun expectedList(who: String) = IllegalArgumentException(
        "'%s' expected a list, but got other".format(who)
    )

    fun expectedProperList(who: String) = IllegalArgumentException(
        "'%s' expected a proper list but improper".format(who)
    )

    fun tooShortList(who: String) = IllegalArgumentException(
        "'%s' got too short list".format(who)
    )


    // Invalid string
    fun expectedString(who: String) = RuntimeException(
        "'%s' expected a string, but got other".format(who)
    )

    // Invalid symbol
    fun expectedSymbol(who: String) = RuntimeException(
        "'%s' expected a symbol, but got other".format(who)
    )

    // Invalid number of arguments
    fun expected1DatumGot0(who: String) = IllegalArgumentException(
        "'%s' expected 1 datum, but got nothing".format(who)
    )

    fun expected1DatumGotMore(who: String) = IllegalArgumentException(
        "'%s' expected 1 datum, but got more".format(who)
    )

    fun expected1OrMoreDatumGot0(who: String) = IllegalArgumentException(
        "'%s' expected 1 or more datum, but got nothing".format(who)
    )

    fun expected1Or2DatumGot0(who: String) = IllegalArgumentException(
        "'%s' expected 1 or 2 datum, but got nothing".format(who)
    )

    fun expected1Or2DatumGotMore(who: String) = IllegalArgumentException(
        "'%s' expected 1 or 2 datum, but got more".format(who)
    )

    fun expected2DatumGotLess(who: String) = IllegalArgumentException(
        "'%s' expected 2 datum, but got less".format(who)
    )

    fun expected2DatumGotMore(who: String) = IllegalArgumentException(
        "'%s' expected 2 datum, but got more".format(who)
    )

    fun expected2OrMoreDatumGotLess(who: String) = IllegalArgumentException(
        "'%s' expected 2 or more datum, but got less".format(who)
    )

    fun expected3DatumGotLess(who: String) = IllegalArgumentException(
        "'%s' expected 3 datum, but got less".format(who)
    )

    fun expected3DatumGotMore(who: String) = IllegalArgumentException(
        "'%s' expected 3 datum, but got more".format(who)
    )

    val typeCastFailedToBox = TypeCastException("Type cast to Box failed")
    val typeCastFailedToByteVector = TypeCastException("Type cast to ByteVector failed")
    val typeCastFailedToChar = TypeCastException("Type cast to Char failed")
    val typeCastFailedToClosure = TypeCastException("Type cast to Closure failed")
    val typeCastFailedToConstant = TypeCastException("Type cast to Constant failed")
    val typeCastFailedToDouble = TypeCastException("Type cast to Double failed")
    val typeCastFailedToError = TypeCastException("Type cast to Error failed")
    val typeCastFailedToFloat = TypeCastException("Type cast to Float failed")
    val typeCastFailedToInstruction = TypeCastException("Type cast to Instruction failed")
    val typeCastFailedToInstructionApply = TypeCastException("Type cast to Instruction.Apply failed")
    val typeCastFailedToInstructionReturn = TypeCastException("Type cast to Instruction.Return failed")
    val typeCastFailedToInt = TypeCastException("Type cast to Int failed")
    val typeCastFailedToPair = TypeCastException("Type cast to Pair failed")
    val typeCastFailedToPairOrNull = TypeCastException("Type cast to Pair or null failed")
    val typeCastFailedToMacro = TypeCastException("Type cast to Macro failed")
    val typeCastFailedToMutablePair = TypeCastException("Type cast to MutablePair failed")
    val typeCastFailedToProcedure = TypeCastException("Type cast to Procedure failed")
    val typeCastFailedToString = TypeCastException("Type cast to String failed")
    val typeCastFailedToSymbol = TypeCastException("Type cast to Symbol failed")
    val typeCastFailedToSyntax = TypeCastException("Type cast to Syntax failed")
    val typeCastFailedToVector = TypeCastException("Type cast to Vector failed")
}