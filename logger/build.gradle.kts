plugins {
    application
    java
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("koin")
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

application {
    mainClassName = "dev.drzepka.tempmonitor.logger.TemperatureMonitorLoggerKt"
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}