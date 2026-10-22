/*
 * Copyright 2026 CodeMatters, Lda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package io.spine.money;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.money.given.TestCurrencyProvider.BITCOIN;
import static io.spine.money.given.TestCurrencyProvider.TESTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("`JdkCurrencyProvider` should")
class JdkCurrencyProviderTest {

    private final CurrencyProvider provider = new JdkCurrencyProvider();

    @Test
    @DisplayName("provide information about ISO 4217 currencies")
    void iso() {
        var info = provider.find("JPY").orElseThrow();

        assertEquals(Currencies.JPY, info.getCurrency());
        assertEquals("Japanese Yen", info.getName());
        assertEquals("¥", info.getSymbol());
        assertEquals(392, info.getNumericCode());
        assertEquals(0, info.getFractionDigits());
    }

    @Test
    @DisplayName("tell that a currency has no minor units")
    void noMinorUnits() {
        assertEquals(-1, provider.find("XAU").orElseThrow().getFractionDigits());
        assertEquals(-1, provider.find(TESTING).orElseThrow().getFractionDigits());
    }

    @Test
    @DisplayName("not know currencies not defined by ISO 4217")
    void nonIso() {
        assertTrue(provider.find(BITCOIN).isEmpty());
    }
}
