rootProject.name = "build-logic"

dependencyResolutionManagement {
    // Configure repositories
    // TODO: find mystery 'ivy' repository
    repositoriesMode = RepositoriesMode.PREFER_PROJECT
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
