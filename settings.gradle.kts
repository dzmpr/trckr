rootProject.name = "trckr"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("build-logic")
include("trckr-core")
include("trckr-processor")
include("trckr-demo")

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
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
