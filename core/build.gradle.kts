plugins {
    `java-library`
    id("me.philippheuer.configuration")
}

projectConfiguration {
    artifactGroupId.set("io.github.primelib.primecodegen")
    artifactId.set("core")
    artifactDisplayName.set("core")
    artifactDescription.set("core module with reusable code for all code generators")
}

dependencies {
	// OpenAPI Generator
	api("org.openapitools:openapi-generator")
}
