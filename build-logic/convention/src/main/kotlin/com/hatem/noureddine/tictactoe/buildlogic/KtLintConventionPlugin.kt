package com.hatem.noureddine.tictactoe.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

/**
 * Convention plugin that configures KtLint for Kotlin code style checking.
 *
 * Features:
 * - Android-aware linting
 * - Multiple report formats (Plain, Checkstyle, HTML)
 * - Generated code exclusion
 */
class KtLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            extensions.configure<KtlintExtension> {
                android.set(true)
                outputToConsole.set(true)
                outputColorName.set("RED")
                ignoreFailures.set(false)
                reporters {
                    reporter(ReporterType.PLAIN)
                    reporter(ReporterType.CHECKSTYLE)
                    reporter(ReporterType.HTML)
                }
                filter {
                    exclude("**/generated/**")
                    include("**/kotlin/**")
                }
            }
        }
    }
}
