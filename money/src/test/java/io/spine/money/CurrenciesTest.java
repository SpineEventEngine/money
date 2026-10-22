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

import io.spine.testing.UtilityClassTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.money.given.TestCurrencyProvider.BITCOIN;
import static io.spine.money.given.TestCurrencyProvider.BITCOIN_FRACTION_DIGITS;
import static io.spine.money.given.TestCurrencyProvider.MISMATCHED;
import static io.spine.money.given.TestCurrencyProvider.TESTING;
import static io.spine.money.given.TestCurrencyProvider.TESTING_FRACTION_DIGITS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("`Currencies` utility class should")
class CurrenciesTest extends UtilityClassTest<Currencies> {

    CurrenciesTest() {
        super(Currencies.class);
    }

    @Nested
    @DisplayName("create a currency")
    class Create {

        @Test
        @DisplayName("with the given code")
        void withCode() {
            assertEquals("UAH", Currencies.of("UAH").getCode());
        }

        @Test
        @DisplayName("converting the code to upper case")
        void upperCase() {
            assertEquals(Currencies.USD, Currencies.of("usd"));
            assertEquals(Currencies.USD, Currencies.of("Usd"));
        }

        @Test
        @DisplayName("with a code not defined by ISO 4217")
        void nonIso() {
            assertEquals("1INCH", Currencies.of("1inch").getCode());
        }

        @Test
        @DisplayName("rejecting invalid codes")
        void invalidCode() {
            assertRejected("");
            assertRejected("U");
            assertRejected("ABCDEFGHIJKLM");
            assertRejected("US D");
            assertRejected("US-D");
            assertRejected("ÉUR");
        }

        private void assertRejected(String code) {
            assertThrows(IllegalArgumentException.class, () -> Currencies.of(code));
        }
    }

    @Test
    @DisplayName("provide constants for widely used currencies")
    void constants() {
        assertEquals("USD", Currencies.USD.getCode());
        assertEquals("EUR", Currencies.EUR.getCode());
        assertEquals("JPY", Currencies.JPY.getCode());
        assertEquals("GBP", Currencies.GBP.getCode());
        assertEquals("CHF", Currencies.CHF.getCode());
        assertEquals("AUD", Currencies.AUD.getCode());
        assertEquals("CAD", Currencies.CAD.getCode());
    }

    @Nested
    @DisplayName("find information")
    class Find {

        @Test
        @DisplayName("about ISO 4217 currencies")
        void iso() {
            var info = Currencies.find(Currencies.EUR).orElseThrow();

            assertEquals(Currencies.EUR, info.getCurrency());
            assertEquals("Euro", info.getName());
            assertEquals(978, info.getNumericCode());
            assertEquals(2, info.getFractionDigits());
        }

        @Test
        @DisplayName("about currencies of application providers")
        void custom() {
            var info = Currencies.find(Currencies.of(BITCOIN)).orElseThrow();

            assertEquals("Bitcoin", info.getName());
            assertEquals(BITCOIN_FRACTION_DIGITS, info.getFractionDigits());
        }

        @Test
        @DisplayName("from application providers before the built-in one")
        void appProvidersFirst() {
            var info = Currencies.find(Currencies.of(TESTING)).orElseThrow();

            assertEquals(TESTING_FRACTION_DIGITS, info.getFractionDigits());
        }

        @Test
        @DisplayName("returning empty `Optional` for unknown currencies")
        void unknown() {
            assertTrue(Currencies.find(Currencies.of("NOPE")).isEmpty());
        }

        @Test
        @DisplayName("rejecting currencies with invalid codes")
        void invalidCode() {
            var currency = Currency.newBuilder()
                    .setCode("usd")
                    .buildPartial();

            assertThrows(IllegalArgumentException.class, () -> Currencies.find(currency));
        }

        @Test
        @DisplayName("rejecting information about another currency")
        void mismatched() {
            var currency = Currencies.of(MISMATCHED);

            assertThrows(IllegalStateException.class, () -> Currencies.find(currency));
        }
    }
}
