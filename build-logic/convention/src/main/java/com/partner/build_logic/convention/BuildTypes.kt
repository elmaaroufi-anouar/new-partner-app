package com.partner.build_logic.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    moduleType: ModuleType,
    libs: VersionCatalog
) {
    commonExtension.run {

        buildFeatures {
            buildConfig = true
        }

        when (moduleType) {
            ModuleType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(libs = libs)
                        }
                        release {
                            configureReleaseBuildType(
                                commonExtension = commonExtension,
                                moduleType = moduleType,
                                libs = libs
                            )
                        }
                    }
                }
            }

            ModuleType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(libs = libs)
                        }
                        release {
                            configureReleaseBuildType(
                                commonExtension = commonExtension,
                                moduleType = moduleType,
                                libs = libs
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType(
    libs: VersionCatalog
) {
    this.addBuildFields(isRelease = false, libs = libs)
    isMinifyEnabled = false
}

private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    moduleType: ModuleType,
    libs: VersionCatalog
) {
    this.addBuildFields(libs = libs)

    if (moduleType == ModuleType.APPLICATION) {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}

private fun BuildType.addBuildFields(
    isRelease: Boolean = true,
    libs: VersionCatalog
) {
    val baseUrl = libs.findVersion(if (isRelease) "baseUrl" else "baseUrlBeta").get().toString()
    buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

    val eventsBaseUrl = libs.findVersion(if (isRelease) "eventsBaseUrl" else "eventsBaseUrlBeta").get().toString()
    buildConfigField("String", "EVENTS_BASE_URL", "\"$eventsBaseUrl\"")

    val foodBaseUrl = libs.findVersion(if (isRelease) "foodBaseUrl" else "foodBaseUrlBeta").get().toString()
    buildConfigField("String", "FOOD_BASE_URL", "\"$foodBaseUrl\"")

    val projectVersionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
    buildConfigField("int", "VERSION_CODE", "$projectVersionCode")

    val projectVersionName = libs.findVersion("projectVersionName").get().toString()
    buildConfigField("String", "VERSION_NAME", "\"$projectVersionName\"")

    val appId = libs.findVersion(if (isRelease) "projectApplicationId" else "projectApplicationIdBeta").get().toString()
    buildConfigField("String", "APPLICATION_ID", "\"$appId\"")
}