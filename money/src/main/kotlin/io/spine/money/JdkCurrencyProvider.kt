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

import java.util.Locale
import java.util.Optional
import java.util.Currency as JdkCurrency

/**
 * Provides information about the ISO 4217 currencies known to the Java runtime.
 *
 * The information comes from [java.util.Currency], which the JDK keeps up to date
 * with amendments to ISO 4217. Besides current currencies, the JDK knows some
 * historical ones (e.g., `DEM`).
 */
internal class JdkCurrencyProvider : CurrencyProvider {

    override fun find(code: String): Optional<CurrencyInfo> {
        val jdkCurrency = jdkCurrencies[code] ?: return Optional.empty()
        val info = currencyInfo {
            currency = Currencies.of(code)
            name = jdkCurrency.getDisplayName(Locale.ENGLISH)
            symbol = jdkCurrency.getSymbol(Locale.ENGLISH)
            numericCode = jdkCurrency.numericCode
            fractionDigits = jdkCurrency.defaultFractionDigits
        }
        return Optional.of(info)
    }
}

/**
 * The currencies known to the Java runtime by their codes.
 */
private val jdkCurrencies: Map<String, JdkCurrency> =
    JdkCurrency.getAvailableCurrencies().associateBy { it.currencyCode }
