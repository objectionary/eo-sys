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
package org.eolang.sys;

import com.sun.jna.Structure;

/**
 * A `gettimeofday` system function.
 * @since 0.1
 */
public final class ScGetTimeOfDay implements SysCall {
    /**
     * Base system function call.
     */
    private final SysCall underlying;

    /**
     * Ctor.
     * @param underlying Underlying system call
     */
    public ScGetTimeOfDay(final SysCall underlying) {
        this.underlying = underlying;
    }

    @Override
    public long call(final CStdLib lib, final Object[] params) {
        final Timeval timeval = new Timeval();
        final Object[] adjusted = new Object[]{timeval, null};
        this.underlying.call(lib, adjusted);
        return timeval.sec * 1000000 + timeval.usec;
    }

    /**
     * Timeval structure specification as per
     * <a href="https://man7.org/linux/man-pages/man2/gettimeofday.2.html">Linux man page</a>.
     * @since 0.1
     * @checkstyle VisibilityModifierCheck (20 lines)
     */
    @Structure.FieldOrder({"sec", "usec"})
    public static class Timeval extends Structure {
        /**
         * Seconds.
         */
        public long sec;

        /**
         * Microseconds.
         */
        public long usec;
    }
}
