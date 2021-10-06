import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

group = "ch.fhnw"
version = "1.0"

sourceSets {
    named("main") {
        dependencies {
            implementation(compose.desktop.currentOs)
            implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.1.0")

            implementation(project(":compose-forms:common"))
            implementation(project(":compose-forms:desktop"))
        }
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}