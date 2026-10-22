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

import java.io.Serial;

import static java.lang.String.format;

/**
 * Thrown when no {@link CurrencyProvider} knows a currency.
 */
public final class UnknownCurrencyException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 0L;

    private final String code;

    /**
     * Creates a new exception for the currency with the given code.
     */
    UnknownCurrencyException(String code) {
        super(format("No `CurrencyProvider` knows the currency `%s`.", code));
        this.code = code;
    }

    /**
     * Obtains the code of the unknown currency.
     */
    public String code() {
        return code;
    }
}
