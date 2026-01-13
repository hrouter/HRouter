import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("signing")
    alias(libs.plugins.vanniktech)
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

group = "io.github.hrouter"
version = "0.0.3"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(project.group.toString(), project.name, project.version.toString())
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

// 放在 mavenPublishing 块后面
signing {
    val keyId = project.findProperty("signing.keyId") as String?
    val password = project.findProperty("signing.password") as String?
    // 直接读你桌面上那个 .asc 文件，避开 properties 的字符串解析
    val keyFile = file("C:/Users/111/Desktop/HRouter/gpg_secrets.asc")

    if (keyFile.exists() && password != null) {
        val signingKey = keyFile.readText()
        useInMemoryPgpKeys(keyId, signingKey, password)
    }
}
