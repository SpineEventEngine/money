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

package io.spine.money.given

import com.google.auto.service.AutoService
import io.spine.money.Currencies
import io.spine.money.CurrencyInfo
import io.spine.money.CurrencyProvider
import io.spine.money.currencyInfo
import java.util.Optional

/**
 * Provides currencies for the tests of the library.
 */
@AutoService(CurrencyProvider::class)
internal class TestCurrencyProvider : CurrencyProvider {

    override fun find(code: String): Optional<CurrencyInfo> = when (code) {
        BITCOIN -> Optional.of(info(BITCOIN, "Bitcoin", BITCOIN_FRACTION_DIGITS))
        TESTING, MISMATCHED ->
            Optional.of(info(TESTING, "Testing currency", TESTING_FRACTION_DIGITS))
        else -> Optional.empty()
    }

    companion object {

        /**
         * The code of a currency not defined by ISO 4217.
         */
        const val BITCOIN = "BTC"

        /**
         * The number of fraction digits of the [BITCOIN] currency.
         */
        const val BITCOIN_FRACTION_DIGITS = 8

        /**
         * The ISO 4217 code reserved for testing.
         *
         * This provider replaces the information that the Java runtime has about this currency.
         */
        const val TESTING = "XTS"

        /**
         * The number of fraction digits this provider sets for the [TESTING] currency.
         *
         * The Java runtime treats this currency as having no minor units.
         */
        const val TESTING_FRACTION_DIGITS = 4

        /**
         * The code for which this provider wrongly returns information about
         * the [TESTING] currency.
         */
        const val MISMATCHED = "MISMATCH"
    }
}

private fun info(code: String, name: String, fractionDigits: Int): CurrencyInfo =
    currencyInfo {
        currency = Currencies.of(code)
        this.name = name
        this.fractionDigits = fractionDigits
    }
