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
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`MoneyAmount` should")
internal class MoneyAmountSpec {

    @Test
    fun `create positive amounts`() {
        val units = 639_100_000_000L
        val nanos = 949_000_000

        val amount = MoneyAmount.of(Currencies.USD, units, nanos)

        amount.currency() shouldBe Currencies.USD
        amount.units() shouldBe units
        amount.nanos() shouldBe nanos
    }

    @Test
    fun `create negative amounts`() {
        val units = -1L
        val nanos = -750_000_000

        val amount = MoneyAmount.of(Currencies.EUR, units, nanos)

        amount.currency() shouldBe Currencies.EUR
        amount.units() shouldBe units
        amount.nanos() shouldBe nanos
    }

    @Test
    fun `create an amount out of money`() {
        val money = money {
            currency = Currencies.of("UAH")
            units = 1L
        }

        val amount = MoneyAmount.of(money)

        amount.currency() shouldBe money.currency
        amount.units() shouldBe money.units
    }

    @Test
    fun `reject values of different signs`() {
        shouldThrow<IllegalArgumentException> {
            MoneyAmount.of(Currencies.EUR, -1, 999_999)
        }
        shouldThrow<IllegalArgumentException> {
            MoneyAmount.of(Currencies.EUR, 10, -999_999)
        }
    }

    @Test
    fun `reject a currency without a code`() {
        shouldThrow<IllegalArgumentException> {
            MoneyAmount.of(Currency.getDefaultInstance(), 1, 0)
        }
    }

    @Test
    fun `accept a currency not known to any provider`() {
        val currency = Currencies.of("NOPE")

        val amount = MoneyAmount.of(currency, 1, 0)

        amount.currency() shouldBe currency
    }
}
