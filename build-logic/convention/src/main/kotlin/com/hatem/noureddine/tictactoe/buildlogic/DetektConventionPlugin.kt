package com.hatem.noureddine.tictactoe.buildlogic

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

/**
 * Convention plugin that configures Detekt for code analysis.
 *
 * Features:
 * - Configures Detekt with project-specific rules
 * - Adds formatting and Compose rules plugins
 * - Creates a merged report task for all modules
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            configureDetektExtension()
            addDetektPlugins()

            // Register merged report task only on root project
            if (this == rootProject) {
                registerMergedReportTask()
            }
        }
    }

    private fun Project.configureDetektExtension() {
        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            allRules = false
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            basePath = rootDir.absolutePath
            parallel = true
        }

        // Configure individual Detekt tasks
        tasks.withType<Detekt>().configureEach {
            reports {
                xml.required.set(true)
                html.required.set(true)
                sarif.required.set(true)
                md.required.set(false)
            }
        }
    }

    private fun Project.addDetektPlugins() {
        dependencies {
            add("detektPlugins", versionCatalog().findLibrary("detekt-formatting").get())
            add("detektPlugins", versionCatalog().findLibrary("detekt-compose-rules").get())
        }
    }

    private fun Project.registerMergedReportTask() {
        // Register XML merge task
        val detektReportMergeXml =
            tasks.register<ReportMergeTask>("detektReportMergeXml") {
                group = "verification"
                description = "Merges all Detekt XML reports into a single report"
                output.set(layout.buildDirectory.file("reports/detekt/detekt.xml"))
            }

        // Register HTML merge task
        val detektReportMergeHtml =
            tasks.register<ReportMergeTask>("detektReportMergeHtml") {
                group = "verification"
                description = "Merges all Detekt HTML reports into a single report"
                output.set(layout.buildDirectory.file("reports/detekt/detekt.html"))
            }

        // Register SARIF merge task
        val detektReportMergeSarif =
            tasks.register<ReportMergeTask>("detektReportMergeSarif") {
                group = "verification"
                description = "Merges all Detekt SARIF reports into a single report"
                output.set(layout.buildDirectory.file("reports/detekt/detekt.sarif"))
            }

        // Convenience task to run all merges
        tasks.register("detektReportMerge") {
            group = "verification"
            description = "Runs all Detekt report merge tasks"
            dependsOn(detektReportMergeXml, detektReportMergeHtml, detektReportMergeSarif)
        }

        // Configure all subprojects to contribute to merged reports
        subprojects {
            plugins.withId("io.gitlab.arturbosch.detekt") {
                tasks.withType<Detekt>().configureEach {
                    finalizedBy(detektReportMergeXml, detektReportMergeHtml, detektReportMergeSarif)
                }

                detektReportMergeXml.configure {
                    input.from(tasks.withType<Detekt>().map { it.xmlReportFile })
                }

                detektReportMergeHtml.configure {
                    input.from(tasks.withType<Detekt>().map { it.htmlReportFile })
                }

                detektReportMergeSarif.configure {
                    input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
                }
            }
        }
    }
}
