plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
    id("signing") // 如果需要签名
}

group = "io.github.hrouter"
version = "0.0.2"

java {
    withSourcesJar() // 生成源码 Jar
    withJavadocJar() // 生成 Javadoc Jar
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

publishing {
    publications {
        create<MavenPublication>("annotationJar") {
            from(components["java"])
            groupId = "io.github.hrouter"
            artifactId = "lib_annotation"
            version = "0.0.2"

            // 必填 POM 信息
            pom {
                name.set("HRouter Annotation")
                description.set("Annotations for HRouter Gradle Plugin and routing")
                url.set("https://github.com/hrouter/HRouter")
                licenses {
                    license {
                        name.set("Apache-2.0 License")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("qingshan")
                        name.set("青山")
                        email.set("liuhongyi1414@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/hrouter/HRouter.git")
                    developerConnection.set("scm:git:ssh://github.com/hrouter/HRouter.git")
                    url.set("https://github.com/hrouter/HRouter")
                }
            }
        }
    }

    repositories {
        maven {
            name = "MavenCentral"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String?
                    ?: System.getenv("OSSRH_USERNAME")
                password = project.findProperty("ossrhPassword") as String?
                    ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}
