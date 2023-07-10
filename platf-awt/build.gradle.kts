/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
//    js {
//        browser()
//    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":base-portable"))
                implementation(project(":plot-livemap"))
                implementation(project(":vis-canvas"))
                implementation(project(":plot-base-portable"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-builder-portable"))
                implementation(project(":plot-config-portable"))
            }
        }

        jvmTest {
            dependencies {
//                implementation kotlin('test')
                implementation(kotlin("test-junit"))
//                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
//                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
//                implementation("org.mockito:mockito-core:$mockitoVersion")
//                implementation("org.assertj:assertj-core:$assertjVersion")
//                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

    }
}