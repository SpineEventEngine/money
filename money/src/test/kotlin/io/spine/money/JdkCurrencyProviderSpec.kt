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

import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.spine.money.given.TestCurrencyProvider.Companion.BITCOIN
import io.spine.money.given.TestCurrencyProvider.Companion.TESTING
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`JdkCurrencyProvider` should")
internal class JdkCurrencyProviderSpec {

    private val provider = JdkCurrencyProvider()

    @Test
    fun `provide information about ISO 4217 currencies`() {
        val info = provider.find("JPY").orElseThrow()

        info.currency shouldBe Currencies.JPY
        info.name shouldBe "Japanese Yen"
        info.symbol shouldBe "¥"
        info.numericCode shouldBe 392
        info.fractionDigits shouldBe 0
    }

    @Test
    fun `tell that a currency has no minor units`() {
        provider.find("XAU").orElseThrow().fractionDigits shouldBe -1
        provider.find(TESTING).orElseThrow().fractionDigits shouldBe -1
    }

    @Test
    fun `not know currencies not defined by ISO 4217`() {
        provider.find(BITCOIN).shouldBeEmpty()
    }
}
