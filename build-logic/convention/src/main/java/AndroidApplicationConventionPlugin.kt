import com.partner.build_logic.convention.ModuleType
import com.partner.build_logic.convention.configureBuildTypes
import com.partner.build_logic.convention.configureKotlinAndroid
import com.partner.build_logic.convention.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                }

                configureKotlinAndroid(this)

                configureBuildTypes(
                    commonExtension = this,
                    moduleType = ModuleType.APPLICATION,
                    libs = libs
                )

                defaultConfig {
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                    testInstrumentationRunner = "com.done.app.InstrumentationTestRunner"
                }

                flavorDimensions += "env"
                productFlavors {
                    create("beta") {
                        dimension = "env"
                        applicationId = libs.findVersion("projectApplicationIdBeta").get().toString()
                        versionNameSuffix = "-beta"
                    }
                    create("prod") {
                        dimension = "env"
                        applicationId = libs.findVersion("projectApplicationId").get().toString()
                    }
                }
            }
        }
    }
}