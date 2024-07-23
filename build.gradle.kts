plugins {
    application
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versions)
    alias(libs.plugins.graphQl)
}

repositories {
    mavenCentral()
}

application {
    mainClass = "de.tarent.crud.ApplicationKt"
}

dependencies {
    implementation(libs.web.ktor.server.core)
    implementation(libs.web.ktor.server.netty)
    implementation(libs.web.ktor.server.pages)
    implementation(libs.web.ktor.server.content.negotiation)
    implementation(libs.web.ktor.server.logging)
    implementation(libs.web.ktor.server.graphql)
    implementation(libs.web.ktor.serialization.json)
    implementation(libs.web.ktor.serialization.jackson)

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

graphql {
//    client {
//        endpoint = "http://localhost:8080/graphql"
//        // Target package name to be used for generated classes.
//        packageName = "de.tarent.crud.domain"
//        sdlEndpoint = "http://localhost:8080/sdl"
//        queryFiles = listOf()
//    }
    schema {
        // List of supported packages that can contain GraphQL schema type definitions
        packages = listOf("de.tarent.crud")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}
