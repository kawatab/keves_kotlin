/*
 * CompilerTest.kt
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

import io.github.kawatab.keveskotlin.KevesResources
import io.github.kawatab.keveskotlin.PtrPair
import io.github.kawatab.keveskotlin.libraries.R7rs
import io.github.kawatab.keveskotlin.objects.*
import org.junit.Before
import org.junit.Test
import java.lang.reflect.InvocationTargetException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CompilerTest {
    private lateinit var compiler: R7rs
    private val res = KevesResources()

    @Before
    fun setUp() {
        compiler = R7rs(res)
    }

    @Test
    fun testPatternMatchQuote() {
        val patternMatchQuote =
            compiler.javaClass.getDeclaredMethod("patternMatchQuote", PtrPair::class.java)
        patternMatchQuote.isAccessible = true

        val int123 = ScmInt.make(123, res).toObject()
        // should be failed
        val wrongX = ScmPair.list(res.constTrue, int123, res.constUndef, res)
        assertFailsWith<InvocationTargetException> {
            patternMatchQuote.invoke(compiler, wrongX)
        }
        // should be succeed
        val goodX = ScmPair.list(ScmSymbol.get("quote", res).toObject(), int123, res)
        assertEquals(patternMatchQuote.invoke(compiler, goodX.toVal(res)), int123)
    }

    @Test
    fun testPatternMatchLambda() {
        val patternMatchLambda =
            compiler.javaClass.getDeclaredMethod("patternMatchLambda", ScmPair::class.java)
        patternMatchLambda.isAccessible = true

        val vars = ScmPair.list(
            ScmSymbol.get("abc", res).toObject(),
            ScmSymbol.get("def", res).toObject(),
            ScmSymbol.get("ghi", res).toObject(),
            res
        )
        val body = ScmPair.list(ScmSymbol.get("jkl", res).toObject(), ScmInt.make(123, res).toObject(), ScmInt.make(456, res).toObject(), res)

        val wrongX = ScmPair.make(ScmSymbol.get("lambda", res).toObject(), vars.toObject(), res)
        val match1 = patternMatchLambda.invoke(compiler, wrongX.toVal(res)) as? Pair<*, *>
        assertEquals(match1?.first, vars.car(res))
        assertEquals(match1?.second, vars.cdr(res).toPair())


        val goodX = ScmPair.listStar(ScmSymbol.get("lambda", res).toObject(), vars.toObject(), body.toObject(), res)
        val match = patternMatchLambda.invoke(compiler, goodX.toVal(res)) as? Pair<*, *>
        assertEquals(match?.first, vars.toObject())
        assertEquals(match?.second, body.toPairNonNull())
    }

    @Test
    fun testPatternMatchIf() {
        val patternMatchIf =
            compiler.javaClass.getDeclaredMethod("patternMatchIf", ScmPair::class.java)
        patternMatchIf.isAccessible = true

        val test = ScmPair.list(
            ScmSymbol.get("abc", res).toObject(),
            ScmSymbol.get("def", res).toObject(),
            ScmSymbol.get("ghi", res).toObject(),
            res
        )
        val thn = ScmPair.list(ScmSymbol.get("jkl", res).toObject(), ScmInt.make(123, res).toObject(), ScmInt.make(456, res).toObject(), res)
        val els =
            ScmPair.list(ScmSymbol.get("mno", res).toObject(), ScmInt.make(789, res).toObject(), ScmDouble.make(0.12, res).toObject(), res)
        // should be failed
        val wrongX = ScmPair.list(
            ScmSymbol.get("if", res).toObject(),
            test.toObject(),
            thn.toObject(),
            els.toObject(),
            res.constUndef,
            res
        )
        assertFailsWith<InvocationTargetException> {
            patternMatchIf.invoke(compiler, wrongX.toVal(res)) as? Triple<*, *, *>
        }
        // should be succeed
        val goodX =
            ScmPair.list(ScmSymbol.get("if", res).toObject(), test.toObject(), thn.toObject(), els.toObject(), res)
        val match = patternMatchIf.invoke(compiler, goodX.toVal(res)) as? Triple<*, *, *>
        assertEquals(test.toObject(), match?.first)
        assertEquals(thn.toObject(), match?.second)
        assertEquals(els.toObject(), match?.third)
    }

    @Test
    fun testPatternMatchSetE() {
        val patternMatchSetE =
            compiler.javaClass.getDeclaredMethod("patternMatchSetE", PtrPair::class.java)
        patternMatchSetE.isAccessible = true

        val variable = ScmSymbol.get("abc", res)
        val x = ScmPair.list(ScmSymbol.get("jkl", res).toObject(), ScmInt.make(123, res).toObject(), ScmInt.make(456, res).toObject(), res)
        // should be failed
        val wrongX =
            ScmPair.list(ScmSymbol.get("set!", res).toObject(), variable.toObject(), x.toObject(), res.constUndef, res)
        assertFailsWith<InvocationTargetException> {
            patternMatchSetE.invoke(compiler, wrongX.toVal(res)) as? Pair<*, *>
        }
        // should be succeed
        val goodX = ScmPair.list(ScmSymbol.get("set!", res).toObject(), variable.toObject(), x.toObject(), res)
        val match = patternMatchSetE.invoke(compiler, goodX.toVal(res)) as? Pair<*, *>
        assertEquals(expected = match?.first, actual = variable)
        assertEquals(expected = match?.second, actual = x.toObject())
    }

    @Test
    fun testPatternMatchCallCC() {
        val patternMatchCallCC =
            compiler.javaClass.getDeclaredMethod("patternMatchCallCC", PtrPair::class.java)
        patternMatchCallCC.isAccessible = true

        val x = ScmPair.list(ScmSymbol.get("jkl", res).toObject(), ScmInt.make(123, res).toObject(), ScmInt.make(456, res).toObject(), res)
        // should be failed
        val wrongX = ScmPair.list(ScmSymbol.get("call/cc", res).toObject(), x.toObject(), res.constUndef, res)
        assertFailsWith<InvocationTargetException> {
            patternMatchCallCC.invoke(compiler, wrongX)
        }
        // should be succeed
        val goodX = ScmPair.list(ScmSymbol.get("call/cc", res).toObject(), x.toObject(), res)
        assertEquals(patternMatchCallCC.invoke(compiler, goodX), x)
    }
}