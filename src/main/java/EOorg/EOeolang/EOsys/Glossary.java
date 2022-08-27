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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.eolang.ExFailure;

/**
 * Known system functions.
 * @since 0.1
 */
final class Glossary {
    /**
     * Syscall IDs.
     * @link <a href="https://opensource.apple.com/source/xnu/xnu-1504.3.12/bsd/kern/syscalls.master">syscalls.master</a>
     * @link <a href="https://unix.stackexchange.com/questions/421750/where-do-you-find-the-syscall-table-for-linux">Where do you find syscall table></a>
     * @link <a href="https://github.com/torvalds/linux/blob/v4.17/arch/x86/entry/syscalls/syscall_64.tbl#L11">Syscall table fo Linux/64</a>
     */
    private static final Map<String, SysCall> GLOSSARY = new HashMap<>(0);

    /**
     * OS name.
     */
    private static final String UNAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

    static {
        final String uname = Glossary.UNAME;
        if (uname.contains("mac")) {
            Glossary.GLOSSARY.put("write", new ScDefault(4));
            Glossary.GLOSSARY.put("getpid", new ScDefault(20));
            Glossary.GLOSSARY.put(
                "gettimeofday",
                new ScGetTimeOfDay(
                    new ScDefault(116)
                )
            );
        } else if (uname.contains("linux")) {
            Glossary.GLOSSARY.put("write", new ScDefault(1));
            Glossary.GLOSSARY.put("getpid", new ScDefault(39));
            Glossary.GLOSSARY.put(
                "gettimeofday",
                new ScGetTimeOfDay(
                    new ScDefault(96)
                )
            );
        }
    }

    /**
     * Not for instantiation.
     */
    private Glossary() {
    }

    /**
     * Get system function for a name.
     * @param func Function name
     * @return Function spec
     */
    public static SysCall syscall(final String func) {
        final SysCall result;
        if (func.matches("[0-9]+")) {
            result = new ScDefault(
                Integer.parseInt(func)
            );
        } else {
            if (!Glossary.GLOSSARY.containsKey(func)) {
                throw new ExFailure(
                    String.format(
                        "Unknown syscall '%s' for '%s'",
                        func, Glossary.UNAME
                    )
                );
            }
            result = Glossary.GLOSSARY.get(func);
        }
        return result;
    }
}
