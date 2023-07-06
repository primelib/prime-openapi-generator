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
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}

    dependencies {
        constraints {
            add("api", "org.openapitools:openapi-generator:6.6.0")
            add("api", "org.openapitools:openapi-generator-cli:6.6.0")
        }

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

        implementation("commons-io:commons-io:2.13.0")
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}
