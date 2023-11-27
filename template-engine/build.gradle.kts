plugins {
    `java-library`
    id("me.philippheuer.configuration")
}

projectConfiguration {
    artifactGroupId.set("io.github.primelib.primecodegen")
    artifactId.set("template-engine")
    artifactDisplayName.set("template-engine")
    artifactDescription.set("Template Engines for the OpenAPI Generator")
}

dependencies {
    // openapi generator
    api("org.openapitools:openapi-generator")

    // template engine
    api("io.pebbletemplates:pebble:3.2.2")
}
