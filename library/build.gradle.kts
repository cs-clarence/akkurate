plugins {
    id("akkurate.kmp-library-conventions")
    alias(libs.plugins.devtools.ksp)
    id("org.jetbrains.dokka")
    kotlin("multiplatform")
}

kotlin {
    jvm("jvm")
    js {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                //implementation(libs.kotest.assertions.kotlinx.time)
                implementation(libs.kotest.property)
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":akkurate-ksp-plugin"))
    add("kspJvm", project(":akkurate-ksp-plugin"))
}

ksp {
    arg("__PRIVATE_API__validatablePackages", "kotlin|kotlin.collections")
    arg("__PRIVATE_API__prependPackagesWith", "dev.nesk.akkurate.accessors")
    arg("appendPackagesWith", "")
}
