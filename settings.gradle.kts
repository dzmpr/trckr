rootProject.name = "trckr"

includeBuild("build-logic")
include("trckr-core")
include("trckr-processor")
include("trckr-demo")

dependencyResolutionManagement {
    // Configure repositories
    // TODO: find mystery 'ivy' repository
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
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
