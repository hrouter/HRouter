plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "2.0.0"
}

repositories {
    google() // 用于下载 com.android.tools.build:gradle
    mavenCentral() // 用于下载 kotlin-stdlib 等其他库
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.2.2")
    compileOnly("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.1.20-2.0.0")
}

group = "io.github.hrouter"
version = "0.0.3"

gradlePlugin {
    website.set("https://github.com/hrouter/HRouter")
    vcsUrl.set("https://github.com/hrouter/HRouter.git")
    plugins {
        create("hrouterPlugin") {
            id = "io.github.hrouter"
            implementationClass = "com.lq.plugin.HRouterPlugin"
            displayName = "HRouter Plugin"
            description = "HRouter Gradle Plugin for automatic routing and injection"
            tags.set(listOf("router", "android", "hrouter"))
        }
    }
}
