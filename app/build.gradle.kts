plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(Versions.compileSdk)
    defaultConfig {
        applicationId = "dev.namhyun.geokey"
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.compileSdk)
        versionCode = Versions.versionCode
        versionName = Versions.version
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NCP_CLIENT_ID", "\"${getKey("NCP_CLIENT_ID")}\"")
        buildConfigField("String", "NCP_CLIENT_SECRET", "\"${getKey("NCP_CLIENT_SECRET")}\"")
    }
    signingConfigs {
        register("release") {
            storeFile = file("release.keystore")
            storePassword = getKey("KEYSTORE_PASSWORD")
            keyAlias = getKey("KEYSTORE_KEY_ALIAS")
            keyPassword = getKey("KEYSTORE_KEY_PASSWORD")
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
}

dependencies {
    // Kotlin
    implementation(Deps.Kotlin.stdlib)

    // Android
    implementation(Deps.Android.core)
    implementation(Deps.Android.appcompat)
    implementation("androidx.activity:activity-ktx:1.2.0-alpha06")
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha06")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}")

    // Dagger
    implementation("com.google.dagger:dagger:${Versions.dagger}")
    implementation("com.google.dagger:hilt-android:${Versions.daggerHiltAndroidVersion}")
    implementation("com.google.dagger:hilt-android-testing:${Versions.daggerHiltAndroidVersion}")
    implementation("androidx.hilt:hilt-common:${Versions.daggerHiltVersion}")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:${Versions.daggerHiltVersion}")
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.daggerHiltAndroidVersion}")
    kapt("androidx.hilt:hilt-compiler:${Versions.daggerHiltVersion}")

    // moshi
    implementation("com.squareup.moshi:moshi:${Versions.moshiVersion}")
    implementation("com.squareup.moshi:moshi-kotlin:${Versions.moshiVersion}")
    implementation("com.squareup.moshi:moshi-adapters:${Versions.moshiVersion}")
    implementation("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshiVersion}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.moshiConverterVersion}")

    // Network
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}")
    implementation("com.squareup.okhttp3:logging-interceptor:${Versions.okhttpVersion}")
    implementation("com.github.skydoves:sandwich:1.0.1")

    // Google Services
    implementation("com.google.android.gms:play-services-location:17.0.0")
    implementation("com.google.android.gms:play-services-auth:18.0.0")

    // Firebase
    implementation("com.google.firebase:firebase-common-ktx:19.3.0")
    implementation("com.google.firebase:firebase-analytics-ktx:17.4.3")
    implementation("com.google.firebase:firebase-firestore-ktx:21.4.3")
    implementation("com.google.firebase:firebase-config-ktx:19.1.4")
    implementation("com.google.firebase:firebase-crashlytics:17.0.1")
    implementation("com.google.firebase:firebase-auth-ktx:19.3.1")

    // Naver Map
    implementation("com.naver.maps:map-sdk:3.8.0")

    // Debugging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Testing
    testImplementation(Deps.Testing.junit)
    androidTestImplementation(Deps.Android.junit)
    androidTestImplementation(Deps.Android.espresso)
}