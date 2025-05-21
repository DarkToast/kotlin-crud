plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "2.1.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("io.ktor.plugin") version "3.1.3"
}

// Versions
val kotlinVersion = "2.1.21"
val ktorVersion = "3.1.3"
val kotlinLoggingVersion = "7.0.7"
val logbackVersion = "1.5.18"
val koinVersion = "4.0.4"
val exposedVersion = "0.61.0"
val hikariVersion = "6.3.0"
val postgresqlVersion = "42.7.5"
val junitVersion = "5.12.2"
val assertjVersion = "3.27.3"
val h2Version = "2.3.232"

repositories {
    mavenCentral()
}

application {
    mainClass = "de.tarent.crud.ServerKt"
}

// Due to ticket: https://youtrack.jetbrains.com/issue/KTOR-6775
jib {
    container.creationTime = "USE_CURRENT_TIMESTAMP"
}

ktor {
    fatJar {
        archiveFileName = "fat.jar"
    }
    docker {
        localImageName = "crud"
        imageTag = "latest"
        jreVersion = JavaVersion.VERSION_21
    }
}

dependencies {
    // KTOR
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // LOGGING
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // DI
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    // DATABASE
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    // TEST
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testRuntimeOnly("com.h2database:h2:$h2Version")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}
