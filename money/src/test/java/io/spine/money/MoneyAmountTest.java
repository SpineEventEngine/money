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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MoneyAmount utility class should")
class MoneyAmountTest {

    @Test
    @DisplayName("create positive amounts")
    void createPositiveValue() {
        long units = 639_100_000_000L;
        int nanos = 949_000_000;
        MoneyAmount money = MoneyAmount.of(Currencies.USD, units, nanos);

        assertEquals(Currencies.USD, money.currency());
        assertEquals(units, money.units());
        assertEquals(nanos, money.nanos());
    }

    @Test
    @DisplayName("create negative value")
    void createNegativeValue() {
        long units = -1;
        int nanos = -750_000_000;

        MoneyAmount money = MoneyAmount.of(Currencies.EUR, units, nanos);

        assertEquals(Currencies.EUR, money.currency());
        assertEquals(units, money.units());
        assertEquals(nanos, money.nanos());
    }

    @Test
    @DisplayName("create money amount holder out of money")
    void createOfMoney() {
        Money money = Money
                .newBuilder()
                .setCurrency(Currencies.of("UAH"))
                .setUnits(1)
                .build();
        MoneyAmount amount = MoneyAmount.of(money);
        assertEquals(money.getCurrency(), amount.currency());
        assertEquals(money.getUnits(), amount.units());
    }

    @Test
    @DisplayName("reject values of different signs")
    void differentSigns() {
        assertThrows(
                IllegalArgumentException.class,
                () -> MoneyAmount.of(Currencies.EUR, -1, 999_999)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> MoneyAmount.of(Currencies.EUR, 10, -999_999)
        );
    }

    @Test
    @DisplayName("reject a currency without a code")
    void noCurrencyCode() {
        assertThrows(
                IllegalArgumentException.class,
                () -> MoneyAmount.of(Currency.getDefaultInstance(), 1, 0)
        );
    }

    @Test
    @DisplayName("accept a currency not known to any `CurrencyProvider`")
    void unknownCurrency() {
        var currency = Currencies.of("NOPE");
        var amount = MoneyAmount.of(currency, 1, 0);

        assertEquals(currency, amount.currency());
    }
}
