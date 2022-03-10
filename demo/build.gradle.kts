import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.1.0"
    id("org.sonarqube") version "3.3"
}

// sonarqube configuration
sonarqube {
    properties {
        property("sonar.projectKey", "demo")
    }
}

group = "ch.fhnw"
version = "1.0"

kotlin {
    sourceSets {
        named("main") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))

                // Icons for Compose forms header
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:0.4.0")

                // Kotlin Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

                // CSV Library
                implementation("com.opencsv:opencsv:5.5.2")

                // SQLite Database & Exposed Library
                implementation("org.xerial:sqlite-jdbc:3.36.0.3")
                implementation("org.jetbrains.exposed:exposed-core:0.37.3")
                implementation("org.jetbrains.exposed:exposed-dao:0.37.3")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")

                // Logging Library
                implementation("io.github.microutils:kotlin-logging:1.12.5")

                // MQTT Library for Compose Forms
                implementation("com.hivemq:hivemq-community-edition-embedded:2021.1")
            }
        }
        named("test") {
            dependencies {

                // Mock Library
                implementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
                implementation("io.mockk:mockk:1.11.0")

                // Used in FakePagingService for tests
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
            }
        }
    }
}

tasks.apply {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    withType<KotlinCompile> { kotlinOptions.jvmTarget = "11" }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "demo"
            packageVersion = "1.0.0"
        }
    }
}