/*
 * R7rsList.kt
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

package io.github.kawatab.keveskotlin.libraries

import io.github.kawatab.keveskotlin.*
import io.github.kawatab.keveskotlin.objects.*

class R7rsList(private val res: KevesResources) {
    /** procedure: pair? */
    val procPairQ by lazy {
        res.addProcedure(object : ScmProcedure("pair?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val ptr = vm.stack.index(vm.sp, 0)
                        val result =
                            if (ptr.isPair(res)) res.constTrue else res.constFalse
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cons */
    val procCons by lazy {
        res.addProcedure(object : ScmProcedure("cons", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 0)
                        val obj2 = vm.stack.index(sp, 1)
                        val result = ScmMutablePair.make(obj1, obj2, vm.res)
                        vm.scmProcReturn(result.toObject(), n)
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: car */
    val procCar by lazy {
        res.addProcedure(object : ScmProcedure("car", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val pair = vm.stack.index(vm.sp, 0)
                                .also { if (it.isNotPair(res)) throw KevesExceptions.expectedPair(id) }
                                .toPair()
                            vm.scmProcReturn(pair.car(res), n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedPair(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdr */
    val procCdr by lazy {
        res.addProcedure(object : ScmProcedure("cdr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val pair = vm.stack.index(vm.sp, 0)
                                .also { if (it.isNotPair(res)) throw KevesExceptions.expectedPair(id) }
                                .toPair()
                            vm.scmProcReturn(pair.cdr(res), n)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedPair(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: set-car! */
    val procSetCarE by lazy {
        res.addProcedure(object : ScmProcedure("set-car!", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val value = vm.stack.index(sp, 1)
                        val pair = vm.stack.index(sp, 0)
                            .also { if (it.isNotMutablePair(res)) throw KevesExceptions.expectedMutablePair(id) }
                            .toMutablePair()
                        pair.toVal(res).assignCar(value)
                        vm.scmProcReturn(res.constUndef, n)
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: set-cdr! */
    val procSetCdrE by lazy {
        res.addProcedure(object : ScmProcedure("set-cdr!", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val value = vm.stack.index(sp, 1)
                        val pair = vm.stack.index(sp, 0).toMutablePair()
                            .also { if (it.toObject().isNull()) throw KevesExceptions.expectedMutablePair(id) }
                        pair.toVal(res).assignCdr(value)
                        vm.scmProcReturn(res.constUndef, n)
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caar */
    val procCaar by lazy {
        res.addProcedure(object : ScmProcedure("caar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cadr */
    val procCadr by lazy {
        res.addProcedure(object : ScmProcedure("cadr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cadr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdar */
    val procCdar by lazy {
        res.addProcedure(object : ScmProcedure("cdar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cddr */
    val procCddr by lazy {
        res.addProcedure(object : ScmProcedure("cddr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cddr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caaar */
    val procCaaar by lazy {
        res.addProcedure(object : ScmProcedure("caaar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caaar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caadr */
    val procCaadr by lazy {
        res.addProcedure(object : ScmProcedure("caadr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caadr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cadar */
    val procCadar by lazy {
        res.addProcedure(object : ScmProcedure("cadar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cadar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caddr */
    val procCaddr by lazy {
        res.addProcedure(object : ScmProcedure("caddr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caddr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdaar */
    val procCdaar by lazy {
        res.addProcedure(object : ScmProcedure("cdaar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdaar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdadr */
    val procCdadr by lazy {
        res.addProcedure(object : ScmProcedure("cdadr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdadr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cddar */
    val procCddar by lazy {
        res.addProcedure(object : ScmProcedure("cddar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cddar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdddr */
    val procCdddr by lazy {
        res.addProcedure(object : ScmProcedure("cdddr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdddr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caaaar */
    val procCaaaar by lazy {
        res.addProcedure(object : ScmProcedure("caaaar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caaaar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caaadr */
    val procCaaadr by lazy {
        res.addProcedure(object : ScmProcedure("caaadr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caaadr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caadar */
    val procCaadar by lazy {
        res.addProcedure(object : ScmProcedure("caadar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caadar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caaddr */
    val procCaaddr by lazy {
        res.addProcedure(object : ScmProcedure("caaddr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caaddr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cadaar */
    val procCadaar by lazy {
        res.addProcedure(object : ScmProcedure("cadaar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cadaar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cadadr */
    val procCadadr by lazy {
        res.addProcedure(object : ScmProcedure("cadadr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cadadr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: caddar */
    val procCaddar by lazy {
        res.addProcedure(object : ScmProcedure("caddar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.caddar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cadddr */
    val procCadddr by lazy {
        res.addProcedure(object : ScmProcedure("cadddr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cadddr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdaaar */
    val procCdaaar by lazy {
        res.addProcedure(object : ScmProcedure("cdaaar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdaaar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdaadr */
    val procCdaadr by lazy {
        res.addProcedure(object : ScmProcedure("cdaadr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdaadr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdadar */
    val procCdadar by lazy {
        res.addProcedure(object : ScmProcedure("cdadar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdadar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdaddr */
    val procCdaddr by lazy {
        res.addProcedure(object : ScmProcedure("cdaddr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdaddr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cddaar */
    val procCddaar by lazy {
        res.addProcedure(object : ScmProcedure("cddaar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cddaar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cddadr */
    val procCddadr by lazy {
        res.addProcedure(object : ScmProcedure("cddadr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cddadr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cdddar */
    val procCdddar by lazy {
        res.addProcedure(object : ScmProcedure("cdddar", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cdddar(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: cddddr */
    val procCddddr by lazy {
        res.addProcedure(object : ScmProcedure("cddddr", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 ->
                        try {
                            val obj = vm.stack.index(vm.sp, 0)
                            val result = ScmPair.cddddr(obj, res)
                            vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: null? */
    val procNullQ by lazy {
        res.addProcedure(object : ScmProcedure("null?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val ptr = vm.stack.index(vm.sp, 0)
                        val result =
                            if (ptr == PtrObject(0)) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: list? */
    val procListQ by lazy {
        res.addProcedure(object : ScmProcedure("list?", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (ScmPair.isProperList(
                                obj,
                                res
                            )
                        ) res.constTrue else res.constFalse // ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: make-list */
    val procMakeList by lazy {
        res.addProcedure(object : ScmProcedure("make-list", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val result = when (n) {
                    0 -> throw KevesExceptions.expected1Or2DatumGot0(id)
                    1 -> {
                        val k = try {
                            vm.stack.index(vm.sp, 0).toInt().value(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedInt(id)
                        }
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(id)
                        ScmMutablePair.makeList(k, vm.res)
                    }
                    2 -> {
                        val sp = vm.sp
                        val k = try {
                            vm.stack.index(sp, 0).toInt().value(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedInt(id)
                        }
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(id)
                        val fill = vm.stack.index(sp, 1)
                        ScmMutablePair.makeList(k, fill, vm.res)
                    }
                    else -> throw KevesExceptions.expected1Or2DatumGotMore(id)
                }
                vm.scmProcReturn(result.toObject(), n)
            }
        })
    }

    /** procedure: list */
    val procList by lazy {
        res.addProcedure(object : ScmProcedure("list", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val sp = vm.sp
                tailrec fun loop(index: Int, result: PtrPairOrNull): PtrPairOrNull =
                    if (index < 0) {
                        result
                    } else {
                        val obj = vm.stack.index(sp, index)
                        loop(index - 1, ScmMutablePair.make(obj, result.toObject(), vm.res).toPair())
                    }

                val list = loop(index = n - 1, result = PtrPairOrNull(0))
                vm.scmProcReturn(list.toObject(), n)
            }
        })
    }

    /** procedure: length */
    val procLength by lazy {
        res.addProcedure(object : ScmProcedure("length", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val list = vm.stack.index(vm.sp, 0)
                        try {
                            val length = ScmInt.make(ScmPair.length(list, res), vm.res).toObject()
                            vm.scmProcReturn(length, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedProperList(id)
                        }
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: append */
    val procAppend by lazy {
        res.addProcedure(object : ScmProcedure("append", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, result: PtrObject): PtrObject {
                            return if (index < 0) {
                                result
                            } else {
                                val list = vm.stack.index(sp, index)
                                    .also {
                                        if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedProperList(
                                            id
                                        )
                                    }
                                    .toPairOrNull()
                                loop(
                                    index - 1,
                                    ScmMutablePair.append(list, result, vm.res)
                                )
                            }
                        }

                        val last: PtrObject = vm.stack.index(sp, n - 1)
                        val result = loop(n - 2, last)
                        vm.scmProcReturn(result, n)
                    }
                }
            }
        })
    }

    /** procedure: reverse */
    val procReverse by lazy {
        res.addProcedure(object : ScmProcedure("reverse", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val reversed: PtrObject = vm.stack.index(vm.sp, 0).let { obj ->
                            when {
                                obj.isNull() -> PtrObject(0)
                                obj.isNotPair(res) -> throw KevesExceptions.expectedList(id)
                                else -> try {
                                    ScmMutablePair.reverse(obj.toPairOrNull(), vm.res).toObject()
                                } catch (e: IllegalArgumentException) {
                                    throw KevesExceptions.expectedProperList(id)
                                }
                            }
                        }
                        vm.scmProcReturn(reversed, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: list-tail */
    val procListTail by lazy {
        res.addProcedure(object : ScmProcedure("list-tail", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val k = try {
                            vm.stack.index(sp, 1).toInt().value(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedInt(id)
                        }
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(id)
                        val list = vm.stack.index(sp, 0)
                            .also { if (it.isNotPair(res)) throw KevesExceptions.expectedList(id) }
                            .toPairOrNull()

                        try {
                            val result: PtrObject = ScmPair.listTail(list, k, res).toObject()
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.tooShortList(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: list-set! */
    val procListSetE by lazy {
        res.addProcedure(object : ScmProcedure("list-set!", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    in 0..2 -> throw KevesExceptions.expected3DatumGotLess(id)
                    3 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 2)
                        val k = try {
                            vm.stack.index(sp, 1).toInt().value(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedInt(id)
                        }
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(id)
                        val list = vm.stack.index(sp, 0)
                            .also { if (it.isNotPair(res)) throw KevesExceptions.expectedList(id) }
                            .toPairOrNull()
                        val listTail = try {
                            ScmPair.listTail(list, k, res)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.tooShortList(id)
                        }
                        try {
                            if (listTail.isNotMutable(res)) throw KevesExceptions.expectedMutablePair(id)
                            listTail.toMutable().assignCar(obj, res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedMutablePair(id)
                        }
                        return vm.scmProcReturn(res.constUndef, n)
                    }
                    else -> throw KevesExceptions.expected3DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: list-ref */
    val procListRef: PtrProcedure by lazy {
        res.addProcedure(object : ScmProcedure("list-ref", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val k = try {
                            vm.stack.index(sp, 1).toInt().value(res)
                        } catch (e: TypeCastException) {
                            throw KevesExceptions.expectedInt(id)
                        }
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(id)
                        val list = vm.stack.index(sp, 0)
                            .also {
                                if (it.isNotPair(res)) throw KevesExceptions.expectedList(id)
                            }.toPairOrNull()
                        try {
                            val result = ScmPair.listTail(list, k, res).car(res)
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.tooShortList(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: memq */
    val procMemq by lazy {
        res.addProcedure(object : ScmProcedure("memq", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)
                            .also {
                                if (it.isNeitherNullNorPair(res)) throw KevesExceptions.expectedList(id)
                            }
                        try {
                            val result = ScmPair.memq(obj, list, res)
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: memv */
    val procMemv by lazy {
        res.addProcedure(object : ScmProcedure("memv", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)
                            .also {
                                if (it.isNotPair(res)) throw KevesExceptions.expectedList(id)
                            }
                        try {
                            val result = ScmPair.memv(obj, list, res)
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: member */
    val procMember by lazy {
        res.addProcedure(object : ScmProcedure("member", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)
                            .also { if (it.isNotPair(res)) throw KevesExceptions.expectedList(id) }
                        try {
                            val result = ScmPair.member(obj, list, res)
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: assq */
    val procAssq by lazy {
        res.addProcedure(object : ScmProcedure("assq", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)
                            .also { if (it.isNotPair(res)) throw KevesExceptions.expectedList(id) }
                        try {
                            val result = ScmPair.assq(obj, list, res)
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: assv */
    val procAssv by lazy {
        res.addProcedure(object : ScmProcedure("assv", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)
                            .also { if (it.isNotPair(res)) throw KevesExceptions.expectedList(id) }
                        try {
                            val result = ScmPair.assv(obj, list, res)
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: assoc */
    val procAssoc by lazy {
        res.addProcedure(object : ScmProcedure("assoc", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)
                            .also { if (it.isNotPair(res)) throw KevesExceptions.expectedList(id) }
                        try {
                            val result = ScmPair.assoc(obj, list, res)
                            return vm.scmProcReturn(result, n)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(id)
                }
            }
        })
    }

    /** procedure: list-copy */
    val procListCopy by lazy {
        res.addProcedure(object : ScmProcedure("list-copy", PtrSyntaxOrNull(0)) {
            override fun directProc(acc: PtrObject, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                            .also { if (it.isNotPair(res)) throw KevesExceptions.expectedList(id) }
                            .toPairOrNull()
                        val result = ScmMutablePair.listCopy(obj, vm.res).toObject()
                        vm.scmProcReturn(result, n)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(id)
                }
            }
        })
    }
}