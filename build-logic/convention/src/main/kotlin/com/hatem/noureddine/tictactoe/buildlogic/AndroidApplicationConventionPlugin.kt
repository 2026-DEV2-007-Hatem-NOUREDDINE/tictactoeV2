package com.hatem.noureddine.tictactoe.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    targetSdk =
                        versionCatalog()
                            .findVersion("targetSdk")
                            .get()
                            .toString()
                            .toInt()
                }
                configureKotlinAndroid(this)

                tasks.withType(Test::class.java).configureEach {
                    useJUnitPlatform()
                    // Run tests in parallel using available CPU cores
                    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
                }
            }
        }
    }
}
