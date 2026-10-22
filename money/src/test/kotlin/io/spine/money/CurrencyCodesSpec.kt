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
import io.spine.option.OptionsProto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("`CurrencyCodes` should")
internal class CurrencyCodesSpec {

    @Test
    fun `use the pattern declared for the code field of 'Currency'`() {
        val code = Currency.getDescriptor().findFieldByName("code")
        val declared = code.options.getExtension(OptionsProto.pattern).regex

        CURRENCY_CODE_PATTERN.pattern shouldBe declared
    }

    @Test
    fun `reject an invalid code naming it in the message`() {
        val exception = shouldThrow<IllegalArgumentException> {
            checkValidCurrencyCode("usd")
        }
        exception.message shouldContain "`usd`"
    }
}
