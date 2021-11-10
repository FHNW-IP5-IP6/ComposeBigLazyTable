import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha3"
    id("org.sonarqube") version "3.3"
}

//sonarqube {
//    properties {
//        property("sonar.projectKey", "demo")
//        property("sonar.login", "e11521286d382bb374dbf68bc8642d4486e24ee3")
//    }
//}

group = "ch.fhnw"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    sourceSets {
        named("main") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
                implementation(project(":desktop"))
                implementation("com.opencsv:opencsv:5.5.2")
                implementation("org.jetbrains.exposed:exposed-core:0.35.3")
                implementation("org.jetbrains.exposed:exposed-dao:0.35.3")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.35.3")
                implementation("org.xerial:sqlite-jdbc:3.30.1")
            }
        }
    }
}

tasks.apply {
    withType<Test> { useJUnitPlatform() }
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
