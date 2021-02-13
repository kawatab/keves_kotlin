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
                    // "(fib 38)" // 56s47ms 2021-02-07; remove unused arguments in normalProc of ScmProcedure
                    // "(fib 38)" // 47s943ms 2021-02-08; remove unused type property in ScmObject
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
    fun testListTail() {
        assertEquals("(d)", ScmObject.getStringForDisplay(scheme.evaluate2("(list-tail '(a b c d) 3)")))
        assertEquals("(f)", ScmObject.getStringForDisplay(scheme.evaluate2("(list-tail '(a b c d e f) 5)")))
        assertEquals("(a b c d)", ScmObject.getStringForDisplay(scheme.evaluate2("(list-tail '(a b c d) 0)")))
    }

    @Test
    fun testListRef() {
        assertEquals("d", ScmObject.getStringForDisplay(scheme.evaluate2("(list-ref '(a b c d) 3)")))
        assertEquals("f", ScmObject.getStringForDisplay(scheme.evaluate2("(list-ref '(a b c d e f) 5)")))
        assertEquals("a", ScmObject.getStringForDisplay(scheme.evaluate2("(list-ref '(a b c d) 0)")))
    }

    @Test
    fun testSymbolEqualQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(symbol=? 'a 'b 'c 'd)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'abc 'abc 'abc)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'abc 'abc 'ab)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'bc 'abc 'abc)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'bc)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'abc)")))
    }

    @Test
    fun testStringEqualQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(string=? \"a\" \"b\" \"c\" \"d\")")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"abc\" \"abc\" \"abc\")"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"abc\" \"abc\" \"ab\")")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"bc\" \"abc\" \"abc\")")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"bc\")")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"abc\")")))
    }

    @Test
    fun testCharEqualQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\b #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\b)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\A #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\B #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\B)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\A)")))
    }

    @Test
    fun testCharLessThanQ() {
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\b #\\a #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\b)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\B #\\c #\\d)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\A #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\B #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\B)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\A)")))
    }

    @Test
    fun testCharLessThanEqualQ() {
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\b #\\a #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\b)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\A #\\a #\\A)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\B #\\a #\\A)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\B)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\A)")))
    }

    @Test
    fun testCharGraterThanQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\b #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\b)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\a #\\A #\\a)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\a #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\b #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\b)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\a)")))
    }

    @Test
    fun testCharGraterThanEqualQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\b #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\b)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\A #\\a #\\A)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\B #\\a #\\A)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\B)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\A)")))
    }

    @Test
    fun testCharCIEqualQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\b #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\b)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\A #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\B #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\B)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\A)")))
    }

    @Test
    fun testCharCILessThanQ() {
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\b #\\a #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\b)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\B #\\c #\\d)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\A #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\B #\\A #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\B)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\A)")))
    }

    @Test
    fun testCharCILessThanEqualQ() {
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\b #\\a #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\b)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\A #\\a #\\A)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\B #\\a #\\A)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\B)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\A)")))
    }

    @Test
    fun testCharCIGraterThanQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\b #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\b)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#f",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\a #\\A #\\a)"))
        )
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\a #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\b #\\A #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\b)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\a)")))
    }

    @Test
    fun testCharCIGraterThanEqualQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\b #\\c #\\d)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\a #\\a #\\a)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\a #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\b #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\b)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\B #\\c #\\D)")))
        assertEquals(
            "#t",
            ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\A #\\a #\\A)"))
        )
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\A #\\a #\\A)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\B #\\a #\\A)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\B)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\A)")))
    }

    @Test
    fun testCharAlphabeticQ() {
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\B)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\λ)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\1)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\x0664)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\x0AE6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\x0EA6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\.)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\ )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\tab )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\return )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\newline )")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\語)")))
    }

    @Test
    fun testCharNumericQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\B)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\λ)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\1)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\x0664)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\x0AE6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\x0EA6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\.)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\ )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\tab )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\return )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\newline )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\語)")))
    }

    @Test
    fun testCharWhitespaceQ() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\B)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\λ)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\1)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\x0664)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\x0AE6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\x0EA6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\.)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\ )")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\tab )")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\return )")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\newline )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\語)")))
    }

    @Test
    fun testDigitValue() {
        assertEquals("3", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\3)\n")))
        assertEquals("4", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0664)\n")))
        assertEquals("0", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0AE6)\n")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0EA6)\n")))
    }

    @Test
    fun testCharToInteger() {
        assertEquals("51", ScmObject.getStringForDisplay(scheme.evaluate2("(char->integer #\\3)\n")))
        assertEquals("1636", ScmObject.getStringForDisplay(scheme.evaluate2("(char->integer #\\x0664)\n")))
        assertEquals("2790", ScmObject.getStringForDisplay(scheme.evaluate2("(char->integer #\\x0AE6)\n")))
        assertEquals("3750", ScmObject.getStringForDisplay(scheme.evaluate2("(char->integer #\\x0EA6)\n")))
        assertEquals("69944", ScmObject.getStringForDisplay(scheme.evaluate2("(char->integer #\\\uD804\uDD38)\n")))
    }

    @Test
    fun testIntegerToChar() {
        assertEquals("3", ScmObject.getStringForDisplay(scheme.evaluate2("(integer->char 51)\n")))
        assertEquals("٤", ScmObject.getStringForDisplay(scheme.evaluate2("(integer->char 1636)\n")))
        assertEquals("૦", ScmObject.getStringForDisplay(scheme.evaluate2("(integer->char 2790)\n")))
        assertEquals("\u0EA6", ScmObject.getStringForDisplay(scheme.evaluate2("(integer->char 3750)\n")))
        assertEquals("\uD804\uDD38", ScmObject.getStringForDisplay(scheme.evaluate2("(integer->char 69944)\n")))
    }

    @Test
    fun testCharUpperCase() {
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\a)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\B)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\λ)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\1)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\x0664)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\x0AE6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\x0EA6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\.)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\ )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\tab )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\return )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\newline )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\語)")))
    }

    @Test
    fun testCharLowerCase() {
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\a)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\B)")))
        assertEquals("#t", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\λ)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\1)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\x0664)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\x0AE6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\x0EA6)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\.)")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\ )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\tab )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\return )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\newline )")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\語)")))
    }

    @Test
    fun testCharUpcase() {
        assertEquals("#\\A", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\a)")))
        assertEquals("#\\B", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\B)")))
        assertEquals("#\\Λ", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\λ)")))
        assertEquals("#\\1", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\1)")))
        assertEquals("#\\\u0664", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\x0664)")))
        assertEquals("#\\\u0AE6", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\x0AE6)")))
        assertEquals("#\\\u0EA6", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\x0EA6)")))
        assertEquals("#\\.", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\.)")))
        assertEquals("#\\space", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\ )")))
        assertEquals("#\\tab", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\tab )")))
        assertEquals("#\\return", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\return )")))
        assertEquals("#\\newline", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\newline )")))
        assertEquals("#\\語", ScmObject.getStringForWrite(scheme.evaluate2("(char-upcase #\\語)")))
    }

    @Test
    fun testCharDowncase() {
        assertEquals("#\\a", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\a)")))
        assertEquals("#\\b", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\B)")))
        assertEquals("#\\λ", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\λ)")))
        assertEquals("#\\1", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\1)")))
        assertEquals("#\\\u0664", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\x0664)")))
        assertEquals("#\\\u0AE6", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\x0AE6)")))
        assertEquals("#\\\u0EA6", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\x0EA6)")))
        assertEquals("#\\.", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\.)")))
        assertEquals("#\\space", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\ )")))
        assertEquals("#\\tab", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\tab )")))
        assertEquals("#\\return", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\return )")))
        assertEquals("#\\newline", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\newline )")))
        assertEquals("#\\語", ScmObject.getStringForWrite(scheme.evaluate2("(char-downcase #\\語)")))
    }

    @Test
    fun testCharFoldcase() {
        assertEquals("#\\a", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\a)")))
        assertEquals("#\\b", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\B)")))
        assertEquals("#\\λ", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\λ)")))
        assertEquals("#\\1", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\1)")))
        assertEquals("#\\\u0664", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\x0664)")))
        assertEquals("#\\\u0AE6", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\x0AE6)")))
        assertEquals("#\\\u0EA6", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\x0EA6)")))
        assertEquals("#\\.", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\.)")))
        assertEquals("#\\space", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\ )")))
        assertEquals("#\\tab", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\tab )")))
        assertEquals("#\\return", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\return )")))
        assertEquals("#\\newline", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\newline )")))
        assertEquals("#\\語", ScmObject.getStringForWrite(scheme.evaluate2("(char-foldcase #\\語)")))
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
        TODO("must implement =>")
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
            "*** ERROR: 'car' expected a pair, but got other\\n who: vm",
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
            "*** ERROR: 'cdr' expected a pair, but got other\\n who: vm",
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
            "*** ERROR: 'set-car!' expected a mutable pair, but got other\\n who: vm",
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

        assertEquals(
            "c",
            ScmObject.getStringForWrite(scheme.evaluate2("(list-ref '(a b c d) 2)"))
        )

        /* TODO
        assertEquals(
            "c",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list-ref '(a b c d)\n" +
                            "(exact (round 1.8)))"
                )
            )
        )
         */

        assertEquals(
            "(one two three)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(let ((ls (list 'one 'two 'five!)))\n" +
                            "(list-set! ls 2 'three)\n" +
                            "ls)\n"
                )
            )
        )

        assertEquals(
            "*** ERROR: 'list-set!' expected a mutable pair, but got other\\n who: vm",
            ScmObject.getStringForWrite(scheme.evaluate2("(list-set! '(0 1 2) 1 \"oops\")\n")).replace("\n", "\\n")
        )

        assertEquals(
            "((a b c) (b c) #f #f ((a) c)" +
                    // // " (\"b\" \"c\")\n" +
                    " #f (101 102))",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (memq 'a '(a b c))\n" +
                            "(memq 'b '(a b c))\n" +
                            "(memq 'a '(b c d))\n" +
                            "(memq (list 'a) '(b (a) c))\n" +
                            "(member (list 'a)\n" +
                            "'(b (a) c))\n" +
                            // // "(member \"B\"\n" +
                            // // "’(\"a\" \"b\" \"c\")\n" +
                            // // "string-ci=?)\n" +  // TODO
                            "(memq 101 '(100 101 102))\n" +
                            "(memv 101 '(100 101 102)))\n",
                )
            ).replace("\n", "\\n")
        )

        assertEquals(
            "((a 1) (b 2) #f #f ((a)) " +
                    // "(2 4) " +
                    "#f (5 7))",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define e '((a 1) (b 2) (c 3)))\n" +
                            "(list (assq 'a e)\n" +
                            "(assq 'b e)\n" +
                            "(assq 'd e)\n" +
                            "(assq (list 'a) '(((a)) ((b)) ((c))))\n" +
                            "(assoc (list 'a) '(((a)) ((b)) ((c))))\n" +
                            // "(assoc 2.0 '((1 1) (2 4) (3 9)) =)\n" + // TODO
                            "(assq 5 '((2 3) (5 7) (11 13)))\n" +
                            "(assv 5 '((2 3) (5 7) (11 13))))\n"
                )
            )
        )

        assertEquals(
            "((3 8 2 8) (1 8 2 8))",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(define a '(1 8 2 8)) ; a may be immutable\n" +
                            "(define b (list-copy a))\n" +
                            "(set-car! b 3) ; b is mutable\n" +
                            "(list b a)",
                )
            )
        )
    }

    @Test
    fun testR7RS0605() {
        assertEquals(
            "(#t #t #f #t #f #f)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (symbol? 'foo)\n" +
                            "(symbol? (car '(a b)))\n" +
                            "(symbol? \"bar\")\n" +
                            "(symbol? 'nil)\n" +
                            "(symbol? '())\n" +
                            "(symbol? #f))\n"
                )
            )
        )

        assertEquals(
            "(\"flying-fish\" \"Martin\" \"Malvina\")",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (symbol->string 'flying-fish)\n" +
                            "(symbol->string 'Martin)\n" +
                            "(symbol->string\n" +
                            "(string->symbol \"Malvina\")))\n",
                )
            )
        )

        assertEquals(
            "(mISSISSIppi #t #t #t)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list (string->symbol \"mISSISSIppi\")\n" +
                            "(eqv? 'bitBlt (string->symbol \"bitBlt\"))\n" +
                            "(eqv? 'LollyPop\n" +
                            "(string->symbol\n" +
                            "(symbol->string 'LollyPop)))\n" +
                            "(string=? \"K. Harper, M.D.\"\n" +
                            "(symbol->string\n" +
                            "(string->symbol \"K. Harper, M.D.\"))))\n",
                )
            )
        )

        assertEquals(
            "(#\\alarm #\\backspace #\\delete #\\escape #\\newline #\\null #\\return #\\space #\\tab)",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list #\\alarm ; U+0007\n" +
                            "#\\backspace ; U+0008\n" +
                            "#\\delete ; U+007F\n" +
                            "#\\escape ; U+001B\n" +
                            "#\\newline ; the linefeed character, U+000A\n" +
                            "#\\null ; the null character, U+0000\n" +
                            "#\\return ; the return character, U+000D\n" +
                            "#\\space ; the preferred way to write a space\n" +
                            "#\\tab ; the tab character, U+0009\n" +
                            ")",
                )
            )
        )

        assertEquals(
            """(#\a #\A #\( #\space #\λ)""", // #\ι)""",
            ScmObject.getStringForWrite(
                scheme.evaluate2(
                    "(list #\\a ; lower case letter\n" +
                            "#\\A ; upper case letter\n" +
                            "#\\( ; left parenthesis\n" +
                            "#\\ ; the space character\n" +
                            "#\\x03BB ; λ (if character is supported)\n" +
                            // "#\\iota ; ι (if character and name are supported)\n" +
                            ")",
                )
            )
        )
    }

    @Test
    fun testR7RS0606() {
        assertEquals("3", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\3)\n")))
        assertEquals("4", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0664)\n")))
        assertEquals("0", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0AE6)\n")))
        assertEquals("#f", ScmObject.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0EA6)\n")))
        assertEquals(
            "(0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4)",
            ScmObject.getStringForDisplay(
                scheme.evaluate2(
                    "(list\n" +
                            "(digit-value #\\x0030) ;; DIGIT ZERO - 0\n" +
                            "(digit-value #\\x0661) ;; ARABIC-INDIC DIGIT ONE - ١\n" +
                            "(digit-value #\\x06F2) ;; EXTENDED ARABIC-INDIC DIGIT TWO - ۲\n" +
                            "(digit-value #\\x07C3) ;; NKO DIGIT THREE - \n" +
                            "(digit-value #\\x096A) ;; DEVANAGARI DIGIT FOUR - ४\n" +
                            "(digit-value #\\x09EB) ;; BENGALI DIGIT FIVE - ৫\n" +
                            "(digit-value #\\x0A6C) ;; GURMUKHI DIGIT SIX - ੬\n" +
                            "(digit-value #\\x0AED) ;; GUJARATI DIGIT SEVEN - ૭\n" +
                            "(digit-value #\\x0B6E) ;; ORIYA DIGIT EIGHT - ୮\n" +
                            "(digit-value #\\x0BEF) ;; TAMIL DIGIT NINE - ௯\n" +
                            "(digit-value #\\x0C66) ;; TELUGU DIGIT ZERO - ౦\n" +
                            "(digit-value #\\x0CE7) ;; KANNADA DIGIT ONE - ೧\n" +
                            "(digit-value #\\x0D68) ;; MALAYALAM DIGIT TWO - ൨\n" +
                            "(digit-value #\\x0DE9) ;; SINHALA LITH DIGIT THREE - ෩\n" +
                            "(digit-value #\\x0E54) ;; THAI DIGIT FOUR - ๔\n" +
                            "(digit-value #\\x0ED5) ;; LAO DIGIT FIVE - ໕\n" +
                            "(digit-value #\\x0F26) ;; TIBETAN DIGIT SIX - ༦\n" +
                            "(digit-value #\\x1047) ;; MYANMAR DIGIT SEVEN - ၇\n" +
                            "(digit-value #\\x1098) ;; MYANMAR SHAN DIGIT EIGHT - ႘\n" +
                            "(digit-value #\\x17E9) ;; KHMER DIGIT NINE - ៩\n" +
                            "(digit-value #\\x1810) ;; MONGOLIAN DIGIT ZERO - ᠐\n" +
                            "(digit-value #\\x1947) ;; LIMBU DIGIT ONE - ᥇\n" +
                            "(digit-value #\\x19D2) ;; NEW TAI LUE DIGIT TWO - ᧒\n" +
                            "(digit-value #\\x1A83) ;; TAI THAM HORA DIGIT THREE - ᪃\n" +
                            "(digit-value #\\x1A94) ;; TAI THAM THAM DIGIT FOUR - ᪔\n" +
                            "(digit-value #\\x1B55) ;; BALINESE DIGIT FIVE - ᭕\n" +
                            "(digit-value #\\x1BB6) ;; SUNDANESE DIGIT SIX - ᮶\n" +
                            "(digit-value #\\x1C47) ;; LEPCHA DIGIT SEVEN - ᱇\n" +
                            "(digit-value #\\x1C58) ;; OL CHIKI DIGIT EIGHT - ᱘\n" +
                            "(digit-value #\\xA629) ;; VAI DIGIT NINE - ꘩\n" +
                            "(digit-value #\\xA8D0) ;; SAURASHTRA DIGIT ZERO - ꣐\n" +
                            "(digit-value #\\xA901) ;; KAYAH LI DIGIT ONE - ꤁\n" +
                            "(digit-value #\\xA9D2) ;; JAVANESE DIGIT TWO - ꧒\n" +
                            "(digit-value #\\xA9F3) ;; MYANMAR TAI LAING DIGIT THREE - ꧳\n" +
                            "(digit-value #\\xAA54) ;; CHAM DIGIT FOUR - ꩔\n" +
                            "(digit-value #\\xABF5) ;; MEETEI MAYEK DIGIT FIVE - ꯵\n" +
                            "(digit-value #\\xFF16) ;; FULLWIDTH DIGIT SIX - ６\n" +
                            "(digit-value #\\x104A7) ;; OSMANYA DIGIT SEVEN - 𐒧\n" +
                            "(digit-value #\\x10D38) ;; HANIFI ROHINGYA DIGIT EIGHT - 𐴸\n" +
                            "(digit-value #\\x1106F) ;; BRAHMI DIGIT NINE - 𑁯\n" +
                            "(digit-value #\\x110F0) ;; SORA SOMPENG DIGIT ZERO - 𑃰\n" +
                            "(digit-value #\\x11137) ;; CHAKMA DIGIT ONE - 𑄷\n" +
                            "(digit-value #\\x111D2) ;; SHARADA DIGIT TWO - 𑇒\n" +
                            "(digit-value #\\x112F3) ;; KHUDAWADI DIGIT THREE - 𑋳\n" +
                            "(digit-value #\\x11454) ;; NEWA DIGIT FOUR - 𑑔\n" +
                            "(digit-value #\\x114D5) ;; TIRHUTA DIGIT FIVE - 𑓕\n" +
                            "(digit-value #\\x11656) ;; MODI DIGIT SIX - 𑙖\n" +
                            "(digit-value #\\x116C7) ;; TAKRI DIGIT SEVEN - 𑛇\n" +
                            "(digit-value #\\x11738) ;; AHOM DIGIT EIGHT - 𑜸\n" +
                            "(digit-value #\\x118E9) ;; WARANG CITI DIGIT NINE - 𑣩\n" +
                            "(digit-value #\\x11950) ;; DIVES AKURU DIGIT ZERO - 𑥐\n" +
                            "(digit-value #\\x11C51) ;; BHAIKSUKI DIGIT ONE - 𑱑\n" +
                            "(digit-value #\\x11D52) ;; MASARAM GONDI DIGIT TWO - 𑵒\n" +
                            "(digit-value #\\x11DA3) ;; GUNJALA GONDI DIGIT THREE - 𑶣\n" +
                            "(digit-value #\\x16A64) ;; MRO DIGIT FOUR - 𖩤\n" +
                            "(digit-value #\\x16B55) ;; PAHAWH HMONG DIGIT FIVE - 𖭕\n" +
                            "(digit-value #\\x1D7D4) ;; MATHEMATICAL BOLD DIGIT SIX - 𝟔\n" +
                            "(digit-value #\\x1D7DF) ;; MATHEMATICAL DOUBLE-STRUCK DIGIT SEVEN - 𝟟\n" +
                            "(digit-value #\\x1D7EA) ;; MATHEMATICAL SANS-SERIF DIGIT EIGHT - 𝟪\n" +
                            "(digit-value #\\x1D7F5) ;; MATHEMATICAL SANS-SERIF BOLD DIGIT NINE - 𝟵\n" +
                            "(digit-value #\\x1D7F6) ;; MATHEMATICAL MONOSPACE DIGIT ZERO - 𝟶\n" +
                            "(digit-value #\\x1E141) ;; NYIAKENG PUACHUE HMONG DIGIT ONE - 𞅁\n" +
                            "(digit-value #\\x1E2F2) ;; WANCHO DIGIT TWO - 𞋲\n" +
                            "(digit-value #\\x1E953) ;; ADLAM DIGIT THREE - \n" +
                            "(digit-value #\\x1FBF4) ;; SEGMENTED DIGIT FOUR - 🯴\n" +
                            ")"
                )
            )
        )
    }

    /*
    "(list\n" +
    "#\\x0030 ;; DIGIT ZERO - 0\n" +
"#\\x0661 ;; ARABIC-INDIC DIGIT ONE - ١\n" +
"#\\x06F2 ;; EXTENDED ARABIC-INDIC DIGIT TWO - ۲\n" +
"#\\x07C3 ;; NKO DIGIT THREE - "\n +߃
"#\\x096A ;; DEVANAGARI DIGIT FOUR - ४\n" +
"#\\x09EB ;; BENGALI DIGIT FIVE - ৫\n" +
"#\\x0A6C ;; GURMUKHI DIGIT SIX - ੬\n" +
"#\\x0AED ;; GUJARATI DIGIT SEVEN - ૭\n" +
"#\\x0B6E ;; ORIYA DIGIT EIGHT - ୮\n" +
"#\\x0BEF ;; TAMIL DIGIT NINE - ௯\n" +
"#\\x0C66 ;; TELUGU DIGIT ZERO - ౦\n" +
"#\\x0CE7 ;; KANNADA DIGIT ONE - ೧\n" +
"#\\x0D68 ;; MALAYALAM DIGIT TWO - ൨\n" +
"#\\x0DE9 ;; SINHALA LITH DIGIT THREE - ෩\n" +
"#\\x0E54 ;; THAI DIGIT FOUR - ๔\n" +
"#\\x0ED5 ;; LAO DIGIT FIVE - ໕\n" +
"#\\x0F26 ;; TIBETAN DIGIT SIX - ༦\n" +
"#\\x1047 ;; MYANMAR DIGIT SEVEN - ၇\n" +
"#\\x1098 ;; MYANMAR SHAN DIGIT EIGHT - ႘\n" +
"#\\x17E9 ;; KHMER DIGIT NINE - ៩\n" +
"#\\x1810 ;; MONGOLIAN DIGIT ZERO - ᠐\n" +
"#\\x1947 ;; LIMBU DIGIT ONE - ᥇\n" +
"#\\x19D2 ;; NEW TAI LUE DIGIT TWO - ᧒\n" +
"#\\x1A83 ;; TAI THAM HORA DIGIT THREE - ᪃\n" +
"#\\x1A94 ;; TAI THAM THAM DIGIT FOUR - ᪔\n" +
"#\\x1B55 ;; BALINESE DIGIT FIVE - ᭕\n" +
"#\\x1BB6 ;; SUNDANESE DIGIT SIX - ᮶\n" +
"#\\x1C47 ;; LEPCHA DIGIT SEVEN - ᱇\n" +
"#\\x1C58 ;; OL CHIKI DIGIT EIGHT - ᱘\n" +
"#\\xA629 ;; VAI DIGIT NINE - ꘩\n" +
"#\\xA8D0 ;; SAURASHTRA DIGIT ZERO - ꣐\n" +
"#\\xA901 ;; KAYAH LI DIGIT ONE - ꤁\n" +
"#\\xA9D2 ;; JAVANESE DIGIT TWO - ꧒\n" +
"#\\xA9F3 ;; MYANMAR TAI LAING DIGIT THREE - ꧳\n" +
"#\\xAA54 ;; CHAM DIGIT FOUR - ꩔\n" +
"#\\xABF5 ;; MEETEI MAYEK DIGIT FIVE - ꯵\n" +
"#\\xFF16 ;; FULLWIDTH DIGIT SIX - ６\n" +
"#\\x104A7 ;; OSMANYA DIGIT SEVEN - 𐒧\n" +
"#\\x10D38 ;; HANIFI ROHINGYA DIGIT EIGHT - 𐴸\n" +
"#\\x1106F ;; BRAHMI DIGIT NINE - 𑁯\n" +
"#\\x110F0 ;; SORA SOMPENG DIGIT ZERO - 𑃰\n" +
"#\\x11137 ;; CHAKMA DIGIT ONE - 𑄷\n" +
"#\\x111D2 ;; SHARADA DIGIT TWO - 𑇒\n" +
"#\\x112F3 ;; KHUDAWADI DIGIT THREE - 𑋳\n" +
"#\\x11454 ;; NEWA DIGIT FOUR - 𑑔\n" +
"#\\x114D5 ;; TIRHUTA DIGIT FIVE - 𑓕\n" +
"#\\x11656 ;; MODI DIGIT SIX - 𑙖\n" +
"#\\x116C7 ;; TAKRI DIGIT SEVEN - 𑛇\n" +
"#\\x11738 ;; AHOM DIGIT EIGHT - 𑜸\n" +
"#\\x118E9 ;; WARANG CITI DIGIT NINE - 𑣩\n" +
"#\\x11950 ;; DIVES AKURU DIGIT ZERO - 𑥐\n" +
"#\\x11C51 ;; BHAIKSUKI DIGIT ONE - 𑱑\n" +
"#\\x11D52 ;; MASARAM GONDI DIGIT TWO - 𑵒\n" +
"#\\x11DA3 ;; GUNJALA GONDI DIGIT THREE - 𑶣\n" +
"#\\x16A64 ;; MRO DIGIT FOUR - 𖩤\n" +
"#\\x16B55 ;; PAHAWH HMONG DIGIT FIVE - 𖭕\n" +
"#\\x1D7D4 ;; MATHEMATICAL BOLD DIGIT SIX - 𝟔\n" +
"#\\x1D7DF ;; MATHEMATICAL DOUBLE-STRUCK DIGIT SEVEN - 𝟟\n" +
"#\\x1D7EA ;; MATHEMATICAL SANS-SERIF DIGIT EIGHT - 𝟪\n" +
"#\\x1D7F5 ;; MATHEMATICAL SANS-SERIF BOLD DIGIT NINE - 𝟵\n" +
"#\\x1D7F6 ;; MATHEMATICAL MONOSPACE DIGIT ZERO - 𝟶\n" +
"#\\x1E141 ;; NYIAKENG PUACHUE HMONG DIGIT ONE - 𞅁\n" +
"#\\x1E2F2 ;; WANCHO DIGIT TWO - 𞋲\n" +
"#\\x1E953 ;; ADLAM DIGIT THREE - \n" +𞥓
"#\\x1FBF4 ;; SEGMENTED DIGIT FOUR - 🯴\n" +
")"
     */
}
