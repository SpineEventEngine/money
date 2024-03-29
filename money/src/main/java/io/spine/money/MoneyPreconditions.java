/*
 * Copyright 2020, TeamDev. All rights reserved.
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

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Utilities for checking validity of money values.
 */
final class MoneyPreconditions {

    static final int NANOS_MIN = -999_999_999;
    static final int NANOS_MAX = 999_999_999;

    /** Prevents instantiation of this utility class. */
    private MoneyPreconditions() {
    }

    static boolean isValid(int nanos) {
        return nanos >= NANOS_MIN && nanos <= NANOS_MAX;
    }

    static boolean isValid(long units, int nanos) {
        if (!isValid(nanos)) {
            return false;
        }
        if (units < 0 || nanos < 0) {
            return units <= 0 && nanos <= 0;
        }
        return true;
    }

    static boolean isValid(Currency currency) {
        return currency != Currency.CURRENCY_UNDEFINED &&
                currency != Currency.UNRECOGNIZED;
    }

    static boolean isValid(Currency currency, long units, int nanos) {
        var validCurrency = isValid(currency);
        var validNanos = isValid(nanos);
        var validUnits = isValid(units, nanos);
        return validCurrency && validNanos && validUnits;
    }

    static void checkValid(Currency currency, long units, int nanos) {
        checkArgument(isValid(currency), "A currency must be defined.");
        checkArgument(isValid(nanos),
                      "Nanos (%s) must be in range [-999,999,999, +999,999,999].",
                      nanos);
        checkArgument(isValid(units, nanos),
                      "`units` and `nanos` must be of the same sign.");
    }

    static void checkValid(Money amount) {
        checkValid(amount.getCurrency(), amount.getUnits(), amount.getNanos());
    }
}
