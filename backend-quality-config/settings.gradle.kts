plugins {
    // Lets Gradle download a matching JDK if the toolchain below is missing (mise normally provides it).
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "backend-api"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// Multi-module: add subprojects here and move the quality block in build.gradle.kts
// into a convention plugin under buildSrc/ or build-logic/ so every module inherits it.
// include("api", "domain", "infrastructure")
