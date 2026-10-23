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

import com.google.common.annotations.VisibleForTesting
import io.spine.logging.WithLogging
import java.util.Optional
import java.util.ServiceConfigurationError
import java.util.ServiceLoader

/**
 * Finds information about currencies using the available [CurrencyProvider]s.
 */
internal object CurrencyProviders : WithLogging {

    /**
     * The providers in the order of asking them, loaded when needed for the first time.
     */
    private val providers: List<CurrencyProvider> by lazy {
        load(ServiceLoader.load(CurrencyProvider::class.java).iterator())
    }

    /**
     * Finds information about the currency with the given code.
     *
     * Asks the providers found by the [ServiceLoader] in the order it returns them,
     * and then the built-in provider of ISO 4217 currencies.
     *
     * @throws IllegalArgumentException
     *         if the code is not a valid currency code
     * @throws IllegalStateException
     *         if a provider returns information about a currency with another code
     */
    fun find(code: String): Optional<CurrencyInfo> {
        checkValidCurrencyCode(code)
        for (provider in providers) {
            val info = provider.find(code)
            if (info.isPresent) {
                val foundCode = info.get().currency.code
                check(code == foundCode) {
                    "The provider `${provider.javaClass.name}` returned the currency" +
                            " `$foundCode` for the code `$code`."
                }
                return info
            }
        }
        return Optional.empty()
    }

    /**
     * Creates the list of providers in the order of asking them.
     *
     * The list starts with the discovered providers, and ends with the built-in
     * provider of ISO 4217 currencies.
     *
     * A discovered provider that fails to load is skipped with a warning, so that
     * the other providers keep working.
     *
     * @param discovered
     *         the providers found by the [ServiceLoader]
     */
    @VisibleForTesting
    fun load(discovered: Iterator<CurrencyProvider>): List<CurrencyProvider> = buildList {
        // The iterator of `ServiceLoader` moves past a provider that fails to load,
        // so the loop proceeds after an error.
        while (hasNext(discovered)) {
            try {
                add(discovered.next())
            } catch (e: ServiceConfigurationError) {
                logger.atWarning()
                    .withCause(e)
                    .log { "Skipping a `CurrencyProvider` that failed to load." }
            }
        }
        add(JdkCurrencyProvider())
    }

    /**
     * Tells whether there are more discovered providers.
     *
     * Returns `false` if looking for providers fails, so that the providers
     * found before the failure keep working.
     */
    private fun hasNext(discovered: Iterator<CurrencyProvider>): Boolean =
        try {
            discovered.hasNext()
        } catch (e: ServiceConfigurationError) {
            logger.atWarning()
                .withCause(e)
                .log { "Stopped looking for `CurrencyProvider`s after an error." }
            false
        }
}
