plugins {
    application
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versions)
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
    implementation(libs.web.ktor.server.core)
    implementation(libs.web.ktor.server.netty)
    implementation(libs.web.ktor.server.pages)
    implementation(libs.web.ktor.server.content.negotiation)
    implementation(libs.web.ktor.server.logging)
    implementation(libs.web.ktor.serialization.json)

    implementation(libs.di.koin.core)
    implementation(libs.di.koin.ktor)
    implementation(libs.di.koin.logger)

    implementation(libs.db.exposed.core)
    implementation(libs.db.exposed.dao)
    implementation(libs.db.exposed.jdbc)
    implementation(libs.db.exposed.time)

    implementation(libs.db.hikari)
    runtimeOnly(libs.db.postgres.driver)

    implementation(libs.logging.logback)
    implementation(libs.logging.kotlin)

    testImplementation(libs.test.junit.engine)
    testImplementation(libs.test.junit.params)
    testImplementation(libs.test.assertj)
    testImplementation(libs.test.ktor.server)
    testImplementation(libs.test.koin)

    testRuntimeOnly(libs.test.h2)
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
