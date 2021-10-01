buildscript {
    // Compose kotlin version
    val kotlinVersion = "1.5.21"

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

group = "ch.fhnw"
version = "1.0"

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // TODO: Not working with jitpack until now
        //  maven("https://jitpack.io") // try also https://www.jitpack.io
    }
}
