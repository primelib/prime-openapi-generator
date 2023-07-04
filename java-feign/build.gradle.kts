dependencies {
    // OpenAPI Generator
    api("org.openapitools:openapi-generator")

    // Nitro CodeGen
    api(project(":core"))

    // OpenAPI Generator - Template Engine
    testImplementation(project(":template-engine"))
}
