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

@file:JvmName("CurrencyCodes")

package io.spine.money

/**
 * The pattern of a currency code.
 *
 * Must be the same as the `(pattern)` of the `Currency.code` field in `money.proto`.
 * The tests of this file check that.
 */
internal val CURRENCY_CODE_PATTERN: Regex = Regex("^[A-Z0-9]{2,12}$")

/**
 * Tells whether the given string is a valid currency code.
 */
internal fun isValidCurrencyCode(code: String): Boolean = CURRENCY_CODE_PATTERN.matches(code)

/**
 * Ensures that the given string is a valid currency code.
 *
 * @throws IllegalArgumentException
 *         if the string is not a valid currency code
 */
internal fun checkValidCurrencyCode(code: String) {
    require(isValidCurrencyCode(code)) {
        "A currency code must consist of 2 to 12 uppercase ASCII letters or digits." +
                " Encountered: `$code`."
    }
}
