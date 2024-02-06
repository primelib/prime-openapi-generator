import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("application")
    id("me.philippheuer.configuration")
    id("com.github.johnrengelman.shadow")
}

projectConfiguration {
    type.set(me.philippheuer.projectcfg.domain.ProjectType.APP)
}

dependencies {
    implementation("org.openapitools:openapi-generator-cli")

    implementation(project(":core"))
    implementation(project(":template-engine"))
    implementation(project(":gen-java-feign"))

    // logging
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

application {
    mainClass.set("io.github.primelib.primecodegen.cli.Application")
}

tasks {
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        if (this is ShadowJar) {
            archiveBaseName.set("openapi-generator")
            archiveClassifier.set("")
            archiveVersion.set("")
            mergeServiceFiles()
        }
    }
}
