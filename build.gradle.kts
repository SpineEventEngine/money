/*
 * Copyright 2023, TeamDev. All rights reserved.
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

import io.spine.internal.dependency.Dokka
import io.spine.internal.dependency.JUnit
import io.spine.internal.dependency.Jackson
import io.spine.internal.dependency.Spine
import io.spine.internal.dependency.Validation
import io.spine.internal.gradle.publish.PublishingRepos
import io.spine.internal.gradle.publish.spinePublishing
import io.spine.internal.gradle.report.coverage.JacocoConfig
import io.spine.internal.gradle.report.license.LicenseReporter
import io.spine.internal.gradle.report.pom.PomGenerator
import io.spine.internal.gradle.standardToSpineSdk

buildscript {
    standardSpineSdkRepositories()
    doForceVersions(configurations)

    dependencies {
        classpath(io.spine.internal.dependency.Spine.McJava.pluginLib)
        // TODO: Define McJs dependency object.
        classpath("io.spine.tools:spine-mc-js:2.0.0-SNAPSHOT.130")
    }

    configurations {
        all {
            resolutionStrategy {
                val validation = io.spine.internal.dependency.Validation
                val jackson = io.spine.internal.dependency.Jackson
                force(
                    io.spine.internal.dependency.Spine.base,
                    io.spine.internal.dependency.Spine.toolBase,
                    io.spine.internal.dependency.Spine.Logging.lib,

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
            cloudRepo,
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
                    Spine.base,
                    Spine.toolBase,
                    Spine.Logging.lib,
                    io.spine.internal.dependency.Spine.Logging.backend,

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
