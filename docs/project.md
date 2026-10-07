# Project: Spine Money

## Overview

Spine Money provides Protobuf-based types for monetary values and Java utilities
for working with them. The `Money` message holds an amount in whole units and
nano-units of a `Currency`, an enum of ISO 4217 currencies whose constants carry
`CurrencyOptions` (name, symbol, numeric code, and `exponent_digits`, the number
of minor-unit digits). `MoneyAmount` is a validated wrapper around a `Money`
value; its factories check the value through the package-private
`MoneyPreconditions`. The library is experimental, so its API is likely to
change. It is published as `io.spine:spine-money`.

## Architecture

Role in the org: a **library** and a leaf of the dependency graph — no other SDK
repository depends on it. It builds on `base-libraries` and the `validation`
runtime, uses the Spine Compiler and CoreJvm Compiler plugins to generate code
from its Protobuf types, and uses `testlib` in its tests.

- The root project `spine-money` has a single module, `money`, which applies the
  shared `jvm-module` convention plugin. Its Java sources are in `io.spine.money`,
  and its Protobuf types in `spine/money/money.proto`.
- The root `build.gradle.kts` forces many versions on the buildscript classpath:
  the published compiler plugins were built against earlier generations of
  their dependencies, and `failOnVersionConflict()` rejects any disagreement.

Read [`.agents/guidelines/jvm-project.md`](../.agents/guidelines/jvm-project.md) for the
build stack, coding style, tests, and versioning.
