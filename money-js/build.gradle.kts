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

import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove
import io.spine.internal.gradle.report.license.LicenseReporter

plugins {
    protobuf
    `java-library`
}
LicenseReporter.generateReportIn(project)

val moneyModule = project(":money")

dependencies {
    implementation(moneyModule)
}

sourceSets {
    main {
        proto.srcDir(moneyModule.projectDir.resolve("src/main/proto"))
    }
}

protobuf {
    protoc {
        // Temporarily use this version, since 3.21.x is known to provide
        // a broken `protoc-gen-js` artifact.
        // See the following links for details:
        //
        // https://github.com/protocolbuffers/protobuf-javascript/issues/127.
        // https://gist.github.com/Siedlerchr/0fd9e463f6ffa2ea3c4b5e5ae3e5889e

        // Once it is addressed, this artifact should be `Protobuf.compiler`.
        artifact = "com.google.protobuf:protoc:3.19.6"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                // Do not use java builtin output in this project.
                remove("java")

                id("js") {
                    option("library=spine-money-${project.project.version}")
                    outputSubDir = "js"
                }
            }
        }
    }
}
