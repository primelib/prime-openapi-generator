plugins {
    `java`
    `java-library`
    id("io.freefair.lombok") version "8.1.0"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenCentral()
    }

    lombok {
        version = "1.18.28"
    }

    java {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

    dependencies {
        constraints {
            add("api", "org.openapitools:openapi-generator:6.6.0")
            add("api", "org.openapitools:openapi-generator-cli:6.6.0")
        }

        // annotations
        implementation("org.jetbrains:annotations:16.0.2")

        // testing
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

        // logging
        implementation("commons-io:commons-io:2.13.0")
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}
