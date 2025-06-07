plugins {
    id("java")
}

group = "eu.virtusdevelops.easyclans"
version = "3.0.0"

val minecraftVersion: String by rootProject
val jdkVersion: Int by rootProject

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly(libs.bundles.cloudEcosystem)

    implementation(project(":api"))
}

tasks.test {
    useJUnitPlatform()
}