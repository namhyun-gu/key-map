plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(rootProject.extra["compileSdkVersion"] as Int)
    defaultConfig {
        applicationId = "dev.namhyun.geokey"
        minSdkVersion(rootProject.extra["minSdkVersion"] as Int)
        targetSdkVersion(rootProject.extra["targetSdkVersion"] as Int)
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NCP_CLIENT_ID", "\"${rootProject.extra["NCP_CLIENT_ID"]}\"")
        buildConfigField("String", "NCP_CLIENT_SECRET", "\"${rootProject.extra["NCP_CLIENT_SECRET"]}\"")
    }
    signingConfigs {
        register("release") {
            storeFile = file("release.keystore")
            storePassword = rootProject.extra["KEYSTORE_PASSWORD"] as String
            keyAlias = rootProject.extra["KEYSTORE_KEY_ALIAS"] as String
            keyPassword = rootProject.extra["KEYSTORE_KEY_PASSWORD"] as String
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.0")

    // Android
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.activity:activity-ktx:1.2.0-alpha08")
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha08")
    implementation("com.google.android.material:material:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    // Dagger
    implementation("com.google.dagger:dagger:2.28.3")
    implementation("com.google.dagger:hilt-android:2.28-alpha")
    implementation("com.google.dagger:hilt-android-testing:2.28-alpha")
    implementation("androidx.hilt:hilt-common:1.0.0-SNAPSHOT")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-SNAPSHOT")
    kapt("com.google.dagger:dagger-compiler:2.28")
    kapt("com.google.dagger:hilt-android-compiler:2.28-alpha")
    kapt("androidx.hilt:hilt-compiler:1.0.0-SNAPSHOT")

    // moshi
    implementation("com.squareup.moshi:moshi:1.9.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.2")
    implementation("com.squareup.moshi:moshi-adapters:1.9.2")
    implementation("com.squareup.moshi:moshi-kotlin-codegen:1.9.2")
    implementation("com.squareup.retrofit2:converter-moshi:2.7.2")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")

    // Google Services
    implementation("com.google.android.gms:play-services-location:17.0.0")
    implementation("com.google.android.gms:play-services-auth:18.1.0")

    // Firebase
    implementation("com.google.firebase:firebase-common-ktx:19.3.1")
    implementation("com.google.firebase:firebase-analytics-ktx:17.5.0")
    implementation("com.google.firebase:firebase-firestore-ktx:21.6.0")
    implementation("com.google.firebase:firebase-config-ktx:19.2.0")
    implementation("com.google.firebase:firebase-crashlytics:17.2.1")
    implementation("com.google.firebase:firebase-auth-ktx:19.3.2")

    // Naver Map
    implementation("com.naver.maps:map-sdk:3.8.0")

    // Debugging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Testing
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}