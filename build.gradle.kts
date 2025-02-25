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

dependencies {
    // KTOR
    implementation("io.ktor:ktor-server-core:2.3.10")
    implementation("io.ktor:ktor-server-netty:2.3.10")
    implementation("io.ktor:ktor-server-status-pages:2.3.10")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-server-call-logging:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")

    // LOGGING
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // DI
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("io.insert-koin:koin-ktor:3.5.6")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.6")

    // DATABASE
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.49.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    runtimeOnly("org.postgresql:postgresql:42.7.3")

    // TEST
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.23")
    testImplementation("io.ktor:ktor-server-test-host:2.3.10")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("io.insert-koin:koin-test:3.5.6")
    testRuntimeOnly("com.h2database:h2:2.2.224")
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
