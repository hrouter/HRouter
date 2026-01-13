import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    id("signing")
    alias(libs.plugins.vanniktech)
}

android {
    namespace = "com.lq.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    api(project(":annotation"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.coroutine.test)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.withType<Test> {
    useJUnitPlatform() // 关键
}

group = "io.github.hrouter"
version = "0.0.3"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(project.group.toString(), project.name, project.version.toString())
    pom {
        name.set("HRouter Core")
        description.set("Core for HRouter Gradle Plugin and routing")
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
                email.set("177282081+hongyi3715@users.noreply.github.com")
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
