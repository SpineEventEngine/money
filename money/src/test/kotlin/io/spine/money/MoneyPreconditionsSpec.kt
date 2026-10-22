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

import io.kotest.matchers.shouldBe
import io.spine.money.MoneyPreconditions.NANOS_MAX
import io.spine.money.MoneyPreconditions.NANOS_MIN
import io.spine.money.MoneyPreconditions.isValid
import io.spine.testing.TestValues.random
import io.spine.testing.UtilityClassTest
import io.spine.validation.NonValidated
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`MoneyPreconditions` should")
internal class MoneyPreconditionsSpec :
    UtilityClassTest<MoneyPreconditions>(MoneyPreconditions::class.java) {

    @Nested inner class
    `check nanos` {

        @Test
        fun `accepting the minimum`() {
            isValid(NANOS_MIN) shouldBe true
        }

        @Test
        fun `accepting the maximum`() {
            isValid(NANOS_MAX) shouldBe true
        }

        @Test
        fun `accepting a value in the range`() {
            isValid(random(NANOS_MIN, NANOS_MAX)) shouldBe true
        }

        @Test
        fun `rejecting values outside the range`() {
            isValid(NANOS_MIN - 1) shouldBe false
            isValid(NANOS_MAX + 1) shouldBe false
        }
    }

    @Nested inner class
    `check a currency` {

        @Test
        fun `rejecting a currency without a code`() {
            isValid(Currency.getDefaultInstance()) shouldBe false
        }

        @Test
        fun `rejecting a code not in upper case`() {
            isValid(currency("usd")) shouldBe false
            isValid(currency("Usd")) shouldBe false
        }

        @Test
        fun `rejecting a code that is too short or too long`() {
            isValid(currency("U")) shouldBe false
            isValid(currency("ABCDEFGHIJKLM")) shouldBe false
        }

        @Test
        fun `rejecting a code with symbols other than ASCII letters or digits`() {
            isValid(currency("US D")) shouldBe false
            isValid(currency("US-D")) shouldBe false
            isValid(currency("ÉUR")) shouldBe false
        }

        @Test
        fun `accepting ISO 4217 codes`() {
            isValid(currency("USD")) shouldBe true
            isValid(currency("XAU")) shouldBe true
        }

        @Test
        fun `accepting codes not defined by ISO 4217`() {
            isValid(currency("OP")) shouldBe true
            isValid(currency("BTC")) shouldBe true
            isValid(currency("1INCH")) shouldBe true
            isValid(currency("ABCDEFGHIJKL")) shouldBe true
        }
    }

    @Nested inner class
    `check units and nanos` {

        @Test
        fun `accepting both negative`() {
            isValid(Long.MIN_VALUE, NANOS_MIN) shouldBe true
        }

        @Test
        fun `accepting both positive`() {
            isValid(Long.MAX_VALUE, NANOS_MAX) shouldBe true
        }

        @Test
        fun `accepting one of them being zero`() {
            isValid(Long.MAX_VALUE, 0) shouldBe true
            isValid(0, NANOS_MIN) shouldBe true
        }

        @Test
        fun `rejecting different signs`() {
            isValid(Long.MIN_VALUE, NANOS_MAX) shouldBe false
            isValid(Long.MAX_VALUE, NANOS_MIN) shouldBe false
        }

        @Test
        fun `rejecting nanos outside the range`() {
            isValid(Long.MIN_VALUE, NANOS_MIN - 1) shouldBe false
            isValid(Long.MIN_VALUE, NANOS_MAX + 1) shouldBe false
        }
    }

    @Nested inner class
    `check currency, units, and nanos together` {

        private val validCurrency = currency("AED")
        private val invalidCurrencies = listOf(Currency.getDefaultInstance(), currency("usd"))

        @Test
        fun `accepting a valid currency with positive units and nanos`() {
            isValid(validCurrency, Long.MAX_VALUE, NANOS_MAX) shouldBe true
        }

        @Test
        fun `accepting a valid currency with negative units and nanos`() {
            isValid(validCurrency, Long.MIN_VALUE, NANOS_MIN) shouldBe true
        }

        @Test
        fun `rejecting an invalid currency with valid units and nanos`() {
            invalidCurrencies.forEach {
                isValid(it, Long.MAX_VALUE, NANOS_MAX) shouldBe false
                isValid(it, Long.MIN_VALUE, NANOS_MIN) shouldBe false
            }
        }

        @Test
        fun `rejecting a valid currency with units and nanos of different signs`() {
            isValid(validCurrency, Long.MIN_VALUE, NANOS_MAX) shouldBe false
            isValid(validCurrency, Long.MAX_VALUE, NANOS_MIN) shouldBe false
        }

        @Test
        fun `rejecting a valid currency with nanos outside the range`() {
            isValid(validCurrency, Long.MIN_VALUE, NANOS_MIN - 1) shouldBe false
            isValid(validCurrency, Long.MAX_VALUE, NANOS_MAX + 1) shouldBe false
        }

        @Test
        fun `rejecting an invalid currency with nanos outside the range`() {
            invalidCurrencies.forEach {
                isValid(it, Long.MIN_VALUE, NANOS_MIN - 1) shouldBe false
                isValid(it, Long.MAX_VALUE, NANOS_MAX + 1) shouldBe false
            }
        }

        @Test
        fun `rejecting an invalid currency with units and nanos of different signs`() {
            invalidCurrencies.forEach {
                isValid(it, Long.MIN_VALUE, NANOS_MAX) shouldBe false
                isValid(it, Long.MAX_VALUE, NANOS_MIN) shouldBe false
            }
        }
    }
}

/**
 * Creates a currency without validating it, so that the code may be invalid.
 */
private fun currency(code: String): @NonValidated Currency =
    Currency.newBuilder()
        .setCode(code)
        .buildPartial()
