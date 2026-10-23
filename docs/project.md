# Project: Spine Money

## Overview

Spine Money provides Protobuf-based types for monetary values and Java utilities
for working with them. The `Money` message holds an amount in whole units and
nano-units of a `Currency`, a message that wraps a currency code: an ISO 4217
code or a code an application defines (e.g. for a cryptocurrency). Information
about a currency (`CurrencyInfo`: name, symbol, numeric code, and the number of
fraction digits) is looked up at runtime, not stored with the value.
`CurrencyProvider` implementations found by `ServiceLoader` supply it, and the
built-in `JdkCurrencyProvider`, backed by `java.util.Currency`, covers ISO 4217.
`Currencies` creates `Currency` values and finds their information;
`CurrencyMixin` adds `info()` and `fractionDigits()` to `Currency`.
`MoneyAmount` is a validated wrapper around a `Money` value; its factories check
the value through the package-private `MoneyPreconditions`. The library is
experimental, so its API is likely to change. It is published as `io.spine:spine-money`.

`Money` keeps nine fraction digits, like `google.type.Money`. That covers fiat
currencies, Bitcoin (8), and stablecoins such as USDC (6), but not Ether or
other 18-decimal tokens. This is a deliberate decision for 2.0: if such amounts
are needed, a separate high-precision message is to be added instead of changing
`Money`, which keeps the change non-breaking.

## Architecture

Role in the org: a **library** and a leaf of the dependency graph — no other SDK
repository depends on it. It builds on `base-libraries`, `logging`, and the
`validation` runtime, uses the Spine Compiler and CoreJvm Compiler plugins to
generate code from its Protobuf types, and uses `testlib` in its tests.

- The root project `money` has a single module, `money`, which applies the
  shared `jvm-module` convention plugin. Its Kotlin and Java sources are in
  `io.spine.money`, and its Protobuf types in `spine/money/money.proto`. New code
  and tests are Kotlin; test `CurrencyProvider`s are registered with `@AutoService`
  through KSP.
- The root `build.gradle.kts` forces many versions on the buildscript classpath:
  the published compiler plugins were built against earlier generations of
  their dependencies, and `failOnVersionConflict()` rejects any disagreement.

Read [`.agents/guidelines/jvm-project.md`](../.agents/guidelines/jvm-project.md) for the
build stack, coding style, tests, and versioning.
