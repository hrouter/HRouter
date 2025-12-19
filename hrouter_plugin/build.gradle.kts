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

//dependencies {
//    implementation("com.lq.hrouter:lib_annotation:1.0.0")
//}

gradlePlugin {
    plugins {
        create("hrouterPlugin") {
            id = "com.lq.hrouter"
            implementationClass = "com.lq.hrouter_plugin.HRouterPlugin"
        }
    }
}
