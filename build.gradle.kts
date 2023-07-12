plugins {
    `java-library`
    id("me.philippheuer.configuration") version "0.10.5"
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "me.philippheuer.configuration")
    apply(plugin = "io.freefair.lombok")

    projectConfiguration {
        type.set(me.philippheuer.projectcfg.domain.ProjectType.LIBRARY)
        language.set(me.philippheuer.projectcfg.domain.ProjectLanguage.KOTLIN)
        javaVersion.set(JavaVersion.VERSION_17)

        pom = { pom ->
            pom.url.set("https://github.com/primelib/primecodegen")
            pom.issueManagement {
                system.set("GitHub")
                url.set("https://github.com/primelib/primecodegen/issues")
            }
            pom.inceptionYear.set("2017")
            pom.developers {
                developer {
                    id.set("PhilippHeuer")
                    name.set("Philipp Heuer")
                    email.set("git@philippheuer.me")
                    roles.addAll("maintainer")
                }
            }
            pom.licenses {
                license {
                    name.set("MIT Licence")
                    distribution.set("repo")
                    url.set("https://github.com/primelib/primecodegen/blob/main/LICENSE")
                }
            }
            pom.scm {
                connection.set("scm:git:git://github.com/primelib/primecodegen.git")
                developerConnection.set("scm:git:git://github.com/primelib/primecodegen.git")
                url.set("https://github.com/primelib/primecodegen")
            }
        }
    }

    dependencies {
        constraints {
            add("api", "org.openapitools:openapi-generator:7.0.0-beta")
            add("api", "org.openapitools:openapi-generator-cli:7.0.0-beta")
        }

        // annotations
        implementation("org.jetbrains:annotations:16.0.2")

        // testing
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    }
}
