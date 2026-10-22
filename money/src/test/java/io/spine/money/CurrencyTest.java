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
import static io.spine.money.given.TestCurrencyProvider.BITCOIN_FRACTION_DIGITS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("`Currency` should")
class CurrencyTest {

    @Test
    @DisplayName("obtain information about itself")
    void info() {
        var info = Currencies.USD.info();

        assertEquals(Currencies.USD, info.getCurrency());
        assertEquals("US Dollar", info.getName());
        assertEquals(840, info.getNumericCode());
    }

    @Test
    @DisplayName("obtain the number of fraction digits")
    void fractionDigits() {
        assertEquals(2, Currencies.USD.fractionDigits());
        assertEquals(0, Currencies.JPY.fractionDigits());
        assertEquals(3, Currencies.of("BHD").fractionDigits());
        assertEquals(-1, Currencies.of("XAU").fractionDigits());
        assertEquals(BITCOIN_FRACTION_DIGITS, Currencies.of(BITCOIN).fractionDigits());
    }

    @Test
    @DisplayName("throw `UnknownCurrencyException` if no provider knows the currency")
    void unknown() {
        var currency = Currencies.of("NOPE");

        var exception = assertThrows(UnknownCurrencyException.class, currency::info);
        assertEquals("NOPE", exception.code());
        assertTrue(exception.getMessage().contains("`NOPE`"));
    }
}
