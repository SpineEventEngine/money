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

package io.spine.money.given

import com.google.auto.service.AutoService
import io.spine.money.CurrencyInfo
import io.spine.money.CurrencyProvider
import java.util.Optional

/**
 * A provider that fails to load because its constructor throws.
 *
 * The provider is registered for `ServiceLoader` along with [TestCurrencyProvider].
 * So, the tests which use currencies of [TestCurrencyProvider] also check that
 * a failing provider does not break the others.
 */
@AutoService(CurrencyProvider::class)
internal class FailingCurrencyProvider : CurrencyProvider {

    init {
        error("`FailingCurrencyProvider` always fails to load.")
    }

    override fun find(code: String): Optional<CurrencyInfo> = Optional.empty()
}
