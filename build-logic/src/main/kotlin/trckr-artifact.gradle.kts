import com.vanniktech.maven.publish.SonatypeHost
import ru.cookedapp.trckr.gradle.isVersionStringValid

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
        name.set("Trckr")
        description.set("Kotlin Symbol Processor to simplify analytics tracking.")
        inceptionYear.set("2022")
        url.set("https://github.com/dzmpr/trckr/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("dzmpr")
                name.set("Dzmitry Pryskoka")
                url.set("https://github.com/dzmpr/")
            }
        }
        issueManagement {
            system.set("Github")
            url.set("https://github.com/dzmpr/trckr/issues")
        }
        scm {
            url.set("https://github.com/dzmpr/trckr/")
            connection.set("scm:git:git://github.com/dzmpr/trckr.git")
            developerConnection.set("scm:git:ssh://git@github.com/dzmpr/trckr.git")
        }
    }
}
