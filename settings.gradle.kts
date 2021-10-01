pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// TODO: Variant 2 add source dependencies from git
//  https://stackoverflow.com/questions/18748436/is-it-possible-to-declare-git-repository-as-dependency-in-android-gradle
//
//sourceControl {
//    gitRepository(uri("https://github.com/FHNW-IP5-IP6/ComposeForms.git")) {
//        producesModule("ch.fhnw.forms")
//    }
//}

rootProject.name = "ComposeBigLazyTable"

//include(":android")
include(":common")
include(":desktop")
include(":demo")
include(":compose-forms")