import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    implementation("org.openapitools:openapi-generator-cli")

    implementation(project(":core"))
    implementation(project(":template-engine"))
    implementation(project(":java-feign"))

    implementation("org.jetbrains:annotations:16.0.2")
}

application {
    mainClass.set("com.github.twitch4j.codegen.cli.Application")
}

tasks {
    withType<Jar> {
        if (this is ShadowJar) {
            archiveBaseName.set("openapi-generator")
            archiveClassifier.set("")
            archiveVersion.set("")
            mergeServiceFiles()
        }
    }
}
