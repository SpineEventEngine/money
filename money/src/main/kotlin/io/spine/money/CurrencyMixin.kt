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

package io.spine.money

import io.spine.annotation.GeneratedMixin

/**
 * The mixin for the [Currency] message.
 */
@GeneratedMixin
public interface CurrencyMixin : CurrencyOrBuilder {

    /**
     * Obtains information about this currency.
     *
     * @throws UnknownCurrencyException
     *         if no [CurrencyProvider] knows this currency
     * @throws IllegalArgumentException
     *         if the code of this currency is invalid
     * @see Currencies.find
     */
    public fun info(): CurrencyInfo =
        CurrencyProviders.find(code)
            .orElseThrow { UnknownCurrencyException(code) }

    /**
     * Obtains the number of digits after the decimal separator for this currency.
     *
     * The value is less than zero if there are no minor units for this currency
     * (e.g., for gold).
     *
     * @throws UnknownCurrencyException
     *         if no [CurrencyProvider] knows this currency
     * @throws IllegalArgumentException
     *         if the code of this currency is invalid
     */
    public fun fractionDigits(): Int = info().fractionDigits
}
