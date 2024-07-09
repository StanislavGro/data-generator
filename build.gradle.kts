plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

dependencies {
    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.1")
//    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.3.0")

    implementation("org.json:json:20230227")

    implementation("org.postgresql:postgresql:42.5.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest.attributes["Main-Class"] = "ru.one.files.DataGeneratorKt"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.create("myJar", Jar::class) {
    group = "build"
    description = "Creates a self-contained fat JAR of the application that can be run."
    manifest.attributes["Main-Class"] = "ru.one.files.DataGeneratorKt"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    with(tasks.jar.get())
}

