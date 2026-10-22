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

package io.spine.money.given;

import io.spine.money.Currencies;
import io.spine.money.CurrencyInfo;
import io.spine.money.CurrencyProvider;

import java.util.Optional;

/**
 * Provides currencies for the tests of the library.
 *
 * <p>The provider is listed in {@code META-INF/services/io.spine.money.CurrencyProvider}
 * among the test resources.
 */
public final class TestCurrencyProvider implements CurrencyProvider {

    /**
     * The code of a currency not defined by ISO 4217.
     */
    public static final String BITCOIN = "BTC";

    /**
     * The number of fraction digits of the {@link #BITCOIN} currency.
     */
    public static final int BITCOIN_FRACTION_DIGITS = 8;

    /**
     * The ISO 4217 code reserved for testing.
     *
     * <p>This provider replaces the information which the Java runtime has about this currency.
     */
    public static final String TESTING = "XTS";

    /**
     * The number of fraction digits this provider sets for the {@link #TESTING} currency.
     *
     * <p>The Java runtime treats this currency as having no minor units.
     */
    public static final int TESTING_FRACTION_DIGITS = 4;

    /**
     * The code for which this provider wrongly returns information about
     * the {@link #TESTING} currency.
     */
    public static final String MISMATCHED = "MISMATCH";

    @Override
    public Optional<CurrencyInfo> find(String code) {
        return switch (code) {
            case BITCOIN -> Optional.of(info(BITCOIN, "Bitcoin", BITCOIN_FRACTION_DIGITS));
            case TESTING, MISMATCHED ->
                    Optional.of(info(TESTING, "Testing currency", TESTING_FRACTION_DIGITS));
            default -> Optional.empty();
        };
    }

    private static CurrencyInfo info(String code, String name, int fractionDigits) {
        return CurrencyInfo.newBuilder()
                .setCurrency(Currencies.of(code))
                .setName(name)
                .setFractionDigits(fractionDigits)
                .build();
    }
}
