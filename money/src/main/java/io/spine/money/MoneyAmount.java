/*
 * Copyright 2018, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.money;

import io.spine.value.ValueHolder;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.money.MoneyPreconditions.checkValid;

/**
 * The utility class containing convenience methods for working with
 * {@link io.spine.money.Money Money}.
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
        return create(value);
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
        Money amount = Money
                .newBuilder()
                .setCurrency(currency)
                .setUnits(units)
                .setNanos(nanos)
                .build();
        return create(amount);
    }

    private static MoneyAmount create(Money amount) {
        return new MoneyAmount(amount);
    }

    /**
     * Obtains the currency of the amount of money.
     */
    public Currency getCurrency() {
        return value().getCurrency();
    }

    /**
     * Obtains units value of the amount.
     */
    public long getUnits() {
        return value().getUnits();
    }

    /**
     * Obtains the number of (10^-9) units of the amount for representing amounts in
     * minor currency units (for the currencies that support such amounts).
     *
     * <p>If the currency does not support minor currency units the value returned by
     * this method for instances with such a currency will always be zero.
     */
    public int getNanos() {
        return value().getNanos();
    }
}
