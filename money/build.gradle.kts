/*
 * Copyright 2026 CodeMatters, Lda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

import io.spine.dependency.lib.AutoService
import io.spine.dependency.local.Base
import io.spine.dependency.local.TestLib
import io.spine.dependency.local.Validation
import io.spine.gradle.publish.IncrementGuard

plugins {
    protobuf
    `jvm-module`
    id(coreJvmCompiler.pluginId)
}

apply<IncrementGuard>()

dependencies {
    annotationProcessor(AutoService.processor)
    compileOnly(AutoService.annotations)

    api(Base.lib)
    implementation(Validation.runtime)

    testImplementation(TestLib.lib)
}

configurations {
    excludeProtobufLite()
}
