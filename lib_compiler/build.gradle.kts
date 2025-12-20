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
    implementation(project(":lib_annotation"))

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
        mavenLocal()
    }
}