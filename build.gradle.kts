buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradleBuildTool}")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.daggerHiltAndroidVersion}")
        classpath("com.google.gms:google-services:${Versions.googleServices}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlytics}")
        classpath("com.diffplug.spotless:spotless-plugin-gradle:${Versions.spotless}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://androidx.dev/snapshots/builds/6543454/artifacts/repository/")
        }
    }
}

subprojects {
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