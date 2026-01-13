package com.hatem.noureddine.tictactoe.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<org.jetbrains.kotlin.gradle.plugin.KaptExtension>("kapt") {
                correctErrorTypes = true
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val hiltCompiler = "hilt.compiler"
            dependencies {
                "implementation"(libs.findLibrary("hilt.android").get())
                "kapt"(libs.findLibrary(hiltCompiler).get())
                "kaptTest"(libs.findLibrary(hiltCompiler).get())
                "kaptAndroidTest"(libs.findLibrary(hiltCompiler).get())
                "testImplementation"(libs.findLibrary("hilt.android.testing").get())
            }
        }
    }
}
