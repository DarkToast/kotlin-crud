plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("io.ktor.plugin") version "2.3.10"
}

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

// Define versions as variables
val ktorVersion = "2.3.10"
val kotlinLoggingVersion = "6.0.9"
val logbackVersion = "1.5.6"
val koinVersion = "3.5.6"
val exposedVersion = "0.49.0"
val hikariCPVersion = "5.0.1"
val postgresqlVersion = "42.7.3"
val kotlinTestVersion = "1.9.23"
val junitVersion = "5.10.2"
val assertjVersion = "3.25.3"
val h2Version = "2.2.224"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinTestVersion")
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

// tasks.register("prepareImage", Copy) {
//    from "$buildDir/resources/main/Dockerfile"
//    into "$buildDir/install"
// }
