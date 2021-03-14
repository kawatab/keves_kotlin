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

    fun add(obj: ScmObject): PtrObject {
        synchronized(this) {
            if (objectCnt >= MAX_NUMBER_OF_OBJECT - 1) throw RuntimeException("cannot create object any more, due to memory full")
            val temp = objectCnt
            allObjectList[objectCnt] = obj
            objectCnt += 1
            return PtrObject(temp shl 2)
        }
    }

    fun addByteVector(obj: ScmByteVector) = PtrByteVector(add(obj).ptr)
    fun addClosure(obj: ScmClosure) = PtrClosure(add(obj).ptr)
    fun addError(obj: ScmError) = PtrError(add(obj).ptr)
    fun addInstruction(obj: ScmInstruction) = PtrInstruction(add(obj).ptr)
    fun addMutablePair(obj: ScmMutablePair) = PtrMutablePair(add(obj).ptr)
    fun addPair(obj: ScmPair) = PtrPair(add(obj).ptr)
    fun addSymbol(obj: ScmSymbol) = PtrSymbol(add(obj).ptr)
    fun addVector(obj: ScmVector) = PtrVector(add(obj).ptr)

    fun addMacro(macro: ScmMacro) = add(macro)
    fun addProcedure(proc: ScmProcedure) = add(proc)
    fun addSyntax(syntax: ScmSyntax) = add(syntax)

    fun isScmObject(id: PtrObject) = (id.ptr and 3) == 0
    fun isInt(id: PtrObject) = (id.ptr and 3) == 1
    fun toInt(id: PtrObject) = id.ptr ushr 2
    fun toPointer(value: Int) = (value shl 2) + 1

    fun getNonNull(id: PtrObjectNonNull): ScmObject {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i] ?: throw RuntimeException("null is not acceptable")
    }

    fun get(id: PtrObject): ScmObject? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object") else allObjectList[i]
    }

    fun getByteVector(id: PtrByteVector): ScmByteVector {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i].let { it as? ScmByteVector ?: throw RuntimeException("object is not closure") }
    }

    fun getClosure(id: PtrClosure): ScmClosure {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i].let { it as? ScmClosure ?: throw RuntimeException("object is not closure") }
    }

    fun getError(id: PtrError): ScmError {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i].let { it as? ScmError ?: throw RuntimeException("object is not error") }
    }

    fun getInstruction(id: PtrInstruction): ScmInstruction {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i].let { it as? ScmInstruction ?: throw RuntimeException("object is not instruction") }
    }

    fun getPair(id: PtrPair): ScmPair? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmPair ?: throw RuntimeException("object is not pair") }
    }

    fun getPairNonNull(id: PtrPairNonNull): ScmPair {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i].let { it as? ScmPair ?: throw RuntimeException("object is not pair") }
    }

    fun getMutablePair(id: PtrMutablePair): ScmMutablePair? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmMutablePair ?: throw RuntimeException("object is not mutable pair") }
    }

    fun getSymbol(id: PtrSymbol): ScmSymbol? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmSymbol ?: throw RuntimeException("object is not symbol") }
    }

    fun getVector(id: PtrVector): ScmVector? {
        val i = id.ptr shr 2
        return if (i >= objectCnt) throw RuntimeException("cannot find such object")
        else allObjectList[i]?.let { it as? ScmVector ?: throw RuntimeException("object is not vector") }
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
    fun isPair(res: KevesResources) = toVal(res) is ScmPair
    fun isNotPair(res: KevesResources) = toVal(res) !is ScmPair
    fun isNotMutablePair(res: KevesResources) = toVal(res) !is ScmMutablePair
    fun isSymbol(res: KevesResources) = toVal(res) is ScmSymbol
    fun isNotSymbol(res: KevesResources) = toVal(res) !is ScmSymbol
    fun isNeitherNullNorPair(res: KevesResources) = ptr != 0 && toVal(res) !is ScmPair
    fun toVal(res: KevesResources) = res.get(this)
    fun toNonNull() = PtrObjectNonNull(ptr)
    fun toByteVector() = PtrByteVector(ptr)
    fun toClosure() = PtrClosure(ptr)
    fun toError() = PtrError(ptr)
    fun toInstruction() = PtrInstruction(ptr)
    fun toPair() = PtrPair(ptr)
    fun toPairNonNull() = PtrPairNonNull(ptr)
    fun toMutablePair() = PtrMutablePair(ptr)
    fun toSymbol() = PtrSymbol(ptr)
    fun toVector() = PtrVector(ptr)
}

inline class PtrObjectNonNull(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getNonNull(this)
    fun toObject() = PtrObject(ptr)
    fun toPairNonNull() = PtrPairNonNull(ptr)
}

inline class PtrByteVector(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getByteVector(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrClosure(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getClosure(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrError(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getError(this)
    fun toObject() = PtrObject(ptr)
}

inline class PtrInstruction(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getInstruction(this)
    fun toObject() = PtrObject(ptr)
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

inline class PtrSymbol(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getSymbol(this)
    fun toObject() = PtrObject(ptr)
    fun toNonNull() = PtrObjectNonNull(ptr)
}

inline class PtrVector(val ptr: Int) {
    fun toVal(res: KevesResources) = res.getVector(this)
    fun toObject() = PtrObject(ptr)
    fun toNonNull() = PtrObjectNonNull(ptr)
}
