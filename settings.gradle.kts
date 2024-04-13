rootProject.name = "trckr"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("build-logic")
include("trckr-core")
include("trckr-processor")
include("trckr-demo")

dependencyResolutionManagement {
    // Configure repositories
    // Use FAIL_ON_PROJECT_REPOS when https://youtrack.jetbrains.com/issue/KT-51379 will be fixed
    repositoriesMode = RepositoriesMode.PREFER_PROJECT
    repositories {
        mavenCentral()
    }
    // Configure version catalog
    versionCatalogs {
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
}
