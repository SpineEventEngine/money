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

import com.google.common.base.Ascii
import java.util.Optional

/**
 * Utilities for working with [Currency] values.
 *
 * Provides constants for the G10 currencies, the most traded ones.
 * Other currencies are available via [of].
 */
public object Currencies {

    /** The US dollar. */
    @JvmField
    public val USD: Currency = of("USD")

    /** The euro. */
    @JvmField
    public val EUR: Currency = of("EUR")

    /** The Japanese yen. */
    @JvmField
    public val JPY: Currency = of("JPY")

    /** The pound sterling. */
    @JvmField
    public val GBP: Currency = of("GBP")

    /** The Swiss franc. */
    @JvmField
    public val CHF: Currency = of("CHF")

    /** The Australian dollar. */
    @JvmField
    public val AUD: Currency = of("AUD")

    /** The Canadian dollar. */
    @JvmField
    public val CAD: Currency = of("CAD")

    /** The New Zealand dollar. */
    @JvmField
    public val NZD: Currency = of("NZD")

    /** The Swedish krona. */
    @JvmField
    public val SEK: Currency = of("SEK")

    /** The Norwegian krone. */
    @JvmField
    public val NOK: Currency = of("NOK")

    /**
     * Creates a currency with the given code.
     *
     * ASCII letters of the code are converted to upper case, so `"usd"` and
     * `"USD"` produce the same currency. Other characters are left as they are.
     *
     * The method does not check that a [CurrencyProvider] knows the currency.
     * Use [find] for that.
     *
     * @param code
     *         the code of the currency, e.g. `"EUR"` or `"BTC"`
     * @throws IllegalArgumentException
     *         if the code in upper case does not consist of 2 to 12 ASCII letters or digits
     */
    @JvmStatic
    public fun of(code: String): Currency {
        val upperCase = Ascii.toUpperCase(code)
        checkValidCurrencyCode(upperCase)
        return currency {
            this.code = upperCase
        }
    }

    /**
     * Finds information about the given currency.
     *
     * @return information about the currency, or an empty `Optional` if
     *         no [CurrencyProvider] knows the currency
     * @throws IllegalArgumentException
     *         if the code of the currency is invalid, which may happen if the currency
     *         was not created by [of] or a validating builder
     * @see CurrencyMixin.info
     */
    @JvmStatic
    public fun find(currency: Currency): Optional<CurrencyInfo> =
        CurrencyProviders.find(currency.code)
}
