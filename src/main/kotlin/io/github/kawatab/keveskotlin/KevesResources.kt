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

    fun addBox(box: ScmBox) = PtrBox(add(box).ptr)
    fun addByteVector(byteVector: ScmByteVector) = PtrByteVector(add(byteVector).ptr)
    fun addChar(char: ScmChar) = PtrChar(add(char).ptr)
    fun addClosure(closure: ScmClosure) = PtrClosure(add(closure).ptr)
    fun addDouble(double: ScmDouble) = PtrDouble(add(double).ptr)
    fun addError(error: ScmError) = PtrError(add(error).ptr)
    fun addFloat(float: ScmFloat) = PtrFloat(add(float).ptr)
    fun addInstruction(instruction: ScmInstruction) = PtrInstruction(add(instruction).ptr)
    fun addInstructionApply(instruction: ScmInstruction.Apply) = PtrInstructionApply(add(instruction).ptr)
    fun addInstructionReturn(instruction: ScmInstruction.Return) = PtrInstructionReturn(add(instruction).ptr)
    fun addInt(int: ScmInt) = PtrInt(add(int).ptr)
    fun addMutablePair(mutablePair: ScmMutablePair) = PtrMutablePair(add(mutablePair).ptr)
    fun addPair(pair: ScmPair) = PtrPair(add(pair).ptr)
    fun addString(string: ScmString) = PtrString(add(string).ptr)
    fun addSymbol(symbol: ScmSymbol) = PtrSymbol(add(symbol).ptr)
    fun addVector(vector: ScmVector) = PtrVector(add(vector).ptr)

    fun addMacro(macro: ScmMacro) = add(macro)
    fun addProcedure(proc: ScmProcedure) = PtrProcedure(add(proc).ptr)
    fun addSyntax(syntax: ScmSyntax) = PtrSyntax(add(syntax).ptr)

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

    internal fun getPair(id: PtrPair): ScmPair? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmPair ?: throw RuntimeException("object is not pair") }
    }

    internal fun getPairNonNull(id: PtrPairNonNull): ScmPair {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmPair ?: throw KevesExceptions.typeCastFailedToPair
    }

    internal fun getMutablePair(id: PtrMutablePair): ScmMutablePair {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] as? ScmMutablePair ?: throw KevesExceptions.typeCastFailedToMutablePairOrNull
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
    fun asBox(res: KevesResources): ScmBox = res.getBox(this.toBox())
    fun asChar(res: KevesResources): ScmChar = res.getChar(this.toChar())
    fun asInstruction(res: KevesResources): ScmInstruction = res.getInstruction(this.toInstruction())
    fun asInt(res: KevesResources): ScmInt = res.getInt(this.toInt())
    fun asMutablePair(res: KevesResources): ScmMutablePair = res.getMutablePair(this.toMutablePair())
    fun asPair(res: KevesResources): ScmPair = res.getPairNonNull(this.toPairNonNull())
    fun asPairOrNull(res: KevesResources): ScmPair? = res.getPair(this.toPair())
    fun asProcedure(res: KevesResources): ScmProcedure = res.getProcedure(this.toProcedure())
    fun asString(res: KevesResources): ScmString = res.getString(this.toString2())
    fun asSymbol(res: KevesResources): ScmSymbol = res.getSymbol(this.toSymbol())
    fun asVector(res: KevesResources): ScmVector = res.getVector(this.toVector())

    fun isNull() = ptr == 0
    fun isNotNull() = ptr != 0
    fun isPair(res: KevesResources) = toVal(res) is ScmPair
    fun isNotPair(res: KevesResources) = toVal(res) !is ScmPair
    fun isNotMutablePair(res: KevesResources) = toVal(res) !is ScmMutablePair
    fun isSymbol(res: KevesResources) = toVal(res) is ScmSymbol
    fun isNotSymbol(res: KevesResources) = toVal(res) !is ScmSymbol
    fun isNeitherNullNorPair(res: KevesResources) = ptr != 0 && toVal(res) !is ScmPair
    fun toVal(res: KevesResources) = res.get(this)
    fun toNonNull() = PtrObjectNonNull(ptr)
    private fun toBox() = PtrBox(ptr)
    private fun toChar() = PtrChar(ptr)
    private fun toInstructionReturn() = PtrInstructionReturn(ptr)
    fun toByteVector() = PtrByteVector(ptr)
    fun toClosure() = PtrClosure(ptr)
    fun toError() = PtrError(ptr)
    fun toInstruction() = PtrInstruction(ptr)
    private fun toInt() = PtrInt(ptr)
    fun toPair() = PtrPair(ptr)
    fun toPairNonNull() = PtrPairNonNull(ptr)
    fun toMutablePair() = PtrMutablePair(ptr)
    private fun toProcedure() = PtrProcedure(ptr)
    private fun toString2() = PtrString(ptr)
    fun toSymbol() = PtrSymbol(ptr)
    private fun toVector() = PtrVector(ptr)
}

inline class PtrObjectNonNull(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getNonNull(this)
    fun toObject() = PtrObject(ptr)
    fun toPairNonNull() = PtrPairNonNull(ptr)
}

inline class PtrBox(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getBox(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrByteVector(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getByteVector(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrChar(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getChar(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrClosure(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getClosure(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrDouble(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getDouble(this)
    fun toObject() = PtrObject(ptr)
    fun toObjectNonNull() = PtrObjectNonNull(ptr)
}

inline class PtrError(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getError(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrFloat(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getFloat(this)
    fun toObject() = PtrObject(ptr)
    fun toObjectNonNull() = PtrObjectNonNull(ptr)
}

inline class PtrInstruction(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getInstruction(this)
    fun toObject() = PtrObject(ptr)
    fun asInstructionReturn(res: KevesResources) = res.getInstructionReturn(PtrInstructionReturn(ptr))
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
}

inline class PtrPair(val ptr: Int) {
    fun isNull() = ptr == 0
    fun isNotNull() = ptr != 0
    fun car(res: KevesResources): PtrObject = toVal(res)!!.car
    fun cdr(res: KevesResources): PtrObject = toVal(res)!!.cdr
    fun toVal(res: KevesResources) = res.getPair(this)
    fun toObject() = PtrObject(ptr)
    fun toPairNonNull() = PtrPairNonNull(ptr)
}

inline class PtrPairNonNull(val ptr: Int) {
    fun car(res: KevesResources): PtrObject = toVal(res).car
    fun cdr(res: KevesResources): PtrObject = toVal(res).cdr
    fun toVal(res: KevesResources) = res.getPairNonNull(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrMutablePair(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getMutablePair(this)
    fun toObject() = PtrObject(ptr)
    fun toPair() = PtrPair(ptr)
}

inline class PtrProcedure(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getProcedure(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrString(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getString(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrSymbol(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getSymbol(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrSyntax(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getSyntax(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrVector(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getVector(this)
    fun toObject() = PtrObject(ptr)
}
