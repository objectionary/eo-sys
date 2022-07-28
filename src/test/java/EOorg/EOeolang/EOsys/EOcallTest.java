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

import java.lang.management.ManagementFactory;
import org.eolang.Data;
import org.eolang.Dataized;
import org.eolang.PhWith;
import org.eolang.Phi;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * CALL test.
 *
 * @since 0.1
 * @checkstyle TypeNameCheck (100 lines)
 */
public final class EOcallTest {

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void readsPid() {
        MatcherAssert.assertThat(
            new Dataized(
                new PhWith(
                    new EOcall(Phi.Φ),
                    "id",
                    new Data.ToPhi("getpid")
                )
            ).take(Long.class),
            Matchers.equalTo(
                Long.parseLong(
                    ManagementFactory.getRuntimeMXBean()
                        .getName().split("[^0-9]")[0]
                )
            )
        );
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void printsToConsole() {
        final String msg = "Hello, друг!\n";
        MatcherAssert.assertThat(
            new Dataized(
                new PhWith(
                    new PhWith(
                        new PhWith(
                            new PhWith(
                                new EOcall(Phi.Φ),
                                0, new Data.ToPhi("write")
                            ),
                            1, new Data.ToPhi(1L)
                        ),
                        2, new Data.ToPhi(msg)
                    ),
                    3, new Data.ToPhi((long) msg.length())
                )
            ).take(Long.class),
            Matchers.equalTo((long) msg.length())
        );
    }

}
