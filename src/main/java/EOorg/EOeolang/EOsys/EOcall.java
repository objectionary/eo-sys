/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
// @checkstyle PackageNameCheck (1 line)
package EOorg.EOeolang.EOsys;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.eolang.AtComposite;
import org.eolang.AtFree;
import org.eolang.Data;
import org.eolang.Param;
import org.eolang.PhDefault;
import org.eolang.Phi;

/**
 * CALL.
 *
 * @since 0.1
 * @checkstyle TypeNameCheck (100 lines)
 */
public class EOcall extends PhDefault {

    /**
     * Interface to stdlib.
     * @since 0.1
     */
    interface CStdLib extends Library {
        /**
         * Make syscall.
         * @param cid Call ID from sys/syscall.h
         * @param args Arguments
         * @return The result as LONG
         */
        int syscall(int cid, Object... args);
    }

    /**
     * Ctor.
     * @param sigma The \sigma
     * @checkstyle BracketsStructureCheck (200 lines)
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public EOcall(final Phi sigma) {
        super(sigma);
        this.add("id", new AtFree());
        this.add("φ", new AtComposite(this, rho -> {
            final EOcall.CStdLib lib = EOcall.CStdLib.class.cast(
                Native.load("c", EOcall.CStdLib.class)
            );
            return new Data.ToPhi(
                (long) lib.syscall(
                    new Param(rho, "id").strong(Long.class).intValue()
                )
            );
        }));
    }

}
