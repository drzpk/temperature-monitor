allprojects {
    group = "dev.drzepka.tempmonitor"
    version = "1.0.0-SNAPSHOT"
}

subprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
    }
}

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        val koinVersion: String by project
        classpath("org.koin:koin-gradle-plugin:$koinVersion")
    }
}