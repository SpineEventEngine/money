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

import io.spine.annotation.SPI
import java.util.Optional

/**
 * Provides information about currencies.
 *
 * The library finds providers using [java.util.ServiceLoader]. To make a provider
 * available, list it in the `META-INF/services/io.spine.money.CurrencyProvider` file,
 * or annotate it with `@AutoService(CurrencyProvider.class)`.
 *
 * When looking up a currency, the library asks the providers in the order
 * the `ServiceLoader` returns them, and uses the first non-empty answer.
 * The built-in provider of ISO 4217 currencies is asked last. So, an application may both
 * add currencies and replace the information about ISO 4217 currencies.
 *
 * If a provider fails to load, the library logs a warning and skips the provider.
 */
@SPI
public fun interface CurrencyProvider {

    /**
     * Finds information about the currency with the given code.
     *
     * The returned information must be about the currency with the given code.
     *
     * @param code
     *         the code of the currency, which is a valid value of [Currency.getCode]
     * @return information about the currency, or an empty `Optional` if
     *         this provider does not know the currency
     */
    public fun find(code: String): Optional<CurrencyInfo>
}
