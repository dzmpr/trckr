plugins {
    id("org.jetbrains.dokka")
}

tasks.dokkaHtml {
    suppressInheritedMembers = true
    dokkaSourceSets {
        configureEach {
            includeNonPublic = true
        }
    }
}
