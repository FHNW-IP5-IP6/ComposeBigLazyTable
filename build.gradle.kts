buildscript {
    // Compose kotlin version
    val kotlinVersion = "1.5.21"

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        // TODO: Which is better of those 2?
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        //classpath("org.jetbrains.compose:compose-gradle-plugin:0.4.0") TODO: This makes problems
    }
}

group = "ch.fhnw"
version = "1.0"

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
