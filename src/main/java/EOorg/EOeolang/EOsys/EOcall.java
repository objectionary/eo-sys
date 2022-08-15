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

import com.sun.jna.Native;
import java.nio.charset.StandardCharsets;
import org.eolang.AtComposite;
import org.eolang.AtFree;
import org.eolang.AtVararg;
import org.eolang.Data;
import org.eolang.Dataized;
import org.eolang.ExFailure;
import org.eolang.Param;
import org.eolang.PhDefault;
import org.eolang.Phi;
import org.eolang.sys.CStdLib;
import org.eolang.sys.Glossary;
import org.eolang.sys.ScDefault;
import org.eolang.sys.SysCall;

/**
 * CALL.
 *
 * @since 0.1
 * @checkstyle TypeNameCheck (100 lines)
 */
public class EOcall extends PhDefault {

    /**
     * Name of the OS.
     */
    public static final String UNAME = System.getProperty("os.name");

    /**
     * Ctor.
     * @param sigma The \sigma
     */
    public EOcall(final Phi sigma) {
        super(sigma);
        this.add("id", new AtFree());
        this.add("args", new AtVararg());
        this.add(
            "φ",
            new AtComposite(
                this,
                rho -> {
                    final CStdLib lib = CStdLib.class.cast(
                        Native.load("c", CStdLib.class)
                    );
                    final Phi[] args = new Param(rho, "args").strong(Phi[].class);
                    final Object[] params = new Object[args.length];
                    for (int index = 0; index < args.length; ++index) {
                        Object val = new Dataized(args[index]).take();
                        if (val instanceof Long) {
                            val = Long.class.cast(val).intValue();
                        }
                        if (val instanceof String) {
                            val = Native.toByteArray(
                                String.class.cast(val), StandardCharsets.UTF_8
                            );
                        }
                        if (val instanceof Double || val instanceof Boolean
                            || val instanceof Phi[]) {
                            throw new ExFailure(
                                String.format(
                                    "Type '%s' is not supported by syscall",
                                    val.getClass().getCanonicalName()
                                )
                            );
                        }
                        params[index] = val;
                    }
                    final SysCall syscall = EOcall.sysfunc(rho);
                    final long ret = syscall.call(lib, params);
                    return new Data.ToPhi(ret);
                }
            )
        );
    }

    /**
     * Take call ID.
     * @param rho The \rho
     * @return ID
     */
    private static SysCall sysfunc(final Phi rho) {
        final String txt = new Param(rho, "id").strong(String.class);
        if (!Glossary.contains(txt)) {
            throw new ExFailure(
                String.format(
                    "Unknown syscall '%s' for '%s'",
                    txt, EOcall.UNAME
                )
            );
        }
        final SysCall result;
        if (txt.matches("[0-9]+")) {
            result = new ScDefault(
                Integer.parseInt(txt)
            );
        } else {
            result = Glossary.sysfunc(txt);
        }
        return result;
    }

}
