rootProject.name = "build-logic"

dependencyResolutionManagement {
    // Configure repositories
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        gradlePluginPortal()
    }
    // Configure version catalog
    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
}
