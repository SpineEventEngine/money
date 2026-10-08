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
