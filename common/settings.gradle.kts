pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

}
systemProp.sonar.host.url="http://localhost:9000"
rootProject.name = "ch.fhnw.forms.common"