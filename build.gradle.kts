buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.29.1-alpha")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://naver.jfrog.io/artifactory/maven/")
    }
}

tasks.register("copyGitHooks", Copy::class) {
    from("${rootDir}/script/hooks") {
        include("**/*")
        rename("(.*)", "$1")
    }
    into("${rootDir}/.git/hooks")
}