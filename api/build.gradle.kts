plugins {
    kotlin("jvm")
    id("java")
}

group = "eu.virtusdevelops"
version = "3.0.0"

val minecraftVersion: String by rootProject
val jdkVersion: Int by rootProject


dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
}


kotlin {
    jvmToolchain(jdkVersion)
}

tasks.test {
    useJUnitPlatform()
}