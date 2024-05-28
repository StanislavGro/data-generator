plugins {
    kotlin("jvm") version "1.9.22"
}

group = "ru.youngstanis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.json:json:20230227")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}