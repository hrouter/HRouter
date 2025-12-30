plugins{
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

kotlin {
    jvmToolchain(17)
}
repositories {
    mavenLocal()
    google()
    mavenCentral()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.2.2")
}

gradlePlugin {
    plugins {
        create("hrouterPlugin") {
            id = "com.lq.hrouter"
            implementationClass = "com.lq.hrouter_plugin.HRouterPlugin"
        }
    }
}
