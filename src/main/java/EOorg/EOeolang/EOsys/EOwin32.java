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
import com.sun.jna.platform.win32.Kernel32;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

/**
 * WIN32.
 *
 * @since 0.1
 * @checkstyle TypeNameCheck (100 lines)
 * @link https://java-native-access.github.io/jna/4.2.1/com/sun/jna/platform/win32/WinBase.html
 */
public class EOwin32 extends PhDefault {

    /**
     * Kernel32 lib.
     */
    private static final Kernel32 KERNEL = Native.load("kernel32", Kernel32.class);

    /**
     * Ctor.
     * @param sigma The \sigma
     */
    public EOwin32(final Phi sigma) {
        super(sigma);
        this.add("function", new AtFree());
        this.add("args", new AtVararg());
        this.add(
            "φ",
            new AtComposite(
                this,
                rho -> {
                    final Phi[] args = new Param(rho, "args").strong(Phi[].class);
                    final Object[] params = new Object[args.length];
                    final Class<?>[] types = new Class<?>[args.length];
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
                                    "Type '%s' is not supported by Win32",
                                    val.getClass().getCanonicalName()
                                )
                            );
                        }
                        params[index] = val;
                        types[index] = val.getClass();
                    }
                    final int ret = Integer.class.cast(
                        EOwin32.func(
                            new Param(rho, "function").strong(String.class), types
                        ).invoke(EOwin32.KERNEL, params)
                    );
                    return new Data.ToPhi((long) ret);
                }
            )
        );
    }

    /**
     * Find the function in the library.
     * @param name The name of it, for example "GetCurrentProcessId"
     * @param types The types it accepts
     * @return The method
     */
    private static Method func(final String name, final Class<?>... types) {
        final Method mtd;
        try {
            mtd = Class.class.getMethod("getMethod", String.class, Class[].class);
        } catch (final NoSuchMethodException ex) {
            throw new IllegalStateException(
                "Can't find method 'getMethod()'",
                ex
            );
        }
        try {
            return Method.class.cast(
                mtd.invoke(EOwin32.KERNEL.getClass(), name, types)
            );
        } catch (final IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(
                String.format(
                    "Can't invoke getMethod('%s', %d types)",
                    name, types.length
                ),
                ex
            );
        }
    }

}
