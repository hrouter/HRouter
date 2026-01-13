pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
//        includeBuild("hrouter_plugin")
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "HRouter"
include(":app")
include(":login")
include(":hrouter_plugin")
include(":lib_compiler")
include(":lib_annotation")
include(":lib_api")
