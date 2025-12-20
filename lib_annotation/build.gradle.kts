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
publishing {
    publications {
        create<MavenPublication>("annotationJar") {
            groupId = "com.lq.hrouter"
            artifactId = "lib_annotation"
            version = "0.0.1"

            from(components["java"])
        }
    }

    repositories {
        mavenLocal() // 本地仓库发布
    }
}