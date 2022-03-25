import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.1.0"
    kotlin("plugin.serialization") version("1.4.32")
    id("org.sonarqube") version "3.3"
}

// sonarqube configuration
sonarqube {
    properties {
        property("sonar.projectKey", "common")
    }
}

group = "ch.fhnw"
version = "1.0"

sourceSets {
    named("main") {
        dependencies {
            // Compose Forms
            api(compose.desktop.currentOs)

            // Icons for Compose forms header
            implementation("org.jetbrains.compose.material:material-icons-extended-desktop:0.4.0")

            // Kotlin Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

            // SQLite Database & Exposed Library
            implementation("org.xerial:sqlite-jdbc:3.36.0.3")
            implementation("org.jetbrains.exposed:exposed-core:0.37.3")
            implementation("org.jetbrains.exposed:exposed-dao:0.37.3")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")

            // Logging Library
            implementation("io.github.microutils:kotlin-logging:1.12.5")

            // MQTT Library for Compose Forms
            implementation("com.hivemq:hivemq-community-edition-embedded:2021.1")

            api("org.jetbrains.kotlin:kotlin-reflect:1.4.32")
            api("org.junit.jupiter:junit-jupiter:5.7.1")
            api("com.hivemq:hivemq-mqtt-client:1.2.1")
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
            testImplementation("io.mockk:mockk:1.11.0")

            implementation("org.jetbrains.exposed:exposed-core:0.37.3")
            implementation("org.jetbrains.exposed:exposed-dao:0.37.3")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")
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