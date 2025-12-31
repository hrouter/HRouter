plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "2.0.0"
}

group = "com.lq.hrouter"
version = "0.0.1"

dependencies {
    compileOnly("com.android.tools.build:gradle:8.2.2")
}


gradlePlugin {
    website.set("https://maven.pkg.github.com/hongyi3715/HRouter")
    vcsUrl.set("https://maven.pkg.github.com/hongyi3715/HRouter.git")
    plugins {
        create("hrouterPlugin") {
            id = "com.lq.hrouter"
            implementationClass = "com.lq.hrouter_plugin.HRouterPlugin"
            displayName = "HRouter Plugin"
            description = "HRouter Gradle Plugin for automatic routing and injection"
            tags.set(listOf("router", "android", "hrouter"))
        }
    }
}
