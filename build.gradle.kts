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
import io.spine.dependency.lib.Coroutines
import io.spine.dependency.lib.Jackson
import io.spine.dependency.lib.KotlinPoet
import io.spine.dependency.local.Base
import io.spine.dependency.local.Logging
import io.spine.dependency.local.ProtoData
import io.spine.dependency.local.ToolBase
import io.spine.dependency.local.Validation
import io.spine.dependency.test.JUnit
import io.spine.gradle.publish.PublishingRepos
import io.spine.gradle.publish.spinePublishing
import io.spine.gradle.report.coverage.JacocoConfig
import io.spine.gradle.report.license.LicenseReporter
import io.spine.gradle.report.pom.PomGenerator
import io.spine.gradle.standardToSpineSdk

buildscript {
    standardSpineSdkRepositories()
    doForceVersions(configurations)

    dependencies {
        classpath(io.spine.dependency.local.McJava.pluginLib)
        // TODO: Define McJs dependency object.
        classpath("io.spine.tools:spine-mc-js:2.0.0-SNAPSHOT.130")
    }

    configurations {
        all {
            resolutionStrategy {
                val coroutines = io.spine.dependency.lib.Coroutines
                val validation = io.spine.dependency.local.Validation
                val jackson = io.spine.dependency.lib.Jackson
                force(
                    coroutines.bom,
                    coroutines.core,
                    coroutines.coreJvm,
                    coroutines.jdk8,

                    io.spine.dependency.local.Base.lib,
                    io.spine.dependency.local.ToolBase.lib,
                    io.spine.dependency.local.ToolBase.pluginBase,
                    io.spine.dependency.local.Logging.lib,

                    validation.runtime,
                    jackson.annotations,
                    jackson.bom,
                    jackson.databind,
                    jackson.moduleKotlin
                )
            }
        }
    }
}

repositories {
    // Required to grab the dependencies for `JacocoConfig`.
    standardToSpineSdk()
}

plugins {
    idea
    jacoco
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

    dokkaJar {
        kotlin = true
        java = true
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
                force(
                    KotlinPoet.lib,

                    Coroutines.bom,
                    Coroutines.core,
                    Coroutines.coreJvm,
                    Coroutines.debug,
                    Coroutines.test,
                    Coroutines.testJvm,
                    Coroutines.jdk8,

                    Base.lib,
                    ToolBase.lib,
                    Logging.lib,
                    Logging.middleware,
                    ProtoData.api,
                    Validation.runtime,
                    Dokka.BasePlugin.lib,
                    Jackson.databind,
                    JUnit.runner,
                )
            }
        }
    }
}

gradle.projectsEvaluated {
    JacocoConfig.applyTo(project)
    LicenseReporter.mergeAllReports(project)
    PomGenerator.applyTo(project)
}
