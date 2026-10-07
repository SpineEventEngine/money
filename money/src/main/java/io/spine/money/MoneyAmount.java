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

import io.spine.value.ValueHolder;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.money.MoneyPreconditions.checkValid;

/**
 * An amount of money.
 *
 * <p>This class wraps a {@link io.spine.money.Money Money} value, and allows to create
 * monetary amounts.
 */
public final class MoneyAmount extends ValueHolder<Money> {

    private static final long serialVersionUID = 0L;

    private MoneyAmount(Money amount) {
        super(amount);
    }

    /**
     * Creates a new amount of money with the passed value.
     */
    public static MoneyAmount of(Money value) {
        checkNotNull(value);
        checkValid(value);
        return new MoneyAmount(value);
    }

    /**
     * Creates a new amount of money.
     *
     * @param currency
     *         the currency of the amount of money
     * @param units
     *         the amount of whole currency units
     * @param nanos
     *         the number of (10^-9) units of the amount for representing amounts in
     *         minor currency units (for the currencies that support such amounts).
     */
    public static MoneyAmount of(Currency currency, long units, int nanos) {
        checkNotNull(currency);
        checkValid(currency, units, nanos);
        var amount = Money.newBuilder()
                .setCurrency(currency)
                .setUnits(units)
                .setNanos(nanos)
                .build();
        return new MoneyAmount(amount);
    }

    /**
     * Obtains the currency of the amount of money.
     */
    public Currency currency() {
        return value().getCurrency();
    }

    /**
     * Obtains units value of the amount.
     */
    public long units() {
        return value().getUnits();
    }

    /**
     * Obtains the number of (10^-9) units of the amount for representing amounts in
     * minor currency units (for the currencies that support such amounts).
     *
     * <p>If the currency does not support minor currency units the value returned by
     * this method for instances with such a currency will always be zero.
     */
    public int nanos() {
        return value().getNanos();
    }
}
