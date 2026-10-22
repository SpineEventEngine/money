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

package io.spine.money;

import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.ServiceLoader;

import static com.google.common.base.Preconditions.checkState;
import static io.spine.money.MoneyPreconditions.checkValidCode;

/**
 * Finds information about currencies using the available {@link CurrencyProvider}s.
 */
final class CurrencyProviders {

    /** Prevents instantiation of this utility class. */
    private CurrencyProviders() {
    }

    /**
     * Finds information about the currency with the given code.
     *
     * <p>Asks the providers found by the {@link ServiceLoader} in the order it returns them,
     * and then the built-in provider of ISO 4217 currencies.
     *
     * @throws IllegalArgumentException
     *         if the code is not a valid currency code
     * @throws IllegalStateException
     *         if a provider returns information about a currency with another code
     */
    static Optional<CurrencyInfo> find(String code) {
        checkValidCode(code);
        for (var provider : Loaded.PROVIDERS) {
            var info = provider.find(code);
            if (info.isPresent()) {
                var foundCode = info.get().getCurrency().getCode();
                checkState(code.equals(foundCode),
                           "The provider `%s` returned the currency `%s` for the code `%s`.",
                           provider.getClass().getName(), foundCode, code);
                return info;
            }
        }
        return Optional.empty();
    }

    /**
     * Loads the providers when they are needed for the first time.
     */
    private static final class Loaded {

        private static final ImmutableList<CurrencyProvider> PROVIDERS = load();

        private static ImmutableList<CurrencyProvider> load() {
            var result = ImmutableList.<CurrencyProvider>builder();
            ServiceLoader.load(CurrencyProvider.class)
                         .forEach(result::add);
            result.add(new JdkCurrencyProvider());
            return result.build();
        }
    }
}
