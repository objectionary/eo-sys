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
import java.util.Arrays;
import org.eolang.ExFailure;
import org.eolang.Phi;

/**
 * Base call to `syscall`.
 * @since 0.1
 */
final class ScDefault implements SysCall {

    /**
     * Id of a function to call via `syscall`.
     */
    private final int cid;

    /**
     * Ctor.
     * @param cid ID of a function
     */
    ScDefault(final int cid) {
        this.cid = cid;
    }

    @Override
    public long call(final Object[] params) {
        final Object[] prepared = Arrays.stream(params).sequential()
            .map(
                p -> {
                    Object checked = p;
                    if (checked instanceof String) {
                        checked = Native.toByteArray(
                            String.class.cast(checked), StandardCharsets.UTF_8
                        );
                    }
                    if (checked instanceof Long) {
                        checked = Long.class.cast(checked).intValue();
                    }
                    if (checked instanceof Double || checked instanceof Boolean
                        || checked instanceof Phi[]) {
                        throw new ExFailure(
                            String.format(
                                "Type '%s' is not supported by syscall",
                                checked.getClass().getCanonicalName()
                            )
                        );
                    }
                    return checked;
                }
            ).toArray();
        final int ret = CStdLib.CSTDLIB.syscall(this.cid, prepared);
        if (ret == -1) {
            throw new ExFailure(
                String.format(
                    "Syscall #%d returned -1, while errno=%d",
                    this.cid, Native.getLastError()
                )
            );
        }
        return ret;
    }
}
