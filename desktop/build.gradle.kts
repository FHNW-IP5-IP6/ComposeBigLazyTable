import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha3"
    id("org.sonarqube") version "3.3"
}

sonarqube {
    properties {
        property("sonar.projectKey", "desktop")
        property("sonar.login", "9c5888b7f8bb7f1af8bbb955f1544088a5f68a5d")
    }
}

group = "ch.fhnw"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

sourceSets {
    named("main") {
        dependencies {
            // ComposeForms
            api(compose.desktop.currentOs)
            implementation("org.jetbrains.compose.material:material-icons-extended-desktop:0.4.0")
            implementation("com.hivemq:hivemq-community-edition-embedded:2021.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
            testImplementation("io.mockk:mockk:1.11.0")

            implementation(compose.desktop.currentOs)
            implementation(project(":common"))
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "desktop"
            packageVersion = "1.0.0"
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}