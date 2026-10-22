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

import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.spine.money.given.TestCurrencyProvider.Companion.BITCOIN
import java.util.Optional
import java.util.ServiceConfigurationError
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("`CurrencyProviders` should")
internal class CurrencyProvidersSpec {

    @Nested inner class
    `load providers` {

        @Test
        fun `asking the built-in provider last`() {
            val discovered = stubProvider()

            val providers = CurrencyProviders.load(Discovered(listOf(discovered)))

            providers.dropLast(1) shouldBe listOf(discovered)
            providers.last().shouldBeInstanceOf<JdkCurrencyProvider>()
        }

        @Test
        fun `skipping a provider that fails to load`() {
            val first = stubProvider()
            val third = stubProvider()

            val providers = CurrencyProviders.load(Discovered(listOf(first, null, third)))

            providers.dropLast(1) shouldBe listOf(first, third)
            providers.last().shouldBeInstanceOf<JdkCurrencyProvider>()
        }

        @Test
        fun `keeping the found providers if looking for more fails`() {
            val found = stubProvider()

            val providers = CurrencyProviders.load(
                Discovered(listOf(found), failOnEnd = true)
            )

            providers.dropLast(1) shouldBe listOf(found)
            providers.last().shouldBeInstanceOf<JdkCurrencyProvider>()
        }
    }

    /**
     * Checks the providers of the test sources, which include
     * [io.spine.money.given.FailingCurrencyProvider].
     */
    @Test
    fun `keep working when a discovered provider fails to load`() {
        Currencies.find(Currencies.of(BITCOIN)).shouldBePresent()
        Currencies.find(Currencies.EUR).shouldBePresent()
    }
}

private fun stubProvider() = CurrencyProvider { Optional.empty() }

/**
 * Iterates over the given providers the way the iterator of `ServiceLoader` does.
 *
 * A `null` item stands for a provider that fails to load: [next] throws
 * [ServiceConfigurationError] instead of returning it.
 *
 * @param failOnEnd
 *         if `true`, [hasNext] throws [ServiceConfigurationError] after the last item,
 *         as when looking for more providers fails.
 */
private class Discovered(
    private val items: List<CurrencyProvider?>,
    private val failOnEnd: Boolean = false
) : Iterator<CurrencyProvider> {

    private var index = 0

    override fun hasNext(): Boolean {
        if (index < items.size) {
            return true
        }
        if (failOnEnd) {
            throw ServiceConfigurationError("Looking for providers failed.")
        }
        return false
    }

    override fun next(): CurrencyProvider {
        if (index == items.size) {
            throw NoSuchElementException()
        }
        return items[index++] ?: throw ServiceConfigurationError("The provider failed to load.")
    }
}
