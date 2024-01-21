import dev.nesk.akkurate.gradle.configurePom

plugins {
    id("akkurate.publishing-conventions")
    alias(libs.plugins.dokka)
    kotlin("multiplatform")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.karumi.kotlinsnapshot:plugin:2.3.0")
    }
}

apply(plugin = "com.karumi.kotlin-snapshot")

kotlin {
    explicitApi()
    jvm("jvm")
    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
        }

        val jvmMain by getting {
            dependencies {
                implementation("com.squareup:kotlinpoet:1.14.2")
                implementation("com.squareup:kotlinpoet-ksp:1.14.2")
                implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
            }
        }
    }
}

java {
//    withSourcesJar()
//    withJavadocJar()
}

//test {
//    useJUnitPlatform()
//}

//publishing.publications.create<MavenPublication>("release") {
//    from(components["kotlin"])
//    artifact(tasks.named("javadocJar").get())
//    artifact(tasks.named("sourcesJar").get())
//    configurePom()
//}
