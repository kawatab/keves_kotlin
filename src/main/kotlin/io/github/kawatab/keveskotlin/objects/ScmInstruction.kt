/*
 * ScmInstruction.kt
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

import io.github.kawatab.keveskotlin.*

abstract class ScmInstruction private constructor() : ScmObject() {
    override fun toStringForDisplay(res: KevesResources): String = toStringForWrite(res)
    override fun toString(): String = "Instruction"

    open fun exec(vm: KevesVM) {}

    companion object {
        val HALT = object : ScmInstruction() {
            override fun toStringForWrite(res: KevesResources): String = "<HALT>"
        }
    }

    class ReferLocal private constructor(private val n: Int, private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<REFER_LOCAL $n ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.acc = vm.stack.index(vm.fp, n)
            vm.x = next
        }

        companion object {
            fun make(n: Int, next: PtrInstruction, res: KevesResources) =
                ReferLocal(n, next).let { res.addInstruction(it) }
        }
    }

    class ReferFree private constructor(private val n: Int, private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<REFER_FREE $n ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.acc = vm.clsr.toVal(vm.res).indexClosure(n)
            vm.x = next
        }

        companion object {
            fun make(n: Int, next: PtrInstruction, res: KevesResources) =
                ReferFree(n, next).let { res.addInstruction(it) }
        }
    }

    class Indirect private constructor(private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<INDIRECT ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            if (vm.acc.isNotBox(vm.res)) throw IllegalArgumentException("<INDIRECT> required box, but got other")
            vm.acc = vm.acc.toBox().getValue(vm.res)
            vm.x = next
        }

        companion object {
            fun make(next: PtrInstruction, res: KevesResources) = Indirect(next).let { res.addInstruction(it) }
        }
    }

    // class Constant private constructor(private val obj: ScmObject?, private val next: ScmInstruction) :
    class Constant private constructor(private val ptr: PtrObject, private val next: PtrInstruction) :
        ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<CONSTANT ${getStringForWrite(ptr, res)} ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            // vm.acc = obj
            vm.acc = ptr
            vm.x = next
        }

        companion object {
            // fun make(obj: ScmObject?, next: ScmInstruction, res: KevesResources) = Constant(obj, next).let { res.addInstruction(it) }
            fun make(ptr: PtrObject, next: PtrInstruction, res: KevesResources) =
                Constant(ptr, next).let { res.addInstruction(it) }
        }
    }

    class Close private constructor(
        private val n: Int,
        private val numArg: Int,
        private val body: PtrInstruction,
        private val next: PtrInstruction
    ) :
        ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<CLOSE $n $numArg ${body.toVal(res).toStringForWrite(res)} ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.acc = vm.closure(body, numArg, n, vm.sp).toObject()
            vm.x = next
            vm.sp -= n
        }

        companion object {
            fun make(n: Int, numArg: Int, body: PtrInstruction, next: PtrInstruction, res: KevesResources) =
                Close(n, numArg, body, next).let { res.addInstruction(it) }
        }
    }

    class Box private constructor(private val n: Int, private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String = "<BOX $n ${next.toVal(res).toStringForWrite(res)}>"
        override fun exec(vm: KevesVM) {
            vm.stack.indexSetE(vm.sp, n, ScmBox.make(vm.stack.index(vm.sp, n), vm.res).toObject())
            vm.x = next
        }

        companion object {
            fun make(n: Int, next: PtrInstruction, res: KevesResources) = Box(n, next).let { res.addInstruction(it) }
        }
    }

    class BoxRest private constructor(private val n: Int, private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<BOX_REST $n ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.stack.indexSetE(vm.sp, n, ScmBox.make(vm.stack.index(vm.sp, n), vm.res).toObject())
            vm.x = next
        }

        companion object {
            fun make(n: Int, next: PtrInstruction, res: KevesResources) =
                BoxRest(n, next).let { res.addInstruction(it) }
        }
    }

    class Test private constructor(private val thn: PtrInstruction, private val els: PtrInstruction) :
        ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<TEST ${thn.toVal(res).toStringForWrite(res)} ${els.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.x = if (vm.acc != vm.res.constFalse) thn else els
        }

        companion object {
            fun make(thn: PtrInstruction, els: PtrInstruction, res: KevesResources) =
                Test(thn, els).let { res.addInstruction(it) }
        }
    }

    class AssignLocal private constructor(private val n: Int, private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<ASSIGN_LOCAL $n ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            try {
                val box: PtrBox =
                    vm.stack.index(vm.fp, n)
                        .also {
                            if (it.isNotBox(vm.res)) throw IllegalArgumentException(
                                "<ASSIGN_LOCAL> expected box but got other ${
                                    getStringForWrite(vm.stack.index(vm.fp, n), vm.res)
                                }, ${getStringForWrite(vm.acc, vm.res)}"
                            )
                        }.toBox()
                box.setValue(vm.acc, vm.res)
                vm.x = next
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("<ASSIGN_LOCAL> expected box but got other")
            }
        }

        companion object {
            fun make(n: Int, next: PtrInstruction, res: KevesResources) =
                AssignLocal(n, next).let { res.addInstruction(it) }
        }
    }

    class AssignFree private constructor(private val n: Int, private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<ASSIGN_FREE $n ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            try {
                val box: PtrBox =
                    vm.clsr.toVal(vm.res).indexClosure(n)
                        .also {
                            if (it.isNotBox(vm.res)) throw IllegalArgumentException("<ASSIGN_FREE> expected box but got other")
                        }.toBox()
                box.setValue(vm.acc, vm.res)
                vm.x = next
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("<ASSIGN_FREE> expected box but got other")
            }
        }

        companion object {
            fun make(n: Int, next: PtrInstruction, res: KevesResources) = res.addInstruction(AssignFree(n, next))
        }
    }

    class Conti private constructor(private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String = "<CONTI ${next.toVal(res).toStringForWrite(res)}>"
        override fun exec(vm: KevesVM) {
            vm.acc = vm.continuation(vm.sp).toObject()
            vm.x = next
        }

        companion object {
            fun make(next: PtrInstruction, res: KevesResources) = Conti(next).let { res.addInstruction(it) }
        }
    }

    class Nuate private constructor(private val s: ScmVector, private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<NUATE ${s.toStringForWrite(res)} ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.sp = vm.stack.restoreStack(s)
        }

        companion object {
            fun make(s: ScmVector, next: PtrInstruction, res: KevesResources) = res.addInstruction(Nuate(s, next))
        }
    }

    class Frame private constructor(private val ret: PtrInstruction, private val next: PtrInstruction) :
        ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<FRAME ${ret.toVal(res).toStringForWrite(res)} ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.sp = vm.stack.push(
                ret.toObject(),
                vm.stack.push(ScmInt.make(vm.fp, vm.res).toObject(), vm.stack.push(vm.clsr.toObject(), vm.sp))
            )
        }

        companion object {
            fun make(ret: PtrInstruction, next: PtrInstruction, res: KevesResources) =
                Frame(ret, next).let { res.addInstruction(it) }
        }
    }

    class Argument private constructor(private val next: PtrInstruction) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<ARGUMENT ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.sp = vm.stack.push(vm.acc, vm.sp)
        }

        companion object {
            fun make(next: PtrInstruction, res: KevesResources) = Argument(next).let { res.addInstruction(it) }
        }
    }

    class Shift private constructor(private val n: Int, private val m: Int, private val next: PtrInstruction) :
        ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<SHIFT $n $m ${next.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.shiftArgs(n, m, vm.sp)
        }

        companion object {
            fun make(n: Int, m: Int, next: PtrInstruction, res: KevesResources) =
                Shift(n, m, next).let { res.addInstruction(it) }
        }
    }

    class Apply private constructor(var n: Int) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String = "<APPLY $n>"
        override fun exec(vm: KevesVM) {
            try {
                vm.acc.toProcedure().normalProc(n, vm)
            } catch (e: TypeCastException) {
                throw IllegalArgumentException("<APPLY> got non procedure")
            }
        }

        companion object {
            fun make(n: Int, res: KevesResources) = Apply(n).let { res.addInstructionApply(it) }
        }
    }

    class ApplyDirect private constructor(private val proc: PtrProcedure) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String =
            "<APPLY_DIRECT ${proc.toVal(res).toStringForWrite(res)}>"

        override fun exec(vm: KevesVM) {
            proc.toVal(vm.res).directProc(vm.acc, vm.sp, vm)
        }

        companion object {
            fun make(proc: PtrProcedure, res: KevesResources) = ApplyDirect(proc).let { res.addInstruction(it) }
        }
    }

    class Return private constructor(val n: Int) : ScmInstruction() {
        override fun toStringForWrite(res: KevesResources): String = "<RETURN $n>"
        override fun exec(vm: KevesVM) {
            val sp1 = vm.sp - n
            val s0: PtrInstruction = try {
                vm.stack.index(sp1, 0).toInstruction()
            } catch (e: TypeCastException) {
                throw IllegalArgumentException("SP pointed by <RETURN> did not include pair")
            }
            val s1: Int = try {
                vm.stack.index(sp1, 1).toInt().value(vm.res)
            } catch (e: TypeCastException) {
                throw IllegalArgumentException("SP pointed by <RETURN> did not include Int")
            }
            val s2 = vm.stack.index(sp1, 2).also {
                if (it.isNotClosure(vm.res)) throw IllegalArgumentException("SP pointed by <RETURN> did not include vector")
            }.toClosure()
            // acc = acc
            vm.x = s0
            vm.fp = s1
            vm.clsr = s2
            vm.sp = sp1 - 3
        }

        companion object {
            fun make(n: Int, res: KevesResources) = Return(n).let { res.addInstruction(it) }
        }
    }
}
