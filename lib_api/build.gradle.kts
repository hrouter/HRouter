plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.lq.lib_api"
    compileSdk = 35

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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api("com.lq.hrouter:lib_annotation:0.0.1")


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


publishing {
    publications {
        create<MavenPublication>("apiAar") {
            groupId = "com.lq.hrouter"
            artifactId = "lib_api"
            version = "0.0.1"

            afterEvaluate {
                from(components["release"])
            }
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