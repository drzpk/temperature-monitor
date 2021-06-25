plugins {
    application
    java
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("koin")
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

allOpen {
    annotation("dev.drzepka.tempmonitor.logger.util.Mockable")
}

application {
    mainClassName = "dev.drzepka.tempmonitor.logger.TemperatureMonitorLoggerKt"
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.11.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testImplementation("org.mockito:mockito-core:3.9.0")
    testImplementation("org.mockito:mockito-junit-jupiter:3.9.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}