import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    id("org.jetbrains.dokka")
}

configure<DokkaExtension> {
    dokkaSourceSets.configureEach {
        documentedVisibilities(VisibilityModifier.Public)
    }
    dokkaPublications.html {
        suppressObviousFunctions = true
        suppressInheritedMembers = true
    }
}
