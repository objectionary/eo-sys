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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.eolang.AtComposite;
import org.eolang.AtFree;
import org.eolang.AtVararg;
import org.eolang.Data;
import org.eolang.Dataized;
import org.eolang.ExFailure;
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
     * Name of the OS.
     */
    public static final String UNAME = System.getProperty("os.name");

    /**
     * Syscall IDs.
     */
    private static final Map<String, Integer> IDS = new HashMap<>(0);

    static {
        final String uname = EOcall.UNAME.toLowerCase(Locale.ENGLISH);
        if (uname.contains("mac")) {
            EOcall.IDS.put("write", 1);
            EOcall.IDS.put("getpid", 20);
        } else if (uname.contains("linux")) {
            EOcall.IDS.put("write", 1);
            EOcall.IDS.put("getpid", 39);
        }
    }

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
                    final EOcall.CStdLib lib = EOcall.CStdLib.class.cast(
                        Native.load("c", EOcall.CStdLib.class)
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
                    final int cid = EOcall.number(rho);
                    final int ret = Integer.class.cast(
                        lib.getClass().getMethod("syscall", int.class, Object[].class).invoke(
                            lib, cid, params
                        )
                    );
                    if (ret == -1) {
                        throw new ExFailure(
                            String.format(
                                "Syscall #%d returned -1, while errno=%d",
                                cid, Native.getLastError()
                            )
                        );
                    }
                    return new Data.ToPhi((long) ret);
                }
            )
        );
    }

    /**
     * Take call ID.
     * @param rho The \rho
     * @return ID
     */
    private static int number(final Phi rho) {
        final String txt = new Param(rho, "id").strong(String.class);
        final Integer cid;
        if (txt.matches("[0-9]+")) {
            cid = Integer.parseInt(txt);
        } else {
            cid = EOcall.IDS.get(txt);
        }
        if (EOcall.IDS.isEmpty()) {
            throw new ExFailure(
                String.format(
                    "It's impossible to syscall at this OS: '%s'",
                    EOcall.UNAME
                )
            );
        }
        if (cid == null) {
            throw new ExFailure(
                String.format(
                    "Unknown syscall '%s' for '%s'",
                    txt, EOcall.UNAME
                )
            );
        }
        return cid;
    }

}
