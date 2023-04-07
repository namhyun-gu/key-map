plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "io.github.namhyungu.keymap"
    compileSdk = 33
    defaultConfig {
        applicationId = "io.github.namhyungu.keymap"
        minSdk = 23
        targetSdk = 33
        versionCode = buildVersionCode()
        versionName = buildVersionName()
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NCP_CLIENT_ID", "\"${getKeyProperty("NCP_CLIENT_ID")}\"")
        buildConfigField(
            "String",
            "NCP_CLIENT_SECRET",
            "\"${getKeyProperty("NCP_CLIENT_SECRET")}\""
        )
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
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packagingOptions {
        resources {
            excludes.add("META-INF/versions/9/previous-compilation-data.bin")
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0-Beta")

    // AndroidX
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.0-alpha09")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.startup:startup-runtime:1.1.1")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("com.google.dagger:hilt-android-compiler:2.43.2")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // Google Services
    implementation("com.google.android.gms:play-services-cronet:18.0.1")
    // implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:26.3.0"))
    implementation("com.google.firebase:firebase-common-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    implementation("com.firebase:geofire-android-common:3.1.0")

    implementation("io.github.fornewid:naver-map-compose:1.3.3")

    // Accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.31.0-alpha")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.04.00"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material3:material3:1.1.0-beta02")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Naver Map
    implementation("com.naver.maps:map-sdk:3.16.2")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // UI
    implementation("com.airbnb.android:epoxy:4.3.1")
    kapt("com.airbnb.android:epoxy-processor:4.3.1")

    implementation("com.airbnb.android:paris:1.7.2")
    kapt("com.airbnb.android:paris-processor:1.7.2")

    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.github.skydoves:bundler:1.0.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}