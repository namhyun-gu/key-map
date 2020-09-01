buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.4.0"))
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha")
        classpath("com.google.gms:google-services:4.3.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.2.1")
        classpath("com.diffplug.spotless:spotless-plugin-gradle:4.0.1")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://navercorp.bintray.com/maps")
        maven("https://androidx.dev/snapshots/builds/6543454/artifacts/repository/")
    }
}

subprojects {
    apply(from = rootProject.file("dependencies.gradle.kts"))
    apply(plugin = "com.diffplug.gradle.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint().userData(mapOf("indent_size" to "2"))
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile("../spotless.license.kt")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}