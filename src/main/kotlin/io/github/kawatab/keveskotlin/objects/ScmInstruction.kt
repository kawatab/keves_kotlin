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

class ScmInstruction private constructor(private val instructionType: InstructionType) : ScmObject() {
    @Suppress("SpellCheckingInspection")
    enum class InstructionType {
        HALT,
        REFER_LOCAL,
        REFER_FREE,
        REFER, // TODO("delete it, if unused")
        INDIRECT,
        CONSTANT,
        CLOSE,
        BOX,
        BOX_REST,
        TEST,
        ASSIGN_LOCAL,
        ASSIGN_FREE,
        CONTI,
        NUATE,
        FRAME,
        ARGUMENT,
        SHIFT,
        APPLY,
        RETURN,
    }

    override val type = ObjectType.INSTRUCTION

    override fun toStringForWrite(): String = when (instructionType) {
        InstructionType.HALT -> "<HALT>"
        InstructionType.REFER_LOCAL -> "<REFER_LOCAL>"
        InstructionType.REFER_FREE -> "<REFER_FREE>"
        InstructionType.REFER -> "<REFER>" // TODO("delete it, if unused")
        InstructionType.INDIRECT -> "<INDIRECT>"
        InstructionType.CONSTANT -> "<CONSTANT>"
        InstructionType.CLOSE -> "<CLOSE>"
        InstructionType.BOX -> "<BOX>"
        InstructionType.BOX_REST -> "<BOX_REST>"
        InstructionType.TEST -> "<TEST>"
        InstructionType.ASSIGN_LOCAL -> "<ASSIGN_LOCAL>"
        InstructionType.ASSIGN_FREE -> "<ASSIGN_FREE>"
        InstructionType.CONTI -> @Suppress("SpellCheckingInspection") "<CONTI>"
        InstructionType.NUATE -> @Suppress("SpellCheckingInspection") "<NUATE>"
        InstructionType.FRAME -> "<FRAME>"
        InstructionType.ARGUMENT -> "<ARGUMENT>"
        InstructionType.SHIFT -> "<SHIFT>"
        InstructionType.APPLY -> "<APPLY>"
        InstructionType.RETURN -> "<RETURN>"
    }

    override fun toStringForDisplay(): String = toStringForWrite()
    override fun toString(): String = toStringForWrite()

    companion object {
        val HALT = ScmInstruction(InstructionType.HALT)
        val REFER_LOCAL = ScmInstruction(InstructionType.REFER_LOCAL)
        val REFER_FREE = ScmInstruction(InstructionType.REFER_FREE)
        val REFER = ScmInstruction(InstructionType.REFER) // TODO("delete it, if unused")
        val INDIRECT = ScmInstruction(InstructionType.INDIRECT)
        val CONSTANT = ScmInstruction(InstructionType.CONSTANT)
        val CLOSE = ScmInstruction(InstructionType.CLOSE)
        val BOX = ScmInstruction(InstructionType.BOX)
        val BOX_REST = ScmInstruction(InstructionType.BOX_REST)
        val TEST = ScmInstruction(InstructionType.TEST)
        val ASSIGN_LOCAL = ScmInstruction(InstructionType.ASSIGN_LOCAL)
        val ASSIGN_FREE = ScmInstruction(InstructionType.ASSIGN_FREE)

        @Suppress("SpellCheckingInspection")
        val CONTI = ScmInstruction(InstructionType.CONTI)

        @Suppress("SpellCheckingInspection")
        val NUATE = ScmInstruction(InstructionType.NUATE)
        val FRAME = ScmInstruction(InstructionType.FRAME)
        val ARGUMENT = ScmInstruction(InstructionType.ARGUMENT)
        val SHIFT = ScmInstruction(InstructionType.SHIFT)
        val APPLY = ScmInstruction(InstructionType.APPLY)
        val RETURN = ScmInstruction(InstructionType.RETURN)
    }
}