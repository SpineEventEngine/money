/*
 * Copyright 2025, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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

@file:Suppress("RemoveRedundantQualifierName")

import io.spine.dependency.build.Dokka
import io.spine.dependency.kotlinx.Coroutines
import io.spine.dependency.lib.Jackson
import io.spine.dependency.lib.KotlinPoet
import io.spine.dependency.local.Base
import io.spine.dependency.local.Logging
import io.spine.dependency.local.Compiler
import io.spine.dependency.local.ToolBase
import io.spine.dependency.local.Validation
import io.spine.dependency.test.JUnit
import io.spine.gradle.publish.PublishingRepos
import io.spine.gradle.publish.spinePublishing
import io.spine.gradle.report.coverage.KoverConfig
import io.spine.gradle.report.license.LicenseReporter
import io.spine.gradle.report.pom.PomGenerator
import io.spine.gradle.repo.standardToSpineSdk

buildscript {
    standardSpineSdkRepositories()
    doForceVersions(configurations)

    dependencies {
        // The transitive Spine artifacts declare BOM-managed gRPC members
        // without versions; the platform must be on the classpath for them
        // to resolve.
        classpath(enforcedPlatform(io.spine.dependency.kotlinx.Coroutines.bom))
        classpath(enforcedPlatform(io.spine.dependency.lib.Grpc.bom))
        classpath(io.spine.dependency.local.Compiler.pluginLib)
        classpath(io.spine.dependency.local.CoreJvmCompiler.gradlePlugin)
    }

    configurations {
        all {
            resolutionStrategy {
                val coroutines = io.spine.dependency.lib.Coroutines
                val validation = io.spine.dependency.local.Validation
                val jackson = io.spine.dependency.lib.Jackson
                // The 2.x submodules need their own alignment: the BOM alone
                // does not settle versions the refresh-era plugins request.
                io.spine.dependency.lib.JacksonV2.Core.forceArtifacts(project, this@all, this@resolutionStrategy)
                io.spine.dependency.lib.JacksonV2.DataType.forceArtifacts(project, this@all, this@resolutionStrategy)
                io.spine.dependency.lib.JacksonV2.DataFormat.forceArtifacts(project, this@all, this@resolutionStrategy)
                io.spine.dependency.lib.JacksonV2.Module.forceArtifacts(project, this@all, this@resolutionStrategy)
                // Jackson 2.x artifacts that only the IntelliJ Platform brings.
                io.spine.dependency.lib.JacksonV2.Junior.forceArtifacts(project, this@all, this@resolutionStrategy)
                force(
                    // Policy: force the Kotlin runtime at the toolchain
                    // version over the Gradle-embedded one — refresh-era
                    // plugin jars require it.
                    io.spine.dependency.lib.Kotlin.bom,
                    // gRPC members are BOM-managed too.
                    io.spine.dependency.lib.Grpc.bom,
                    // `aedile-core` requests the 3.0.4 line.
                    io.spine.dependency.lib.Caffeine.lib,
                    "org.jetbrains.kotlin:kotlin-stdlib:${io.spine.dependency.lib.Kotlin.runtimeVersion}",
                    "org.jetbrains.kotlin:kotlin-reflect:${io.spine.dependency.lib.Kotlin.runtimeVersion}",
                    // Only the BOM carries a version; the members are
                    // BOM-managed and cannot be forced by coordinate.
                    coroutines.bom,

                    io.spine.dependency.local.Base.lib,
                    io.spine.dependency.local.ToolBase.lib,
                    io.spine.dependency.local.ToolBase.pluginBase,
                    io.spine.dependency.local.Logging.lib,

                    validation.runtime,
                    jackson.annotations,
                    // The other Jackson 3 members are BOM-managed and carry
                    // no version of their own, so the BOM is what is forced.
                    jackson.bom,
                    // The refresh-era plugins bring the 2.x line as well, and
                    // the wave's fresh Time meets the floor requested by
                    // still-published artifacts.
                    io.spine.dependency.lib.JacksonV2.bom,
                    io.spine.dependency.local.Time.lib,
                    io.spine.dependency.local.Time.javaExtensions,
                    io.spine.dependency.local.Base.environment,
                )
            }
        }
    }
}

repositories {
    // Required to grab the dependencies for `KoverConfig`.
    standardToSpineSdk()
}

plugins {
    // Gives the aggregator root the lifecycle tasks (`build`, `check`, ...)
    // that the shared reporting helpers expect to find.
    base
    idea
    `gradle-doctor`
    `project-report`
}

spinePublishing {
    modules = setOf(
        "money",
        "money-js"
    )
    destinations = with(PublishingRepos) {
        setOf(
            gitHub("money"),
            cloudArtifactRegistry
        )
    }
}

allprojects {
    apply(from = "$rootDir/version.gradle.kts")

    group = "io.spine"
    version = extra["versionToPublish"]!!

    repositories.standardToSpineSdk()
    configurations {
        forceVersions()
        all {
            exclude("io.spine:spine-validate")
            resolutionStrategy {
                // The refresh-era plugins and their transitive dependencies
                // bring many Jackson 2.x artifacts at more than one patch
                // level; align the whole family in one rule rather than
                // enumerating coordinates.
                eachDependency {
                    // `jackson-annotations` keeps its own version line, so it
                    // is left to the value the dependency object declares.
                    if (requested.group.startsWith("com.fasterxml.jackson")
                        && requested.name != "jackson-annotations") {
                        useVersion(io.spine.dependency.lib.JacksonV2.version)
                    }
                    // The plugin-managed `spineCompiler` classpath does not
                    // honour `force`, so the Base family is aligned by rule
                    // as well: the wave's fresh Base meets the floor that
                    // still-published artifacts request.
                    if (requested.group == "io.spine"
                        && requested.name in setOf(
                            "spine-base", "spine-annotations",
                            "spine-environment", "spine-format"
                        )) {
                        useVersion(io.spine.dependency.local.Base.version)
                    }
                }
                force(
                    KotlinPoet.lib,

                    Coroutines.bom,

                    // Floor artifacts request the pre-refresh versions;
                    // the Protobuf runtime must never be older than the
                    // refreshed gencode.
                    io.spine.dependency.kotlinx.Coroutines.bom,
                    io.spine.dependency.kotlinx.AtomicFu.lib,
                    io.spine.dependency.lib.Protobuf.javaLib,
                    io.spine.dependency.lib.Caffeine.lib,
                    Base.lib,
                    Logging.lib,
                    Logging.middleware,
                    Compiler.api,
                    // The wave's fresh Time meets the floor requested by
                    // still-published artifacts.
                    io.spine.dependency.local.Time.lib,
                    io.spine.dependency.local.Time.javaExtensions,
                    Validation.runtime,
                    Dokka.BasePlugin.lib,
                    // The Jackson 3 object's members are BOM-managed and
                    // carry no version, so the BOM is what can be forced.
                    Jackson.bom,
                    JUnit.bom,
                )
            }
        }
    }
}

// Kover must be applied while the project is still configurable, so it is
// invoked here rather than from `gradle.projectsEvaluated`.
KoverConfig.applyTo(project)

gradle.projectsEvaluated {
    LicenseReporter.mergeAllReports(project)
    PomGenerator.applyTo(project)
}
