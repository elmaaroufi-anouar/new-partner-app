plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {

// Target declarations - add or remove as needed below. These define
// which platforms this KMP module supports.
// See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.done.core"
        compileSdk = 35
        minSdk = 24
    }

// For iOS targets, this is also where you should
// configure native binary output. For more information, see:
// https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

// A step-by-step guide on how to include this library in an XCode
// project can be found here:
// https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "composeCoreKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

//    jvm("desktop")

// Source set declarations.
// Declaring a target automatically creates a source set with the same name. By default, the
// Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
// common to share sources between related targets.
// See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
//        val desktopMain by getting

        commonMain {
            dependencies {
                // Kotlin
                implementation(libs.kotlin.stdlib)

                // Compose
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                api(compose.components.resources)
                api(compose.components.uiToolingPreview)
                api(compose.materialIconsExtended)

                // Lifecycle
                api(libs.androidx.lifecycle.viewmodel)
                api(libs.androidx.lifecycle.runtimeCompose)

                // Koin
                api(libs.bundles.koin)

                // Networking
                implementation(libs.bundles.ktor)

                // DateTime
                implementation(libs.jetbrains.kotlinx.datetime)

                // DataStore
                api(libs.androidx.datastore.preferences)
                api(libs.androidx.datastore)

                // Serialization
                api(libs.kotlinx.serialization.json)

            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Kotlin
                implementation(libs.kotlin.stdlib)

                api(libs.androidx.core.ktx)
                api(libs.androidx.lifecycle.runtime.ktx)

                // Firebase
                api(project.dependencies.platform(libs.firebase.bom))
                api(libs.firebase.messaging.ktx)
                api(libs.firebase.analytics)
                api(libs.firebase.crashlytics)
                api(libs.firebase.config)

                // Networking
                implementation(libs.ktor.client.okhttp)

                // Koin
                api(libs.koin.android)
                api(libs.koin.androidx.compose)

                // DataStore
                api(libs.androidx.datastore.preferences)
                api(libs.androidx.datastore)

                // Serialization
                api(libs.kotlinx.serialization.json)

                // Pushy
                implementation(libs.pushy)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
                implementation(libs.ktor.client.darwin)
            }
        }

//        desktopMain.dependencies {
//            // Desktop-specific dependencies
//        }
    }

}

