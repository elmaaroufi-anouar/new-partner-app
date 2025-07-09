import com.partner.build_logic.convention.ModuleType
import com.partner.build_logic.convention.configureBuildTypes
import com.partner.build_logic.convention.configureKotlinAndroid
import com.android.build.api.dsl.LibraryExtension
import com.partner.build_logic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
                configureKotlinAndroid(this)
                configureBuildTypes(
                    commonExtension = this,
                    moduleType = ModuleType.LIBRARY,
                    libs = libs
                )
            }
        }
    }
}