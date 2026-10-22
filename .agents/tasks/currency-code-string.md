---
slug: currency-code-string
branch: claude/currency-abstraction-v2-171729
owner: claude
status: in-review
started: 2026-10-22
---

## Goal

Replace the `Currency` enum with a `Currency` message that holds a currency code
(issue [#2](https://github.com/SpineEventEngine/money/issues/2)), so that `Money`
can carry any currency: current and future ISO 4217 codes, cryptocurrencies, and
application-defined units. Currency metadata is looked up at runtime instead of
being stored in the schema.

## Context

- The enum can't represent non-ISO currencies, and its ISO data is stale. It still
  lists HRK, VEF, MRO, STD, BYR, SLL, CUC, ANG, and ZWL, and lacks their
  replacements (VES/VED, MRU, STN, BYN, SLE, ZWG, XCG).
- Money libraries agree on the model: a currency is identified by a code string,
  and its metadata comes from a registry (JSR 354 `CurrencyProviderSpi`, Joda-Money
  `registerCurrency`, NodaMoney `CurrencyInfo.Register`).
- Breaking change made before `2.0.0`. The JavaMoney conversions of
  [#3](https://github.com/SpineEventEngine/money/issues/3) build on this and stay
  out of scope.

### Decisions

- Wire: `message Currency { string code = 1; }`, validated by `(pattern)`:
  2–12 uppercase ASCII letters or digits.
- `Money.currency` moves to field `4`; field `1` is reserved, so old data keeps the
  enum number as an unknown field instead of being misread.
- `CurrencyOptions` becomes `CurrencyInfo`, a standalone message. The
  `EnumValueOptions` extension `1025` is removed.
- Metadata comes from `CurrencyProvider` implementations found by `ServiceLoader`.
  The first provider that knows a code wins. A built-in provider backed by
  `java.util.Currency` is consulted last.
- Unknown codes are checked lazily. `MoneyAmount` checks the code's syntax only;
  `CurrencyMixin.info()` throws `UnknownCurrencyException` for an unknown code.
- `Currencies` holds `of(String)` and constants for the currencies Joda-Money has
  constants for: USD, EUR, JPY, GBP, CHF, AUD, CAD.

### Out of scope

- Precision beyond 9 fraction digits (needed for ETH and most ERC-20 tokens).
- Conversions to and from JavaMoney (#3).
- Telling the Protobuf team that extension `1025` is free; do it after merge.

## Plan

- [x] Replace the enum, `CurrencyOptions`, and the extension in `money.proto`.
- [x] Add `CurrencyMixin`, `CurrencyProvider`, `JdkCurrencyProvider`,
      `CurrencyProviders`, `Currencies`, and `UnknownCurrencyException`.
- [x] Switch `MoneyPreconditions` to check the currency code.
- [x] Update `MoneyAmountTest` and `MoneyPreconditionsTest`; add tests for
      `Currencies`, the mixin, and a custom provider loaded from test resources.
- [x] Update `docs/project.md` and `README.md`.
- [x] Bump the version to `2.0.0-SNAPSHOT.20` (breaking change).
- [x] `./gradlew clean build dokkaGenerate` passes.

## Log

- 2026-10-22 20:50 — approved by the user in chat; executing.
- 2026-10-22 21:00 — implementation done; `clean build dokkaGenerate` passed.
- 2026-10-22 21:10 — `CurrencyProviders.find` now rejects invalid codes, so
  providers only ever receive valid ones.
- 2026-10-22 21:15 — at the user's request, ran `./config/pull` (`config` at
  `58cd2d4a`); restored the five `dependency/local` pins it lowered, as in
  `11fcf91`. Re-running the full build.
- 2026-10-22 21:25 — `clean build dokkaGenerate` passed with the refreshed
  `config`: 52 tests, Kover, PMD, and Dokka green. Awaiting review; nothing
  committed.
- 2026-10-22 21:40 — opened PR #34. Applied the pre-PR review findings:
  `Currencies.of` now upper-cases ASCII letters only (`ß` and `ı` were turning
  into valid codes); the new tests are Kotlin `Spec` suites with Kotest
  assertions; `CurrencyProvider` is `@SPI`; Javadoc and wording fixes.
- 2026-10-22 22:30 — at the user's request: converted the new production code
  and the existing tests to Kotlin; added Spine Logging; a provider that fails
  to load is now skipped with a warning; test providers are registered with
  `@AutoService` through KSP; a test checks the code pattern against
  `money.proto`.
