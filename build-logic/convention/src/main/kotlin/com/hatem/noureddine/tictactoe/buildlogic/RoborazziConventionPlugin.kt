package com.hatem.noureddine.tictactoe.buildlogic

import io.github.takahirom.roborazzi.RoborazziExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import java.io.File

class RoborazziConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.github.takahirom.roborazzi")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                "testImplementation"(libs.findBundle("testing-roborazzi").get())
            }

            extensions.configure<RoborazziExtension> {
                val output = File(rootDir.absolutePath + "/snapshots/roborazzi/")
                outputDir.set(output)
            }
        }
    }
}
