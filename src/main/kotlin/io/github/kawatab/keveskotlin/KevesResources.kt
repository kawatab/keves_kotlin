/*
 * KevesResources.kt
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

import io.github.kawatab.keveskotlin.KevesResources.Companion.F_BOX
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_BYTE_VECTOR
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_CHAR
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_CLOSURE
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_CONSTANT
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_DOUBLE
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_ERROR
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_FLOAT
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_INSTRUCTION
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_MACRO
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_MUTABLE_PAIR
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_PAIR
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_PROCEDURE
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_STRING
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_SYMBOL
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_SYNTAX
import io.github.kawatab.keveskotlin.KevesResources.Companion.F_VECTOR
import io.github.kawatab.keveskotlin.objects.*

/**
 * Pointers & instant values
 * null:         00000000 00000000 00000000 00000000
 * char:         xxxxxxxx xxxxxxxx xxxxxxxx 00000010
 * float:        xxxxxxxx xxxxxxxx xxxxxxxx 00000100
 * double:       xxxxxxxx xxxxxxxx xxxxxxxx 00000110
 * pair:         xxxxxxxx xxxxxxxx xxxxxxxx 00001000
 * mutable pair: xxxxxxxx xxxxxxxx xxxxxxxx 00001010
 * box:          xxxxxxxx xxxxxxxx xxxxxxxx 00001100
 * vector:       xxxxxxxx xxxxxxxx xxxxxxxx 00001110
 * symbol:       xxxxxxxx xxxxxxxx xxxxxxxx 00010000
 * string:       xxxxxxxx xxxxxxxx xxxxxxxx 00010010
 * byte vector:  xxxxxxxx xxxxxxxx xxxxxxxx 00010100
 * closure:      xxxxxxxx xxxxxxxx xxxxxxxx 00010110
 * macro:        xxxxxxxx xxxxxxxx xxxxxxxx 00011000
 * procedure:    xxxxxxxx xxxxxxxx xxxxxxxx 00011010
 * syntax:       xxxxxxxx xxxxxxxx xxxxxxxx 00011100
 * error:        xxxxxxxx xxxxxxxx xxxxxxxx 00011110
 * instruction:  xxxxxxxx xxxxxxxx xxxxxxxx 00100000
 * constant:     xxxxxxxx xxxxxxxx xxxxxxxx 01000000
 * int:          xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxx1
 */
class KevesResources {
    companion object {
        private const val MAX_NUMBER_OF_OBJECT = 10000
        const val F_CHAR = 0x2
        const val F_FLOAT = 0x4
        const val F_DOUBLE = 0x6
        const val F_PAIR = 0x8
        const val F_MUTABLE_PAIR = 0xa
        const val F_BOX = 0xc
        const val F_VECTOR = 0xe
        const val F_SYMBOL = 0x10
        const val F_STRING = 0x12
        const val F_BYTE_VECTOR = 0x14
        const val F_CLOSURE = 0x16
        const val F_MACRO = 0x18
        const val F_PROCEDURE = 0x1a
        const val F_SYNTAX = 0x1c
        const val F_ERROR = 0x1e
        const val F_INSTRUCTION = 0x20
        const val F_CONSTANT = 0x40
    }

    private val allObjectList = Array<ScmObject?>(MAX_NUMBER_OF_OBJECT) { null }
    var symbolList = mutableMapOf<String, PtrSymbol>() // ("" to Pointer(0)) // ScmSymbol(""))
    private var objectCnt = 0

    // val constNull = PtrObject(0)
    var constUndef = PtrObject(0)
    var constTrue = PtrObject(0)
    var constFalse = PtrObject(0)
    var constNaN = PtrObject(0)
    var constPositiveInfinity = PtrObject(0)
    var constNegativeInfinity = PtrObject(0)
    var constHalt = PtrObject(0)

    init {
        reset()
    }

    private fun reset() {
        allObjectList.fill(null)
        objectCnt = 1 // null at 0
        constUndef = addConstant(ScmConstant.UNDEF).toObject()
        constTrue = addConstant(ScmConstant.TRUE).toObject()
        constFalse = addConstant(ScmConstant.FALSE).toObject()
        constNaN = addDouble(ScmDouble.NaN).toObject()
        constPositiveInfinity = addDouble(ScmDouble.POSITIVE_INFINITY).toObject()
        constNegativeInfinity = addDouble(ScmDouble.NEGATIVE_INFINITY).toObject()
        constHalt = addInstruction(ScmInstruction.HALT).toObject()
    }

    private fun add(obj: ScmObject, flag: Int): PtrObject {
        synchronized(this) {
            if (objectCnt >= MAX_NUMBER_OF_OBJECT - 1) throw RuntimeException("cannot create object any more, due to memory full")
            val temp = objectCnt
            allObjectList[objectCnt] = obj
            objectCnt += 1
            return PtrObject((temp shl 8) + flag)
        }
    }

    fun addBox(box: ScmBox) = add(box, F_BOX).toBox()
    fun addByteVector(byteVector: ScmByteVector) = add(byteVector, F_BYTE_VECTOR).toByteVector()
    fun addChar(char: ScmChar) = add(char, F_CHAR).toChar()
    fun addClosure(closure: ScmClosure) = add(closure, F_CLOSURE).toClosure()
    fun addConstant(constant: ScmConstant) = add(constant, F_CONSTANT).toConstant()
    fun addDouble(double: ScmDouble) = add(double, F_DOUBLE).toDouble()
    fun addError(error: ScmError) = add(error, F_ERROR).toError()
    fun addFloat(float: ScmFloat) = add(float, F_FLOAT).toFloat()
    fun addInstruction(instruction: ScmInstruction) = add(instruction, F_INSTRUCTION).toInstruction()
    fun addInstructionApply(instruction: ScmInstruction.Apply) = add(instruction, F_INSTRUCTION).toInstructionApply()
    fun addInstructionReturn(instruction: ScmInstruction.Return) = add(instruction, F_INSTRUCTION).toInstructionReturn()
    fun addInt(int: ScmInt) = add(int, 0x1).toInt()
    fun addMutablePair(mutablePair: ScmMutablePair) = add(mutablePair, F_MUTABLE_PAIR).toMutablePair()
    fun addPair(pair: ScmPair) = add(pair, F_PAIR).toPairOrNull()
    fun addString(string: ScmString) = add(string, F_STRING).toString2()
    fun addSymbol(symbol: ScmSymbol) = add(symbol, F_SYMBOL).toSymbol()
    fun addVector(vector: ScmVector) = add(vector, F_VECTOR).toVector()

    fun addMacro(macro: ScmMacro) = add(macro, F_MACRO)
    fun addProcedure(proc: ScmProcedure) = PtrProcedure(add(proc, F_PROCEDURE).ptr)
    fun addSyntax(syntax: ScmSyntax) = PtrSyntaxOrNull(add(syntax, F_SYNTAX).ptr)

    fun equalQ(obj1: PtrObject, obj2: PtrObject, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>): Boolean =
        when {
            obj1.isNull() -> obj2.isNull()
            obj1.isBox(this) -> obj2.isBox(this) && obj1.toBox().equalQ(obj2.toBox(), duplicated, this)
            obj1.isPair(this) -> obj2.isPair(this) && obj1.toPair().equalQ(obj2.toPair(), duplicated, this)
            obj1.isVector(this) -> obj2.isVector(this) && obj1.toVector().equalQ(obj2.toVector(), duplicated, this)
            obj1.isByteVector(this) -> obj1.toByteVector().toVal(this).equalQ(obj2, this)
            obj1.isChar(this) -> obj1.toChar().toVal(this).equalQ(obj2, this)
            obj1.isClosure(this) -> obj1 == obj2
            obj1.isConstant(this) -> obj1 == obj2
            obj1.isDouble(this) -> obj1.toDouble().toVal(this).equalQ(obj2, this)
            obj1.isError(this) -> obj1 == obj2
            obj1.isFloat(this) -> obj1.toFloat().toVal(this).equalQ(obj2, this)
            obj1.isInstruction(this) -> obj1 == obj2
            obj1.isInt(this) -> obj1.toInt().toVal(this).equalQ(obj2, this)
            obj1.isMacro(this) -> obj1 == obj2
            obj1.isProcedure(this) -> obj1 == obj2
            obj1.isString(this) -> obj1.toString2().toVal(this).equalQ(obj2, this)
            obj1.isSymbol(this) -> obj1 == obj2
            obj1.isSyntax(this) -> obj1 == obj2
            else -> false
        }

    fun isScmObject(id: PtrObject) = (id.ptr and 0x1) == 0
    fun isInt(id: PtrObject) = (id.ptr and 0x1) == 1
    fun toInt(id: PtrObject) = id.ptr ushr 8
    fun toPointer(value: Int) = (value shl 8) + 1

    fun getNonNull(id: PtrObjectNonNull): ScmObject {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] ?: throw RuntimeException("null is not acceptable")
    }

    /*
    internal fun get(id: PtrObject): ScmObject? {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object") else allObjectList[i]
    }
     */

    internal fun getBox(id: PtrBox): ScmBox {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmBox ?: throw KevesExceptions.typeCastFailedToBox
    }

    internal fun getByteVector(id: PtrByteVector): ScmByteVector {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmByteVector ?: throw KevesExceptions.typeCastFailedToByteVector
    }

    internal fun getChar(id: PtrChar): ScmChar {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmChar ?: throw KevesExceptions.typeCastFailedToChar
    }

    internal fun getClosure(id: PtrClosure): ScmClosure {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmClosure ?: throw KevesExceptions.typeCastFailedToClosure
    }

    internal fun getConstant(id: PtrConstant): ScmConstant {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmConstant ?: throw KevesExceptions.typeCastFailedToConstant
    }

    internal fun getDouble(id: PtrDouble): ScmDouble {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmDouble ?: throw KevesExceptions.typeCastFailedToDouble
    }

    internal fun getError(id: PtrError): ScmError {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmError ?: throw KevesExceptions.typeCastFailedToError
    }

    internal fun getFloat(id: PtrFloat): ScmFloat {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmFloat ?: throw KevesExceptions.typeCastFailedToFloat
    }

    internal fun getInstruction(id: PtrInstruction): ScmInstruction {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInstruction ?: throw KevesExceptions.typeCastFailedToInstruction
    }

    internal fun getInstructionApply(id: PtrInstructionApply): ScmInstruction.Apply {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInstruction.Apply ?: throw KevesExceptions.typeCastFailedToInstructionApply
    }

    internal fun getInstructionReturn(id: PtrInstructionReturn): ScmInstruction.Return {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInstruction.Return ?: throw KevesExceptions.typeCastFailedToInstructionReturn
    }

    internal fun getInt(id: PtrInt): ScmInt {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInt ?: throw KevesExceptions.typeCastFailedToInt
    }

    internal fun getPairOrNull(id: PtrPairOrNull): ScmPair? {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmPair ?: throw KevesExceptions.typeCastFailedToPairOrNull }
    }

    internal fun getPair(id: PtrPair): ScmPair {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmPair ?: throw KevesExceptions.typeCastFailedToPair
    }

    internal fun getMacro(id: PtrMacro): ScmMacro {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmMacro ?: throw KevesExceptions.typeCastFailedToMacro
    }

    internal fun getMutablePair(id: PtrMutablePair): ScmMutablePair {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmMutablePair ?: throw KevesExceptions.typeCastFailedToMutablePair
    }

    internal fun getProcedure(id: PtrProcedure): ScmProcedure {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmProcedure ?: throw KevesExceptions.typeCastFailedToProcedure
    }

    internal fun getString(id: PtrString): ScmString {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmString ?: throw KevesExceptions.typeCastFailedToString
    }

    internal fun getSymbol(id: PtrSymbol): ScmSymbol {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmSymbol ?: throw KevesExceptions.typeCastFailedToSymbol
    }

    internal fun getSyntax(id: PtrSyntax): ScmSyntax {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmSyntax ?: throw KevesExceptions.typeCastFailedToSyntax
    }

    internal fun getSyntaxOrNull(id: PtrSyntaxOrNull): ScmSyntax? {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmSyntax ?: throw KevesExceptions.typeCastFailedToSyntax }
    }

    internal fun getVector(id: PtrVector): ScmVector {
        val i = id.ptr shr 8
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmVector ?: throw KevesExceptions.typeCastFailedToVector
    }
}

inline class PtrObject(val ptr: Int) {
    private val flag get() = ptr and 0xff
    fun isNull() = ptr == 0
    fun isNotNull() = ptr != 0
    fun isBox(res: KevesResources) = flag == F_BOX
    fun isNotBox(res: KevesResources) = flag != F_BOX
    fun isByteVector(res: KevesResources) = flag == F_BYTE_VECTOR
    fun isNotByteVector(res: KevesResources) = flag != F_BYTE_VECTOR
    fun isChar(res: KevesResources) = flag == F_CHAR
    fun isNotChar(res: KevesResources) = flag != F_CHAR
    fun isClosure(res: KevesResources) = flag == F_CLOSURE
    fun isConstant(res: KevesResources) = flag == F_CONSTANT
    fun isNotClosure(res: KevesResources) = flag != F_CLOSURE
    fun isDouble(res: KevesResources) = flag == F_DOUBLE
    fun isNotDouble(res: KevesResources) = flag != F_DOUBLE
    fun isError(res: KevesResources) = flag == F_ERROR
    fun isNotError(res: KevesResources) = flag != F_ERROR
    fun isFloat(res: KevesResources) = flag == F_FLOAT
    fun isNotFloat(res: KevesResources) = flag != F_FLOAT
    fun isInstruction(res: KevesResources) = flag == F_INSTRUCTION
    fun isNotInstruction(res: KevesResources) = flag != F_INSTRUCTION
    fun isInt(res: KevesResources) = ptr and 0x1 == 1
    fun isNotInt(res: KevesResources) = ptr and 0x1 != 1
    fun isMacro(res: KevesResources) = flag == F_MACRO
    fun isNotMacro(res: KevesResources) = flag != F_MACRO
    fun isNotMutablePair(res: KevesResources) = flag != F_MUTABLE_PAIR
    fun isPair(res: KevesResources) = flag == F_PAIR || flag == F_MUTABLE_PAIR
    fun isNotPair(res: KevesResources) = flag != F_PAIR && flag != F_MUTABLE_PAIR
    fun isProcedure(res: KevesResources) = flag == F_PROCEDURE
    fun isNotProcedure(res: KevesResources) = flag != F_PROCEDURE
    fun isString(res: KevesResources) = flag == F_STRING
    fun isNotString(res: KevesResources) = flag != F_STRING
    fun isSymbol(res: KevesResources) = flag == F_SYMBOL
    fun isNotSymbol(res: KevesResources) = flag != F_SYMBOL
    fun isSyntax(res: KevesResources) = flag == F_SYNTAX
    fun isNotSyntax(res: KevesResources) = flag != F_SYNTAX
    fun isVector(res: KevesResources) = flag == F_VECTOR
    fun isNotVector(res: KevesResources) = flag != F_VECTOR
    fun isNeitherNullNorPair(res: KevesResources) = ptr != 0 && flag != F_PAIR && flag != F_MUTABLE_PAIR
    // fun toVal(res: KevesResources) = res.get(this)
    fun toNonNull() = PtrObjectNonNull(ptr)
    fun toBox() = PtrBox(ptr)
    fun toByteVector() = PtrByteVector(ptr)
    fun toChar() = PtrChar(ptr)
    fun toClosure() = PtrClosure(ptr)
    fun toConstant() = PtrConstant(ptr)
    fun toDouble() = PtrDouble(ptr)
    fun toError() = PtrError(ptr)
    fun toFloat() = PtrFloat(ptr)
    fun toInstruction() = PtrInstruction(ptr)
    fun toInstructionApply() = PtrInstructionApply(ptr)
    fun toInstructionReturn() = PtrInstructionReturn(ptr)
    fun toInt() = PtrInt(ptr)
    fun toMacro() = PtrMacro(ptr)
    fun toPair() = PtrPair(ptr)
    fun toPairOrNull() = PtrPairOrNull(ptr)
    fun toMutablePair() = PtrMutablePair(ptr)
    fun toProcedure() = PtrProcedure(ptr)
    fun toString2() = PtrString(ptr)
    fun toSymbol() = PtrSymbol(ptr)
    fun toSyntax() = PtrSyntax(ptr)
    fun toSyntaxOrNull() = PtrSyntaxOrNull(ptr)
    fun toVector() = PtrVector(ptr)
}

inline class PtrObjectNonNull(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getNonNull(this)
    fun toObject() = PtrObject(ptr)
    fun toPairNonNull() = PtrPair(ptr)
}

inline class PtrBox(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getBox(this)
    fun toObject() = PtrObject(ptr)
    fun equalQ(other: PtrBox, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean =
        res.getBox(this).equalQ(other, duplicated, res)

    fun getValue(res: KevesResources): PtrObject = res.getBox(this).value
    fun setValue(value: PtrObject, res: KevesResources) {
        res.getBox(this).value = value
    }
}

inline class PtrByteVector(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getByteVector(this)
    fun toObject() = PtrObject(ptr)
    fun getArray(res: KevesResources) = res.getByteVector(this).array
}

inline class PtrChar(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getChar(this)
    fun toObject() = PtrObject(ptr)
    fun digitToInt(res: KevesResources) = res.getChar(this).digitToInt()
    fun isAlphabetic(res: KevesResources) = res.getChar(this).isAlphabetic()
    fun isLowerCase(res: KevesResources) = res.getChar(this).isLowerCase()
    fun isNumeric(res: KevesResources) = res.getChar(this).isNumeric()
    fun isUpperCase(res: KevesResources) = res.getChar(this).isUpperCase()
    fun isWhitespace(res: KevesResources) = res.getChar(this).isWhitespace()
    fun toFoldCase(res: KevesResources) = res.getChar(this).toFoldCase()
    fun toLowerCase(res: KevesResources) = res.getChar(this).toLowerCase()
    fun toUpperCase(res: KevesResources) = res.getChar(this).toUpperCase()
    fun toUtf32(res: KevesResources) = res.getChar(this).toUtf32()
}

inline class PtrClosure(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getClosure(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrConstant(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getConstant(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrDouble(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getDouble(this)
    fun toObject() = PtrObject(ptr)
    fun toObjectNonNull() = PtrObjectNonNull(ptr)
    fun value(res: KevesResources): Double = toVal(res).value
}

inline class PtrError(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getError(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrFloat(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getFloat(this)
    fun toObject() = PtrObject(ptr)
    fun toObjectNonNull() = PtrObjectNonNull(ptr)
    fun value(res: KevesResources): Float = toVal(res).value
}

inline class PtrInstruction(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getInstruction(this)
    fun toObject() = PtrObject(ptr)
    fun isInstructionReturn(res: KevesResources) = toVal(res) is ScmInstruction.Return
    fun asInstructionReturn(res: KevesResources) = res.getInstructionReturn(PtrInstructionReturn(ptr))
    fun exec(vm: KevesVM) {
        vm.res.getInstruction(this).exec(vm)
    }
}

inline class PtrInstructionApply(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getInstructionApply(this)
    fun toInstruction() = PtrInstruction(this.ptr)
    fun toObject() = PtrObject(ptr)
}

inline class PtrInstructionReturn(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getInstructionReturn(this)
    fun toInstruction() = PtrInstruction(this.ptr)
    fun toObject() = PtrObject(ptr)
}

inline class PtrInt(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getInt(this)
    fun toObject() = PtrObject(ptr)
    fun toObjectNonNull() = PtrObjectNonNull(ptr)
    fun value(res: KevesResources): Int = toVal(res).value
}

inline class PtrMacro(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getMacro(this)
    fun toObject() = PtrObject(ptr)
    fun transform(x: PtrPair, compiler: KevesCompiler, res: KevesResources) = res.getMacro(this).transform(x, compiler)
}

inline class PtrMutablePair(val ptr: Int) {
    fun car(res: KevesResources): PtrObject = toVal(res).car
    fun cdr(res: KevesResources): PtrObject = toVal(res).cdr
    fun toVal(res: KevesResources) = res.getMutablePair(this)
    fun toObject() = PtrObject(ptr)
    fun toPair() = PtrPairOrNull(ptr)
    fun assignCar(obj: PtrObject, res: KevesResources) {
        toVal(res).assignCar(obj)
    }

    fun assignCdr(obj: PtrObject, res: KevesResources) {
        toVal(res).assignCdr(obj)
    }
}

inline class PtrPair(val ptr: Int) {
    fun car(res: KevesResources): PtrObject = toVal(res).car
    fun cdr(res: KevesResources): PtrObject = toVal(res).cdr
    fun toVal(res: KevesResources) = res.getPair(this)
    fun toObject() = PtrObject(ptr)
    fun equalQ(other: PtrPair, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean =
        res.getPair(this).equalQ(other, duplicated, res)
    fun toStringForWrite(res: KevesResources) = res.getPair(this).toStringForWrite(res)
}

inline class PtrPairOrNull(val ptr: Int) {
    fun isNull() = ptr == 0
    fun isNotNull() = ptr != 0
    fun isMutable(res: KevesResources) = toVal(res) is ScmMutablePair
    fun isNotMutable(res: KevesResources) = toVal(res) !is ScmMutablePair
    fun car(res: KevesResources): PtrObject = toVal(res)?.car ?: throw KevesExceptions.typeCastFailedToPair
    fun cdr(res: KevesResources): PtrObject = toVal(res)?.cdr ?: throw KevesExceptions.typeCastFailedToPair
    fun toVal(res: KevesResources) = res.getPairOrNull(this)
    fun toObject() = PtrObject(ptr)
    fun toPairNonNull() = PtrPair(ptr)
    fun toMutable() = PtrMutablePair(ptr)
}

inline class PtrProcedure(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getProcedure(this)
    fun toObject() = PtrObject(ptr)
    fun normalProc(n: Int, vm: KevesVM) {
        vm.res.getProcedure(this).normalProc(n, vm)
    }

    fun hasSyntax(res: KevesResources): Boolean = getSyntax(res).isNotNull()
    fun getSyntax(res: KevesResources) = res.getProcedure(this).syntax
}

inline class PtrString(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getString(this)
    fun toObject() = PtrObject(ptr)
    fun equalQ(other: PtrObject, res: KevesResources): Boolean = res.getString(this).equalQ(other, res)
    fun toStringForDisplay(res: KevesResources) = res.getString(this).toStringForDisplay(res)
}

inline class PtrSymbol(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getSymbol(this)
    fun toObject() = PtrObject(ptr)
    fun getRawString(res: KevesResources) = res.getSymbol(this).rawString
}

inline class PtrSyntax(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getSyntax(this)
    fun toObject() = PtrObject(ptr)
    fun compile(
        x: PtrPair,
        e: PtrPairOrNull,
        s: PtrPairOrNull,
        next: PtrInstruction,
        compiler: KevesCompiler,
        res: KevesResources
    ) = res.getSyntax(this).compile(x, e, s, next, compiler)

    fun findSets(x: PtrPair, v: PtrPairOrNull, compiler: KevesCompiler, res: KevesResources) =
        res.getSyntax(this).findSets(x, v, compiler)

    fun findFree(x: PtrPair, b: PtrPairOrNull, compiler: KevesCompiler, res: KevesResources) =
        res.getSyntax(this).findFree(x, b, compiler)
}

inline class PtrSyntaxOrNull(val ptr: Int) {
    fun isNull() = ptr == 0
    fun isNotNull() = ptr != 0
    fun toVal(res: KevesResources) = res.getSyntaxOrNull(this)
    fun toObject() = PtrObject(ptr)
    fun toSyntaxNonNull() = PtrSyntax(ptr)
}

inline class PtrVector(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getVector(this)
    fun toObject() = PtrObject(ptr)
    fun equalQ(other: PtrVector, duplicated: ArrayDeque<Pair<ScmObject, ScmObject>>, res: KevesResources): Boolean =
        res.getVector(this).equalQ(other, duplicated, res)
}
