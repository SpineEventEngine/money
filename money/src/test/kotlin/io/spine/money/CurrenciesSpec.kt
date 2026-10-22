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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.spine.money.given.TestCurrencyProvider.Companion.BITCOIN
import io.spine.money.given.TestCurrencyProvider.Companion.BITCOIN_FRACTION_DIGITS
import io.spine.money.given.TestCurrencyProvider.Companion.MISMATCHED
import io.spine.money.given.TestCurrencyProvider.Companion.TESTING
import io.spine.money.given.TestCurrencyProvider.Companion.TESTING_FRACTION_DIGITS
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`Currencies` should")
internal class CurrenciesSpec {

    @Nested inner class
    `create a currency` {

        @Test
        fun `with the given code`() {
            Currencies.of("UAH").code shouldBe "UAH"
        }

        @Test
        fun `converting ASCII letters of the code to upper case`() {
            Currencies.of("usd") shouldBe Currencies.USD
            Currencies.of("Usd") shouldBe Currencies.USD
        }

        @Test
        fun `with a code not defined by ISO 4217`() {
            Currencies.of("1inch").code shouldBe "1INCH"
        }

        @Test
        fun `rejecting invalid codes`() {
            listOf("", "U", "ABCDEFGHIJKLM", "US D", "US-D", "ÉUR").forEach(::assertRejected)
        }

        @Test
        fun `rejecting non-ASCII letters that turn into ASCII ones in upper case`() {
            // In upper case, `ß` turns into `SS`.
            assertRejected("ß")
            // In upper case, the dotless `ı` turns into `I`, which would make `ILS`.
            assertRejected("ıls")
        }
    }

    @Test
    fun `provide constants for widely used currencies`() {
        Currencies.USD.code shouldBe "USD"
        Currencies.EUR.code shouldBe "EUR"
        Currencies.JPY.code shouldBe "JPY"
        Currencies.GBP.code shouldBe "GBP"
        Currencies.CHF.code shouldBe "CHF"
        Currencies.AUD.code shouldBe "AUD"
        Currencies.CAD.code shouldBe "CAD"
    }

    @Nested inner class
    `find information` {

        @Test
        fun `about ISO 4217 currencies`() {
            val info = Currencies.find(Currencies.EUR).orElseThrow()

            info.currency shouldBe Currencies.EUR
            info.name shouldBe "Euro"
            info.numericCode shouldBe 978
            info.fractionDigits shouldBe 2
        }

        @Test
        fun `about currencies of application providers`() {
            val info = Currencies.find(Currencies.of(BITCOIN)).orElseThrow()

            info.name shouldBe "Bitcoin"
            info.fractionDigits shouldBe BITCOIN_FRACTION_DIGITS
        }

        @Test
        fun `from application providers before the built-in one`() {
            val info = Currencies.find(Currencies.of(TESTING)).orElseThrow()

            info.fractionDigits shouldBe TESTING_FRACTION_DIGITS
        }

        @Test
        fun `returning nothing for unknown currencies`() {
            Currencies.find(Currencies.of("NOPE")).shouldBeEmpty()
        }

        @Test
        fun `rejecting currencies with invalid codes`() {
            val currency = Currency.newBuilder()
                .setCode("usd")
                .buildPartial()

            shouldThrow<IllegalArgumentException> {
                Currencies.find(currency)
            }
        }

        @Test
        fun `rejecting information about another currency`() {
            val currency = Currencies.of(MISMATCHED)

            shouldThrow<IllegalStateException> {
                Currencies.find(currency)
            }
        }
    }
}

private fun assertRejected(code: String) {
    shouldThrow<IllegalArgumentException> {
        Currencies.of(code)
    }
}
