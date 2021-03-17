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

import io.github.kawatab.keveskotlin.objects.*

class KevesResources {
    companion object {
        private const val MAX_NUMBER_OF_OBJECT = 10000
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
        constUndef = add(ScmConstant.UNDEF)
        constTrue = add(ScmConstant.TRUE)
        constFalse = add(ScmConstant.FALSE)
        constNaN = add(ScmDouble.NaN)
        constPositiveInfinity = add(ScmDouble.POSITIVE_INFINITY)
        constNegativeInfinity = add(ScmDouble.NEGATIVE_INFINITY)
        constHalt = add(ScmInstruction.HALT)
    }

    private fun add(obj: ScmObject): PtrObject {
        synchronized(this) {
            if (objectCnt >= MAX_NUMBER_OF_OBJECT - 1) throw RuntimeException("cannot create object any more, due to memory full")
            val temp = objectCnt
            allObjectList[objectCnt] = obj
            objectCnt += 1
            return PtrObject(temp shl 2)
        }
    }

    fun addBox(box: ScmBox) = add(box).toBox()
    fun addByteVector(byteVector: ScmByteVector) = add(byteVector).toByteVector()
    fun addChar(char: ScmChar) = add(char).toChar()
    fun addClosure(closure: ScmClosure) = add(closure).toClosure()
    fun addDouble(double: ScmDouble) = add(double).toDouble()
    fun addError(error: ScmError) = add(error).toError()
    fun addFloat(float: ScmFloat) = add(float).toFloat()
    fun addInstruction(instruction: ScmInstruction) = add(instruction).toInstruction()
    fun addInstructionApply(instruction: ScmInstruction.Apply) = add(instruction).toInstructionApply()
    fun addInstructionReturn(instruction: ScmInstruction.Return) = add(instruction).toInstructionReturn()
    fun addInt(int: ScmInt) = add(int).toInt()
    fun addMutablePair(mutablePair: ScmMutablePair) = add(mutablePair).toMutablePair()
    fun addPair(pair: ScmPair) = add(pair).toPairOrNull()
    fun addString(string: ScmString) = add(string).toString2()
    fun addSymbol(symbol: ScmSymbol) = add(symbol).toSymbol()
    fun addVector(vector: ScmVector) = add(vector).toVector()

    fun addMacro(macro: ScmMacro) = add(macro)
    fun addProcedure(proc: ScmProcedure) = PtrProcedure(add(proc).ptr)
    fun addSyntaxOrNull(syntax: ScmSyntax) = PtrSyntaxOrNull(add(syntax).ptr)

    fun isScmObject(id: PtrObject) = (id.ptr and 3) == 0
    fun isInt(id: PtrObject) = (id.ptr and 3) == 1
    fun toInt(id: PtrObject) = id.ptr ushr 2
    fun toPointer(value: Int) = (value shl 2) + 1

    fun getNonNull(id: PtrObjectNonNull): ScmObject {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] ?: throw RuntimeException("null is not acceptable")
    }

    internal fun get(id: PtrObject): ScmObject? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object") else allObjectList[i]
    }

    internal fun getBox(id: PtrBox): ScmBox {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmBox ?: throw KevesExceptions.typeCastFailedToBox
    }

    internal fun getByteVector(id: PtrByteVector): ScmByteVector {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmByteVector ?: throw KevesExceptions.typeCastFailedToByteVector
    }

    internal fun getChar(id: PtrChar): ScmChar {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmChar ?: throw KevesExceptions.typeCastFailedToChar
    }

    internal fun getClosure(id: PtrClosure): ScmClosure {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmClosure ?: throw KevesExceptions.typeCastFailedToClosure
    }

    internal fun getDouble(id: PtrDouble): ScmDouble {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmDouble ?: throw KevesExceptions.typeCastFailedToDouble
    }

    internal fun getError(id: PtrError): ScmError {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmError ?: throw KevesExceptions.typeCastFailedToError
    }

    internal fun getFloat(id: PtrFloat): ScmFloat {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmFloat ?: throw KevesExceptions.typeCastFailedToFloat
    }

    internal fun getInstruction(id: PtrInstruction): ScmInstruction {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInstruction ?: throw KevesExceptions.typeCastFailedToInstruction
    }

    internal fun getInstructionApply(id: PtrInstructionApply): ScmInstruction.Apply {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInstruction.Apply ?: throw KevesExceptions.typeCastFailedToInstructionApply
    }

    internal fun getInstructionReturn(id: PtrInstructionReturn): ScmInstruction.Return {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInstruction.Return ?: throw KevesExceptions.typeCastFailedToInstructionReturn
    }

    internal fun getInt(id: PtrInt): ScmInt {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmInt ?: throw KevesExceptions.typeCastFailedToInt
    }

    internal fun getPairOrNull(id: PtrPairOrNull): ScmPair? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmPair ?: throw KevesExceptions.typeCastFailedToPairOrNull }
    }

    internal fun getPair(id: PtrPair): ScmPair {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmPair ?: throw KevesExceptions.typeCastFailedToPair
    }

    internal fun getMacro(id: PtrMacro): ScmMacro {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmMacro ?: throw KevesExceptions.typeCastFailedToMacro
    }

    internal fun getMutablePair(id: PtrMutablePair): ScmMutablePair {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmMutablePair ?: throw KevesExceptions.typeCastFailedToMutablePair
    }

    internal fun getProcedure(id: PtrProcedure): ScmProcedure {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmProcedure ?: throw KevesExceptions.typeCastFailedToProcedure
    }

    internal fun getString(id: PtrString): ScmString {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmString ?: throw KevesExceptions.typeCastFailedToString
    }

    internal fun getSymbol(id: PtrSymbol): ScmSymbol {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmSymbol ?: throw KevesExceptions.typeCastFailedToSymbol
    }

    internal fun getSyntax(id: PtrSyntax): ScmSyntax {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmSyntax ?: throw KevesExceptions.typeCastFailedToSyntax
    }

    internal fun getSyntaxOrNull(id: PtrSyntaxOrNull): ScmSyntax? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmSyntax ?: throw KevesExceptions.typeCastFailedToSyntax }
    }

    internal fun getVector(id: PtrVector): ScmVector {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmVector ?: throw KevesExceptions.typeCastFailedToVector
    }
}

/**
 * Pointers & instant values
 * null: 00000000 00000000 00000000 00000000
 * ptr:  xxxxxxxx xxxxxxxx xxxxxxxx xxxxxx00
 * int:  xxxxxxxx xxxxxxxx xxxxxxxx xxxxxx01
 */
inline class PtrObject(val ptr: Int) {
    fun isNull() = ptr == 0
    fun isNotNull() = ptr != 0
    fun isBox(res: KevesResources) = toVal(res) is ScmBox
    fun isNotBox(res: KevesResources) = toVal(res) !is ScmBox
    fun isByteVector(res: KevesResources) = toVal(res) is ScmByteVector
    fun isNotByteVector(res: KevesResources) = toVal(res) !is ScmByteVector
    fun isChar(res: KevesResources) = toVal(res) is ScmChar
    fun isNotChar(res: KevesResources) = toVal(res) !is ScmChar
    fun isClosure(res: KevesResources) = toVal(res) is ScmClosure
    fun isNotClosure(res: KevesResources) = toVal(res) !is ScmClosure
    fun isDouble(res: KevesResources) = toVal(res) is ScmDouble
    fun isNotDouble(res: KevesResources) = toVal(res) !is ScmDouble
    fun isFloat(res: KevesResources) = toVal(res) is ScmFloat
    fun isNotFloat(res: KevesResources) = toVal(res) !is ScmFloat
    fun isInt(res: KevesResources) = toVal(res) is ScmInt
    fun isNotInt(res: KevesResources) = toVal(res) !is ScmInt
    fun isMacro(res: KevesResources) = toVal(res) is ScmMacro
    fun isNotMacro(res: KevesResources) = toVal(res) !is ScmMacro
    fun isPair(res: KevesResources) = toVal(res) is ScmPair
    fun isNotPair(res: KevesResources) = toVal(res) !is ScmPair
    fun isProcedure(res: KevesResources) = toVal(res) is ScmProcedure
    fun isNotProcedure(res: KevesResources) = toVal(res) !is ScmProcedure
    fun isNotMutablePair(res: KevesResources) = toVal(res) !is ScmMutablePair
    fun isString(res: KevesResources) = toVal(res) is ScmString
    fun isNotString(res: KevesResources) = toVal(res) !is ScmString
    fun isSymbol(res: KevesResources) = toVal(res) is ScmSymbol
    fun isNotSymbol(res: KevesResources) = toVal(res) !is ScmSymbol
    fun isSyntax(res: KevesResources) = toVal(res) is ScmSyntax
    fun isNotSyntax(res: KevesResources) = toVal(res) !is ScmSyntax
    fun isVector(res: KevesResources) = toVal(res) is ScmVector
    fun isNotVector(res: KevesResources) = toVal(res) !is ScmVector
    fun isNeitherNullNorPair(res: KevesResources) = ptr != 0 && toVal(res) !is ScmPair
    fun toVal(res: KevesResources) = res.get(this)
    fun toNonNull() = PtrObjectNonNull(ptr)
    fun toBox() = PtrBox(ptr)
    fun toByteVector() = PtrByteVector(ptr)
    fun toChar() = PtrChar(ptr)
    fun toClosure() = PtrClosure(ptr)
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
