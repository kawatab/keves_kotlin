/*
 * ParserTest.kt
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
import io.github.kawatab.keveskotlin.Keves.Companion.getStringForWrite
import org.junit.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun test001() {
        val scheme = Keves()
        val result = scheme.parse("()")
        assertEquals("(begin ())", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test002() {
        val scheme = Keves()
        val result = scheme.parse("symbol")
        assertEquals("(begin symbol)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test003() {
        val scheme = Keves()
        val result = scheme.parse("\"string\"")
        assertEquals("(begin \"string\")", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test004() {
        val scheme = Keves()
        val result = scheme.parse("123")
        assertEquals("(begin 123)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test005() {
        val scheme = Keves()
        val result = scheme.parse("-123.45")
        assertEquals("(begin -123.45)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test006() {
        val scheme = Keves()
        val result = scheme.parse("-123.45 123 abc")
        assertEquals("(begin -123.45 123 abc)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test007() {
        val scheme = Keves()
        val result = scheme.parse("-123.45 (123 abc)")
        assertEquals("(begin -123.45 (123 abc))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test008() {
        val scheme = Keves()
        val result = scheme.parse("(123 abc)")
        assertEquals("(begin (123 abc))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test009() {
        val scheme = Keves()
        val result = scheme.parse("(123 abc")
        assertEquals(false, result.second.isEmpty())
    }

    @Test
    fun test010() {
        val scheme = Keves()
        val result = scheme.parse("-123.45 ;; ) \n 123 abc")
        assertEquals("(begin -123.45 123 abc)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test011() {
        val scheme = Keves()
        val result = scheme.parse("-123.45 #t;; ) \n #t #true 123 abc #f")
        assertEquals("(begin -123.45 #t #t #t 123 abc #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test012() {
        val scheme = Keves()
        val result = scheme.parse("-123.45 #t;; ) \n#true\n #f #true 123 abc #true")
        assertEquals("(begin -123.45 #t #t #f #t 123 abc #t)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test013() {
        val scheme = Keves()
        val result = scheme.parse("-123.45 #false;; ) \n#true\n #f #true 123 abc #false")
        assertEquals("(begin -123.45 #f #t #f #t 123 abc #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test014() {
        val scheme = Keves()
        val result = scheme.parse("#|-123.45 #false;; ) \n#true\n|# #f #true 123 abc #false")
        assertEquals("(begin #f #t 123 abc #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test015() {
        val scheme = Keves()
        val result = scheme.parse("13 \"abc\" #| #|-123.45 #false;; ) \n#true\n|# #f #true|# 123 abc #false")
        assertEquals("(begin 13 \"abc\" 123 abc #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test016() {
        val scheme = Keves()
        val result = scheme.parse("13 \"abc\"#| #|-123.45 #false;; ) \n#true\n|# #f #true |# 123 abc #false")
        assertEquals("(begin 13 \"abc\" 123 abc #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test017() {
        val scheme = Keves()
        val result = scheme.parse("    ")
        assertEquals("()", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test018() {
        val scheme = Keves()
        val result = scheme.parse("#;13 \"abc\"#| #|-123.45 #false;; ) \n#true\n|# #f #true |# 123 def #false")
        assertEquals("(begin \"abc\" 123 def #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test019() {
        val scheme = Keves()
        val result =
            scheme.parse("(13 \"abc\"#| #|-123.45 (#false;; )) \n#true\n|# #f #true |# (123 (def) (134 567)) #false)")
        assertEquals("(begin (13 \"abc\" (123 (def) (134 567)) #f))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test020() {
        val scheme = Keves()
        val result =
            scheme.parse("#;13 #;\"abc\"#| #|-123.45 (#false;; )) \n#true\n|# #f #true |# (123 (def) (134 567)) #false")
        assertEquals("(begin (123 (def) (134 567)) #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test021() {
        val scheme = Keves()
        val result =
            scheme.parse("#;13 #;  \"abc\"#| #|-123.45 (#false;; )) \n#true\n|# #f #true |# (123 (def) #;(134 (567) \"abc\")) #false")
        assertEquals("(begin (123 (def)) #f)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test022() {
        val scheme = Keves()
        val result = scheme.parse(
            "... +\n" +
                    "+soup+ <=?\n" +
                    "->string a34kTMNs\n" +
                    "lambda list->vector\n" +
                    "q V17a\n" +
                    "#;|two words| |two\\x20;words|\n" +
                    "the-word-recursion-has-many-meanings"
        )
        assertEquals(
            "(begin ... + +soup+ <=? ->string a34kTMNs lambda list->vector q V17a |two words| the-word-recursion-has-many-meanings)",
            getStringForWrite(result.first)
        )
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test023() {
        val scheme = Keves()
        val result = scheme.parse("'a")
        assertEquals("(begin (quote a))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test024() {
        val scheme = Keves()
        val result = scheme.parse("'    a")
        assertEquals("(begin (quote a))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test025() {
        val scheme = Keves()
        val result = scheme.parse("'    a   b")
        assertEquals("(begin (quote a) b)", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test026() {
        val scheme = Keves()
        val result = scheme.parse("'(    a   b)")
        assertEquals("(begin (quote (a b)))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test027() {
        val scheme = Keves()
        val result = scheme.parse("'(  (123 4.5)  a ()  b)")
        assertEquals("(begin (quote ((123 4.5) a () b)))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test028() {
        val scheme = Keves()
        val result = scheme.parse("'(list a b)")
        assertEquals("(begin (quote (list a b)))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test029() {
        val scheme = Keves()
        val result = scheme.parse("`(list a b)")
        assertEquals("(begin (quasiquote (list a b)))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun test030() {
        val scheme = Keves()
        val result = scheme.parse("'(list a 'b)")
        assertEquals("(begin (quote (list a (quote b))))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun testR7RS0201() {
        val scheme = Keves()
        val result1 = scheme.parse("|H\\x65;llo|")
        assertEquals("(begin Hello)", getStringForWrite(result1.first))
        assertEquals(true, result1.second.isEmpty())

        val result2 = scheme.parse("|\\x3BB;|")
        assertEquals("(begin λ)", getStringForWrite(result2.first))
        assertEquals(true, result2.second.isEmpty())

        val result3 = scheme.parse("|\\t\\t|")
        assertEquals("(begin |\\x09;\\x09;|)", getStringForWrite(result3.first))
        assertEquals(true, result3.second.isEmpty())

        val result4 = scheme.parse(
            "... +\n" +
                    "+soup+ <=?\n" +
                    "->string a34kTMNs\n" +
                    "lambda list->vector\n" +
                    "q V17a\n" +
                    "|two words| |two\\x20;words|\n" +
                    "the-word-recursion-has-many-meanings"
        )
        assertEquals(
            "(begin ... + +soup+ <=? ->string a34kTMNs lambda list->vector q V17a |two words| |two words| the-word-recursion-has-many-meanings)",
            getStringForWrite(result4.first)
        )
        assertEquals(true, result4.second.isEmpty())
    }

    @Test
    fun testR7RS0202() {
        val scheme = Keves()
        val result = scheme.parse(
            "#|\n" +
                    "   The FACT procedure computes the factorial\n" +
                    "   of a non-negative integer.\n" +
                    "|#\n" +
                    "(define fact\n" +
                    "  (lambda (n)\n" +
                    "    (if (= n 0)\n" +
                    "        #;(= n 1)\n" +
                    "        1        ;Base case: return 1\n" +
                    "        (* n (fact (- n 1))))))\n"
        )
        assertEquals(
            "(begin (define fact (lambda (n) (if (= n 0) 1 (* n (fact (- n 1)))))))",
            getStringForWrite(result.first)
        )
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun testR7RS040104() {
        val scheme = Keves()
        val result1 = scheme.parse("((lambda x x) 3 4 5 6)")
        val result2 = scheme.parse("((lambda (x y . z) z) 3 4 (list 5 6))")
        assertEquals("(begin ((lambda x x) 3 4 5 6))", getStringForWrite(result1.first))
        assertEquals(true, result1.second.isEmpty())
        assertEquals("(begin ((lambda (x y . z) z) 3 4 (list 5 6)))", getStringForWrite(result2.first))
        assertEquals(true, result2.second.isEmpty())
    }

    @Test
    fun testR7RS040208() {
        val scheme = Keves()
        val result = scheme.parse(
            "`(list ,(+ 1 2) 4)\n" +
                    "(let ((name 'a)) `(list ,name ',name))\n" +
                    "`(a ,(+ 1 2) ,@(map abs '(4 -5 6)) b)\n" +
                    "`(( foo ,(- 10 3)) ,@(cdr '(c)) . ,(car '(cons)))\n" +
                    "`#(10 5 ,(sqrt 4) ,@(map sqrt '(16 9)) 8)\n" +
                    "(let ((foo '(foo bar)) (@baz 'baz))\n" +
                    "`(list ,@foo , @baz))\n"
        )
        assertEquals(
            "(begin (quasiquote (list (unquote (+ 1 2)) 4)) " +
                    "(let ((name (quote a))) (quasiquote (list (unquote name) (quote (unquote name))))) " +
                    "(quasiquote (a (unquote (+ 1 2)) (unquote-splicing (map abs (quote (4 -5 6)))) b)) " +
                    "(quasiquote ((foo (unquote (- 10 3))) (unquote-splicing (cdr (quote (c)))) unquote (car (quote (cons))))) " +
                    "(quasiquote #(10 5 (unquote (sqrt 4)) (unquote-splicing (map sqrt (quote (16 9)))) 8)) " +
                    "(let ((foo (quote (foo bar))) (@baz (quote baz))) (quasiquote (list (unquote-splicing foo) (unquote @baz)))))",
            getStringForWrite(result.first)
        )
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun testR7RS060901() {
        val scheme = Keves()
        val result = scheme.parse("#u8(0 10\n 5)")
        assertEquals("(begin #u8(0 10 5))", getStringForWrite(result.first))
        assertEquals(true, result.second.isEmpty())
    }

    @Test
    fun testR7RS0601() {
        val scheme = Keves()
        val expectedEq01 = "(begin (eq? (quote a) (quote a)))"
        val expectedEq02 = "(begin (eq? (quote (a)) (quote (a))))"
        val expectedEq03 = "(begin (eq? (list (quote a)) (list (quote a))))"
        val expectedEq04 = "(begin (eq? \"a\" \"a\"))"
        val expectedEq05 = "(begin (eq? \"\" \"\"))"
        val expectedEq06 = "(begin (eq? (quote ()) (quote ())))"
        val expectedEq07 = "(begin (eq? + 2 2))"
        val expectedEq08 = "(begin (eq? #\\A #\\A))"
        val expectedEq09 = "(begin (eq? car car))"
        val expectedEq10 = "(begin (let ((n (+ 2 3))) (eq? n n)))"
        val expectedEq11 = "(begin (let ((x (quote (a)))) (eq? x x)))"
        val expectedEq12 = "(begin (let ((x (quote #()))) (eq? x x)))"
        val expectedEq13 = "(begin (let ((p (lambda (x) x))) (eq? p p)))"
        val testEq01 = scheme.parse("(eq? (quote a) (quote a))")
        val testEq02 = scheme.parse("(eq? (quote (a)) (quote (a)))")
        val testEq03 = scheme.parse("(eq? (list (quote a)) (list (quote a)))")
        val testEq04 = scheme.parse("(eq? \"a\" \"a\")")
        val testEq05 = scheme.parse("(eq? \"\" \"\")")
        val testEq06 = scheme.parse("(eq? (quote ()) (quote ()))")
        val testEq07 = scheme.parse("(eq? + 2 2)")
        val testEq08 = scheme.parse("(eq? #\\A #\\A)")
        val testEq09 = scheme.parse("(eq? car car)")
        val testEq10 = scheme.parse("(let ((n (+ 2 3))) (eq? n n))")
        val testEq11 = scheme.parse("(let ((x (quote (a)))) (eq? x x))")
        val testEq12 = scheme.parse("(let ((x (quote #()))) (eq? x x))")
        val testEq13 = scheme.parse("(let ((p (lambda (x) x))) (eq? p p))\n")
        assertEquals(expectedEq01, getStringForWrite(testEq01.first))
        assertEquals(expectedEq02, getStringForWrite(testEq02.first))
        assertEquals(expectedEq03, getStringForWrite(testEq03.first))
        assertEquals(expectedEq04, getStringForWrite(testEq04.first))
        assertEquals(expectedEq05, getStringForWrite(testEq05.first))
        assertEquals(expectedEq06, getStringForWrite(testEq06.first))
        assertEquals(expectedEq07, getStringForWrite(testEq07.first))
        assertEquals(expectedEq08, getStringForWrite(testEq08.first))
        assertEquals(expectedEq09, getStringForWrite(testEq09.first))
        assertEquals(expectedEq10, getStringForWrite(testEq10.first))
        assertEquals(expectedEq11, getStringForWrite(testEq11.first))
        assertEquals(expectedEq12, getStringForWrite(testEq12.first))
        assertEquals(expectedEq13, getStringForWrite(testEq13.first))
    }
}