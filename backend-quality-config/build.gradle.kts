import com.diffplug.gradle.spotless.SpotlessExtension
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    checkstyle
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
    alias(libs.plugins.dependency.updates)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Nullness annotations (Spring Framework 7 / Boot 4 use JSpecify natively).
    implementation(libs.jspecify)

    // Compile-time static analysis.
    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ---------------------------------------------------------------------------
// Compilation: warnings are errors, Error Prone and NullAway run on every compile.
// ---------------------------------------------------------------------------
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = libs.versions.java.get().toInt()
    options.compilerArgs.addAll(
        listOf(
            "-Werror",
            "-Xlint:all,-processing,-serial,-this-escape",
            "-parameters",
        ),
    )
    options.errorprone {
        disableWarningsInGeneratedCode = true
        excludedPaths = ".*/build/generated/.*"
        // Promote a few high-value checks beyond their defaults.
        error(
            "DefaultCharset",
            "FutureReturnValueIgnored",
            "MissingOverride",
            "RemoveUnusedImports",
            "UnusedVariable",
        )
        // NullAway in JSpecify mode: a NullPointerException becomes a compile error.
        check("NullAway", CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", group.toString())
        option("NullAway:JSpecifyMode", "true")
        option("NullAway:ExcludedFieldAnnotations", "org.springframework.beans.factory.annotation.Autowired")
        option("NullAway:HandleTestAssertionLibraries", "true")
    }
}

// Tests may use nulls deliberately; keep Error Prone but relax NullAway there.
tasks.named<JavaCompile>("compileTestJava") {
    options.errorprone.check("NullAway", CheckSeverity.OFF)
}

// ---------------------------------------------------------------------------
// Spotless: formatting. The pre-commit hook passes -PspotlessTargets=<a.java,b.java>
// so only staged files are formatted; without the property the whole tree is.
// ---------------------------------------------------------------------------
configure<SpotlessExtension> {
    val stagedTargets = providers.gradleProperty("spotlessTargets").map { it.split(',').filter(String::isNotBlank) }

    java {
        if (stagedTargets.isPresent) {
            target(stagedTargets.get())
        } else {
            target("src/**/*.java")
        }
        targetExclude("**/build/**", "**/generated/**")
        palantirJavaFormat(libs.versions.palantirJavaFormat.get()).style("PALANTIR").formatJavadoc(true)
        importOrder("", "javax", "java", "\\#")
        removeUnusedImports()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts", "gradle/**/*.gradle.kts", "buildSrc/**/*.gradle.kts", "build-logic/**/*.gradle.kts")
        ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// ---------------------------------------------------------------------------
// Checkstyle: style rules that a formatter cannot enforce (naming, imports, design).
// ---------------------------------------------------------------------------
checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configDirectory = rootProject.layout.projectDirectory.dir("config/checkstyle")
    configFile = configDirectory.get().file("checkstyle.xml").asFile
    maxWarnings = 0
    maxErrors = 0
    isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = false
        html.required = true
    }
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("failed", "skipped")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
}

// ---------------------------------------------------------------------------
// Dependency hygiene: `./gradlew dependencyUpdates` reports newer stable versions.
// ---------------------------------------------------------------------------
tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    rejectVersionIf {
        val unstable = Regex("(?i).*(alpha|beta|rc|cr|m\\d|preview|snapshot|ea).*")
        unstable.matches(candidate.version) && !unstable.matches(currentVersion)
    }
    checkConstraints = true
    gradleReleaseChannel = "current"
}
