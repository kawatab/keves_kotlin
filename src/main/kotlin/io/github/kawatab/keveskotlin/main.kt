/*
 * main.kt
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

fun main(args: Array<String>) {
    val scheme = Keves()
    scheme.load("13 \"abc\"#| #|-123.45 #false;; ) \n#true\n|# #f #true |# 123 abc #false")
    scheme.load("#;13 \"abc\"#| #|-123.45 #false;; ) \n#true\n|# #f #true |# 123 def #false")
    scheme.load("#;13 \"abc\"#| #|-123.45 (#false;; )) \n#true\n|# #f #true |# (123 (def) (134 567)) #false")
    scheme.load("(13 \"abc\"#| #|-123.45 (#false;; )) \n#true\n|# #f #true |# (123 (def) (134 567)) #false)")
    scheme.load("#;13 #;\"abc\"#| #|-123.45 (#false;; )) \n#true\n|# #f #true |# (123 (def) (134 567)) #false")
    scheme.load("#;13 #;\"abc\"#| #|-123.45 (#false;; )) \n#true\n|# #f #true |# (123 (def) #;(134 (567) \"abc\")) #false")
    scheme.load("(call/cc (lambda (exit) 1 2 3 (exit 4) 5 6))")
    scheme.load("'(call/cc (lambda (exit) 1 2 3 (exit 4) 5 6) 2)")
    scheme.load("'(list a 'b)")
    scheme.load("'(list a '(b))")
    scheme.load("#")
    scheme.load("(1 . 1)")
    scheme.load("(4 3 2 1 . 0)")
    scheme.load("(4 3 2 1 . (1 2 3))")
    scheme.load("(4 3 2 1 . (1 2 . 3))")
    scheme.load("(4 3 2 1 . (1 2 . 3) 5)")


    scheme.compile("(lambda (x) (set! x 2) (+ 123 456 x))")
    scheme.compile("((lambda (x) (set! x 2)) #f)")
    scheme.compile("((lambda (x) (if x x (begin (set! x 2) x))) #f)")
    scheme.compile("(call/cc (lambda (exit) (begin 1 2 3 4 5 6)))")
    scheme.compile("(call/cc (lambda (exit) 1 2 3 (exit 4) 5 6))")
    scheme.compile("(let ((a 1)) a)")
    scheme.compile("(let ((a 1)(b 1)) (+ a b))")
    scheme.compile("(let () (+ 1 2))")
    scheme.compile("(let* () (+ 1 2))")
    scheme.compile("(letrec () (+ 1 2))")
    scheme.compile("(letrec* () (+ 1 2))")
    scheme.compile("(letrec* ((a 1)(b 2)) (+ a b))")
    scheme.compile("(letrec* ((a 1)(b 2)(c 3)) (+ a b c))")
    scheme.compile("(define a 234) (+ a 432)")
    scheme.compile("(let ([a #t]) (if a (+ 2 3) (* 4 5)))")
    scheme.compile("(define a 234) (define b 455) (define c +) (c a b)")
    scheme.compile(" ((lambda (w x y z) x) 3 4 5 6)")
    scheme.compile(" ((lambda x x) 3 4 5 6)")
    scheme.compile(" ((lambda (x y . z) z) 3 4  5 6)")
    scheme.compile(" (cond ((> 2 3) 'true) ((< 2 3) 'false) (else 'equal))")

    scheme.evaluate("((lambda (x) (set! x 6)) #f)")
    scheme.evaluate("((lambda (x) (if x x (begin (set! x 8) x))) #f)")
    scheme.evaluate("(+ 123.5)")
    scheme.evaluate("(+ (+ 23.4 345 (+ 123) 678) 901)")
    scheme.evaluate("(* 123)")
    scheme.evaluate("(* 123.5)")
    scheme.evaluate("(* (+ 23.4 345 (+ 123) 678) 901)")
    scheme.evaluate("(- 123)")
    scheme.evaluate("(- 123.5)")
    scheme.evaluate("(* (- 23.4 345 (+ 123) 678) 901)")
    scheme.evaluate("(let ([a 1][b 1]) (+ a b))")
    scheme.evaluate("(let ([a 6][b 5][c (lambda (a b) (* a b))]) (c a b))")
    scheme.evaluate("(let () (+ 2 3))")
    scheme.evaluate("((lambda (a b) ((lambda () (+ a 6)))) 3 7)")
    scheme.evaluate("(let ([a 3]) (let () (+ a 5)))")
    scheme.evaluate("(let* () (+ 2 3))")
    scheme.evaluate("(let* ([a 3]) (+ a 5))")
    scheme.evaluate("(let* ([a 3] [b (* a 2)]) (+ b 5))")
    scheme.evaluate("(list 1 2 3)")
    scheme.evaluate("(letrec* ([a (lambda (x) (* b x))] [b 2]) (a 5))")
    scheme.evaluate("(define a 234) (define b 455) (define c +) (c a b 456)")
    scheme.evaluate("(define a 234) (define b 455) (define c (lambda (x y) (+ x y))) (c a b)")
    scheme.evaluate("((lambda () (+ 2 3 4)))")
    scheme.evaluate("((lambda (w x y z) x) 3 4 5 6)")
    scheme.evaluate("((lambda x x) 3 4 5 6)")
    scheme.evaluate("((lambda (x y . z) z) 3 4  5 6)")
    scheme.evaluate("((lambda x x))")
    scheme.evaluate("((lambda (x y . z) (list x y z)) 3 4  5 6)")
    scheme.evaluate("((lambda (x . y) (list x y)) 1)")
    scheme.evaluate("((lambda (x . y) (list x y)) 1 2 3 4 5 6 7 8 9 10 11)")
    scheme.evaluate("((lambda (v w x y . z) (list v w x y z)) 1 2 3 4 5 6 7 8 9 10 11)")
    scheme.evaluate("((lambda (x y . z) (list x y z)) 1 2)")
    scheme.evaluate("((lambda (v w x y . z) (list v w x y z)) 1 2 3 4)")
    scheme.evaluate("(cond ((> 2 3) 'grater) ((< 2 3) 'less) (else 'equal))")
    scheme.evaluate("(cond ((> 4 3) 'grater) ((< 4 3) 'less) (else 'equal))")
    scheme.evaluate("(when (> 2 3) (display 5))")
    scheme.evaluate("(when (< 2 3) (display 5))")
    scheme.evaluate("(until (> 2 3) (display 5))")
    scheme.evaluate("(until (< 2 3) (display 5))")
    scheme.evaluate("(let ([x 2]) (display (+ x 1)))")
    scheme.evaluate(
        "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))\n" +
                "(fib 5)"
    )

    scheme.evaluate(
        "(letrec ((f (lambda () (if (eqv? f g) 'both 'f)))\n" +
                "(g (lambda () (if (eqv? f g) 'both 'g))))\n" +
                "(eqv? f g))\n"
    )

    scheme.evaluate(
        "(list (equal? 'a 'a)\n" +
                "(equal? '(a) '(a))\n" +
                "(equal? '(a (b) c)\n" +
                "'(a (b) c))\n" +
                "(equal? \"abc\" \"abc\")\n" +
                "(equal? 2 2)\n" +
                "(equal? (make-vector 5 'a)\n" +
                "(make-vector 5 'a))\n" +
                // "(equal? '#1=(a b . #1#)\n" + // TODO
                // "'#2=(a b a b . #2#))=⇒\n" + // TODO
                "(equal? (lambda (x) x)\n" +
                "(lambda (y) y)))\n"
    )

    scheme.evaluate(
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
    scheme.evaluate(
        "(list (append '(x) '(y))\n" +
                "(append '(a) '(b c d))\n" +
                "(append '(a (b)) '((c)))\n" +
                "(append '(a b) '(c . d))\n" +
                "(append '() 'a))\n"
    )
}
