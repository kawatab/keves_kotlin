/*
 * ScmSymbol.kt
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

import io.github.kawatab.keveskotlin.KevesResources
import io.github.kawatab.keveskotlin.PtrSymbol
import java.util.*

class ScmSymbol private constructor(private val symbol: String) : ScmObject() {
    override fun toStringForWrite(res: KevesResources): String =
        if (symbol.run {
                indexOf(' ') >= 0 ||
                        indexOf('\t') >= 0 ||
                        indexOf('\n') >= 0 ||
                        indexOf('\r') >= 0
            }
        ) {
            "|$symbol|"
                .replace("\t", "\\x09;")
                .replace("\n", "\\x0a;")
                .replace("\r", "\\x0d;")
        } else {
            symbol
        }

    override fun toStringForDisplay(res: KevesResources): String = toStringForWrite(res)

    val rawString get() = symbol

    companion object {
        fun get(symbol: String, res: KevesResources): PtrSymbol =
            res.symbolList.getOrPut(
                symbol,
                {
                    val obj = ScmSymbol(symbol)
                    val ptr = res.addSymbol(obj)
                    res.symbolList[symbol] = ptr
                    ptr
                })

        fun generate(res: KevesResources): PtrSymbol {
            val uuid = "symbol${UUID.randomUUID()}"
            val obj = ScmSymbol(uuid)
            val ptr = res.addSymbol(obj)
            res.symbolList[uuid] = ptr
            return ptr
        }
    }
}
