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

import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;

/**
 * Provides information about the ISO 4217 currencies known to the Java runtime.
 *
 * <p>The information comes from {@link java.util.Currency}, which the JDK keeps up to date
 * with amendments to ISO 4217. Besides current currencies, the JDK knows some
 * historical ones (e.g., {@code DEM}).
 */
final class JdkCurrencyProvider implements CurrencyProvider {

    private static final ImmutableMap<String, java.util.Currency> CURRENCIES =
            java.util.Currency.getAvailableCurrencies()
                              .stream()
                              .collect(toImmutableMap(java.util.Currency::getCurrencyCode,
                                                      identity()));

    @Override
    public Optional<CurrencyInfo> find(String code) {
        checkNotNull(code);
        var currency = CURRENCIES.get(code);
        if (currency == null) {
            return Optional.empty();
        }
        var info = CurrencyInfo.newBuilder()
                .setCurrency(Currencies.of(code))
                .setName(currency.getDisplayName(Locale.ENGLISH))
                .setSymbol(currency.getSymbol(Locale.ENGLISH))
                .setNumericCode(currency.getNumericCode())
                .setFractionDigits(currency.getDefaultFractionDigits())
                .build();
        return Optional.of(info);
    }
}
