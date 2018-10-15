/*
 * Copyright 2018, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.money;

import io.spine.testing.UtilityClassTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.money.MoneyPreconditions.NANOS_MAX;
import static io.spine.money.MoneyPreconditions.NANOS_MIN;
import static io.spine.money.MoneyPreconditions.isValid;
import static io.spine.testing.TestValues.random;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MoneyPreconditions utility class should")
class MoneyPreconditionsTest extends UtilityClassTest<MoneyPreconditions> {

    MoneyPreconditionsTest() {
        super(MoneyPreconditions.class);
    }

    @Nested
    @DisplayName("check nano value")
    class NanoValue {

        @Test
        @DisplayName("is valid when equal to " + NANOS_MIN)
        void min() {
            assertValid(NANOS_MIN);
        }

        @Test
        @DisplayName("is valid when equal to " + NANOS_MAX)
        void max() {
            assertValid(NANOS_MAX);
        }

        @Test
        @DisplayName("is valid when in the range")
        void inside() {
            assertTrue(isValid(random(NANOS_MIN, NANOS_MAX)));
        }

        @Test
        @DisplayName("is invalid when outside the range")
        void isNanoValid() {
            assertInvalid(NANOS_MIN - 1);
            assertInvalid(NANOS_MAX + 1);
        }

        private void assertValid(int value) {
            assertTrue(isValid(value));
        }

        private void assertInvalid(int value) {
            assertFalse(isValid(value));
        }
    }

    @Nested
    @DisplayName("check currency")
    class CurrencyValue {

        @Test
        @DisplayName("is invalid when UNDEFINED or UNRECOGNIZED")
        void invalid() {
            assertInvalid(Currency.CURRENCY_UNDEFINED);
            assertInvalid(Currency.UNRECOGNIZED);
        }

        @Test
        @DisplayName("is valid")
        void valid() {
            assertValid(Currency.forNumber(random(Currency.AED_VALUE, Currency.ZWL_VALUE)));
        }

        private void assertValid(Currency value) {
            assertTrue(isValid(value));
        }

        private void assertInvalid(Currency value) {
            assertFalse(isValid(value));
        }
    }

    @Nested
    @DisplayName("check units and nanos combination")
    class UnitsAndNanos {

        @Nested
        @DisplayName("is valid if")
        class IsValidIf {

            @Test
            @DisplayName("both negative")
            void bothNegative() {
                assertValid(Long.MIN_VALUE, NANOS_MIN);
            }

            @Test
            @DisplayName("both positive")
            void bothPositive() {
                assertValid(Long.MAX_VALUE, NANOS_MAX);
            }

            @Test
            @DisplayName("one is zero")
            void oneIsZero() {
                assertValid(Long.MAX_VALUE, 0);
                assertValid(0, NANOS_MIN);
            }

            private void assertValid(long units, int nanos) {
                assertTrue(MoneyPreconditions.isValid(units, nanos));
            }
        }

        @Nested
        @DisplayName("is invalid if")
        class IsInvalidIf {
            @Test
            @DisplayName("have different sign")
            void haveDifferentSign() {
                assertInvalid(Long.MIN_VALUE, NANOS_MAX);
                assertInvalid(Long.MAX_VALUE, NANOS_MIN);
            }

            private void assertInvalid(long units, int nanos) {
                assertFalse(MoneyPreconditions.isValid(units, nanos));
            }
        }
    }
}
