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

import io.github.kawatab.keveskotlin.KevesExceptions
import io.github.kawatab.keveskotlin.KevesVM
import io.github.kawatab.keveskotlin.objects.*

object R7rsList {
    /** procedure: pair? */
    val procPairQ: ScmProcedure by lazy {
        object : ScmProcedure("pair?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procPairQ.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (ScmPair.isPair(obj)) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procPairQ.id)
                }
            }
        }
    }

    /** procedure: cons */
    val procCons: ScmProcedure by lazy {
        object : ScmProcedure("cons", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procCons.id)
                    2 -> {
                        val sp = vm.sp
                        val obj1 = vm.stack.index(sp, 0)
                        val obj2 = vm.stack.index(sp, 1)
                        val result = ScmMutablePair(obj1, obj2)
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procCons.id)
                }
            }
        }
    }

    /** procedure: car */
    val procCar: ScmProcedure by lazy {
        object : ScmProcedure("car", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCar.id)
                    1 -> {
                        val pair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCar.id)
                        vm.scmProcReturn(pair.car, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCar.id)
                }
            }
        }
    }

    /** procedure: cdr */
    val procCdr: ScmProcedure by lazy {
        object : ScmProcedure("cdr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdr.id)
                    1 -> {
                        val pair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdr.id)
                        vm.scmProcReturn(pair.cdr, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdr.id)
                }
            }
        }
    }

    /** procedure: set-car! */
    val procSetCarE: ScmProcedure by lazy {
        object : ScmProcedure("set-car!", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procSetCarE.id)
                    2 -> {
                        val sp = vm.sp
                        val value = vm.stack.index(sp, 1)
                        val pair = vm.stack.index(sp, 0) as? ScmMutablePair
                            ?: throw KevesExceptions.expectedMutablePair(procSetCarE.id)
                        pair.assignCar(value)
                        vm.scmProcReturn(ScmConstant.UNDEF, n, this)
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procSetCarE.id)
                }
            }
        }
    }

    /** procedure: set-cdr! */
    val procSetCdrE: ScmProcedure by lazy {
        object : ScmProcedure("set-cdr!", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procSetCdrE.id)
                    2 -> {
                        val sp = vm.sp
                        val value = vm.stack.index(sp, 1)
                        val pair = vm.stack.index(sp, 0) as? ScmMutablePair
                            ?: throw KevesExceptions.expectedMutablePair(procSetCdrE.id)
                        pair.assignCdr(value)
                        vm.scmProcReturn(ScmConstant.UNDEF, n, this)
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procSetCdrE.id)
                }
            }
        }
    }

    /** procedure: caar */
    val procCaar: ScmProcedure by lazy {
        object : ScmProcedure("caar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaar.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaar.id)
                        val result = try {
                            ScmPair.caar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaar.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaar.id)
                }
            }
        }
    }

    /** procedure: cadr */
    val procCadr: ScmProcedure by lazy {
        object : ScmProcedure("cadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCadr.id)
                        val result = try {
                            ScmPair.cadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCadr.id)
                }
            }
        }
    }

    /** procedure: cdar */
    val procCdar: ScmProcedure by lazy {
        object : ScmProcedure("cdar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCadr.id)
                        val result = try {
                            ScmPair.cdar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCadr.id)
                }
            }
        }
    }

    /** procedure: cddr */
    val procCddr: ScmProcedure by lazy {
        object : ScmProcedure("cddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCddr.id)
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCddr.id)
                        val result = try {
                            ScmPair.cddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCddr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCddr.id)
                }
            }
        }
    }

    /** procedure: caaar */
    val procCaaar: ScmProcedure by lazy {
        object : ScmProcedure("caaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaaar.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaaar.id)
                        val result = try {
                            ScmPair.caaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaaar.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaaar.id)
                }
            }
        }
    }

    /** procedure: caadr */
    val procCaadr: ScmProcedure by lazy {
        object : ScmProcedure("caadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaadr.id)
                        val result = try {
                            ScmPair.caadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaadr.id)
                }
            }
        }
    }

    /** procedure: cadar */
    val procCadar: ScmProcedure by lazy {
        object : ScmProcedure("cadar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaadr.id)
                        val result = try {
                            ScmPair.cadar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaadr.id)
                }
            }
        }
    }

    /** procedure: caddr */
    val procCaddr: ScmProcedure by lazy {
        object : ScmProcedure("caddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaddr.id)
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaddr.id)
                        val result = try {
                            ScmPair.caddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaddr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaddr.id)
                }
            }
        }
    }

    /** procedure: cdaar */
    val procCdaar: ScmProcedure by lazy {
        object : ScmProcedure("cdaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdaar.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdaar.id)
                        val result = try {
                            ScmPair.cdaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdaar.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdaar.id)
                }
            }
        }
    }

    /** procedure: cdadr */
    val procCdadr: ScmProcedure by lazy {
        object : ScmProcedure("cdadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdadr.id)
                        val result = try {
                            ScmPair.cdadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdadr.id)
                }
            }
        }
    }

    /** procedure: cddar */
    val procCddar: ScmProcedure by lazy {
        object : ScmProcedure("cddar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdadr.id)
                        val result = try {
                            ScmPair.cddar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdadr.id)
                }
            }
        }
    }

    /** procedure: cdddr */
    val procCdddr: ScmProcedure by lazy {
        object : ScmProcedure("cdddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdddr.id)
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdddr.id)
                        val result = try {
                            ScmPair.cdddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdddr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdddr.id)
                }
            }
        }
    }

    /** procedure: caaaar */
    val procCaaaar: ScmProcedure by lazy {
        object : ScmProcedure("caaaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaaaar.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaaaar.id)
                        val result = try {
                            ScmPair.caaaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaaaar.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaaaar.id)
                }
            }
        }
    }

    /** procedure: caaadr */
    val procCaaadr: ScmProcedure by lazy {
        object : ScmProcedure("caaadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaaadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaaadr.id)
                        val result = try {
                            ScmPair.caaadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaaadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaaadr.id)
                }
            }
        }
    }

    /** procedure: caadar */
    val procCaadar: ScmProcedure by lazy {
        object : ScmProcedure("caadar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaaadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaaadr.id)
                        val result = try {
                            ScmPair.caadar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaaadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaaadr.id)
                }
            }
        }
    }

    /** procedure: caaddr */
    val procCaaddr: ScmProcedure by lazy {
        object : ScmProcedure("caaddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCaaddr.id)
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCaaddr.id)
                        val result = try {
                            ScmPair.caaddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCaaddr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCaaddr.id)
                }
            }
        }
    }

    /** procedure: cadaar */
    val procCadaar: ScmProcedure by lazy {
        object : ScmProcedure("cadaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCadaar.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCadaar.id)
                        val result = try {
                            ScmPair.cadaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCadaar.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCadaar.id)
                }
            }
        }
    }

    /** procedure: cadadr */
    val procCadadr: ScmProcedure by lazy {
        object : ScmProcedure("cadadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCadadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCadadr.id)
                        val result = try {
                            ScmPair.cadadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCadadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCadadr.id)
                }
            }
        }
    }

    /** procedure: caddar */
    val procCaddar: ScmProcedure by lazy {
        object : ScmProcedure("caddar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCadadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCadadr.id)
                        val result = try {
                            ScmPair.caddar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCadadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCadadr.id)
                }
            }
        }
    }

    /** procedure: cadddr */
    val procCadddr: ScmProcedure by lazy {
        object : ScmProcedure("cadddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCadddr.id)
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCadddr.id)
                        val result = try {
                            ScmPair.cadddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCadddr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCadddr.id)
                }
            }
        }
    }

    /** procedure: cdaaar */
    val procCdaaar: ScmProcedure by lazy {
        object : ScmProcedure("cdaaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdaaar.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdaaar.id)
                        val result = try {
                            ScmPair.cdaaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdaaar.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdaaar.id)
                }
            }
        }
    }

    /** procedure: cdaadr */
    val procCdaadr: ScmProcedure by lazy {
        object : ScmProcedure("cdaadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdaadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdaadr.id)
                        val result = try {
                            ScmPair.cdaadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdaadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdaadr.id)
                }
            }
        }
    }

    /** procedure: cdadar */
    val procCdadar: ScmProcedure by lazy {
        object : ScmProcedure("cdadar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdaadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdaadr.id)
                        val result = try {
                            ScmPair.cdadar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdaadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdaadr.id)
                }
            }
        }
    }

    /** procedure: cdaddr */
    val procCdaddr: ScmProcedure by lazy {
        object : ScmProcedure("cdaddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCdaddr.id)
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCdaddr.id)
                        val result = try {
                            ScmPair.cdaddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCdaddr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCdaddr.id)
                }
            }
        }
    }

    /** procedure: cddaar */
    val procCddaar: ScmProcedure by lazy {
        object : ScmProcedure("cddaar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCddaar.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCddaar.id)
                        val result = try {
                            ScmPair.cddaar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCddaar.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCddaar.id)
                }
            }
        }
    }

    /** procedure: cddadr */
    val procCddadr: ScmProcedure by lazy {
        object : ScmProcedure("cddadr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCddadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCddadr.id)
                        val result = try {
                            ScmPair.cddadr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCddadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCddadr.id)
                }
            }
        }
    }

    /** procedure: cdddar */
    val procCdddar: ScmProcedure by lazy {
        object : ScmProcedure("cdddar", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCddadr.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCddadr.id)
                        val result = try {
                            ScmPair.cdddar(obj)
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException(procCddadr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCddadr.id)
                }
            }
        }
    }

    /** procedure: cddddr */
    val procCddddr: ScmProcedure by lazy {
        object : ScmProcedure("cddddr", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procCddddr.id)
                    1 -> {
                        val obj: ScmPair = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedPair(procCddddr.id)
                        val result = try {
                            ScmPair.cddddr(obj)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procCddddr.id)
                        }
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procCddddr.id)
                }
            }
        }
    }

    /** procedure: null? */
    val procNullQ: ScmProcedure by lazy {
        object : ScmProcedure("null?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procNullQ.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (obj == null) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procNullQ.id)
                }
            }
        }
    }

    /** procedure: list? */
    val procListQ: ScmProcedure by lazy {
        object : ScmProcedure("list?", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procListQ.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0)
                        val result = if (ScmPair.isProperList(obj)) ScmConstant.TRUE else ScmConstant.FALSE
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procListQ.id)
                }
            }
        }
    }

    /** procedure: make-list */
    val procMakeList: ScmProcedure by lazy {
        object : ScmProcedure("make-list", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val result = when (n) {
                    0 -> throw KevesExceptions.expected1Or2DatumGot0(procMakeList.id)
                    1 -> {
                        val k = (vm.stack.index(vm.sp, 0) as? ScmInt)?.value
                            ?: throw KevesExceptions.expectedInt(procMakeList.id)
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(procMakeList.id)
                        ScmMutablePair.makeList(k)
                    }
                    2 -> {
                        val sp = vm.sp
                        val k = (vm.stack.index(sp, 0) as? ScmInt)?.value
                            ?: throw KevesExceptions.expectedInt(procMakeList.id)
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(procMakeList.id)
                        val fill = vm.stack.index(sp, 1)
                        ScmMutablePair.makeList(k, fill)
                    }
                    else -> throw KevesExceptions.expected1Or2DatumGotMore(procMakeList.id)
                }
                vm.scmProcReturn(result, n, this)
            }
        }
    }

    /** procedure: list */
    val procList: ScmProcedure by lazy {
        object : ScmProcedure("list", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                val sp = vm.sp
                tailrec fun loop(index: Int, result: ScmPair?): ScmPair? =
                    if (index < 0) {
                        result
                    } else {
                        val obj = vm.stack.index(sp, index)
                        loop(index - 1, ScmMutablePair(obj, result))
                    }

                val list = loop(index = n - 1, result = null)
                vm.scmProcReturn(list, n, this)
            }
        }
    }

    /** procedure: length */
    val procLength: ScmProcedure by lazy {
        object : ScmProcedure("length", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procLength.id)
                    1 -> {
                        val list = vm.stack.index(vm.sp, 0)
                        try {
                            val length = ScmInt(ScmPair.length(list))
                            vm.scmProcReturn(length, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedProperList(procLength.id)
                        }
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procLength.id)
                }
            }
        }
    }

    /** procedure: append */
    val procAppend: ScmProcedure by lazy {
        object : ScmProcedure("append", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2OrMoreDatumGotLess(procAppend.id)
                    else -> {
                        val sp = vm.sp
                        tailrec fun loop(index: Int, result: ScmObject?): ScmObject? {
                            return if (index < 0) {
                                result
                            } else {
                                val list = vm.stack.index(sp, index)?.let {
                                    it as? ScmPair
                                        ?: throw KevesExceptions.expectedProperList(procAppend.id)
                                }
                                loop(index - 1, ScmMutablePair.append(list, result))
                            }
                        }

                        val last: ScmObject? = vm.stack.index(sp, n - 1)
                        val result = loop(n - 2, last)
                        vm.scmProcReturn(result, n, this)
                    }
                }
            }
        }
    }

    /** procedure: reverse */
    val procReverse: ScmProcedure by lazy {
        object : ScmProcedure("reverse", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procReverse.id)
                    1 -> {
                        val reversed = vm.stack.index(vm.sp, 0)?.let { obj ->
                            (obj as? ScmPair)?.let { list ->
                                try {
                                    ScmMutablePair.reverse(list)
                                } catch (e: IllegalArgumentException) {
                                    throw KevesExceptions.expectedProperList(procReverse.id)
                                }
                            } ?: throw KevesExceptions.expectedList(procReverse.id)
                        }
                        vm.scmProcReturn(reversed, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procReverse.id)
                }
            }
        }
    }

    /** procedure: list-tail */
    val procListTail: ScmProcedure by lazy {
        object : ScmProcedure("list-tail", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procListTail.id)
                    2 -> {
                        val sp = vm.sp
                        val k = (vm.stack.index(sp, 1) as? ScmInt)?.value
                            ?: throw KevesExceptions.expectedInt(procListTail.id)
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(procListTail.id)
                        val list = vm.stack.index(sp, 0)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procListTail.id)
                        }
                        try {
                            val result = ScmPair.listTail(list, k)
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.tooShortList(procListTail.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procListTail.id)
                }
            }
        }
    }

    /** procedure: list-set! */
    val procListSetE: ScmProcedure by lazy {
        object : ScmProcedure("list-set!", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    in 0..2 -> throw KevesExceptions.expected3DatumGotLess(procListSetE.id)
                    3 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 2)
                        val k = (vm.stack.index(sp, 1) as? ScmInt)?.value
                            ?: throw KevesExceptions.expectedInt(procListSetE.id)
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(procListSetE.id)
                        val list = vm.stack.index(sp, 0)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procListSetE.id)
                        }
                        val listTail = try {
                            ScmPair.listTail(list, k)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.tooShortList(procListSetE.id)
                        }
                        (listTail as? ScmMutablePair)?.assignCar(obj)
                            ?: throw KevesExceptions.expectedMutablePair(procListSetE.id)
                        return vm.scmProcReturn(ScmConstant.UNDEF, n, this)
                    }
                    else -> throw KevesExceptions.expected3DatumGotMore(procListSetE.id)
                }
            }
        }
    }

    /** procedure: list-ref */
    val procListRef: ScmProcedure by lazy {
        object : ScmProcedure("list-ref", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procListRef.id)
                    2 -> {
                        val sp = vm.sp
                        val k = (vm.stack.index(sp, 1) as? ScmInt)?.value
                            ?: throw KevesExceptions.expectedInt(procListRef.id)
                        if (k < 0) throw KevesExceptions.expectedPositiveNumberGotNegative(procListRef.id)
                        val list = vm.stack.index(sp, 0)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procListRef.id)
                        }
                        try {
                            val result = ScmPair.car(ScmPair.listTail(list, k))
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.tooShortList(procListRef.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procListRef.id)
                }
            }
        }
    }

    /** procedure: memq */
    val procMemq: ScmProcedure by lazy {
        object : ScmProcedure("memq", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procMemq.id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procMemq.id)
                        }
                        try {
                            val result = ScmPair.memq(obj, list)
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(procMemq.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procMemq.id)
                }
            }
        }
    }

    /** procedure: memv */
    val procMemv: ScmProcedure by lazy {
        object : ScmProcedure("memv", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procMemv.id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procMemv.id)
                        }
                        try {
                            val result = ScmPair.memv(obj, list)
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(procMemv.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procMemv.id)
                }
            }
        }
    }

    /** procedure: member */
    val procMember: ScmProcedure by lazy {
        object : ScmProcedure("member", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procMember.id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procMember.id)
                        }
                        try {
                            val result = ScmPair.member(obj, list)
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(procMember.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procMember.id)
                }
            }
        }
    }

    /** procedure: assq */
    val procAssq: ScmProcedure by lazy {
        object : ScmProcedure("assq", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procAssq.id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procAssq.id)
                        }
                        try {
                            val result = ScmPair.assq(obj, list)
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.expectedList(procAssq.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procAssq.id)
                }
            }
        }
    }

    /** procedure: assv */
    val procAssv: ScmProcedure by lazy {
        object : ScmProcedure("assv", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procAssv.id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procAssv.id)
                        }
                        try {
                            val result = ScmPair.assv(obj, list)
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procAssv.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procAssv.id)
                }
            }
        }
    }

    /** procedure: assoc */
    val procAssoc: ScmProcedure by lazy {
        object : ScmProcedure("assoc", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0, 1 -> throw KevesExceptions.expected2DatumGotLess(procAssoc.id)
                    2 -> {
                        val sp = vm.sp
                        val obj = vm.stack.index(sp, 0)
                        val list = vm.stack.index(sp, 1)?.let {
                            it as? ScmPair ?: throw KevesExceptions.expectedList(procAssoc.id)
                        }
                        try {
                            val result = ScmPair.assoc(obj, list)
                            return vm.scmProcReturn(result, n, this)
                        } catch (e: IllegalArgumentException) {
                            throw KevesExceptions.failed(procAssoc.id)
                        }
                    }
                    else -> throw KevesExceptions.expected2DatumGotMore(procAssoc.id)
                }
            }
        }
    }

    /** procedure: list-copy */
    val procListCopy: ScmProcedure by lazy {
        object : ScmProcedure("list-copy", null) {
            override fun directProc(acc: ScmObject?, sp: Int, vm: KevesVM) {}
            override fun normalProc(n: Int, vm: KevesVM) {
                when (n) {
                    0 -> throw KevesExceptions.expected1DatumGot0(procListCopy.id)
                    1 -> {
                        val obj = vm.stack.index(vm.sp, 0) as? ScmPair
                            ?: throw KevesExceptions.expectedList(procListCopy.id)
                        val result = ScmMutablePair.listCopy(obj)
                        vm.scmProcReturn(result, n, this)
                    }
                    else -> throw KevesExceptions.expected1DatumGotMore(procListCopy.id)
                }
            }
        }
    }
}