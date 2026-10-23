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
import static io.spine.money.CurrencyCodes.checkValidCurrencyCode;
import static io.spine.money.CurrencyCodes.isValidCurrencyCode;

/**
 * Utilities for checking validity of money values.
 */
final class MoneyPreconditions {

    static final long ATTOS_MIN = -999_999_999_999_999_999L;
    static final long ATTOS_MAX = 999_999_999_999_999_999L;

    /** Prevents instantiation of this utility class. */
    private MoneyPreconditions() {
    }

    static boolean isValid(long attos) {
        return attos >= ATTOS_MIN && attos <= ATTOS_MAX;
    }

    static boolean isValid(long units, long attos) {
        if (!isValid(attos)) {
            return false;
        }
        if (units < 0 || attos < 0) {
            return units <= 0 && attos <= 0;
        }
        return true;
    }

    static boolean isValid(Currency currency) {
        return isValidCurrencyCode(currency.getCode());
    }

    static boolean isValid(Currency currency, long units, long attos) {
        var validCurrency = isValid(currency);
        var validAttos = isValid(attos);
        var validUnits = isValid(units, attos);
        return validCurrency && validAttos && validUnits;
    }

    static void checkValid(Currency currency, long units, long attos) {
        checkValidCurrencyCode(currency.getCode());
        checkArgument(isValid(attos),
                      "Attos (%s) must be in range" +
                              " [-999,999,999,999,999,999, +999,999,999,999,999,999].",
                      attos);
        checkArgument(isValid(units, attos),
                      "`units` and `attos` must be of the same sign.");
    }

    static void checkValid(Money amount) {
        checkValid(amount.getCurrency(), amount.getUnits(), amount.getAttos());
    }
}
