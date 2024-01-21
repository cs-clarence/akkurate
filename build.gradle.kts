plugins {
    alias(libs.plugins.dokka) apply false
    kotlin("multiplatform") apply false
    alias(libs.plugins.kotest.multiplatform.plugin) apply false
}

repositories {
    mavenCentral()
}
