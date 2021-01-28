plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "io.github.namhyungu.keymap"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = buildVersionCode()
        versionName = buildVersionName()
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NCP_CLIENT_ID", "\"${getKeyProperty("NCP_CLIENT_ID")}\"")
        buildConfigField("String",
                         "NCP_CLIENT_SECRET",
                         "\"${getKeyProperty("NCP_CLIENT_SECRET")}\"")
    }
    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = getKeyProperty("KEYSTORE_PASSWORD")
            keyAlias = getKeyProperty("KEYSTORE_KEY_ALIAS")
            keyPassword = getKeyProperty("KEYSTORE_KEY_PASSWORD")
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
    buildFeatures {
        viewBinding = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")

    // AndroidX
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.activity:activity-ktx:1.2.0-rc01")
    implementation("androidx.fragment:fragment-ktx:1.3.0-rc01")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-rc01")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.startup:startup-runtime:1.0.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.29.1-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.29.1-alpha")

    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha01")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha01")
    // Network
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    // Google Services
    implementation("com.google.android.gms:play-services-location:17.1.0")
    implementation("com.google.android.gms:play-services-auth:19.0.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:26.3.0"))
    implementation("com.google.firebase:firebase-common-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    implementation("com.firebase:geofire-android-common:3.1.0")

    // Naver Map
    implementation("com.naver.maps:map-sdk:3.10.0")

    // Logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // UI
    implementation("com.airbnb.android:epoxy:4.3.1")
    kapt("com.airbnb.android:epoxy-processor:4.3.1")

    implementation("com.airbnb.android:paris:1.7.2")
    kapt("com.airbnb.android:paris-processor:1.7.2")

    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.github.skydoves:bundler:1.0.3")

    // Testing
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}