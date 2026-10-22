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

import java.io.Serial

/**
 * Thrown when no [CurrencyProvider] knows a currency.
 *
 * @property code
 *         the code of the unknown currency
 */
public class UnknownCurrencyException internal constructor(
    public val code: String
) : RuntimeException("No `CurrencyProvider` knows the currency `$code`.") {

    private companion object {

        @Serial
        private const val serialVersionUID: Long = 0L
    }
}
