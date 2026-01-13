package com.hatem.noureddine.tictactoe.buildlogic

import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.Installation
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val extension:
                CommonExtension<
                    out BuildFeatures,
                    out BuildType,
                    out DefaultConfig,
                    out ProductFlavor,
                    out AndroidResources,
                    out Installation,
                >? =
                extensions.findByType(ApplicationExtension::class.java)
                    ?: extensions.findByType(com.android.build.gradle.LibraryExtension::class.java)

            extension?.buildFeatures {
                compose = true
            }

            dependencies {
                val bom = versionCatalog().findLibrary("androidx-compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
                // Let's leave specific libraries to modules, but setup BOM.
            }
        }
    }
}
