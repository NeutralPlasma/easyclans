pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.10"
    }
}
// The settings file is the entry point of every Gradle build.
// Its primary purpose is to define the subprojects.
// It is also used for some aspects of project-wide configuration, like managing plugins, dependencies, etc.
// https://docs.gradle.org/current/userguide/settings_file_basics.html

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://repo.papermc.io/repository/maven-public/")

        maven(url = "https://repo.rosewooddev.io/repository/public/")
        maven(url = "https://hub.spigotmc.org/nexus/content/groups/public/")
        maven(url = "https://nexus.bencodez.com/repository/maven-public/")
        maven(url = "https://repo.nightexpressdev.com/releases/")
        maven(url = "https://jitpack.io/")
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")

        mavenLocal()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val minecraftVersion: String = "1.21.4"

gradle.beforeProject {
    extensions.extraProperties["minecraftVersion"] = minecraftVersion
    extensions.extraProperties["jdkVersion"] = 21
}



rootProject.name = "EasyClans"

include(":api")