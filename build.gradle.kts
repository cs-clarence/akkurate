plugins {
    id("org.jetbrains.dokka") version "1.9.0" apply false
    kotlin("multiplatform") apply false
    alias(libs.plugins.kotest.multiplatform.plugin) apply false
}

repositories {
    mavenCentral()
}
