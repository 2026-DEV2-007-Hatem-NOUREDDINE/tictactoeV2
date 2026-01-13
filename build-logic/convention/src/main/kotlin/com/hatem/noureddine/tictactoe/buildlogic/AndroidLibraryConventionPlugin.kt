package com.hatem.noureddine.tictactoe.buildlogic

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk =
                    versionCatalog()
                        .findVersion("targetSdk")
                        .get()
                        .toString()
                        .toInt()
            }
            dependencies {
                add("testImplementation", kotlin("test"))
            }

            tasks.withType(Test::class.java).configureEach {
                useJUnitPlatform()
                // Run tests in parallel using available CPU cores
                maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
            }
        }
    }
}
