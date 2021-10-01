import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

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

                // TODO: Variant 1 Jitpack
                //  https://stackoverflow.com/questions/18748436/is-it-possible-to-declare-git-repository-as-dependency-in-android-gradle
                //  Does not work yet - Could not resolve com.github.FHNW-IP5-IP6:ComposeForms:master-SNAPSHOT.
                //  implementation("com.github.FHNW-IP5-IP6:ComposeForms:master-SNAPSHOT")

                // TODO: Variant 2 add source dependencies from git
                //  implementation("org.gradle.cpp-samples:utilities") {
                //    version {
                //        branch = "master"
                //    }
                //}

                // TODO: Variant 3 git submodules
                //implementation(project(":compose-forms"))
            }
        }
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
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