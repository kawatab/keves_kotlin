/*
 * VMTest.kt
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

import io.github.kawatab.keveskotlin.Keves
import io.github.kawatab.keveskotlin.KevesStack
import io.github.kawatab.keveskotlin.KevesVM
import io.github.kawatab.keveskotlin.objects.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class VMTest {
    private lateinit var vm: KevesVM
    private lateinit var stack: KevesStack
    private lateinit var scheme: Keves

    @Before
    fun setUp() {
        vm = KevesVM()
        stack = KevesStack()
        scheme = Keves()
    }

    @Test
    fun testIndexAndIndexSetE() {
        /*
        val index = vm.javaClass.getDeclaredMethod("stack.index", Int::class.java, Int::class.java)
        index.isAccessible = true
        val indexSetE =
            vm.javaClass.getDeclaredMethod("indexSetE", Int::class.java, Int::class.java, ScmObject::class.java)
        indexSetE.isAccessible = true
         */

        val stringABC = ScmString("abc")
        val stringDEF = ScmString("def")
        val int123 = ScmInt(123)
        val double4p56 = ScmDouble(4.56)

        val tests = listOf(
            listOf(1, 0, stringABC, stringDEF),
            listOf(3, 1, stringDEF, stringABC),
            listOf(4, 1, int123, double4p56),
            listOf(5, 1, double4p56, int123),
            listOf(8, 2, double4p56, stringABC)
        ).map { (s, i, v, w) -> Triple(Triple(s as Int, i as Int, v as ScmObject?), Pair(s, i), Pair(v, w)) }

        tests.forEach { (set, _, _) -> set.let { (s, i, v) -> stack.indexSetE(s, i, v) } }

        tests.forEach { (_, refer, v) ->
            assertEquals(v.first, stack.index(refer.first, refer.second))
            assertNotEquals(v.second, stack.index(refer.first, refer.second))
        }
    }

    @Test
    fun testPatternMatchReferLocal() {
        val patternMatchReferLocal =
            vm.javaClass.getDeclaredMethod("patternMatchReferLocal", ScmPair::class.java)
        patternMatchReferLocal.isAccessible = true

        val int123 = ScmInt(123)
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, int123, next)
        assertEquals(123, (patternMatchReferLocal.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchReferLocal.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchReferFree() {
        val patternMatchReferFree =
            vm.javaClass.getDeclaredMethod("patternMatchReferFree", ScmPair::class.java)
        patternMatchReferFree.isAccessible = true

        val int123 = ScmInt(123)
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, int123, next)
        assertEquals(123, (patternMatchReferFree.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchReferFree.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchIndirect() {
        val patternMatchIndirect =
            vm.javaClass.getDeclaredMethod("patternMatchIndirect", ScmPair::class.java)
        patternMatchIndirect.isAccessible = true

        val next = ScmPair.list(ScmString("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, next)
        assertEquals(next, patternMatchIndirect.invoke(vm, x))
    }

    @Test
    fun testPatternMatchConstant() {
        val patternMatchConstant =
            vm.javaClass.getDeclaredMethod("patternMatchConstant", ScmPair::class.java)
        patternMatchConstant.isAccessible = true

        val obj = ScmConstant.UNDEF
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, obj, next)
        assertEquals(obj, (patternMatchConstant.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchConstant.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchClose() {
        val patternMatchClose =
            vm.javaClass.getDeclaredMethod("patternMatchClose", ScmPair::class.java)
        patternMatchClose.isAccessible = true

        val m = 123
        val n = 456
        val body = ScmPair.list(ScmInstruction.CONSTANT)
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, ScmInt(m), ScmInt(n), body, next)
        assertEquals(m to n, (patternMatchClose.invoke(vm, x) as? Triple<*, *, *>)?.first)
        assertEquals(body, (patternMatchClose.invoke(vm, x) as? Triple<*, *, *>)?.second)
        assertEquals(next, (patternMatchClose.invoke(vm, x) as? Triple<*, *, *>)?.third)
    }

    @Test
    fun testPatternMatchBox() {
        val patternMatchBox =
            vm.javaClass.getDeclaredMethod("patternMatchBox", ScmPair::class.java)
        patternMatchBox.isAccessible = true

        val int123 = ScmInt(123)
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, int123, next)
        assertEquals(123, (patternMatchBox.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchBox.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchTest() {
        val patternMatchTest =
            vm.javaClass.getDeclaredMethod("patternMatchTest", ScmPair::class.java)
        patternMatchTest.isAccessible = true

        val thn = ScmPair.list(ScmInstruction.CONSTANT)
        val els = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, thn, els)
        assertEquals(thn, (patternMatchTest.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(els, (patternMatchTest.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchAssignLocal() {
        val patternMatchAssignLocal =
            vm.javaClass.getDeclaredMethod("patternMatchAssignLocal", ScmPair::class.java)
        patternMatchAssignLocal.isAccessible = true

        val int123 = ScmInt(123)
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, int123, next)
        assertEquals(123, (patternMatchAssignLocal.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchAssignLocal.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchAssignFree() {
        val patternMatchAssignFree =
            vm.javaClass.getDeclaredMethod("patternMatchAssignFree", ScmPair::class.java)
        patternMatchAssignFree.isAccessible = true

        val int123 = ScmInt(123)
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, int123, next)
        assertEquals(123, (patternMatchAssignFree.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchAssignFree.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchConti() {
        val patternMatchConti =
            vm.javaClass.getDeclaredMethod("patternMatchConti", ScmPair::class.java)
        patternMatchConti.isAccessible = true

        val next = ScmPair.list(ScmString("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, next)
        assertEquals(next, patternMatchConti.invoke(vm, x))
    }

    @Test
    fun testPatternMatchNuate() {
        val patternMatchNuate =
            vm.javaClass.getDeclaredMethod("patternMatchNuate", ScmPair::class.java)
        patternMatchNuate.isAccessible = true

        val stack = ScmVector(10)
        val next = ScmPair.list(ScmString("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, stack, next)
        assertEquals(stack, (patternMatchNuate.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchNuate.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchFrame() {
        val patternMatchFrame =
            vm.javaClass.getDeclaredMethod("patternMatchFrame", ScmPair::class.java)
        patternMatchFrame.isAccessible = true

        val ret = ScmPair.list(ScmInstruction.HALT)
        val next = ScmPair.list(ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, ret, next)
        assertEquals(ret, (patternMatchFrame.invoke(vm, x) as? Pair<*, *>)?.first)
        assertEquals(next, (patternMatchFrame.invoke(vm, x) as? Pair<*, *>)?.second)
    }

    @Test
    fun testPatternMatchArgument() {
        val patternMatchArgument =
            vm.javaClass.getDeclaredMethod("patternMatchArgument", ScmPair::class.java)
        patternMatchArgument.isAccessible = true

        val next = ScmPair.list(ScmString("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, next)
        assertEquals(next, patternMatchArgument.invoke(vm, x))
    }

    @Test
    fun testPatternMatchApply() {
        val patternMatchApply =
            vm.javaClass.getDeclaredMethod("patternMatchReturn", ScmPair::class.java)
        patternMatchApply.isAccessible = true

        val n = 150
        val x = ScmPair.list(ScmConstant.TRUE, ScmInt(n))
        assertEquals(n, patternMatchApply.invoke(vm, x))
    }

    @Test
    fun testPatternMatchReturn() {
        val patternMatchReturn =
            vm.javaClass.getDeclaredMethod("patternMatchReturn", ScmPair::class.java)
        patternMatchReturn.isAccessible = true

        val n = 150
        val x = ScmPair.list(ScmConstant.TRUE, ScmInt(n))
        assertEquals(n, patternMatchReturn.invoke(vm, x))
    }

    @Test
    fun test001() {
        assertEquals("123", ScmObject.getStringForDisplay(scheme.evaluate2("(quote 123)")))
    }

    @Test
    fun test002() {
        assertEquals("6", ScmObject.getStringForDisplay(scheme.evaluate2("((lambda (x) (set! x 6)) #f)")))
    }

    @Test
    fun test003() {
        assertEquals(
            "8",
            ScmObject.getStringForDisplay(
                scheme.evaluate2("((lambda (x) (if x x (begin (set! x 8) x))) #f)")
            )
        )
    }

    @Test
    fun test004() {
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("((lambda (x) (if x x (begin (set! x 8) x))) #t)"))
        )
    }

    @Test
    fun test005() {
        assertEquals(
            "6",
            ScmObject.getStringForDisplay(
                scheme.evaluate2("(call/cc (lambda (exit) (begin 1 2 3 4 5 6)))")
            )
        )
        // ScmObject.getStringForDisplay(scheme.evaluate2("(call/cc (lambda (exit) (begin 1 2 3 (exit 4) 5 6)))")
    }

    @Test
    fun test006() {
        assertEquals("#<undef>", ScmObject.getStringForDisplay(scheme.evaluate2("(display \"abc\")")))
    }

    @Test
    fun test007() {
        assertEquals("123", ScmObject.getStringForDisplay(scheme.evaluate2("(+ (+ (+ 123)))")))
    }

    @Test
    fun test008() {
        assertEquals(
            (234 + 345 + 123 + 678 + 901).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(+ (+ 234 345 (+ 123) 678) 901)"))
        )
    }

    @Test
    fun test009() {
        assertEquals("0", ScmObject.getStringForDisplay(scheme.evaluate2("(+)")))
    }

    @Test
    fun test010() {
        assertEquals("123", ScmObject.getStringForDisplay(scheme.evaluate2("(+ 123)")))
    }

    @Test
    fun test011() {
        assertEquals("123.5", ScmObject.getStringForDisplay(scheme.evaluate2("(+ 123.5)")))
    }

    @Test
    fun test012() {
        assertEquals(
            (23.4 + 345 + 123 + 678 + 901).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(+ (+ 23.4 345 (+ 123) 678) 901)"))
        )
    }

    @Test
    fun test013() {
        assertEquals("1", ScmObject.getStringForDisplay(scheme.evaluate2("(*)")))
    }

    @Test
    fun test014() {
        assertEquals("123", ScmObject.getStringForDisplay(scheme.evaluate2("(* 123)")))
    }

    @Test
    fun test015() {
        assertEquals("123.5", ScmObject.getStringForDisplay(scheme.evaluate2("(* 123.5)")))
    }

    @Test
    fun test016() {
        assertEquals(
            ((23.4 + 345 + 123 + 678) * 901).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(* (+ 23.4 345 (+ 123) 678) 901)"))
        )
    }

    @Test
    fun test017() {
        assertEquals(
            (-123.5).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(- 123.5)"))
        )
    }

    @Test
    fun test018() {
        assertEquals(
            ((23.4 - 345 - 123 - 678) * 901).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(* (- 23.4 345 (+ 123) 678) 901)"))
        )
    }

    @Test
    fun test019() {
        assertEquals("1", ScmObject.getStringForDisplay(scheme.evaluate2("(/ 1)")))
    }

    @Test
    fun test020() {
        assertEquals("-1", ScmObject.getStringForDisplay(scheme.evaluate2("(/ -1)")))
    }

    @Test
    fun test021() {
        assertEquals("-1", ScmObject.getStringForDisplay(scheme.evaluate2("(/ -8 4 2)")))
    }

    @Test
    fun test022() {
        assertEquals(
            ((23.4 - 345 - 123 - 678) / 901).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(/ (- 23.4 345 (+ 123) 678) 901)"))
        )
    }

    @Test
    fun test023() {
        assertEquals(
            (Double.POSITIVE_INFINITY).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(/ 0.0)"))
        )
        assertNotEquals(
            (Double.NEGATIVE_INFINITY).toString(),
            ScmObject.getStringForDisplay(scheme.evaluate2("(/ 0.0)"))
        )
    }

    @Test
    fun test024() {
        assertEquals(
            "14",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let () (+ 2 3 4 5))"))
        )
        assertEquals(
            "a",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ([x (list 'a 'b 'c)]) (car x))"))
        )
        assertEquals(
            "(b c)",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ([x (list 'a 'b 'c)]) (cdr x))"))
        )
    }

    @Test
    fun test025() {
        assertEquals(
            "a",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (caar x))"))
        )
        assertEquals(
            "(b 2 3)",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (cadr x))"))
        )
        assertEquals(
            "(1)",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (cdar x))"))
        )
        assertEquals(
            "(c)",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (cddr x))"))
        )
    }

    @Test
    fun test026() {
        assertEquals(
            "#0=(a b c . #0#)",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ((x (list 'a 'b 'c))) (set-cdr! (cddr x) x) x)"))
        )
        assertEquals(
            "(a . #0=(b c . #0#))",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let ((x (list 'a 'b 'c))) (set-cdr! (cddr x) (cdr x)) x)"))
        )
    }

    @Test
    fun testLetStar() {
        assertEquals(
            "15",
            ScmObject.getStringForDisplay(scheme.evaluate2("(let* () (+ 1 2 3 4 5))"))
        )
    }

    @Test
    fun testLetrecStar() {
        assertEquals(
            "15",
            ScmObject.getStringForDisplay(scheme.evaluate2("(letrec* () (+ 1 2 3 4 5))"))
        )
    }

    @Test
    fun testFib5() {
        assertEquals(
            "5",
            ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))\n" +
                            "(fib 5)"
                )
            )
        )
    }

    @Test
    fun testFib15() {
        assertEquals(
            "610",
            ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))\n" +
                            "(fib 15)"
                    // "(fib 38)" // 2m10s285ms 2021-02-03
                    // "(fib 38)" // 56s47ms 2021-02-07
                )
            )
        )
    }

    @Test
    fun testCarCdr() {
        assertEquals("car", ScmObject.getStringForDisplay(scheme.evaluate2("(car '(car . cdr))")))
        assertEquals("cdr", ScmObject.getStringForDisplay(scheme.evaluate2("(cdr '(car . cdr))")))
        assertEquals("caar", ScmObject.getStringForDisplay(scheme.evaluate2("(caar '((caar . cdar) . (cadr . cddr)))")))
        assertEquals("cadr", ScmObject.getStringForDisplay(scheme.evaluate2("(cadr '((caar . cdar) . (cadr . cddr)))")))
        assertEquals("cdar", ScmObject.getStringForDisplay(scheme.evaluate2("(cdar '((caar . cdar) . (cadr . cddr)))")))
        assertEquals("cddr", ScmObject.getStringForDisplay(scheme.evaluate2("(cddr '((caar . cdar) . (cadr . cddr)))")))
        assertEquals(
            "caaar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caaar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "caadr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caadr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "cadar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cadar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "caddr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caddr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "cdaar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "cdadr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdadr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "cddar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cddar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "cdddr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdddr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
            )
        )

        assertEquals(
            "caaaar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caaaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "caaadr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caaadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "caadar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caadar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "caaddr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caaddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cadaar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cadaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cadadr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cadadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "caddar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(caddar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cadddr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cadddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cdaaar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cdaadr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cdadar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdadar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cdaddr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cddaar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cddaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cddadr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cddadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cdddar", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cdddar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )

        assertEquals(
            "cddddr", ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(cddddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
            )
        )
    }

    @Test
    fun testNullQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(null? 'a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(null? '(a b c))")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(null? '())")))
    }

    @Test
    fun testProcLessThan() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(< 1 2 3 2.5)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(< 1.5 3 4 5.5)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(< 105 93 84 75.5)")))
    }

    @Test
    fun testProcGraterThan() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(> 1 2 3 2.5)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(> 1.5 3 4 5.5)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(> 105 93 84 75.5)")))
    }

    @Test
    fun testR7RS0204() {
        assertEquals(
            "#0=(a b c . #0#)",
            ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(let ((x (list 'a 'b 'c)))\n" +
                            "(set-cdr! (cddr x) x)\n" +
                            "x)\n"
                )
            )
        )
    }

    @Test
    fun testR7RS040101() {
        assertEquals(
            "28",
            ScmObject.getStringForDisplay(scheme.evaluate2("(define x 28)\n" + "x\n"))
        )
    }

    @Test
    fun testR7RS040102() {
        assertEquals(
            "a",
            ScmObject.getStringForWrite(scheme.evaluate2("(quote a)\n"))
        )
        assertEquals(
            "#(a b c)",
            ScmObject.getStringForWrite(scheme.evaluate2("(quote #(a b c))"))
        )
        assertEquals(
            "(+ 1 2)",
            ScmObject.getStringForWrite(scheme.evaluate2("(quote (+ 1 2))"))
        )
        assertEquals(
            "a",
            ScmObject.getStringForWrite(scheme.evaluate2("'a\n"))
        )
        assertEquals(
            "#(a b c)",
            ScmObject.getStringForWrite(scheme.evaluate2("'#(a b c)\n"))
        )
        assertEquals(
            "()",
            ScmObject.getStringForWrite(scheme.evaluate2("'()\n"))
        )
        assertEquals(
            "(+ 1 2)",
            ScmObject.getStringForWrite(scheme.evaluate2("'(+ 1 2)\n"))
        )
        assertEquals(
            "(quote a)",
            ScmObject.getStringForWrite(scheme.evaluate2("'(quote a)\n"))
        )
        assertEquals(
            "(quote a)",
            ScmObject.getStringForWrite(scheme.evaluate2("''a\n"))
        )
        assertEquals(
            "145932",
            ScmObject.getStringForWrite(scheme.evaluate2("'145932\n"))
        )
        assertEquals(
            "145932",
            ScmObject.getStringForWrite(scheme.evaluate2("145932\n"))
        )
        assertEquals(
            "\"abc\"",
            ScmObject.getStringForWrite(scheme.evaluate2("'\"abc\"\n"))
        )
        assertEquals(
            "\"abc\"",
            ScmObject.getStringForWrite(scheme.evaluate2("\"abc\"\n"))
        )
        assertEquals(
            "#\\#",
            ScmObject.getStringForWrite(scheme.evaluate2("'#\n"))
        )
        assertEquals(
            "#\\#",
            ScmObject.getStringForWrite(scheme.evaluate2("#\n"))
        )
        assertEquals(
            "#(a 10)",
            ScmObject.getStringForWrite(scheme.evaluate2("'#(a 10)\n"))
        )
        assertEquals(
            "#(a 10)",
            ScmObject.getStringForWrite(scheme.evaluate2("#(a 10)\n"))
        )
        assertEquals(
            "#u8(64 65)",
            ScmObject.getStringForWrite(scheme.evaluate2("'#u8(64 65)"))
        )
        assertEquals(
            "#u8(64 65)",
            ScmObject.getStringForWrite(scheme.evaluate2("#u8(64 65)\n"))
        )
        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("'#t\n"))
        )
        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("#t\n"))
        )
    }

    @Test
    fun testR7RS040103() {
        assertEquals(
            "7",
            ScmObject.getStringForWrite(scheme.evaluate2("(+ 3 4)\n"))
        )
        assertEquals(
            "12",
            ScmObject.getStringForWrite(scheme.evaluate2("((if #f + *) 3 4)\n"))
        )
    }

    @Test
    fun testR7RS040104() {
        assertEquals(
            "#<procedure lambda>",
            ScmObject.getStringForWrite(scheme.evaluate2("(lambda (x) (+ x x))"))
        )
        assertEquals(
            "8",
            ScmObject.getStringForWrite(scheme.evaluate2("((lambda (x) (+ x x)) 4)"))
        )
        assertEquals(
            "3",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define reverse-subtract\n" +
                            "  (lambda (x y) (- y x)))\n" +
                            "(reverse-subtract 7 10)\n"
                )
            )
        )

        assertEquals(
            "10",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define add4\n" +
                            "  (let((x 4))\n" +
                            "    (lambda(y)(+ x y))))\n" +
                            "  (add4 6)"
                )
            )
        )

        assertEquals(
            "(3 4 5 6)",
            ScmObject.getStringForWrite(scheme.evaluate2("((lambda x x) 3 4 5 6)"))
        )

        assertEquals(
            "(5 6)",
            ScmObject.getStringForWrite(scheme.evaluate2("((lambda (x y . z) z) 3 4 5 6)"))
        )
    }

    @Test
    fun testR7RS040105() {
        assertEquals(
            "yes",
            ScmObject.getStringForWrite(scheme.evaluate2("(if (> 3 2) 'yes 'no)"))
        )
        assertEquals(
            "no",
            ScmObject.getStringForWrite(scheme.evaluate2("(if (> 2 3) 'yes 'no)"))
        )
        assertEquals(
            "1",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(if (> 3 2)\n" +
                            "(- 3 2)\n" +
                            "(+ 3 2))\n"
                )
            )
        )
    }

    @Test
    fun testR7RS040106() {
        assertEquals(
            "5",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x 2)\n" +
                            "(+ x 1)\n" +
                            "(set! x 4)\n" +
                            "(+ x 1)\n"
                )
            )
        )
    }

    @Test
    fun testR7RS040201() {
        assertEquals(
            "greater",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(cond ((> 3 2) 'greater)\n" +
                            "((< 3 2) 'less))\n"
                )
            )
        )

        assertEquals(
            "equal",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(cond ((> 3 3) 'greater)\n" +
                            "((< 3 3) 'less)\n" +
                            "(else 'equal))"
                )
            )
        )

        /*
        TODO("must implement assv")
        assertEquals(
            "2",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(cond ((assv 'b '((a 1) (b 2))) => cadr)\n" +
                            "(else #f))"
                )
            )
        )
         */

        assertEquals(
            "#<undef>",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(when (= 1 1.0)\n" +
                            "(display \"1\")\n" +
                            "(display \"2\"))\n"
                )
            )
        )

        assertEquals(
            "#<undef>",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(until (= 1 1.0)\n" +
                            "(display \"1\")\n" +
                            "(display \"2\"))\n"
                )
            )
        )
    }

    @Test
    fun testR7RS040202() {
        assertEquals(
            "6",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x 2) (y 3))\n" +
                            "(* x y))\n"
                )
            )
        )

        assertEquals(
            "35",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x 2) (y 3))\n" +
                            "(let ((x 7)\n" +
                            "(z (+ x y)))\n" +
                            "(* z x)))\n"
                )
            )
        )

        assertEquals(
            "70",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x 2) (y 3))\n" +
                            "(let* ((x 7)\n" +
                            "(z (+ x y)))\n" +
                            "(* z x)))"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(letrec ((even?\n" +
                            "(lambda (n)\n" +
                            "(if (zero? n)\n" +
                            "#t\n" +
                            "(odd? (- n 1)))))\n" +
                            "(odd?\n" +
                            "(lambda (n)\n" +
                            "(if (zero? n)\n" +
                            "#f\n" +
                            "(even? (- n 1))))))\n" +
                            "(even? 88))\n"
                )
            ).replace("\n", "\\n")
        )
    }

    @Test
    fun testR7RS0601() {
        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? 'a 'a)"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? 'a 'b)"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? 2 2)"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? 2 2.0)"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? '() '())"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? 100000000 100000000)"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? 0.0 +nan.0)"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? (cons 1 2) (cons 1 2))"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(eqv? (lambda () 1)\n" +
                            "(lambda () 2))\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((p (lambda (x) x)))\n" +
                            "(eqv? p p))\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? #f 'nil)"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? \"\" \"\")"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? '#() '#())"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(eqv? (lambda (x) x)\n" +
                            "(lambda (x) x))\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(eqv? (lambda (x) x)\n" +
                            "(lambda (y) y))"
                )
            )
        )
        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? 1.0e0 1.0f0)"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eqv? +nan.0 +nan.0)"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define gen-counter\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) n))))\n" +
                            "(let ((g (gen-counter)))\n" +
                            "(eqv? g g))\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define gen-counter\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) n))))\n" +
                            "(let ((g (gen-counter)))\n" +
                            "(eqv? g g))\n" +
                            "(eqv? (gen-counter) (gen-counter))\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define gen-counter\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) n))))\n" +
                            "(let ((g (gen-counter)))\n" +
                            "(eqv? g g))\n" +
                            "(eqv? (gen-counter) (gen-counter))\n" +
                            "(define gen-loser\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) 27))))\n" +
                            "(let ((g (gen-loser)))\n" +
                            "(eqv? g g))\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define gen-counter\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) n))))\n" +
                            "(let ((g (gen-counter)))\n" +
                            "(eqv? g g))\n" +
                            "(eqv? (gen-counter) (gen-counter))\n" +
                            "(define gen-loser\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) 27))))\n" +
                            "(let ((g (gen-loser)))\n" +
                            "(eqv? g g))\n" +
                            "(eqv? (gen-loser) (gen-loser))\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(letrec ((f (lambda () (if (eqv? f g) 'both 'f)))\n" +
                            "(g (lambda () (if (eqv? f g) 'both 'g))))\n" +
                            "(eqv? f g))\n"

                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(letrec ((f (lambda () (if (eqv? f g) 'f 'both)))\n" +
                            "(g (lambda () (if (eqv? f g) 'g 'both))))\n" +
                            "(eqv? f g))\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? 'a 'a)"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? '(a) '(a))\n"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? (list 'a) (list 'a))\n"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? \"a\" \"a\")\n"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? \"\" \"\")\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(eq? '() '())\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? 2 2)\n"))
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? #\\A #\\A)\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(eq? car car)\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((n (+ 2 3)))\n" +
                            "(eq? n n))\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x '(a)))\n" +
                            "(eq? x x))\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x '#()))\n" +
                            "(eq? x x))\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((p (lambda (x) x)))\n" +
                            "(eq? p p)))\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(equal? 'a 'a)\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(equal? '(a) '(a))\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(equal? '(a (b) c)\n" + "'(a (b) c))\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(equal? \"abc\" \"abc\")\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(scheme.evaluate2("(equal? 2 2)\n"))
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(equal? (make-vector 5 'a)\n" +
                            "(make-vector 5 'a))\n"
                )
            )
        )

        /* TODO
        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(equal? '#1=(a b . #1#)\n" +
                    "'#2=(a b a b . #2#))=⇒\n"
                )
            )
        )
         */

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(equal? (lambda (x) x)\n" +
                            "(lambda (y) y))\n"
                )
            )
        )
    }

    @Test
    fun testR7RS0604() {
        assertEquals(
            "(a b c)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n"
                )
            )
        )

        assertEquals(
            "(a . 4)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x 4)\n" +
                            "x\n"
                )
            )
        )

        assertEquals(
            "#t",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x 4)\n" +
                            "x\n" +
                            "(eqv? x y)\n"
                )
            )
        )

        assertEquals(
            "(a . 4)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x 4)\n" +
                            "x\n" +
                            "(eqv? x y)\n" +
                            "y\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x 4)\n" +
                            "x\n" +
                            "(eqv? x y)\n" +
                            "y\n" +
                            "(list? y)\n"
                )
            )
        )

        assertEquals(
            "#f",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x 4)\n" +
                            "x\n" +
                            "(eqv? x y)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x x)\n" +
                            "(list? x)\n"
                )
            )
        )

        assertEquals(
            "(#t #t #f #f)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (pair? '(a . b))\n" +
                            "(pair? '(a b c))\n" +
                            "(pair? '())\n" +
                            "(pair? '#(a b)))\n"
                )
            )
        )

        assertEquals(
            "((a) ((a) b c d) (\"a\" b c) (a . 3) ((a b) . c))",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (cons 'a '())\n" +
                            "(cons '(a) '(b c d))\n" +
                            "(cons \"a\" '(b c))\n" +
                            "(cons 'a 3)\n" +
                            "(cons '(a b) 'c))\n"
                )
            )
        )

        assertEquals(
            "(a (a) 1)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (car '(a b c))\n" +
                            "(car '((a) b c d))\n" +
                            "(car '(1 . 2)))\n"
                )
            )
        )

        assertEquals(
            "*** ERROR: car expected pair but got other\\n who: vm",
            ScmObject.getStringForWrite(scheme.evaluate2("(car '())")).replace("\n", "\\n")
        )

        assertEquals(
            "((b c d) 2)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (cdr '((a) b c d))\n" +
                            "(cdr '(1 . 2)))\n"
                )
            )
        )

        assertEquals(
            "*** ERROR: cdr expected pair but got other\\n who: vm",
            ScmObject.getStringForWrite(scheme.evaluate2("(cdr '())\n")).replace("\n", "\\n")
        )

        assertEquals(
            "#<undef>",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define (f) (list 'not-a-constant-list))\n" +
                            "(define (g) '(constant-list))\n" +
                            "(set-car! (f) 3)\n"
                )
            ).replace("\n", "\\n")
        )

        assertEquals(
            "*** ERROR: set-car! expected mutable pair but got other\\n who: vm",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define (f) (list 'not-a-constant-list))\n" +
                            "(define (g) '(constant-list))\n" +
                            "(set-car! (f) 3)\n" +
                            "(set-car! (g) 3)\n"
                )
            ).replace("\n", "\\n")
        )

        assertEquals(
            "(3 3)",
            ScmObject.getStringForWrite(scheme.evaluate2("(make-list 2 3)"))
        )

        assertEquals(
            "(a 7 c)",
            ScmObject.getStringForWrite(scheme.evaluate2("(list 'a (+ 3 4) 'c)"))
        )

        assertEquals(
            "()",
            ScmObject.getStringForWrite(scheme.evaluate2("(list)"))
        )

        assertEquals(
            "(3 3 0)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (length '(a b c))\n" +
                            "(length '(a (b) (c d e)))\n" +
                            "(length '()))"
                )
            )
        )

        assertEquals(
            "((x y) (a b c d) (a (b) (c)) (a b c . d) a)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (append '(x) '(y))\n" +
                            "(append '(a) '(b c d))\n" +
                            "(append '(a (b)) '((c)))\n" +
                            "(append '(a b) '(c . d))\n" +
                            "(append '() 'a))\n"
                )
            )
        )

        assertEquals(
            "((c b a) ((e (f)) d (b c) a))",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (reverse '(a b c))\n" +
                            "(reverse '(a (b c) d (e (f)))))\n"
                )
            )
        )
    }
}
