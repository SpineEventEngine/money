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
import io.kotest.matchers.string.shouldContain
import io.spine.money.given.TestCurrencyProvider.Companion.BITCOIN
import io.spine.money.given.TestCurrencyProvider.Companion.BITCOIN_FRACTION_DIGITS
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`Currency` should")
internal class CurrencySpec {

    @Test
    fun `obtain information about itself`() {
        val info = Currencies.USD.info()

        info.currency shouldBe Currencies.USD
        info.name shouldBe "US Dollar"
        info.numericCode shouldBe 840
    }

    @Test
    fun `obtain the number of fraction digits`() {
        Currencies.USD.fractionDigits() shouldBe 2
        Currencies.JPY.fractionDigits() shouldBe 0
        Currencies.of("BHD").fractionDigits() shouldBe 3
        Currencies.of("XAU").fractionDigits() shouldBe -1
        Currencies.of(BITCOIN).fractionDigits() shouldBe BITCOIN_FRACTION_DIGITS
    }

    @Test
    fun `throw if no provider knows the currency`() {
        val currency = Currencies.of("NOPE")

        val exception = shouldThrow<UnknownCurrencyException> {
            currency.info()
        }
        exception.code() shouldBe "NOPE"
        exception.message shouldContain "`NOPE`"
    }
}
