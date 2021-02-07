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

import io.github.kawatab.keveskotlin.libraries.R7rs
import io.github.kawatab.keveskotlin.objects.*
import org.junit.Before
import org.junit.Test
import java.lang.reflect.InvocationTargetException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CompilerTest {
    private lateinit var compiler: R7rs

    @Before
    fun setUp() {
        compiler = R7rs()
    }

    @Test
    fun testPatternMatchQuote() {
        val patternMatchQuote =
            compiler.javaClass.getDeclaredMethod("patternMatchQuote", ScmPair::class.java)
        patternMatchQuote.isAccessible = true

        val int123 = ScmInt(123)
        // should be failed
        val wrongX = ScmPair.list(ScmConstant.TRUE, int123, ScmConstant.UNDEF)
        assertFailsWith<InvocationTargetException> {
            patternMatchQuote.invoke(compiler, wrongX)
        }
        // should be succeed
        val goodX = ScmPair.list(ScmSymbol.get("quote"), int123)
        assertEquals(patternMatchQuote.invoke(compiler, goodX), int123)
    }

    @Test
    fun testPatternMatchLambda() {
        val patternMatchLambda =
            compiler.javaClass.getDeclaredMethod("patternMatchLambda", ScmPair::class.java)
        patternMatchLambda.isAccessible = true

        val vars = ScmPair.list(ScmSymbol.get("abc"), ScmSymbol.get("def"), ScmSymbol.get("ghi"))
        val body = ScmPair.list(ScmSymbol.get("jkl"), ScmInt(123), ScmInt(456))

        val wrongX = ScmPair(ScmSymbol.get("lambda"), vars)
        val match1 = patternMatchLambda.invoke(compiler, wrongX) as? Pair<*, *>
        assertEquals(match1?.first, vars.car as? ScmSymbol)
        assertEquals(match1?.second, vars.cdr as? ScmPair)


        val goodX = ScmPair(ScmSymbol.get("lambda"), ScmPair(vars, body))
        val match = patternMatchLambda.invoke(compiler, goodX) as? Pair<*, *>
        assertEquals(match?.first, vars)
        assertEquals(match?.second, body)
    }

    @Test
    fun testPatternMatchIf() {
        val patternMatchIf =
            compiler.javaClass.getDeclaredMethod("patternMatchIf", ScmPair::class.java)
        patternMatchIf.isAccessible = true

        val test = ScmPair.list(ScmSymbol.get("abc"), ScmSymbol.get("def"), ScmSymbol.get("ghi"))
        val thn = ScmPair.list(ScmSymbol.get("jkl"), ScmInt(123), ScmInt(456))
        val els = ScmPair.list(ScmSymbol.get("mno"), ScmInt(789), ScmDouble(0.12))
        // should be failed
        val wrongX = ScmPair.list(ScmSymbol.get("if"), test, thn, els, ScmConstant.UNDEF)
        assertFailsWith<InvocationTargetException> {
            patternMatchIf.invoke(compiler, wrongX) as? Triple<*, *, *>
        }
        // should be succeed
        val goodX = ScmPair.list(ScmSymbol.get("if"), test, thn, els)
        val match = patternMatchIf.invoke(compiler, goodX) as? Triple<*, *, *>
        assertEquals(match?.first, test)
        assertEquals(match?.second, thn)
        assertEquals(match?.third, els)
    }

    @Test
    fun testPatternMatchSetE() {
        val patternMatchSetE =
            compiler.javaClass.getDeclaredMethod("patternMatchSetE", ScmPair::class.java)
        patternMatchSetE.isAccessible = true

        val variable = ScmSymbol.get("abc")
        val x = ScmPair.list(ScmSymbol.get("jkl"), ScmInt(123), ScmInt(456))
        // should be failed
        val wrongX = ScmPair.list(ScmSymbol.get("set!"), variable, x, ScmConstant.UNDEF)
        assertFailsWith<InvocationTargetException> {
            patternMatchSetE.invoke(compiler, wrongX) as? Pair<*, *>
        }
        // should be succeed
        val goodX = ScmPair.list(ScmSymbol.get("set!"), variable, x)
        val match = patternMatchSetE.invoke(compiler, goodX) as? Pair<*, *>
        assertEquals(match?.first, variable)
        assertEquals(match?.second, x)
    }

    @Test
    fun testPatternMatchCallCC() {
        val patternMatchCallCC =
            compiler.javaClass.getDeclaredMethod("patternMatchCallCC", ScmPair::class.java)
        patternMatchCallCC.isAccessible = true

        val x = ScmPair.list(ScmSymbol.get("jkl"), ScmInt(123), ScmInt(456))
        // should be failed
        val wrongX = ScmPair.list(ScmSymbol.get("call/cc"), x, ScmConstant.UNDEF)
        assertFailsWith<InvocationTargetException> {
            patternMatchCallCC.invoke(compiler, wrongX)
        }
        // should be succeed
        val goodX = ScmPair.list(ScmSymbol.get("call/cc"), x)
        assertEquals(patternMatchCallCC.invoke(compiler, goodX), x)
    }
}