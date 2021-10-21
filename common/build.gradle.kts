import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha3"

    kotlin("plugin.serialization") version("1.4.32")
    id("jacoco")
    id("org.sonarqube") version "3.3"
}

sonarqube {
    properties {
        property("sonar.projectKey", "common")
        property("sonar.login", "043a9791e1e6b593759abe6c1435ba1fd74f7955")
    }
}

group = "ch.fhnw"
version = "1.0"

sourceSets {
    named("main") {
        dependencies {
            // COmpose Forms
            api(compose.desktop.currentOs)
            api("org.jetbrains.kotlin:kotlin-reflect:1.4.32")
            api("org.junit.jupiter:junit-jupiter:5.7.1")
            api("com.hivemq:hivemq-mqtt-client:1.2.1")
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
            testImplementation("io.mockk:mockk:1.11.0")

            implementation(compose.desktop.currentOs)
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks{
    jacocoTestReport {
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
            html.destination = file("${buildDir}/jacocoHtml")
        }
    }
}