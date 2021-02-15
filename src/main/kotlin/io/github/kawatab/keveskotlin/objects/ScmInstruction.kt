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

import io.github.kawatab.keveskotlin.KevesVM

abstract class ScmInstruction private constructor() : ScmObject() {
    override fun toStringForDisplay(): String = toStringForWrite()
    override fun toString(): String = toStringForWrite()

    open fun exec(vm: KevesVM) {}

    companion object {
        val HALT = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<HALT>"
        }

        /*
        val REFER_LOCAL = object : ScmInstruction() {
                override fun toStringForWrite(): String = "<REFER_LOCAL>"
            }

        val REFER_FREE = object : ScmInstruction() {
                override fun toStringForWrite(): String = "<REFER_FREE>"
            }
         */

        val REFER = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<REFER>"
        }

        /*
        val INDIRECT = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<INDIRECT>"
        }

        val CONSTANT = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<CONSTANT>"
        }

        val CLOSE = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<CLOSE>"
        }

        val BOX = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<BOX>"
        }

        val BOX_REST = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<BOX_REST>"
        }

        val TEST = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<TEST >"
        }

        val ASSIGN_LOCAL = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<ASSIGN_LOCAL>"
        }

        val ASSIGN_FREE = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<ASSIGN_FREE >"
        }

        @Suppress("SpellCheckingInspection")
        val CONTI = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<CONTI>"
        }


        @Suppress("SpellCheckingInspection")
        val NUATE = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<NUATE>"
        }

        val FRAME = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<FRAME>"
        }

        val ARGUMENT = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<ARGUMENT>"
        }

        val SHIFT = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<SHIFT>"
        }

        val APPLY = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<APPLY>"
        }

        val RETURN = object : ScmInstruction() {
            override fun toStringForWrite(): String = "<RETURN>"
        }
         */

    }

    class ReferLocal(private val n: Int, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<REFER_LOCAL>"
        override fun exec(vm: KevesVM) {
            vm.acc = vm.stack.index(vm.fp, n)
            vm.x = next
        }
    }

    class ReferFree(private val n: Int, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<REFER_FREE>"
        override fun exec(vm: KevesVM) {
            vm.acc = vm.clsr?.indexClosure(n)
            vm.x = next
        }
    }

    class Indirect(private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<INDIRECT>"
        override fun exec(vm: KevesVM) {
            val unboxA = try {
                ScmBox.unbox(vm.acc)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("<INDIRECT> required box, but got other")
            }
            vm.acc = unboxA
            vm.x = next
        }
    }

    class Constant(private val obj: ScmObject?, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<CONSTANT>"
        override fun exec(vm: KevesVM) {
            vm.acc = obj
            vm.x = next
        }
    }

    class Close(private val n: Int, private val numArg: Int, private val body: ScmPair?, private val next: ScmPair?) :
        ScmInstruction() {
        override fun toStringForWrite(): String = "<CLOSE>"
        override fun exec(vm: KevesVM) {
            vm.acc = vm.closure(body, numArg, n, vm.sp)
            vm.x = next
            vm.sp = (vm.sp - n)
        }
    }

    class Box(private val n: Int, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<BOX>"
        override fun exec(vm: KevesVM) {
            vm.stack.indexSetE(vm.sp, n, ScmBox(vm.stack.index(vm.sp, n)))
            vm.x = next
        }
    }

    class BoxRest(private val n: Int, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<BOX_REST>"
        override fun exec(vm: KevesVM) {
            vm.stack.indexSetE(vm.sp, n, ScmBox(vm.stack.index(vm.sp, n)))
            vm.x = next
        }
    }

    class Test(private val thn: ScmPair?, private val els: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<TEST >"
        override fun exec(vm: KevesVM) {
            vm.x = if (vm.acc != ScmConstant.FALSE) thn else els
        }
    }

    class AssignLocal(private val n: Int, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<ASSIGN_LOCAL>"
        override fun exec(vm: KevesVM) {
            val box: ScmBox = try {
                vm.stack.index(vm.fp, n) as? ScmBox
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("<ASSIGN_LOCAL> expected box but got other")
            } ?: throw IllegalArgumentException("<ASSIGN_LOCAL> expected box but got other")
            box.value = vm.acc
            vm.x = next
        }
    }

    class AssignFree(private val n: Int, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<ASSIGN_FREE>"
        override fun exec(vm: KevesVM) {
            val box: ScmBox = try {
                vm.clsr?.indexClosure(n) as? ScmBox
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("<ASSIGN_FREE> expected box but got other")
            } ?: throw IllegalArgumentException("<ASSIGN_FREE> expected box but got other")
            box.value = vm.acc
            vm.x = next
        }
    }

    class Conti(private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<CONTI>"
        override fun exec(vm: KevesVM) {
            vm.acc = vm.continuation(vm.sp)
            vm.x = next
        }
    }

    class Nuate(private val s: ScmVector, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<NUATE>"
        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.sp = vm.stack.restoreStack(s)
        }
    }

    class Frame(private val ret: ScmPair?, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<FRAME>"
        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.sp = vm.stack.push(ret, vm.stack.push(ScmInt(vm.fp), vm.stack.push(vm.clsr, vm.sp)))
        }
    }

    class Argument(private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<ARGUMENT>"
        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.sp = vm.stack.push(vm.acc, vm.sp)
        }
    }

    class Shift(private val n: Int, private val m: Int, private val next: ScmPair?) : ScmInstruction() {
        override fun toStringForWrite(): String = "<SHIFT>"
        override fun exec(vm: KevesVM) {
            vm.x = next
            vm.sp = vm.shiftArgs(n, m, vm.sp)
        }
    }

    class Apply(private val n: Int) : ScmInstruction() {
        override fun toStringForWrite(): String = "<APPLY>"
        override fun exec(vm: KevesVM) {
            if (vm.acc is ScmProcedure) {
                (vm.acc as ScmProcedure).normalProc(n, vm)
            } else throw IllegalArgumentException("<APPLY> got non procedure")
        }
    }

    class Return(val n: Int) : ScmInstruction() {
        override fun toStringForWrite(): String = "<RETURN>"
        override fun exec(vm: KevesVM) {
            val sp1 = vm.sp - n
            val s0: ScmPair? = vm.stack.index(sp1, 0)?.let {
                it as? ScmPair
                    ?: throw IllegalArgumentException("SP pointed by <RETURN> did not include pair")
            }
            val s1: Int = (vm.stack.index(sp1, 1) as? ScmInt)?.value
                ?: throw IllegalArgumentException("SP pointed by <RETURN> did not include Int")
            val s2: ScmClosure = vm.stack.index(sp1, 2) as? ScmClosure
                ?: throw IllegalArgumentException("SP pointed by <RETURN> did not include vector")
            // acc = acc
            vm.x = s0
            vm.fp = s1
            vm.clsr = s2
            vm.sp = sp1 - 3
        }
    }
}
