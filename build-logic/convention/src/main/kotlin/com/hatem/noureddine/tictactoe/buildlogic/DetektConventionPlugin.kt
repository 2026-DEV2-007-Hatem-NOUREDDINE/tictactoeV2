package com.hatem.noureddine.tictactoe.buildlogic

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                allRules = false
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            }

            dependencies {
                add("detektPlugins", versionCatalog().findLibrary("detekt-formatting").get())
                add("detektPlugins", versionCatalog().findLibrary("detekt-compose-rules").get())
            }
        }
    }
}
