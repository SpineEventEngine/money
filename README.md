# Spine Money: Protobuf-based money types

[![Ubuntu build][ubuntu-build-badge]][gh-actions]
[![codecov][codecov-badge]][codecov] &nbsp;
[![license][license-badge]][license]


This library provides Protobuf definition for `Currency` and `Money` types, and 
utilities for working with monetary. 

## Experimental

This library has experimental status, which means its API is likely to change in the near future.

## Supported Languages

Currently, the library supports only Java, with JavaScript and Dart being on the priority list.

## Adding to a Gradle Project

To add a dependency to a Gradle project, please use the following:

```kotlin
dependencies {
    implementation("io.spine:spine-money:$version") 
}
```

[gh-actions]: https://github.com/SpineEventEngine/money/actions
[ubuntu-build-badge]: https://github.com/SpineEventEngine/money/actions/workflows/build-on-ubuntu.yml/badge.svg
[codecov-badge]: https://codecov.io/gh/SpineEventEngine/money/branch/master/graph/badge.svg
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
[codecov]: https://codecov.io/gh/SpineEventEngine/money
