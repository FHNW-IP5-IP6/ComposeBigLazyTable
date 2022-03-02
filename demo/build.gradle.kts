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
                implementation("org.xerial:sqlite-jdbc:3.30.1")
                implementation("org.jetbrains.exposed:exposed-core:0.35.3")
                implementation("org.jetbrains.exposed:exposed-dao:0.35.3")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.35.3")

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

                /*
                TODO: REMOVE ALL DEPENDENCIES IF UI TESTS DON'T WORK
                // UI Testing
                implementation("androidx.compose.ui:ui-test-desktop:1.0.0-beta06")
                implementation("org.jetbrains.compose.ui:ui-test-junit4-desktop:1.0.1")
                implementation(getSkiaDependency()) // WORKAROUND

                // JUnit4
                implementation("junit:junit:4.13.2")

                // JUnit5 dependency
                implementation("org.junit.jupiter:junit-jupiter:5.8.2")

                // needed so that JUnit4/3 tests can also run in the same project with JUnit5 Tests
                implementation("org.junit.vintage:junit-vintage-engine:5.8.2")
                 */
            }
        }
    }
}

/*
TODO: REMOVE THIS IF UI TESTS DON'T WORK
fun getSkiaDependency(): String {
    val target = getTarget()
    val version = "0.6.7"
    return "org.jetbrains.skiko:skiko-jvm-runtime-$target:$version"
}

fun getTarget(): String {
    val osName = System.getProperty("os.name")
    val targetOs = when {
        osName == "Mac OS X" -> "macos"
        osName.startsWith("Win") -> "windows"
        osName.startsWith("Linux") -> "linux"
        else -> error("Unsupported OS: $osName")
    }

    val targetArch = when (val osArch = System.getProperty("os.arch")) {
        "x86_64", "amd64" -> "x64"
        "aarch64" -> "arm64"
        else -> error("Unsupported arch: $osArch")
    }

    return "${targetOs}-${targetArch}"
}
*/

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