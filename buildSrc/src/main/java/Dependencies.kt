/*
 * Copyright 2020 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object Versions {
    const val minSdk = 23
    const val compileSdk = 29
    val version = "git describe --tags --abbrev=0".runCommand()
    val versionCode = getVersionCode()

    // Plugins
    const val kotlin = "1.3.72"
    const val spotless = "4.0.1"
    const val gradleBuildTool = "4.0.1"
    const val googleServices = "4.3.3"
    const val crashlytics = "2.1.1"

    // Dependencies
    object Android {
        const val core = "1.3.0"
        const val appcompat = "1.1.0"
        const val junit = "1.1.1"
        const val espresso = "3.2.0"
    }

    const val dagger = "2.28"
    const val daggerHiltAndroidVersion = "2.28-alpha"
    const val daggerHiltVersion = "1.0.0-SNAPSHOT"

    const val lifecycleVersion = "2.2.0"

    const val retrofitVersion = "2.9.0"
    const val okhttpVersion = "4.7.2"

    const val moshiVersion = "1.9.2"
    const val moshiConverterVersion = "2.7.2"

    object Testing {
        const val junit = "4.12"
    }
}

object Deps {
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    }

    object Android {
        const val core = "androidx.core:core-ktx:${Versions.Android.core}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.Android.appcompat}"
        const val junit = "androidx.test.ext:junit:${Versions.Android.junit}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.Android.espresso}"
    }

    object Testing {
        const val junit = "junit:junit:${Versions.Testing.junit}"
    }
}