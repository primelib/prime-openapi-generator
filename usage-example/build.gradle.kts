buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("org.openapitools:openapi-generator-gradle-plugin:6.6.0")

        classpath(project(":template-engine"))
        classpath(project(":java-feign"))
    }
}

plugins {
    java
    id("org.openapi.generator")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // annotations
    implementation("io.swagger.core.v3:swagger-annotations:2.2.14")
    implementation("org.jetbrains:annotations:24.0.1")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // metrics
    implementation("io.micrometer:micrometer-core:1.11.1")

    // log
    testImplementation("org.slf4j:slf4j-simple:1.7.36")

    // feign
    implementation("io.github.openfeign:feign-core:12.3")
    implementation("io.github.openfeign:feign-slf4j:12.3")
    implementation("io.github.openfeign:feign-jackson:12.3)
    implementation("io.github.openfeign:feign-java11:12.3")

    // resilience4J
    implementation("io.github.resilience4j:resilience4j-core:1.7.1")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:1.7.1")
    implementation("io.github.resilience4j:resilience4j-ratelimiter:1.7.1")
    implementation("io.github.resilience4j:resilience4j-feign:1.7.1")
    implementation("io.github.resilience4j:resilience4j-retry:1.7.1")
    implementation("io.github.resilience4j:resilience4j-bulkhead:1.7.1")
}

openApiGenerate {
    verbose = false
    generatorName = "custom-java-feign"
    inputSpec = "$rootDir/usage-example/src/main/resources/openapi.yaml".toString()
    outputDir = "$rootDir/usage-example".toString()
    apiPackage = "com.github.twitch4j.helix.api"
    invokerPackage = "com.github.twitch4j.helix"
    modelPackage = "com.github.twitch4j.helix.domain"
    configOptions = [
            clientClassName        : "TwitchHelix",
            hideGenerationTimestamp: "false",
            useJackson             : "true",
    ]
    globalProperties = [
            modelDocs: "false"
    ]
    enablePostProcessFile = false
    engine = "auto" // auto select engine based on template file extension
}
