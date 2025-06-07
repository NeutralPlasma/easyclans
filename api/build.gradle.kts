plugins {
    kotlin("jvm")
    id("java")
}

group = "eu.virtusdevelops"
version = "3.0.0"

val minecraftVersion: String by rootProject
val jdkVersion: Int by rootProject


dependencies {
}


kotlin {
    jvmToolchain(jdkVersion)
}

tasks.test {
    useJUnitPlatform()
}