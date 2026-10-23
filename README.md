# Spine Money: Protobuf-based money types

[![Ubuntu build][ubuntu-build-badge]][gh-actions]
[![codecov][codecov-badge]][codecov] &nbsp;
[![license][license-badge]][license]


This library provides Protobuf definition for `Currency` and `Money` types, and 
utilities for working with monetary. 

## Experimental

This library has experimental status, which means its API is likely to change in the near future.

## Supported Languages

The library supports Java.

## Adding to a Gradle Project

To add a dependency to a Gradle project, please use the following:

```kotlin
dependencies {
    implementation("io.spine:spine-money:$version") 
}
```

## Currencies

A `Currency` is identified by its code. It may be an ISO 4217 code, such as `EUR`,
or a code your application defines, such as `BTC`:

```java
var euro = Currencies.EUR;
var bitcoin = Currencies.of("BTC");
var price = MoneyAmount.of(euro, 19, 990_000_000);
```

The library knows the ISO 4217 currencies supported by the Java runtime.
For example, `Currencies.JPY.fractionDigits()` returns `0`.

`Money` keeps up to nine digits after the decimal point. That is enough for
fiat currencies, Bitcoin, and stablecoins such as USDC, but not for Ether
or other tokens with 18 decimals.

To describe other currencies, or to change the information about ISO 4217 ones,
implement `CurrencyProvider` and register it for `java.util.ServiceLoader`:

```java
@AutoService(CurrencyProvider.class)
public final class CryptoCurrencies implements CurrencyProvider {

    @Override
    public Optional<CurrencyInfo> find(String code) {
        if (!code.equals("BTC")) {
            return Optional.empty();
        }
        var info = CurrencyInfo.newBuilder()
                .setCurrency(Currencies.of("BTC"))
                .setName("Bitcoin")
                .setSymbol("₿")
                .setFractionDigits(8)
                .build();
        return Optional.of(info);
    }
}
```

[gh-actions]: https://github.com/SpineEventEngine/money/actions
[ubuntu-build-badge]: https://github.com/SpineEventEngine/money/actions/workflows/build-on-ubuntu.yml/badge.svg
[codecov-badge]: https://codecov.io/gh/SpineEventEngine/money/branch/master/graph/badge.svg
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
[codecov]: https://codecov.io/gh/SpineEventEngine/money
