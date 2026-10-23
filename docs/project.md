# Project: Spine Money

## Overview

Spine Money provides Protobuf-based types for monetary values and Java utilities
for working with them. The `Money` message holds an amount in whole units and
atto (10^-18) units of a `Currency`, a message that wraps a currency code: an ISO 4217
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

`Money` keeps 18 fraction digits in `sint64 attos`, where `google.type.Money`
keeps nine in `int32 nanos`. That covers fiat currencies, Bitcoin (8),
stablecoins such as USDC (6), and Ether and other 18-decimal tokens. Eighteen
digits is the most a signed 64-bit fraction can hold, so tokens with more
decimals would need a separate high-precision message.

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
