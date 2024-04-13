import com.vanniktech.maven.publish.SonatypeHost
import helpers.isVersionStringValid

plugins {
    id("com.vanniktech.maven.publish")
}

val trckrVersion by extra {
    val versionFile = rootProject.layout.projectDirectory.file("trckr.version")
    providers.fileContents(versionFile).asText.get().trim()
}

check(isVersionStringValid(trckrVersion)) {
    "\"$trckrVersion\" is not valid version!"
}

group = "ru.cookedapp.trckr"
version = trckrVersion

mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.S01, automaticRelease = true)
    signAllPublications()

    pom {
        name = "Trckr"
        description = "Kotlin Symbol Processor to simplify analytics tracking."
        inceptionYear = "2022"
        url = "https://github.com/dzmpr/trckr/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "dzmpr"
                name = "Dzmitry Pryskoka"
                url = "https://github.com/dzmpr/"
            }
        }
        issueManagement {
            system = "Github"
            url = "https://github.com/dzmpr/trckr/issues"
        }
        scm {
            url = "https://github.com/dzmpr/trckr/"
            connection = "scm:git:git://github.com/dzmpr/trckr.git"
            developerConnection = "scm:git:ssh://git@github.com/dzmpr/trckr.git"
        }
    }
}
