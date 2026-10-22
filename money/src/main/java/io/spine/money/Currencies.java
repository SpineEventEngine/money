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

import java.util.Locale;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.money.MoneyPreconditions.checkValidCode;

/**
 * Utilities for working with {@link Currency} values.
 */
public final class Currencies {

    /** The US dollar. */
    public static final Currency USD = of("USD");

    /** The euro. */
    public static final Currency EUR = of("EUR");

    /** The Japanese yen. */
    public static final Currency JPY = of("JPY");

    /** The pound sterling. */
    public static final Currency GBP = of("GBP");

    /** The Swiss franc. */
    public static final Currency CHF = of("CHF");

    /** The Australian dollar. */
    public static final Currency AUD = of("AUD");

    /** The Canadian dollar. */
    public static final Currency CAD = of("CAD");

    /** Prevents instantiation of this utility class. */
    private Currencies() {
    }

    /**
     * Creates a currency with the given code.
     *
     * <p>The code is converted to upper case, so {@code "usd"} and {@code "USD"} produce
     * the same currency.
     *
     * <p>The method does not check that a {@link CurrencyProvider} knows the currency.
     * Use {@link #find(Currency)} for that.
     *
     * @param code
     *         the code of the currency, e.g. {@code "EUR"} or {@code "BTC"}
     * @throws IllegalArgumentException
     *         if the code in upper case does not consist of 2 to 12 ASCII letters or digits
     */
    public static Currency of(String code) {
        checkNotNull(code);
        var upperCase = code.toUpperCase(Locale.ROOT);
        checkValidCode(upperCase);
        return Currency.newBuilder()
                .setCode(upperCase)
                .build();
    }

    /**
     * Finds information about the given currency.
     *
     * @return information about the currency, or empty {@code Optional} if
     *         no {@link CurrencyProvider} knows the currency
     * @throws IllegalArgumentException
     *         if the code of the currency is invalid, which may happen if the currency
     *         was not created by {@link #of(String)} or a validating builder
     * @see Currency#info()
     */
    public static Optional<CurrencyInfo> find(Currency currency) {
        checkNotNull(currency);
        return CurrencyProviders.find(currency.getCode());
    }
}
