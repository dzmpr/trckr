
rootProject.name = "trckr"

includeBuild("build-logic")
include("trckr-core")
include("trckr-processor")
include("trckr-demo")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
}
