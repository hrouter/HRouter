plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies{
    implementation("com.lq.hrouter:lib_annotation:0.0.1")

    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.symbol.processing.api)
}
publishing {
    publications {
        create<MavenPublication>("compilerJar") {
            groupId = "com.lq.hrouter"
            artifactId = "lib_compiler"
            version = "0.0.1"

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/hongyi3715/HRouter")
            credentials {
                username = project.findProperty("gpr.user") as String?
                    ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String?
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}