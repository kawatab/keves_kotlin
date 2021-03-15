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

import io.github.kawatab.keveskotlin.*
import io.github.kawatab.keveskotlin.objects.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class VMTest {
    private lateinit var vm: KevesVM
    private lateinit var stack: KevesStack
    private lateinit var scheme: Keves
    private val res = KevesResources()

    @Before
    fun setUp() {
        vm = KevesVM(res)
        stack = KevesStack(res)
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

        val stringABC = ScmString.make("abc", res).toObject()
        val stringDEF = ScmString.make("def", res).toObject()
        val int123 = ScmInt.make(123, res).toObject()
        val double4p56 = ScmDouble.make(4.56, res).toObject()

        val tests = listOf(
            listOf(1, 0, stringABC, stringDEF),
            listOf(3, 1, stringDEF, stringABC),
            listOf(4, 1, int123, double4p56),
            listOf(5, 1, double4p56, int123),
            listOf(8, 2, double4p56, stringABC)
        ).map { (s, i, v, w) -> Triple(Triple(s as Int, i as Int, v as PtrObject), Pair(s, i), Pair(v, w)) }

        tests.forEach { (set, _, _) ->
            set.let { (s, i, v) -> stack.indexSetE( s, i, v) }
        }

        tests.forEach { (_, refer, v) ->
            assertEquals(v.first, stack.index(refer.first, refer.second))
            assertNotEquals(v.second, stack.index(refer.first, refer.second))
        }
    }

    /*
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

        val next = ScmPair.list(ScmString.make("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
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
        val body = ScmPair.list(ScmInstruction.Constant(null, ScmInstruction.HALT)) // CONSTANT)
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

        val thn = ScmPair.list(ScmInstruction.Constant(null, ScmInstruction.HALT))
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

        val next = ScmPair.list(ScmString.make("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
        val x = ScmPair.list(ScmConstant.TRUE, next)
        assertEquals(next, patternMatchConti.invoke(vm, x))
    }

    @Test
    fun testPatternMatchNuate() {
        val patternMatchNuate =
            vm.javaClass.getDeclaredMethod("patternMatchNuate", ScmPair::class.java)
        patternMatchNuate.isAccessible = true

        val stack = res.makeVector(10)
        val next = ScmPair.list(ScmString.make("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
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

        val next = ScmPair.list(ScmString.make("abc"), ScmConstant.TRUE, ScmConstant.FALSE)
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
     */

    @Test
    fun test001() {
        assertEquals("123", scheme.getStringForDisplay(scheme.evaluate2("(quote 123)").toVal(scheme.res)))
    }

    @Test
    fun test002() {
        assertEquals(
            "6",
            scheme.getStringForDisplay(scheme.evaluate2("((lambda (x) (set! x 6)) #f)").toVal(scheme.res))
        )
    }

    @Test
    fun test003() {
        assertEquals(
            "8",
            scheme.getStringForDisplay(
                scheme.evaluate2("((lambda (x) (if x x (begin (set! x 8) x))) #f)")
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun test004() {
        assertEquals(
            "#t",
            scheme.getStringForDisplay(
                scheme.evaluate2("((lambda (x) (if x x (begin (set! x 8) x))) #t)").toVal(scheme.res)
            )
        )
    }

    @Test
    fun test005() {
        assertEquals(
            "6",
            scheme.getStringForDisplay(
                scheme.evaluate2("(call/cc (lambda (exit) (begin 1 2 3 4 5 6)))")
                    .toVal(scheme.res)
            )
        )
        // ScmObject.getStringForDisplay(scheme.evaluate2("(call/cc (lambda (exit) (begin 1 2 3 (exit 4) 5 6)))")
    }

    @Test
    fun test006() {
        assertEquals("#<undef>", scheme.getStringForDisplay(scheme.evaluate2("(display \"abc\")").toVal(scheme.res)))
    }

    @Test
    fun test007() {
        assertEquals("123", scheme.getStringForDisplay(scheme.evaluate2("(+ (+ (+ 123)))").toVal(scheme.res)))
    }

    @Test
    fun test008() {
        assertEquals(
            (234 + 345 + 123 + 678 + 901).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(+ (+ 234 345 (+ 123) 678) 901)").toVal(scheme.res))
        )
    }

    @Test
    fun test009() {
        assertEquals("0", scheme.getStringForDisplay(scheme.evaluate2("(+)").toVal(scheme.res)))
    }

    @Test
    fun test010() {
        assertEquals("123", scheme.getStringForDisplay(scheme.evaluate2("(+ 123)").toVal(scheme.res)))
    }

    @Test
    fun test011() {
        assertEquals("123.5", scheme.getStringForDisplay(scheme.evaluate2("(+ 123.5)").toVal(scheme.res)))
    }

    @Test
    fun test012() {
        assertEquals(
            (23.4 + 345 + 123 + 678 + 901).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(+ (+ 23.4 345 (+ 123) 678) 901)").toVal(scheme.res))
        )
    }

    @Test
    fun test013() {
        assertEquals("1", scheme.getStringForDisplay(scheme.evaluate2("(*)").toVal(scheme.res)))
    }

    @Test
    fun test014() {
        assertEquals("123", scheme.getStringForDisplay(scheme.evaluate2("(* 123)").toVal(scheme.res)))
    }

    @Test
    fun test015() {
        assertEquals("123.5", scheme.getStringForDisplay(scheme.evaluate2("(* 123.5)").toVal(scheme.res)))
    }

    @Test
    fun test016() {
        assertEquals(
            ((23.4 + 345 + 123 + 678) * 901).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(* (+ 23.4 345 (+ 123) 678) 901)").toVal(scheme.res))
        )
    }

    @Test
    fun test017() {
        assertEquals(
            (-123.5).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(- 123.5)").toVal(scheme.res))
        )
    }

    @Test
    fun test018() {
        assertEquals(
            ((23.4 - 345 - 123 - 678) * 901).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(* (- 23.4 345 (+ 123) 678) 901)").toVal(scheme.res))
        )
    }

    @Test
    fun test019() {
        assertEquals("1", scheme.getStringForDisplay(scheme.evaluate2("(/ 1)").toVal(scheme.res)))
    }

    @Test
    fun test020() {
        assertEquals("-1", scheme.getStringForDisplay(scheme.evaluate2("(/ -1)").toVal(scheme.res)))
    }

    @Test
    fun test021() {
        assertEquals("-1", scheme.getStringForDisplay(scheme.evaluate2("(/ -8 4 2)").toVal(scheme.res)))
    }

    @Test
    fun test022() {
        assertEquals(
            ((23.4 - 345 - 123 - 678) / 901).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(/ (- 23.4 345 (+ 123) 678) 901)").toVal(scheme.res))
        )
    }

    @Test
    fun test023() {
        assertEquals(
            (Double.POSITIVE_INFINITY).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(/ 0.0)").toVal(scheme.res))
        )
        assertNotEquals(
            (Double.NEGATIVE_INFINITY).toString(),
            scheme.getStringForDisplay(scheme.evaluate2("(/ 0.0)").toVal(scheme.res))
        )
    }

    @Test
    fun test024() {
        assertEquals(
            "14",
            scheme.getStringForDisplay(scheme.evaluate2("(let () (+ 2 3 4 5))").toVal(scheme.res))
        )

        assertEquals(
            "a",
            scheme.getStringForDisplay(scheme.evaluate2("(let ([x (list 'a 'b 'c)]) (car x))").toVal(scheme.res))
        )
        assertEquals(
            "(b c)",
            scheme.getStringForDisplay(scheme.evaluate2("(let ([x (list 'a 'b 'c)]) (cdr x))").toVal(scheme.res))
        )
    }

    @Test
    fun test025() {
        assertEquals(
            "a",
            scheme.getStringForDisplay(
                scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (caar x))").toVal(scheme.res)
            )
        )
        assertEquals(
            "(b 2 3)",
            scheme.getStringForDisplay(
                scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (cadr x))").toVal(scheme.res)
            )
        )
        assertEquals(
            "(1)",
            scheme.getStringForDisplay(
                scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (cdar x))").toVal(scheme.res)
            )
        )
        assertEquals(
            "(c)",
            scheme.getStringForDisplay(
                scheme.evaluate2("(let ([x (list (list 'a 1) (list 'b 2 3) 'c)]) (cddr x))").toVal(scheme.res)
            )
        )
    }

    @Test
    fun test026() {
        assertEquals(
            "#0=(a b c . #0#)",
            scheme.getStringForDisplay(
                scheme.evaluate2("(let ((x (list 'a 'b 'c))) (set-cdr! (cddr x) x) x)").toVal(scheme.res)
            )
        )
        assertEquals(
            "(a . #0=(b c . #0#))",
            scheme.getStringForDisplay(
                scheme.evaluate2("(let ((x (list 'a 'b 'c))) (set-cdr! (cddr x) (cdr x)) x)").toVal(scheme.res)
            )
        )
    }

    @Test
    fun testLetStar() {
        assertEquals(
            "15",
            scheme.getStringForDisplay(scheme.evaluate2("(let* () (+ 1 2 3 4 5))").toVal(scheme.res))
        )
    }

    @Test
    fun testLetrecStar() {
        assertEquals(
            "15",
            scheme.getStringForDisplay(scheme.evaluate2("(letrec* () (+ 1 2 3 4 5))").toVal(scheme.res))
        )
    }

    @Test
    fun testFib5() {
        assertEquals(
            "5",
            scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))\n" +
                            "(fib 5)"
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testFib15() {
        assertEquals(
            "610",
            scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))\n" +
                            "(fib 15)"
                    // "(fib 38)" // 2m10s285ms 2021-02-03
                    // "(fib 38)" // 56s47ms 2021-02-07; removed unused arguments in normalProc of ScmProcedure
                    // "(fib 38)" // 47s943ms 2021-02-08; removed unused type property in ScmObject
                    // "(fib 38)" // 41s853ms 2021-02-14; replaced list with array as code
                    // "(fib 38)" // 40s771ms 2021-02-14; replaced list code with object of ScmInstruction partly
                    // "(fib 38)" // 36s176ms 2021-02-14; removed list from code
                    // "(fib 38)" // 30s750ms 2021-02-16; replaced MutableList with Array as stack
                    // "(fib 38)" // 29s885ms 2021-02-16; replaced ScmVector with Array in ScmClosure
                    // exec() return x, then invoke x.exec()
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testCarCdr() {
        assertEquals("car", scheme.getStringForDisplay(scheme.evaluate2("(car '(car . cdr))").toVal(scheme.res)))
        assertEquals("cdr", scheme.getStringForDisplay(scheme.evaluate2("(cdr '(car . cdr))").toVal(scheme.res)))
        assertEquals(
            "caar",
            scheme.getStringForDisplay(scheme.evaluate2("(caar '((caar . cdar) . (cadr . cddr)))").toVal(scheme.res))
        )
        assertEquals(
            "cadr",
            scheme.getStringForDisplay(scheme.evaluate2("(cadr '((caar . cdar) . (cadr . cddr)))").toVal(scheme.res))
        )
        assertEquals(
            "cdar",
            scheme.getStringForDisplay(scheme.evaluate2("(cdar '((caar . cdar) . (cadr . cddr)))").toVal(scheme.res))
        )
        assertEquals(
            "cddr",
            scheme.getStringForDisplay(scheme.evaluate2("(cddr '((caar . cdar) . (cadr . cddr)))").toVal(scheme.res))
        )
        assertEquals(
            "caaar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caaar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "caadr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caadr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cadar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cadar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "caddr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caddr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdaar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdadr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdadr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cddar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cddar '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdddr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdddr '(((caaar . cdaar) . (cadar . cddar)) . ((caadr . cdadr) . (caddr . cdddr))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "caaaar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caaaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "caaadr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caaadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "caadar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caadar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "caaddr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caaddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cadaar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cadaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cadadr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cadadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "caddar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(caddar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cadddr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cadddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdaaar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdaadr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdadar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdadar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdaddr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdaddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cddaar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cddaar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cddadr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cddadr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cdddar", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cdddar '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "cddddr", scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(cddddr '((((caaaar . cdaaar) . (cadaar . cddaar)) . ((caadar . cdadar) . (caddar . cdddar))) .\n" +
                            "(((caaadr . cdaadr) . (cadadr . cddadr)) . ((caaddr . cdaddr) . (cadddr . cddddr)))))"
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testNullQ() {
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(null? 'a)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(null? '(a b c))").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(null? '())").toVal(scheme.res)))
    }

    @Test
    fun testListTail() {
        assertEquals("(d)", scheme.getStringForDisplay(scheme.evaluate2("(list-tail '(a b c d) 3)").toVal(scheme.res)))
        assertEquals(
            "(f)",
            scheme.getStringForDisplay(scheme.evaluate2("(list-tail '(a b c d e f) 5)").toVal(scheme.res))
        )
        assertEquals(
            "(a b c d)",
            scheme.getStringForDisplay(scheme.evaluate2("(list-tail '(a b c d) 0)").toVal(scheme.res))
        )
    }

    @Test
    fun testListRef() {
        assertEquals("d", scheme.getStringForDisplay(scheme.evaluate2("(list-ref '(a b c d) 3)").toVal(scheme.res)))
        assertEquals("f", scheme.getStringForDisplay(scheme.evaluate2("(list-ref '(a b c d e f) 5)").toVal(scheme.res)))
        assertEquals("a", scheme.getStringForDisplay(scheme.evaluate2("(list-ref '(a b c d) 0)").toVal(scheme.res)))
    }

    @Test
    fun testSymbolEqualQ() {
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(symbol=? 'a 'b 'c 'd)").toVal(scheme.res)))
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'abc 'abc 'abc)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'abc 'abc 'ab)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'bc 'abc 'abc)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'bc)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(symbol=? 'abc 'abc)").toVal(scheme.res)))
    }

    @Test
    fun testStringEqualQ() {
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(string=? \"a\" \"b\" \"c\" \"d\")").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"abc\" \"abc\" \"abc\")").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"abc\" \"abc\" \"ab\")").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"bc\" \"abc\" \"abc\")").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"bc\")").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(string=? \"abc\" \"abc\")").toVal(scheme.res)))
    }

    @Test
    fun testCharEqualQ() {
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\A #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\B #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char=? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharLessThanQ() {
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\B #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\A #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\B #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char<? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharLessThanEqualQ() {
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\B #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char<=? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharGraterThanQ() {
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\a #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\a #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\b #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\b)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char>? #\\A #\\a)").toVal(scheme.res)))
    }

    @Test
    fun testCharGraterThanEqualQ() {
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\B #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char>=? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharCIEqualQ() {
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\A #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\B #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci=? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharCILessThanQ() {
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\B #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\A #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\B #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharCILessThanEqualQ() {
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\B #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci<=? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharCIGraterThanQ() {
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\a #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\a #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\b #\\A #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\b)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>? #\\A #\\a)").toVal(scheme.res)))
    }

    @Test
    fun testCharCIGraterThanEqualQ() {
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\b #\\c #\\d)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\a #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\b #\\a #\\a)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\b)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\a)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\B #\\c #\\D)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\A #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\B #\\a #\\A)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\B)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-ci>=? #\\a #\\A)").toVal(scheme.res)))
    }

    @Test
    fun testCharAlphabeticQ() {
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\a)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\B)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\λ)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\1)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\x0664)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\x0AE6)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\x0EA6)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\.)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\ )").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\tab )").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\return )").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\newline )").toVal(scheme.res))
        )
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-alphabetic? #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testCharNumericQ() {
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\a)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\B)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\λ)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\1)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\x0664)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\x0AE6)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\x0EA6)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\.)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\ )").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\tab )").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\return )").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\newline )").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-numeric? #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testCharWhitespaceQ() {
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\a)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\B)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\λ)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\1)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\x0664)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\x0AE6)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\x0EA6)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\.)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\ )").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\tab )").toVal(scheme.res)))
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\return )").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\newline )").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-whitespace? #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testDigitValue() {
        assertEquals("3", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\3)\n").toVal(scheme.res)))
        assertEquals("4", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0664)\n").toVal(scheme.res)))
        assertEquals("0", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0AE6)\n").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0EA6)\n").toVal(scheme.res)))
    }

    @Test
    fun testCharToInteger() {
        assertEquals("51", scheme.getStringForDisplay(scheme.evaluate2("(char->integer #\\3)\n").toVal(scheme.res)))
        assertEquals(
            "1636",
            scheme.getStringForDisplay(scheme.evaluate2("(char->integer #\\x0664)\n").toVal(scheme.res))
        )
        assertEquals(
            "2790",
            scheme.getStringForDisplay(scheme.evaluate2("(char->integer #\\x0AE6)\n").toVal(scheme.res))
        )
        assertEquals(
            "3750",
            scheme.getStringForDisplay(scheme.evaluate2("(char->integer #\\x0EA6)\n").toVal(scheme.res))
        )
        assertEquals(
            "69944",
            scheme.getStringForDisplay(scheme.evaluate2("(char->integer #\\\uD804\uDD38)\n").toVal(scheme.res))
        )
    }

    @Test
    fun testIntegerToChar() {
        assertEquals("3", scheme.getStringForDisplay(scheme.evaluate2("(integer->char 51)\n").toVal(scheme.res)))
        assertEquals("٤", scheme.getStringForDisplay(scheme.evaluate2("(integer->char 1636)\n").toVal(scheme.res)))
        assertEquals("૦", scheme.getStringForDisplay(scheme.evaluate2("(integer->char 2790)\n").toVal(scheme.res)))
        assertEquals("\u0EA6", scheme.getStringForDisplay(scheme.evaluate2("(integer->char 3750)\n").toVal(scheme.res)))
        assertEquals(
            "\uD804\uDD38",
            scheme.getStringForDisplay(scheme.evaluate2("(integer->char 69944)\n").toVal(scheme.res))
        )
    }

    @Test
    fun testCharUpperCase() {
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\a)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\B)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\λ)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\1)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\x0664)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\x0AE6)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\x0EA6)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\.)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\ )").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\tab )").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\return )").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\newline )").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-upper-case? #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testCharLowerCase() {
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\a)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\B)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\λ)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\1)").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\x0664)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\x0AE6)").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\x0EA6)").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\.)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\ )").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\tab )").toVal(scheme.res)))
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\return )").toVal(scheme.res))
        )
        assertEquals(
            "#f",
            scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\newline )").toVal(scheme.res))
        )
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(char-lower-case? #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testCharUpcase() {
        assertEquals("#\\A", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\a)").toVal(scheme.res)))
        assertEquals("#\\B", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\B)").toVal(scheme.res)))
        assertEquals("#\\Λ", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\λ)").toVal(scheme.res)))
        assertEquals("#\\1", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\1)").toVal(scheme.res)))
        assertEquals(
            "#\\\u0664",
            scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\x0664)").toVal(scheme.res))
        )
        assertEquals(
            "#\\\u0AE6",
            scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\x0AE6)").toVal(scheme.res))
        )
        assertEquals(
            "#\\\u0EA6",
            scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\x0EA6)").toVal(scheme.res))
        )
        assertEquals("#\\.", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\.)").toVal(scheme.res)))
        assertEquals("#\\space", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\ )").toVal(scheme.res)))
        assertEquals("#\\tab", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\tab )").toVal(scheme.res)))
        assertEquals(
            "#\\return",
            scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\return )").toVal(scheme.res))
        )
        assertEquals(
            "#\\newline",
            scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\newline )").toVal(scheme.res))
        )
        assertEquals("#\\語", scheme.getStringForWrite(scheme.evaluate2("(char-upcase #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testCharDowncase() {
        assertEquals("#\\a", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\a)").toVal(scheme.res)))
        assertEquals("#\\b", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\B)").toVal(scheme.res)))
        assertEquals("#\\λ", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\λ)").toVal(scheme.res)))
        assertEquals("#\\1", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\1)").toVal(scheme.res)))
        assertEquals(
            "#\\\u0664",
            scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\x0664)").toVal(scheme.res))
        )
        assertEquals(
            "#\\\u0AE6",
            scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\x0AE6)").toVal(scheme.res))
        )
        assertEquals(
            "#\\\u0EA6",
            scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\x0EA6)").toVal(scheme.res))
        )
        assertEquals("#\\.", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\.)").toVal(scheme.res)))
        assertEquals("#\\space", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\ )").toVal(scheme.res)))
        assertEquals("#\\tab", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\tab )").toVal(scheme.res)))
        assertEquals(
            "#\\return",
            scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\return )").toVal(scheme.res))
        )
        assertEquals(
            "#\\newline",
            scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\newline )").toVal(scheme.res))
        )
        assertEquals("#\\語", scheme.getStringForWrite(scheme.evaluate2("(char-downcase #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testCharFoldcase() {
        assertEquals("#\\a", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\a)").toVal(scheme.res)))
        assertEquals("#\\b", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\B)").toVal(scheme.res)))
        assertEquals("#\\λ", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\λ)").toVal(scheme.res)))
        assertEquals("#\\1", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\1)").toVal(scheme.res)))
        assertEquals(
            "#\\\u0664",
            scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\x0664)").toVal(scheme.res))
        )
        assertEquals(
            "#\\\u0AE6",
            scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\x0AE6)").toVal(scheme.res))
        )
        assertEquals(
            "#\\\u0EA6",
            scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\x0EA6)").toVal(scheme.res))
        )
        assertEquals("#\\.", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\.)").toVal(scheme.res)))
        assertEquals("#\\space", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\ )").toVal(scheme.res)))
        assertEquals("#\\tab", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\tab )").toVal(scheme.res)))
        assertEquals(
            "#\\return",
            scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\return )").toVal(scheme.res))
        )
        assertEquals(
            "#\\newline",
            scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\newline )").toVal(scheme.res))
        )
        assertEquals("#\\語", scheme.getStringForWrite(scheme.evaluate2("(char-foldcase #\\語)").toVal(scheme.res)))
    }

    @Test
    fun testProcLessThan() {
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(< 1 2 3 2.5)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(< 1.5 3 4 5.5)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(< 105 93 84 75.5)").toVal(scheme.res)))
    }

    @Test
    fun testProcGraterThan() {
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(> 1 2 3 2.5)").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(> 1.5 3 4 5.5)").toVal(scheme.res)))
        assertEquals("#t", scheme.getStringForDisplay(scheme.evaluate2("(> 105 93 84 75.5)").toVal(scheme.res)))
    }

    @Test
    fun testR7RS0204() {
        assertEquals(
            "#0=(a b c . #0#)",
            scheme.getStringForDisplay(
                scheme.evaluate2(
                    "(let ((x (list 'a 'b 'c)))\n" +
                            "(set-cdr! (cddr x) x)\n" +
                            "x)\n"
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testR7RS040101() {
        assertEquals(
            "28",
            scheme.getStringForDisplay(scheme.evaluate2("(define x 28)\n" + "x\n").toVal(scheme.res))
        )
    }

    @Test
    fun testR7RS040102() {
        assertEquals(
            "a",
            scheme.getStringForWrite(scheme.evaluate2("(quote a)\n").toVal(scheme.res))
        )
        assertEquals(
            "#(a b c)",
            scheme.getStringForWrite(scheme.evaluate2("(quote #(a b c))").toVal(scheme.res))
        )
        assertEquals(
            "(+ 1 2)",
            scheme.getStringForWrite(scheme.evaluate2("(quote (+ 1 2))").toVal(scheme.res))
        )
        assertEquals(
            "a",
            scheme.getStringForWrite(scheme.evaluate2("'a\n").toVal(scheme.res))
        )
        assertEquals(
            "#(a b c)",
            scheme.getStringForWrite(scheme.evaluate2("'#(a b c)\n").toVal(scheme.res))
        )
        assertEquals(
            "()",
            scheme.getStringForWrite(scheme.evaluate2("'()\n").toVal(scheme.res))
        )
        assertEquals(
            "(+ 1 2)",
            scheme.getStringForWrite(scheme.evaluate2("'(+ 1 2)\n").toVal(scheme.res))
        )
        assertEquals(
            "(quote a)",
            scheme.getStringForWrite(scheme.evaluate2("'(quote a)\n").toVal(scheme.res))
        )
        assertEquals(
            "(quote a)",
            scheme.getStringForWrite(scheme.evaluate2("''a\n").toVal(scheme.res))
        )
        assertEquals(
            "145932",
            scheme.getStringForWrite(scheme.evaluate2("'145932\n").toVal(scheme.res))
        )
        assertEquals(
            "145932",
            scheme.getStringForWrite(scheme.evaluate2("145932\n").toVal(scheme.res))
        )
        assertEquals(
            "\"abc\"",
            scheme.getStringForWrite(scheme.evaluate2("'\"abc\"\n").toVal(scheme.res))
        )
        assertEquals(
            "\"abc\"",
            scheme.getStringForWrite(scheme.evaluate2("\"abc\"\n").toVal(scheme.res))
        )
        assertEquals(
            "#\\#",
            scheme.getStringForWrite(scheme.evaluate2("'#\n").toVal(scheme.res))
        )
        assertEquals(
            "#\\#",
            scheme.getStringForWrite(scheme.evaluate2("#\n").toVal(scheme.res))
        )
        assertEquals(
            "#(a 10)",
            scheme.getStringForWrite(scheme.evaluate2("'#(a 10)\n").toVal(scheme.res))
        )
        assertEquals(
            "#(a 10)",
            scheme.getStringForWrite(scheme.evaluate2("#(a 10)\n").toVal(scheme.res))
        )
        assertEquals(
            "#u8(64 65)",
            scheme.getStringForWrite(scheme.evaluate2("'#u8(64 65)").toVal(scheme.res))
        )
        assertEquals(
            "#u8(64 65)",
            scheme.getStringForWrite(scheme.evaluate2("#u8(64 65)\n").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("'#t\n").toVal(scheme.res))
        )
        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("#t\n").toVal(scheme.res))
        )
    }

    @Test
    fun testR7RS040103() {
        assertEquals(
            "7",
            scheme.getStringForWrite(scheme.evaluate2("(+ 3 4)\n").toVal(scheme.res))
        )
        assertEquals(
            "12",
            scheme.getStringForWrite(scheme.evaluate2("((if #f + *) 3 4)\n").toVal(scheme.res))
        )
    }

    @Test
    fun testR7RS040104() {
        assertEquals(
            "#<procedure lambda>",
            scheme.getStringForWrite(scheme.evaluate2("(lambda (x) (+ x x))").toVal(scheme.res))
        )
        assertEquals(
            "8",
            scheme.getStringForWrite(scheme.evaluate2("((lambda (x) (+ x x)) 4)").toVal(scheme.res))
        )
        assertEquals(
            "3",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define reverse-subtract\n" +
                            "  (lambda (x y) (- y x)))\n" +
                            "(reverse-subtract 7 10)\n"
                )
                    .toVal(scheme.res)
            )
        )

        /*
        assertEquals(
            "10",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define add4\n" +
                            "  (let((x 4))\n" +
                            "    (lambda(y)(+ x y))))\n" +
                            "  (add4 6)"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(3 4 5 6)",
            scheme.getStringForWrite(scheme.evaluate2("((lambda x x) 3 4 5 6)").toVal(scheme.res))
        )

        assertEquals(
            "(5 6)",
            scheme.getStringForWrite(scheme.evaluate2("((lambda (x y . z) z) 3 4 5 6)").toVal(scheme.res))
        )
         */
    }

    @Test
    fun testR7RS040105() {
        assertEquals(
            "yes",
            scheme.getStringForWrite(scheme.evaluate2("(if (> 3 2) 'yes 'no)").toVal(scheme.res))
        )
        assertEquals(
            "no",
            scheme.getStringForWrite(scheme.evaluate2("(if (> 2 3) 'yes 'no)").toVal(scheme.res))
        )
        assertEquals(
            "1",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(if (> 3 2)\n" +
                            "(- 3 2)\n" +
                            "(+ 3 2))\n"
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testR7RS040106() {
        assertEquals(
            "5",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define x 2)\n" +
                            "(+ x 1)\n" +
                            "(set! x 4)\n" +
                            "(+ x 1)\n"
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testR7RS040201() {
        assertEquals(
            "greater",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(cond ((> 3 2) 'greater)\n" +
                            "((< 3 2) 'less))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "equal",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(cond ((> 3 3) 'greater)\n" +
                            "((< 3 3) 'less)\n" +
                            "(else 'equal))"
                )
                    .toVal(scheme.res)
            )
        )

        /*
        TODO("must implement =>")
        assertEquals(
            "2",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(cond ((assv 'b '((a 1) (b 2))) => cadr)\n" +
                            "(else #f))"
                )
            .toVal(scheme.res))
        )
         */

        assertEquals(
            "#<undef>",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(when (= 1 1.0)\n" +
                            "(display \"1\")\n" +
                            "(display \"2\"))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#<undef>",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(until (= 1 1.0)\n" +
                            "(display \"1\")\n" +
                            "(display \"2\"))\n"
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testR7RS040202() {
        assertEquals(
            "6",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x 2) (y 3))\n" +
                            "(* x y))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "35",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x 2) (y 3))\n" +
                            "(let ((x 7)\n" +
                            "(z (+ x y)))\n" +
                            "(* z x)))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "70",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x 2) (y 3))\n" +
                            "(let* ((x 7)\n" +
                            "(z (+ x y)))\n" +
                            "(* z x)))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            ).replace("\n", "\\n")
        )
    }

    @Test
    fun testR7RS0601() {
        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? 'a 'a)").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? 'a 'b)").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? 2 2)").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? 2 2.0)").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? '() '())").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? 100000000 100000000)").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? 0.0 +nan.0)").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? (cons 1 2) (cons 1 2))").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(eqv? (lambda () 1)\n" +
                            "(lambda () 2))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((p (lambda (x) x)))\n" +
                            "(eqv? p p))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? #f 'nil)").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? \"\" \"\")").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? '#() '#())").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(eqv? (lambda (x) x)\n" +
                            "(lambda (x) x))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(eqv? (lambda (x) x)\n" +
                            "(lambda (y) y))"
                )
                    .toVal(scheme.res)
            )
        )
        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? 1.0e0 1.0f0)").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eqv? +nan.0 +nan.0)").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define gen-counter\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) n))))\n" +
                            "(let ((g (gen-counter)))\n" +
                            "(eqv? g g))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define gen-counter\n" +
                            "(lambda ()\n" +
                            "(let ((n 0))\n" +
                            "(lambda () (set! n (+ n 1)) n))))\n" +
                            "(let ((g (gen-counter)))\n" +
                            "(eqv? g g))\n" +
                            "(eqv? (gen-counter) (gen-counter))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(letrec ((f (lambda () (if (eqv? f g) 'both 'f)))\n" +
                            "(g (lambda () (if (eqv? f g) 'both 'g))))\n" +
                            "(eqv? f g))\n"

                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(letrec ((f (lambda () (if (eqv? f g) 'f 'both)))\n" +
                            "(g (lambda () (if (eqv? f g) 'g 'both))))\n" +
                            "(eqv? f g))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(eq? 'a 'a)").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eq? '(a) '(a))\n").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eq? (list 'a) (list 'a))\n").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eq? \"a\" \"a\")\n").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eq? \"\" \"\")\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(eq? '() '())\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eq? 2 2)\n").toVal(scheme.res))
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(scheme.evaluate2("(eq? #\\A #\\A)\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(eq? car car)\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((n (+ 2 3)))\n" +
                            "(eq? n n))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x '(a)))\n" +
                            "(eq? x x))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((x '#()))\n" +
                            "(eq? x x))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((p (lambda (x) x)))\n" +
                            "(eq? p p)))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(equal? 'a 'a)\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(equal? '(a) '(a))\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(equal? '(a (b) c)\n" + "'(a (b) c))\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(equal? \"abc\" \"abc\")\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(scheme.evaluate2("(equal? 2 2)\n").toVal(scheme.res))
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(equal? (make-vector 5 'a)\n" +
                            "(make-vector 5 'a))\n"
                )
                    .toVal(scheme.res)
            )
        )

        /* TODO
        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(equal? '#1=(a b . #1#)\n" +
                    "'#2=(a b a b . #2#))=⇒\n"
                )
            .toVal(scheme.res))
        )
         */

        assertEquals(
            "#f",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(equal? (lambda (x) x)\n" +
                            "(lambda (y) y))\n"
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testR7RS0604() {
        assertEquals(
            "(a b c)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(a . 4)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x 4)\n" +
                            "x\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#t",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define x (list 'a 'b 'c))\n" +
                            "(define y x)\n" +
                            "y\n" +
                            "(list? y)\n" +
                            "(set-cdr! x 4)\n" +
                            "x\n" +
                            "(eqv? x y)\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(a . 4)",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "#f",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(#t #t #f #f)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (pair? '(a . b))\n" +
                            "(pair? '(a b c))\n" +
                            "(pair? '())\n" +
                            "(pair? '#(a b)))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "((a) ((a) b c d) (\"a\" b c) (a . 3) ((a b) . c))",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (cons 'a '())\n" +
                            "(cons '(a) '(b c d))\n" +
                            "(cons \"a\" '(b c))\n" +
                            "(cons 'a 3)\n" +
                            "(cons '(a b) 'c))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(a (a) 1)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (car '(a b c))\n" +
                            "(car '((a) b c d))\n" +
                            "(car '(1 . 2)))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "*** ERROR: 'car' expected a pair, but got other\\n who: vm",
            scheme.getStringForWrite(scheme.evaluate2("(car '())").toVal(scheme.res)).replace("\n", "\\n")
        )

        assertEquals(
            "((b c d) 2)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (cdr '((a) b c d))\n" +
                            "(cdr '(1 . 2)))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "*** ERROR: 'cdr' expected a pair, but got other\\n who: vm",
            scheme.getStringForWrite(scheme.evaluate2("(cdr '())\n").toVal(scheme.res)).replace("\n", "\\n")
        )

        assertEquals(
            "#<undef>",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define (f) (list 'not-a-constant-list))\n" +
                            "(define (g) '(constant-list))\n" +
                            "(set-car! (f) 3)\n"
                )
                    .toVal(scheme.res)
            ).replace("\n", "\\n")
        )

        assertEquals(
            "*** ERROR: 'set-car!' expected a mutable pair, but got other\\n who: vm",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define (f) (list 'not-a-constant-list))\n" +
                            "(define (g) '(constant-list))\n" +
                            "(set-car! (f) 3)\n" +
                            "(set-car! (g) 3)\n"
                )
                    .toVal(scheme.res)
            ).replace("\n", "\\n")
        )

        assertEquals(
            "(3 3)",
            scheme.getStringForWrite(scheme.evaluate2("(make-list 2 3)").toVal(scheme.res))
        )

        assertEquals(
            "(a 7 c)",
            scheme.getStringForWrite(scheme.evaluate2("(list 'a (+ 3 4) 'c)").toVal(scheme.res))
        )

        assertEquals(
            "()",
            scheme.getStringForWrite(scheme.evaluate2("(list)").toVal(scheme.res))
        )

        assertEquals(
            "(3 3 0)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (length '(a b c))\n" +
                            "(length '(a (b) (c d e)))\n" +
                            "(length '()))"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "((x y) (a b c d) (a (b) (c)) (a b c . d) a)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (append '(x) '(y))\n" +
                            "(append '(a) '(b c d))\n" +
                            "(append '(a (b)) '((c)))\n" +
                            "(append '(a b) '(c . d))\n" +
                            "(append '() 'a))\n"
                ).toVal(scheme.res)
            )
        )

        assertEquals(
            "((c b a) ((e (f)) d (b c) a))",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (reverse '(a b c))\n" +
                            "(reverse '(a (b c) d (e (f)))))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "c",
            scheme.getStringForWrite(scheme.evaluate2("(list-ref '(a b c d) 2)").toVal(scheme.res))
        )

        /* TODO
        assertEquals(
            "c",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list-ref '(a b c d)\n" +
                            "(exact (round 1.8)))"
                )
            .toVal(scheme.res))
        )
         */

        assertEquals(
            "(one two three)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(let ((ls (list 'one 'two 'five!)))\n" +
                            "(list-set! ls 2 'three)\n" +
                            "ls)\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "*** ERROR: 'list-set!' expected a mutable pair, but got other\\n who: vm",
            scheme.getStringForWrite(scheme.evaluate2("(list-set! '(0 1 2) 1 \"oops\")\n").toVal(scheme.res))
                .replace("\n", "\\n")
        )

        assertEquals(
            "((a b c) (b c) #f #f ((a) c)" +
                    // // " (\"b\" \"c\")\n" +
                    " #f (101 102))",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            ).replace("\n", "\\n")
        )

        assertEquals(
            "((a 1) (b 2) #f #f ((a)) " +
                    // "(2 4) " +
                    "#f (5 7))",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "((3 8 2 8) (1 8 2 8))",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(define a '(1 8 2 8)) ; a may be immutable\n" +
                            "(define b (list-copy a))\n" +
                            "(set-car! b 3) ; b is mutable\n" +
                            "(list b a)",
                )
                    .toVal(scheme.res)
            )
        )
    }

    @Test
    fun testR7RS0605() {
        assertEquals(
            "(#t #t #f #t #f #f)",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (symbol? 'foo)\n" +
                            "(symbol? (car '(a b)))\n" +
                            "(symbol? \"bar\")\n" +
                            "(symbol? 'nil)\n" +
                            "(symbol? '())\n" +
                            "(symbol? #f))\n"
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(\"flying-fish\" \"Martin\" \"Malvina\")",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list (symbol->string 'flying-fish)\n" +
                            "(symbol->string 'Martin)\n" +
                            "(symbol->string\n" +
                            "(string->symbol \"Malvina\")))\n",
                )
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(mISSISSIppi #t #t #t)",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            "(#\\alarm #\\backspace #\\delete #\\escape #\\newline #\\null #\\return #\\space #\\tab)",
            scheme.getStringForWrite(
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
                    .toVal(scheme.res)
            )
        )

        assertEquals(
            """(#\a #\A #\( #\space #\λ)""", // #\ι)""",
            scheme.getStringForWrite(
                scheme.evaluate2(
                    "(list #\\a ; lower case letter\n" +
                            "#\\A ; upper case letter\n" +
                            "#\\( ; left parenthesis\n" +
                            "#\\ ; the space character\n" +
                            "#\\x03BB ; λ (if character is supported)\n" +
                            // "#\\iota ; ι (if character and name are supported)\n" +
                            ")",
                ).toVal(scheme.res)
            )
        )
    }

    @Test
    fun testR7RS0606() {
        assertEquals("3", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\3)\n").toVal(scheme.res)))
        assertEquals("4", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0664)\n").toVal(scheme.res)))
        assertEquals("0", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0AE6)\n").toVal(scheme.res)))
        assertEquals("#f", scheme.getStringForDisplay(scheme.evaluate2("(digit-value #\\x0EA6)\n").toVal(scheme.res)))
        assertEquals(
            "(0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4)",
            scheme.getStringForDisplay(
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
                    .toVal(scheme.res)
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
