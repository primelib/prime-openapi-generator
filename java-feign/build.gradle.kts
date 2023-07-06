plugins {
    `java-library`
    id("me.philippheuer.configuration")
}

projectConfiguration {
    artifactGroupId.set("io.github.primelib.codegen")
    artifactId.set("java-feign")
    artifactDisplayName.set("java-feign")
    artifactDescription.set("java-feign generator")
}

dependencies {
    // OpenAPI Generator
    api("org.openapitools:openapi-generator")

    // Nitro CodeGen
    api(project(":core"))

    // OpenAPI Generator - Template Engine
    testImplementation(project(":template-engine"))
}
